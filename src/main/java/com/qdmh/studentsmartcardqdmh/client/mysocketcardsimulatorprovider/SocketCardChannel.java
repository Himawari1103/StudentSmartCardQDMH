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
import java.nio.ByteBuffer;

public class SocketCardChannel extends CardChannel {

    private final SocketCard card;

    public SocketCardChannel(SocketCard card) {
        this.card = card;
    }

    @Override
    public Card getCard() {
        return card;
    }

    @Override
    public int getChannelNumber() {
        return 0;
    }

    @Override
    public ResponseAPDU transmit(CommandAPDU apdu) throws CardException {
        try {
            byte[] data = apdu.getBytes();
            card.getOut().writeByte(data.length);
            card.getOut().write(data);
            int len = card.getIn().readUnsignedByte();
            byte[] resp = new byte[len];
            card.getIn().readFully(resp);
            return new ResponseAPDU(resp);
        } catch (IOException e) {
            throw new CardException("APDU transmit failed", e);
        }
    }

    @Override
    public void close() {
    }

    @Override
    public int transmit(ByteBuffer command, ByteBuffer response) throws CardException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
