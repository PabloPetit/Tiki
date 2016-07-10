package com.example.pablo.tiki;

import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    public static final String DEFAULT_NAME = "TIKI_CLIENT";
    public static final String DEFAULT_IP = "81.64.39.231";
    public static final int DEFAULT_ID = -1;
    public static final int DEFAULT_PORT = 4200;
    public static final String DEFAULT_PASSWORD = "";


    public static final String SET = "SET";
    public static final String NAME = "NAME";
    public static final String IP = "IP";
    public static final String ID = "ID";
    public static final String PORT = "PORT";
    public static final String  PASSWORD = "PASSWORD";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_parametres);
        setFields();
    }


    public void setFields(){

        SharedPreferences settings = getPreferences(0);

        if (! settings.contains("SET")){
            // Set initial fields
        }

        TextView name = (TextView) findViewById(R.id.editTextName);
        name.setText(settings.getString());

        TextView ip = (TextView) findViewById(R.id.editTextIP);
        ip.setText(settings.getIp());

        TextView port = (TextView) findViewById(R.id.editTextPort);
        port.setText(settings.getPort()+"");

        TextView pass = (TextView) findViewById(R.id.editTextPassword);
        pass.setText(settings.getPassword());

    }

    public void submit(View v){


        TextView name = (TextView) findViewById(R.id.editTextName);
        settings.setName(name.getText().toString());

        TextView ip = (TextView) findViewById(R.id.editTextIP);
        settings.setIp(ip.getText().toString());

        TextView port = (TextView) findViewById(R.id.editTextPort);

        settings.setPort(Integer.parseInt(port.getText().toString()));

        TextView pass = (TextView) findViewById(R.id.editTextPassword);
        settings.setPassword(pass.getText().toString());

        Settings.saveSettings(settings);

    }
}
