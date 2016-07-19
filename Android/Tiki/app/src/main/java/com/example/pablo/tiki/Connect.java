package com.example.pablo.tiki;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connect extends AsyncTask {


    public boolean checkInternetConnexion(MainMenu main){
        ConnectivityManager connMgr = (ConnectivityManager)main.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean connexionToServer(SharedPreferences settings){
        String ip = settings.getString(Settings.IP,Settings.DEFAULT_IP);
        int port = settings.getInt(Settings.PORT,Settings.DEFAULT_PORT);

        try{
            Connexion.socket = new Socket(ip, port);
            Connexion.output = new ObjectOutputStream(Connexion.socket.getOutputStream());
            Connexion.input = new ObjectInputStream(Connexion.socket.getInputStream());
            Connexion.connected.set(true);
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean simpleLoggin(SharedPreferences settings){

        String password = settings.getString(Settings.PASSWORD,Settings.DEFAULT_PASSWORD);
        String name = settings.getString(Settings.NAME,Settings.DEFAULT_NAME);
        int id = settings.getInt(Settings.ID,Settings.DEFAULT_ID);

        try {

            Proto server_name = (Proto) Connexion.input.readObject();
            Connexion.serverName = (String) server_name.getData().get("NAME");

            Proto logData = new Proto(Proto.LOG_DATA);

            logData.getData().put("PASS",password);
            logData.getData().put("NAME",name);
            logData.getData().put("ID",id);

            Connexion.output.writeObject(logData);
            Connexion.output.flush();

            Proto response = (Proto) Connexion.input.readObject();

            if (response.getPerformative() == Proto.DENIED){
                return false;
            }

            else if (response.getPerformative() == Proto.NEW_ID){
                // Set and save the new id
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(Settings.ID, (Integer) response.getData().get("ID"));
                editor.commit();
            }

            Proto accepted = (Proto) Connexion.input.readObject();

            if (accepted.getPerformative() == Proto.ACCEPTED){
                Connexion.logged.set(true);
                Proto ack = new Proto(Proto.ACK);
                Connexion.output.writeObject(ack);
                Connexion.output.flush();

            }else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean adminLogin(SharedPreferences settings){
        String adminPassword = settings.getString(Settings.ADMIN_PASSWORD,Settings.DEFAULT_PASSWORD);

        try{
            Proto adminPass = new Proto(Proto.LOG_ADMIN_DATA);
            adminPass.getData().put("ADMIN_PASSWORD",adminPassword);
            Connexion.output.writeObject(adminPass);
            Connexion.output.flush();

            Proto response = (Proto) Connexion.input.readObject();

            if (response.getPerformative() == Proto.DENIED){
                return false;
            }
            else if(response.getPerformative() == Proto.ACCEPTED){
                Connexion.admin_logged.set(true);
                return true;
            }


        }catch (Exception e) {
            return false;
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
                Connexion.socket.close();
                Connexion.socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        return null;
    }
}
