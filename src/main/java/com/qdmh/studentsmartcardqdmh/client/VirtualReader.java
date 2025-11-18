/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qdmh.studentsmartcardqdmh.client;

import com.licel.jcardsim.smartcardio.CardSimulator;
import com.licel.jcardsim.utils.AIDUtil;
import com.qdmh.studentsmartcardqdmh.applet.helloworldapplet.HelloWorldApplet;
import com.qdmh.studentsmartcardqdmh.applet.studentapplet.StudentApplet;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

/**
 *
 * @author nhnquang
 */
public class VirtualReader {
    public static void main(String[] args) {
        List<MyApplet> myApplets = new ArrayList<>();
        myApplets.add(new MyApplet(HelloWorldApplet.class, AIDUtil.create("A0000000010101")));
        myApplets.add(new MyApplet(StudentApplet.class, AIDUtil.create("11223344550000")));

        // create simulator
        CardSimulator simulator = new CardSimulator();

        // install applet
        for (MyApplet myApplet : myApplets) {
            simulator.installApplet(myApplet.getAid(), myApplet.getClassName());
        }
        
        simulator.selectApplet(myApplets.get(0).getAid());
        
        // use socket for simulator
        try {
            ServerSocket serverSocket = new ServerSocket(9025);
            System.out.println("Simulator listening on port 9025...");
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Client connected: " + client.getInetAddress());
                DataInputStream in = new DataInputStream(client.getInputStream());
                DataOutputStream out = new DataOutputStream(client.getOutputStream());
                try {
                    while (true) {
                        int len = in.readUnsignedByte();
                        byte[] apdu = new byte[len];
                        in.readFully(apdu);
                        // !TODO
                        CommandAPDU commandAPDU = null;
                        try {
                            commandAPDU = new CommandAPDU(apdu);
                        } catch (Exception e){
                            System.out.println(e.getCause());
                            continue;
                        }
                        ResponseAPDU response = simulator.transmitCommand(commandAPDU);
                        byte[] responseBytes = response.getBytes();
                        out.writeByte(responseBytes.length);
                        out.write(responseBytes);
                    }
                } catch (EOFException e) {
                    System.out.println("Client disconnected");
                }

            }
        } catch (IOException ex) {
            System.getLogger(VirtualReader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

    }
}
