/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qdmh.studentsmartcardqdmh.client;

import javacard.framework.AID;
import com.licel.jcardsim.smartcardio.CardSimulator;
import com.licel.jcardsim.utils.AIDUtil;
import com.qdmh.studentsmartcardqdmh.applet.helloworldapplet.HelloWorldApplet;
import java.util.Scanner;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

/**
 *
 * @author nhnquang
 */
public class Test {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // create simulator
        CardSimulator simulator = new CardSimulator();

        // install applet
        AID appletAID = AIDUtil.create("A0000000010101");
        simulator.installApplet(appletAID, HelloWorldApplet.class);
        simulator.selectApplet(appletAID);

        // send APDU
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0x01, 0x00, 0x00);
        ResponseAPDU response = simulator.transmitCommand(commandAPDU);

        // check response
        System.out.println("response: " + Integer.toHexString(response.getSW()));
        for (int i = 0; i < response.getData().length; i++) {
            System.out.print((char) response.getData()[i]);
        }

    }

    private static void selectApplet(CardSimulator cardSimulator, AID aid) {
        cardSimulator.selectApplet(aid);
    }

    private static String bytesToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
    
    private static byte[] hexStringToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) * 16) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
