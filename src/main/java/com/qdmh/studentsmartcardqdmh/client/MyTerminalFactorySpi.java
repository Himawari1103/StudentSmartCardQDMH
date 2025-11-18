///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.qdmh.studentsmartcardqdmh.client;
//
//import com.licel.jcardsim.smartcardio.CardSimulator;
//import java.util.List;
//import javax.smartcardio.CardTerminal;
//import javax.smartcardio.CardTerminals;
//import javax.smartcardio.TerminalFactorySpi;
//
///**
// *
// * @author nhnquang
// */
//public class MyTerminalFactorySpi extends TerminalFactorySpi {
//
//    private final List<CardTerminal> terminals;
//
//    public MyTerminalFactorySpi(Object params) {
//        // Tạo simulator và terminal ảo
//        CardSimulator simulator = new CardSimulator();
//        CardTerminal terminal = new CardTerminal("VirtualReader0", simulator);
//
//        terminals = new ArrayList<>();
//        terminals.add(terminal);
//    }
//
//    @Override
//    protected CardTerminals engineTerminals() {
//        return new CardTerminals() {
//            @Override
//            public List<CardTerminal> list(State state) {
//                return terminals;
//            }
//
//            @Override
//            public boolean waitForChange(long timeout) {
//                return false;
//            }
//        };
//    }
//}
