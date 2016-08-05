package com.example.pablo.tiki;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Settings extends AppCompatActivity {

    public static final String DEFAULT_NAME = "TIKI_CLIENT";
    public static final String DEFAULT_IP =   "192.168.1.38";//"88.8.83.8" "81.64.39.231";
    public static final int DEFAULT_ID = -1;
    public static final int DEFAULT_PORT = 4200;
    public static final String DEFAULT_PASSWORD = "";



    public static final String FILENAME = "SETTINGS";
    public static final int MODE = 0;
    public static final String SET = "SET";
    public static final String NAME = "NAME";
    public static final String ID = "ID";
    public static final String IP = "IP";
    public static final String PORT = "PORT";
    public static final String  PASSWORD = "PASSWORD";
    public static final String  ADMIN_PASSWORD = "ADMIN_PASSWORD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_parametres);
        setFields();
    }

    public static void initSettings(Context context){
        SharedPreferences settings = context.getSharedPreferences(FILENAME,MODE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(SET,SET);
        editor.putString(NAME,DEFAULT_NAME);
        editor.putString(IP,DEFAULT_IP);
        editor.putString(PASSWORD,DEFAULT_PASSWORD);
        editor.putString(ADMIN_PASSWORD,DEFAULT_PASSWORD);
        editor.putInt(PORT,DEFAULT_PORT);
        editor.putInt(ID,DEFAULT_ID);

        editor.commit();

    }


    public void setFields(){

        SharedPreferences settings = getSharedPreferences(FILENAME,MODE);

        if (!settings.contains("SET")){
            initSettings(getApplicationContext());
        }

        TextView name = (TextView) findViewById(R.id.editTextName);
        name.setText(settings.getString(NAME,DEFAULT_NAME));

        TextView ip = (TextView) findViewById(R.id.editTextIP);
        ip.setText(settings.getString(IP,DEFAULT_IP));

        TextView port = (TextView) findViewById(R.id.editTextPort);
        port.setText(settings.getInt(PORT,0)+"");

        TextView pass = (TextView) findViewById(R.id.editTextPassword);
        pass.setText(settings.getString(PASSWORD,DEFAULT_PASSWORD));

        TextView admin = (TextView) findViewById(R.id.editTextAdmin);
        admin.setText(settings.getString(ADMIN_PASSWORD,DEFAULT_PASSWORD));

    }

    public void submit(View v){

        TextView name = (TextView) findViewById(R.id.editTextName);
        TextView ip = (TextView) findViewById(R.id.editTextIP);
        TextView port = (TextView) findViewById(R.id.editTextPort);
        TextView pass = (TextView) findViewById(R.id.editTextPassword);
        TextView adminPass = (TextView) findViewById(R.id.editTextAdmin);

        SharedPreferences settings = v.getContext().getSharedPreferences(FILENAME,MODE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(NAME,name.getText().toString());
        editor.putString(IP,ip.getText().toString());
        editor.putString(PASSWORD,pass.getText().toString());
        editor.putString(ADMIN_PASSWORD,adminPass.getText().toString());
        editor.putInt(PORT,Integer.parseInt(port.getText().toString()));

        editor.commit();

        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);

    }
}
