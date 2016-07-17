package com.example.pablo.tiki;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainMenu extends AppCompatActivity {

    private Handler connexionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);

        connexionHandler = new Handler(Looper.getMainLooper()){

            public void handleMessage(Message inputMessage) {

                if (!Connexion.connected.get() || !Connexion.logged.get()){

                    new AlertDialog.Builder(getApplicationContext())
                            .setTitle(inputMessage.getData().getString(Connexion.TITLE))
                            .setMessage(inputMessage.getData().getString(Connexion.MESSAGE))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }else {
                    new AlertDialog.Builder(getApplicationContext())
                            .setTitle("Connexion Successfull !!")
                            .setMessage("Whoouuuuuuhouuuu")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }

            }

        };
    }

    public void connexion(View v){

        Thread connexion = new Thread(){

            @Override
            public void run() {
                Connect connect = new Connect();
                connect.execute(new Object[]{this,getApplicationContext()});
            }
        };

        connexion.run();
    }



    public void settings(View v){

        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);

    }

    public Handler getConnexionHandler() {
        return connexionHandler;
    }
}
