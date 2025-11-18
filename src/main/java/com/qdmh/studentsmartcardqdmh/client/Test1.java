/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qdmh.studentsmartcardqdmh.client;

import com.licel.jcardsim.smartcardio.JCardSimProvider;

import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;

/**
 *
 * @author nhnquang
 */
public class Test1 {

    public static void main(String[] args) {
        try {
            if (Security.getProvider("jCardSim") == null) {
                JCardSimProvider provider = new JCardSimProvider();
                Security.addProvider(provider);
            }

            TerminalFactory factory = TerminalFactory.getInstance("jCardSim", null);
            TerminalFactory factory1 = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();

            if (terminals.isEmpty()) {
                System.out.println("No card readers found.");
                return;
            }

            CardTerminal terminal = terminals.get(0);
            
            System.out.println("Using reader: " + terminal.getName());

            // 1) chờ card xuất hiện (timeout ms; 0 = wait forever)
            System.out.println("Waiting for card insertion...");
            boolean present = terminal.waitForCardPresent(0);
            if (!present) {
                System.out.println("No card presented (timeout).");
                return;
            }

            // 2) connect -> chọn protocol tự động
            Card card = null;
            try {
                card = terminal.connect("*"); // or "T=0" / "T=1"
                System.out.println("Card connected. ATR: " + bytesToHex(card.getATR().getBytes()));

                CardChannel channel = card.getBasicChannel();

                // 3) Select applet
                byte[] aid = new byte[]{(byte) 0xA0, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01};
                CommandAPDU select = new CommandAPDU(0x00, 0xA4, 0x04, 0x00, aid);
                ResponseAPDU r = channel.transmit(select);
                System.out.println("SELECT SW: " + String.format("%04X", r.getSW()));

                // 4) Send apdu
                CommandAPDU cmd = new CommandAPDU(0x00, 0x01, 0x00, 0x00);
                ResponseAPDU resp = channel.transmit(cmd);
                System.out.println("Resp SW: " + String.format("%04X", resp.getSW()));
                System.out.println("Data: " + bytesToHex(resp.getData()));
            } finally {
                if (card != null) {
                    card.disconnect(false);
                }
                // tuỳ muốn chờ removal:
                // terminal.waitForCardAbsent(0);
            }
        } catch (CardException ex) {
            Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            System.getLogger(Test1.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    private static String bytesToHex(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

}
