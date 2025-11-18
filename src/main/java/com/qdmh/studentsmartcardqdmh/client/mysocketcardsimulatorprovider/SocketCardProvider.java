/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qdmh.studentsmartcardqdmh.client.mysocketcardsimulatorprovider;

import java.security.Provider;

/**
 *
 * @author nhnquang
 */
public class SocketCardProvider extends Provider {

    public SocketCardProvider() {
        super("SocketCardSim", 1.0, "Socket-based JavaCard Terminal Provider");
        put("TerminalFactory.SocketCardSim", SocketCardProviderSpi.class.getName());
    }
}
