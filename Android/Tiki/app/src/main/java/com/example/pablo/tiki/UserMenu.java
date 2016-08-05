package com.example.pablo.tiki;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class UserMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);

        findViewById(R.id.adminZone).setVisibility((Connexion.admin_logged.get())?View.VISIBLE:View.INVISIBLE);

    }
    
    public void toggle(View v){

    }

    public void playSequence(View v){

    }

    public void sound(View v){

    }

    public void quit(View v){
        Pack.sendPack(new Pack(Pack.QUIT),Connexion.output);
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }

    public void lockServer(View v){

        String title = "Locking Server";
        String message = "";

        if (! Connexion.server_locked.get()) {
            Pack.sendPack(new Pack(Pack.BLOCK_ACCESS), Connexion.output);
            Pack response = Pack.readPack(Connexion.input);
            if (response != null) {
                if (response.getPerformative() == Pack.ACCEPTED) {
                    message = "The server is now locked";
                    Connexion.server_locked.set(true);
                } else if (response.getPerformative() == Pack.DENIED) {
                    message = "Lock denied";
                } else {
                    message = "Wrong performative received";
                }
            } else {
                connexionLost();
                return;
            }

        }else {
            message = "The server is already locked";
        }
        showPopUp(title,message);

    }

    public void shutdown(View v){
        Pack.sendPack(new Pack(Pack.SHUTDOWN),Connexion.output);
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);

    }


    public void showPopUp(String title, String message){
        new AlertDialog.Builder(UserMenu.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    public void connexionLost(){
        Connect.closeConnexion();
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }



}
