//package com.qdmh.studentsmartcardqdmh.applet.helloworldapplet;
//
//import javacard.framework.*;
//
//public class HelloWorldApplet extends Applet {
//
//    private static final byte[] HELLO_WORLD = {(byte) 'H', (byte) 'e', (byte) 'l', (byte) 'l', (byte) 'o'};
//
//    protected HelloWorldApplet() {
//        register();
//    }
//
//    public static void install(byte[] bArray, short bOffset, byte bLength) {
//        new HelloWorldApplet();
//    }
//
//    public void process(APDU apdu) {
//        if (selectingApplet()) {
//            return;
//        }
//
//        byte[] buffer = apdu.getBuffer();
//        short len = (short) HELLO_WORLD.length;
//        Util.arrayCopyNonAtomic(HELLO_WORLD, (short) 0, buffer, ISO7816.OFFSET_CDATA, len);
//        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, len);
//    }
//}
