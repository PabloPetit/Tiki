package com.example.pablo.tiki;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class Pack implements Serializable{

    private static final long serialVersionUID = -2746547097176689395L;

    public static final int TIMEOUT = 5000;
    public static final int LITTLE_SLEEP = 50;


    public static final String PASSWORD = "password";
    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String ADMIN_PASSWORD = "admin_password";


    public static final int LOG_DATA = 1;
    public static final int DENIED = 2;
    public static final int ACCEPTED = 3;
    public static final int NEW_ID = 4;
    public static final int ACK = 5;
    public static final int SERVER_NAME = 6;
    public static final int QUIT = 7;
    public static final int SHUTDOWN = 8;
    public static final int LOG_ADMIN_DATA = 9;
    public static final int TOGGLE = 10;
    public static final int SEQUENCE = 11;
    public static final int ON = 12;
    public static final int OFF = 13;
    public static final int BLOCK_ACCESS = 14;
    public static final int RANDOM = 15;
    public static final int CLIENT_LIST = 16;
    public static final int SLEEP = 17;

    private int performative;
    private HashMap<String, Object> data;

    public Pack(){}

    public Pack(int performative) {
        this.performative = performative;
        this.data = new HashMap<String, Object>();
    }

    public Pack(int performative, HashMap<String, Object> data) {
        this.performative = performative;
        this.data = data;
    }

    public int getPerformative() {
        return this.performative;
    }

    public HashMap<String, Object> getData() {
        return this.data;
    }

    public static Pack readPack(ObjectInputStream input){
        Pack p = null;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < TIMEOUT){
            try {
                p = (Pack)input.readObject();
                break;
            } catch (EOFException e) {
                try {
                    Thread.sleep(LITTLE_SLEEP);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    break;
                }
            } catch (Exception e){
                e.printStackTrace();
                break;
            }
        }
        return p;
    }


    public static boolean sendPack(Pack p, ObjectOutputStream output){
        try {
            output.reset();
            output.writeObject(p);
            output.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}

