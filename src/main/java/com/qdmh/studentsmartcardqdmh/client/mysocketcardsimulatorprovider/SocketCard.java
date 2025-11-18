/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qdmh.studentsmartcardqdmh.client.mysocketcardsimulatorprovider;

/**
 *
 * @author nhnquang
 */
import javax.smartcardio.*;
import java.io.*;
import java.net.Socket;

public class SocketCard extends Card {

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public SocketCard(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    // basic atr - unuse
    @Override
    public ATR getATR() {
        return new ATR(new byte[]{0x3B, 0x00}); // Answer To Reset
    }

    @Override
    public String getProtocol() {
        return "T=0";
    }

    @Override
    public CardChannel getBasicChannel() {
        return new SocketCardChannel(this);
    }

    @Override
    public void disconnect(boolean reset) throws CardException {
        try {
            socket.close();
        } catch (IOException e) {
            throw new CardException("Error closing socket", e);
        }
    }

    protected DataInputStream getIn() {
        return in;
    }

    protected DataOutputStream getOut() {
        return out;
    }

    @Override
    public CardChannel openLogicalChannel() throws CardException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void beginExclusive() throws CardException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void endExclusive() throws CardException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public byte[] transmitControlCommand(int controlCode, byte[] command) throws CardException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
