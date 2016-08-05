package com.example.pablo.tiki;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class Toggle extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toggle);


        final Button left = (Button) findViewById(R.id.buttonLeftEye);
        left.setOnClickListener(new ToggleListener(0));
        final Button right = (Button) findViewById(R.id.buttonRightEye);
        right.setOnClickListener(new ToggleListener(1));

        final Button bit0 = (Button) findViewById(R.id.buttonBit0);
        bit0.setOnClickListener(new ToggleListener(2));

        final Button bit1 = (Button) findViewById(R.id.buttonBit1);
        bit1.setOnClickListener(new ToggleListener(3));

        final Button bit2 = (Button) findViewById(R.id.buttonBit2);
        bit2.setOnClickListener(new ToggleListener(3));

        final Button bit3 = (Button) findViewById(R.id.buttonBit3);
        bit3.setOnClickListener(new ToggleListener(3));

        final Button bit4 = (Button) findViewById(R.id.buttonBit4);
        bit4.setOnClickListener(new ToggleListener(4));

        final Button bit5 = (Button) findViewById(R.id.buttonBit5);
        bit5.setOnClickListener(new ToggleListener(5));

        final Button bit6 = (Button) findViewById(R.id.buttonBit6);
        bit6.setOnClickListener(new ToggleListener(6));

        final Button bit7 = (Button) findViewById(R.id.buttonBit7);
        bit7.setOnClickListener(new ToggleListener(7));

    }


    class ToggleListener implements View.OnClickListener {

        private int id;

        public ToggleListener(int id){
            this.id = id;
        }

        @Override
        public void onClick(View v) {
            Pack p = new Pack(Pack.TOGGLE);
            p.getData().put(Pack.ID,id);
            Pack.sendPack(p,Connexion.output);
        }
    }
}
