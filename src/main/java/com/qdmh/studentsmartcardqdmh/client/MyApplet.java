/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qdmh.studentsmartcardqdmh.client;

import javacard.framework.AID;

/**
 *
 * @author nhnquang
 */
public class MyApplet {

    private Class className;
    private AID aid;

    public MyApplet() {
    }

    public MyApplet(Class className, AID aid) {
        this.className = className;
        this.aid = aid;
    }

    public AID getAid() {
        return aid;
    }

    public void setAid(AID aid) {
        this.aid = aid;
    }

    public Class getClassName() {
        return className;
    }

    public void setClassName(Class className) {
        this.className = className;
    }
}
