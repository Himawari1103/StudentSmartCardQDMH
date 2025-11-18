/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qdmh.studentsmartcardqdmh.client.mysocketcardsimulatorprovider;

import java.util.ArrayList;
import java.util.List;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactorySpi;

/**
 *
 * @author nhnquang
 */
public class SocketCardProviderSpi extends TerminalFactorySpi {

    private final List<CardTerminal> terminals = new ArrayList<>();

    public SocketCardProviderSpi(Object parameter) {
        if(parameter == null){
            terminals.add(new SocketCardTerminal("localhost", 9025));
        } else {
            SocketProviderParameter p = (SocketProviderParameter) parameter;
            terminals.add(new SocketCardTerminal(p.host, p.port));
        }
    }

    @Override
    protected CardTerminals engineTerminals() {
        return new CardTerminals() {
            @Override
            public List<CardTerminal> list(CardTerminals.State state) {
                return terminals;
            }

            @Override
            public boolean waitForChange(long timeout) {
                return false;
            }
        };
    }

}
