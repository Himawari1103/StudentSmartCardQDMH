/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qdmh.studentsmartcardqdmh.client.mysocketcardsimulatorprovider;

import java.util.ArrayList;
import java.util.List;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;

/**
 *
 * @author nhnquang
 */
public class SocketCardTerminals extends CardTerminals {

    private final List<CardTerminal> list;

    public SocketCardTerminals(String host, int port) {
        list = new ArrayList<>();
        list.add(new SocketCardTerminal(host, port));
    }

    @Override
    public List<CardTerminal> list(CardTerminals.State state) {
        return list;
    }

    @Override
    public boolean waitForChange(long timeout) {
        return false;
    }
}
