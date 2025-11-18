/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qdmh.studentsmartcardqdmh.client.mysocketcardsimulatorprovider;

import java.io.IOException;
import java.net.Socket;
import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

/**
 *
 * @author nhnquang
 */
public class SocketCardTerminal extends CardTerminal {

    private final String host;
    private final int port;
    private boolean connected = false;

    public SocketCardTerminal(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public String getName() {
        return "SocketCardTerminal[" + host + ":" + port + "]";
    }

    @Override
    public Card connect(String protocol) throws CardException {
        try {
            Socket socket = new Socket(host, port);
            connected = true;
            return new SocketCard(socket);
        } catch (IOException e) {
            throw new CardException("Cannot connect to virtual reader", e);
        }
    }

    @Override
    public boolean isCardPresent() {
        return connected;
    }

    @Override
    public boolean waitForCardPresent(long timeout) {
        return true;
    }

    @Override
    public boolean waitForCardAbsent(long timeout) {
        return false;
    }
}
