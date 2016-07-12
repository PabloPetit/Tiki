package com.example.pablo.tiki;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
    }

    public void connexion(View v){

        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);


        Thread t = new Thread(){
            public void run(){

                Connexion.connect(getApplicationContext());
                if (!Connexion.connected)return;
                Connexion.loggin(getApplicationContext());

            }

        };

        t.run();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);

        if (Connexion.connected && Connexion.logged){

            Intent intent = new Intent(this, MainMenu.class);
            startActivity(intent);

        }else {

            String error = "";

            if(!Connexion.connected) error = "Try to change the settings and retry";
            if(Connexion.connected && Connexion.logged) error = "Wrong password";

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Connexion Failed")
                    .setMessage(error)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
    }

    public void settings(View v){

        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);

    }
}
