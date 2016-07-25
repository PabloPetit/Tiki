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

    public static final String CONNEXION_FAILED = "Connexion failed";
    public static final String NO_INTERNET = "No internet connexion";
    public static final String WRONG_PASSWORD = "Wrong password, change the settings and try again";
    public static final String FAILED_CONNECT_SERVER = "Failed to connect to the server";
    public static final String ERROR = "An error as occured";

    private Handler connexionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);

        connexionHandler = new Handler(Looper.getMainLooper()){

            public void handleMessage(Message inputMessage) {

                if (!Connexion.connected.get() || !Connexion.logged.get()){
                    showPopUp(inputMessage.getData().getString(Connexion.TITLE),
                            inputMessage.getData().getString(Connexion.MESSAGE));
                }else {
                    showPopUp(inputMessage.getData().getString("All ok"),
                            inputMessage.getData().getString("All right"));
                }
            }
        };
    }

    public void showPopUp(String title, String message){
        new AlertDialog.Builder(MainMenu.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    public void connexion(View v){

        Connect connect = new Connect();
        connect.execute(new Object[]{this,getApplicationContext()});

    }

    @Override
    protected void onStop() {
        // TODO: Connect.closeConnexion(); ?
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Connect.closeConnexion();
        super.onDestroy();
    }

    public void settings(View v){

        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);

    }

    public Handler getConnexionHandler() {
        return connexionHandler;
    }
}
