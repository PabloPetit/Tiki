package com.example.pablo.tiki;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

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

}

class Connect extends AsyncTask{


    @Override
    protected Object doInBackground(Object[] params) {

        MainMenu main = (MainMenu)params[0];
        Context context = (Context) params[1];
        SharedPreferences settings = context.getSharedPreferences(Settings.FILENAME,Settings.MODE);

        main.showLoadingAnimtion();

        //CONNEXION

        try {

            String ip = settings.getString(Settings.IP,Settings.DEFAULT_IP);
            int port = settings.getInt(Settings.PORT,Settings.DEFAULT_PORT);

            Log.d("IP",ip);
            Log.d("IP",port+"");

            Connexion.socket = new Socket(ip, port);

            Connexion.output = new ObjectOutputStream(Connexion.socket.getOutputStream());
            Connexion.input = new ObjectInputStream(Connexion.socket.getInputStream());

            Connexion.connected = true;

            // LOGIN

            String password = settings.getString(Settings.PASSWORD,Settings.DEFAULT_PASSWORD);
            String name = settings.getString(Settings.NAME,Settings.DEFAULT_NAME);
            int id = settings.getInt(Settings.ID,Settings.DEFAULT_ID);


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
                main.popConnexionFailedDialog();
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
                Connexion.logged = true;
                Proto ack = new Proto(Proto.ACK);
                Connexion.output.writeObject(ack);
                Connexion.output.flush();

            }else {
                main.popConnexionFailedDialog();
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            main.popConnexionFailedDialog();
            return false;
        }

        main.connexionSuccesfull();

        return true;

    }
}
