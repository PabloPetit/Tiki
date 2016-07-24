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
import java.util.concurrent.atomic.AtomicBoolean;

public class Connect extends AsyncTask {

    public static String LOG_TAG = "CONNECT";
    public static AtomicBoolean running = new AtomicBoolean(false);


    public boolean checkInternetConnexion(MainMenu main){
        Log.d(LOG_TAG,"Checking internet connexion");
        ConnectivityManager connMgr = (ConnectivityManager)main.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d(LOG_TAG,"Connected to internet");
            return true;
        } else {
            Log.d(LOG_TAG,"No internet connexion");
            return false;
        }
    }

    public boolean connexionToServer(SharedPreferences settings){
        Log.d(LOG_TAG,"Connecting to server...");
        String ip = settings.getString(Settings.IP,Settings.DEFAULT_IP);
        int port = settings.getInt(Settings.PORT,Settings.DEFAULT_PORT);
        try{
            Connexion.socket = new Socket(ip, port);
            Connexion.input = new ObjectInputStream(Connexion.socket.getInputStream());
            Connexion.output = new ObjectOutputStream(Connexion.socket.getOutputStream());
            Connexion.output.flush();
            Connexion.connected.set(true);
        }catch (IOException e){
            Log.d(LOG_TAG,"Could not open socket");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean simpleLoggin(SharedPreferences settings){
        Log.d(LOG_TAG,"Trying to log on server...");

        String password = settings.getString(Settings.PASSWORD,Settings.DEFAULT_PASSWORD);
        String name = settings.getString(Settings.NAME,Settings.DEFAULT_NAME);
        int id = settings.getInt(Settings.ID,Settings.DEFAULT_ID);


        Pack server_name = Pack.readPack(Connexion.input);
        if (server_name == null){
            Log.d(LOG_TAG,"Server name not received");
            return false;
        }
        Connexion.serverName = (String) server_name.getData().get("NAME");
        Log.d(LOG_TAG,"Server name received : "+Connexion.serverName);
        Log.d(LOG_TAG,"Sending log data");
        Pack logData = new Pack(Pack.LOG_DATA);

        logData.getData().put("PASS",password);
        logData.getData().put("NAME",name);
        logData.getData().put("ID",id);

        Pack.sendPack(logData,Connexion.output);

        Pack response = Pack.readPack(Connexion.input);

        if (response == null || response.getPerformative() == Pack.DENIED){
            Log.d(LOG_TAG,(response == null)?"No response":"Server denied login");
            return false;
        }
        else if (response.getPerformative() == Pack.ACCEPTED){
            Connexion.logged.set(true);
            Pack ack = new Pack(Pack.ACK);
            Pack.sendPack(ack,Connexion.output);
        }
        else if (response.getPerformative() == Pack.NEW_ID){
            Log.d(LOG_TAG,"New id received : "+(Integer)response.getData().get("ID"));
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(Settings.ID, (Integer) response.getData().get("ID"));
            editor.commit();

            Pack accepted = Pack.readPack(Connexion.input);

            if (accepted != null && accepted.getPerformative() == Pack.ACCEPTED){
                Connexion.logged.set(true);
                Pack ack = new Pack(Pack.ACK);
                Pack.sendPack(ack,Connexion.output);
            }else {
                Log.d(LOG_TAG,(accepted == null)?"ACCEPTED not received":"Wrong performative, ACCEPTED expected [1]");
                return false;
            }
        }else {
            Log.d(LOG_TAG,"Wrong performative, ACCEPTED expected [2]");
            return false;
        }
        return true;
    }

    public boolean adminLogin(SharedPreferences settings){
        String adminPassword = settings.getString(Settings.ADMIN_PASSWORD,Settings.DEFAULT_PASSWORD);
        Pack adminPass = new Pack(Pack.LOG_ADMIN_DATA);
        adminPass.getData().put("ADMIN_PASSWORD",adminPassword);
        Pack.sendPack(adminPass,Connexion.output);

        Pack response = Pack.readPack(Connexion.input);

        if (response == null || response.getPerformative() == Pack.DENIED){
            return false;
        }
        else if(response.getPerformative() == Pack.ACCEPTED){
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
        running.set(false);
    }


    @Override
    protected Object doInBackground(Object[] params) {

        if (running.get()){
            Log.d(LOG_TAG,"Connect:doInBackgroung is already running");
            return null;
        }
        running.set(true);

        MainMenu main = (MainMenu)params[0];
        Context context = (Context) params[1];
        SharedPreferences settings = context.getSharedPreferences(Settings.FILENAME,Settings.MODE);

        Log.d(LOG_TAG,"Connect:doInBackgroung");

        if (!checkInternetConnexion(main)){
            abortConnexion(main,MainMenu.CONNEXION_FAILED,MainMenu.NO_INTERNET);
            return null;
        }

        if (!connexionToServer(settings)){
            abortConnexion(main,MainMenu.CONNEXION_FAILED,MainMenu.FAILED_CONNECT_SERVER);
            return null;
        }

        if (!simpleLoggin(settings)){
            abortConnexion(main,MainMenu.CONNEXION_FAILED,MainMenu.WRONG_PASSWORD);
            return null;
        }
        adminLogin(settings);
        return null;
    }
}
