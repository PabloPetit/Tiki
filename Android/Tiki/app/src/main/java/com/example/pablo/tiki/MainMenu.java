package com.example.pablo.tiki;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
        Connect connect = new Connect();
        connect.execute(new Object[]{this,getApplicationContext()});
    }

    public void popConnexionFailedDialog() {

        hideLoadingAnimtion();
        String error = "";

        if(!Connexion.connected) error = "Try to change the settings and retry";
        if(Connexion.connected && Connexion.logged) error = "Wrong password";

        new AlertDialog.Builder(getApplicationContext())
                .setTitle("Connexion Failed")
                .setMessage(error)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    public void connexionSuccesfull(){

        hideLoadingAnimtion();

        new AlertDialog.Builder(getApplicationContext())
                .setTitle("Connexion Successfull !!")
                .setMessage("Whoouuuuuuhouuuu")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }


    public void showLoadingAnimtion() {
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
    }

    public void hideLoadingAnimtion() {
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
    }

    public void settings(View v){

        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);

    }
}
