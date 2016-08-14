package com.example.pablo.tiki;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class Toggle extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toggle);


        final Button left = (Button) findViewById(R.id.buttonLeftEye);
        left.setOnTouchListener(new ToggleListener(0));
        final Button right = (Button) findViewById(R.id.buttonRightEye);
        right.setOnTouchListener(new ToggleListener(1));

        final Button bit0 = (Button) findViewById(R.id.buttonBit0);
        bit0.setOnTouchListener(new ToggleListener(2));

        final Button bit1 = (Button) findViewById(R.id.buttonBit1);
        bit1.setOnTouchListener(new ToggleListener(3));

        final Button bit2 = (Button) findViewById(R.id.buttonBit2);
        bit2.setOnTouchListener(new ToggleListener(4));

        final Button bit3 = (Button) findViewById(R.id.buttonBit3);
        bit3.setOnTouchListener(new ToggleListener(5));

        final Button bit4 = (Button) findViewById(R.id.buttonBit4);
        bit4.setOnTouchListener(new ToggleListener(6));

        final Button bit5 = (Button) findViewById(R.id.buttonBit5);
        bit5.setOnTouchListener(new ToggleListener(7));

        final Button bit6 = (Button) findViewById(R.id.buttonBit6);
        bit6.setOnTouchListener(new ToggleListener(8));

        final Button bit7 = (Button) findViewById(R.id.buttonBit7);
        bit7.setOnTouchListener(new ToggleListener(9));

    }


    class ToggleListener implements View.OnTouchListener {

        private int id;

        public ToggleListener(int id){
            this.id = id;
        }

        public boolean onTouch(View v, MotionEvent event) {
            Pack p;
            Log.d(Connect.LOG_TAG,"Event : "+event.getAction());
            if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
                p = new Pack(Pack.ON);
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                p = new Pack(Pack.OFF);
            }else {
                return false;
            }
            p.getData().put(Pack.ID,id);
            Pack.sendPack(p,Connexion.output);
            return true;
        }
    }
}
