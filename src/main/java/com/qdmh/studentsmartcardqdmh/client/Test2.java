/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qdmh.studentsmartcardqdmh.client;

import com.qdmh.studentsmartcardqdmh.client.mysocketcardsimulatorprovider.SocketCardProvider;
import com.qdmh.studentsmartcardqdmh.client.mysocketcardsimulatorprovider.SocketProviderParameter;
import java.security.Security;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

/**
 *
 * @author nhnquang
 */
public class Test2 {

    public static void main(String[] args) throws Exception {
        Security.addProvider(new SocketCardProvider());

        TerminalFactory factory = TerminalFactory.getInstance(
                "SocketCardSim",
                new SocketProviderParameter("localhost", 9025)
        );

        CardTerminal terminal = factory.terminals().list().get(0);
        System.out.println("Using terminal: " + terminal.getName());

        Card card = terminal.connect("*");
        CardChannel channel = card.getBasicChannel();
        // Select AID
        byte[] aid = {(byte) 0xA0, 0x00, 0x00, 0x00, 0x02, 0x01, 0x01};
        ResponseAPDU resp = channel.transmit(new CommandAPDU(0x00, 0xA4, 0x04, 0x00, aid));
        System.out.println("SELECT SW=" + String.format("%04X", resp.getSW()));

        // send apdu
        byte[] commandBytes = new byte[]{(byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00};
        CommandAPDU commandAPDU = new CommandAPDU(commandBytes);
        ResponseAPDU resp2 = channel.transmit(commandAPDU);
        System.out.println("DATA HEX: " + bytesToHex(resp2.getData()));
        System.out.println("DATA: " + bytesToString(resp2.getData()));
        System.out.println("SW=" + String.format("%04X", resp2.getSW()));
    }
    static String bytesToHex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) {
            sb.append(String.format("%02X ", x));
        }
        return sb.toString();
    }
    
    static String bytesToString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) {
            sb.append((char)x);
        }
        return sb.toString();
    }
}
