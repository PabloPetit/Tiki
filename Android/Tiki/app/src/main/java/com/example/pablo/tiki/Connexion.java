package com.example.pablo.tiki;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Pablo on 10/07/2016.
 */
public class Connexion {

    public static Socket socket = null;
    public static ObjectInputStream input;
    public static ObjectOutputStream output;
    public static boolean connected = false;
    public static boolean logged = false;


    public static String serverName = "NOT_SET";

    public static boolean connect(Context context){

        SharedPreferences settings = context.getSharedPreferences(Settings.FILENAME,Settings.MODE);

        try {
            socket = new Socket(settings.getString(Settings.IP,Settings.DEFAULT_IP),
                    settings.getInt(Settings.PORT,Settings.DEFAULT_PORT));

            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            connected = true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    public static boolean loggin(Context context){

        SharedPreferences settings = context.getSharedPreferences(Settings.FILENAME,Settings.MODE);
        String password = settings.getString(Settings.PASSWORD,Settings.DEFAULT_PASSWORD);
        String name = settings.getString(Settings.NAME,Settings.DEFAULT_NAME);
        int id = settings.getInt(Settings.ID,Settings.DEFAULT_ID);

        try {

            Proto server_name = (Proto) input.readObject();
            serverName = (String) server_name.getData().get("NAME");

            Proto logData = new Proto(Proto.LOG_DATA);

            logData.getData().put("PASS",password);
            logData.getData().put("NAME",name);
            logData.getData().put("ID",id);

            output.writeObject(logData);

            Proto response = (Proto) input.readObject();

            if (response.getPerformative() == Proto.DENIED){
                return false;
            }

            else if (response.getPerformative() == Proto.NEW_ID){
                // Set and save the new id
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(Settings.ID, (Integer) response.getData().get("ID"));
                editor.commit();
            }

            logged = true;

            //Verify if ACCEPTED ?

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void closeConnexion(){

    }


}
