package com.example.pablo.tiki;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void connexion(View v){


    }

    public void settings(View v){

        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);

    }
}
