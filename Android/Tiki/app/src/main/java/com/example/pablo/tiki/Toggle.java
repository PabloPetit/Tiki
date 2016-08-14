package com.example.pablo.tiki;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class Toggle extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toggle);


        final Button left = (Button) findViewById(R.id.buttonLeftEye);
        left.setOnDragListener(new ToggleListener(0));
        final Button right = (Button) findViewById(R.id.buttonRightEye);
        right.setOnDragListener(new ToggleListener(1));

        final Button bit0 = (Button) findViewById(R.id.buttonBit0);
        bit0.setOnDragListener(new ToggleListener(2));

        final Button bit1 = (Button) findViewById(R.id.buttonBit1);
        bit1.setOnDragListener(new ToggleListener(3));

        final Button bit2 = (Button) findViewById(R.id.buttonBit2);
        bit2.setOnDragListener(new ToggleListener(4));

        final Button bit3 = (Button) findViewById(R.id.buttonBit3);
        bit3.setOnDragListener(new ToggleListener(5));

        final Button bit4 = (Button) findViewById(R.id.buttonBit4);
        bit4.setOnDragListener(new ToggleListener(6));

        final Button bit5 = (Button) findViewById(R.id.buttonBit5);
        bit5.setOnDragListener(new ToggleListener(7));

        final Button bit6 = (Button) findViewById(R.id.buttonBit6);
        bit6.setOnDragListener(new ToggleListener(8));

        final Button bit7 = (Button) findViewById(R.id.buttonBit7);
        bit7.setOnDragListener(new ToggleListener(9));

    }

/*
    class ToggleListener2 implements View.OnDragListener {

        private int id;

        public ToggleListener2(int id){
            this.id = id;
        }

        public boolean onDrag(View v, MotionEvent event) {
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

    */
    class ToggleListener implements View.OnDragListener{

        private int id;
        private Rect r;
        private boolean on;
        private boolean off;

        public ToggleListener(int id){
            this.id = id;
            this.on = false;
            this.off = false;
            r = new Rect();
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            v.getHitRect(r);
            if (r.contains((int) event.getX(), (int) event.getY()) && !on) {
                Pack p = new Pack(Pack.ON);
                p.getData().put(Pack.ID,id);
                Pack.sendPack(p,Connexion.output);
                on = true;
                off = false;
                return true;
            }else if(!r.contains((int) event.getX(), (int) event.getY()) && !off){
                Pack p = new Pack(Pack.OFF);
                p.getData().put(Pack.ID,id);
                Pack.sendPack(p,Connexion.output);
                on = false;
                off = true;
                return true;
            }

            return false;
        }
    }
}
