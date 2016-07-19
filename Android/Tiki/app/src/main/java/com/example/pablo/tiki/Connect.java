package com.example.pablo.tiki;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connect extends AsyncTask {

    public static String LOGD_REF = "CONNECT";


    public boolean checkInternetConnexion(MainMenu main){
        Log.d(LOGD_REF,"Checking internet connexion");
        ConnectivityManager connMgr = (ConnectivityManager)main.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d(LOGD_REF,"Connected to internet");
            return true;
        } else {
            Log.d(LOGD_REF,"No internet connexion");
            return false;
        }
    }

    public boolean connexionToServer(SharedPreferences settings){
        Log.d(LOGD_REF,"Connecting to server...");
        String ip = settings.getString(Settings.IP,Settings.DEFAULT_IP);
        int port = settings.getInt(Settings.PORT,Settings.DEFAULT_PORT);
        try{
            Connexion.socket = new Socket(ip, port);
            Connexion.input = new ObjectInputStream(Connexion.socket.getInputStream());
            Connexion.output = new ObjectOutputStream(Connexion.socket.getOutputStream());
            Connexion.connected.set(true);
        }catch (IOException e){
            Log.d(LOGD_REF,"Could not open socket");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean simpleLoggin(SharedPreferences settings){
        Log.d(LOGD_REF,"Trying to log on server...");

        String password = settings.getString(Settings.PASSWORD,Settings.DEFAULT_PASSWORD);
        String name = settings.getString(Settings.NAME,Settings.DEFAULT_NAME);
        int id = settings.getInt(Settings.ID,Settings.DEFAULT_ID);


        Proto server_name = readProto();
        if (server_name == null){
            Log.d(LOGD_REF,"Server name not received");
            return false;
        }
        Connexion.serverName = (String) server_name.getData().get("NAME");
        Log.d(LOGD_REF,"Server name received : "+Connexion.serverName);
        Log.d(LOGD_REF,"Sending log data");
        Proto logData = new Proto(Proto.LOG_DATA);

        logData.getData().put("PASS",password);
        logData.getData().put("NAME",name);
        logData.getData().put("ID",id);

        sendProto(logData);

        Proto response = readProto();

        if (response == null || response.getPerformative() == Proto.DENIED){
            Log.d(LOGD_REF,(response == null)?"No response":"Server denied login");
            return false;
        }
        else if (response.getPerformative() == Proto.ACCEPTED){
            Connexion.logged.set(true);
            Proto ack = new Proto(Proto.ACK);
            sendProto(ack);
        }
        else if (response.getPerformative() == Proto.NEW_ID){
            Log.d(LOGD_REF,"New id received : "+(Integer)response.getData().get("ID"));
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(Settings.ID, (Integer) response.getData().get("ID"));
            editor.commit();

            Proto accepted = readProto();

            if (accepted != null && accepted.getPerformative() == Proto.ACCEPTED){
                Connexion.logged.set(true);
                Proto ack = new Proto(Proto.ACK);
                sendProto(ack);
            }else {
                Log.d(LOGD_REF,(accepted == null)?"ACCEPTED not received":"Wrong performative, ACCEPTED expected [1]");
                return false;
            }
        }else {
            Log.d(LOGD_REF,"Wrong performative, ACCEPTED expected [2]");
            return false;
        }
        return true;
    }

    public boolean adminLogin(SharedPreferences settings){
        String adminPassword = settings.getString(Settings.ADMIN_PASSWORD,Settings.DEFAULT_PASSWORD);
        Proto adminPass = new Proto(Proto.LOG_ADMIN_DATA);
        adminPass.getData().put("ADMIN_PASSWORD",adminPassword);
        sendProto(adminPass);

        Proto response = readProto();

        if (response == null || response.getPerformative() == Proto.DENIED){
            return false;
        }
        else if(response.getPerformative() == Proto.ACCEPTED){
            Connexion.admin_logged.set(true);
        }
        return true;
    }

    public void abortConnexion(MainMenu main,String title, String message){
        Message messageHandler = main.getConnexionHandler().obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString(Connexion.TITLE,title);
        bundle.putString(Connexion.MESSAGE,message);
        messageHandler.setData(bundle);
        messageHandler.sendToTarget();

        if(Connexion.socket != null){
            try {
                Connexion.input.close();
                Connexion.output.close();
                Connexion.socket.close();
                Connexion.socket = null;
                Connexion.input = null;
                Connexion.output = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Proto readProto(){
        Proto p = null;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < Connexion.TIMEOUT){
            try {
                if (Connexion.input.available() > 0){
                    p = (Proto)Connexion.input.readObject();
                }else {
                    Thread.sleep(Connexion.LITTLE_SLEEP);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return p;
    }

    public boolean sendProto(Proto p){
        try {
            Connexion.output.writeObject(p);
            Connexion.output.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }



    @Override
    protected Object doInBackground(Object[] params) {
        MainMenu main = (MainMenu)params[0];
        Context context = (Context) params[1];
        SharedPreferences settings = context.getSharedPreferences(Settings.FILENAME,Settings.MODE);

        if (!checkInternetConnexion(main)){
            abortConnexion(main,"Connexion failed","No internet connexion");
            return null;
        }

        if (!connexionToServer(settings)){
            abortConnexion(main,"Connexion failed","Failed to connect to the server");
            return null;
        }

        if (!simpleLoggin(settings)){
            abortConnexion(main,"Connexion failed","Wrong password, change the settings and try again");
            return null;
        }
        adminLogin(settings);
        return null;
    }
}
