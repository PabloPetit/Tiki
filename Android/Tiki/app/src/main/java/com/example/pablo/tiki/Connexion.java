package com.example.pablo.tiki;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;


/**
 * Created by Pablo on 10/07/2016.
 */
public class Connexion {

    public static final String TITLE = "title";
    public static final String MESSAGE = "message";

    public static Socket socket = null;
    public static ObjectInputStream input;
    public static ObjectOutputStream output;
    public static String serverName = "NOT_SET";


    public static AtomicBoolean connected = new AtomicBoolean(false);
    public static AtomicBoolean logged = new AtomicBoolean(false);
    public static AtomicBoolean admin_logged = new AtomicBoolean(false);



}
