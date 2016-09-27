package com.example.pablo.tiki;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Toggle extends AppCompatActivity {


    public static ArrayList<Led> leds;
    public static boolean lock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toggle);

        lock = false;

        leds = new ArrayList<>();
        leds.add(new Led(0,(ImageView) findViewById(R.id.buttonLeftEye)));
        leds.add(new Led(1,(ImageView) findViewById(R.id.buttonRightEye)));
        leds.add(new Led(2,(ImageView) findViewById(R.id.buttonBit0)));
        leds.add(new Led(3,(ImageView) findViewById(R.id.buttonBit1)));
        leds.add(new Led(4,(ImageView) findViewById(R.id.buttonBit2)));
        leds.add(new Led(5,(ImageView) findViewById(R.id.buttonBit3)));
        leds.add(new Led(6,(ImageView) findViewById(R.id.buttonBit4)));
        leds.add(new Led(7,(ImageView) findViewById(R.id.buttonBit5)));
        leds.add(new Led(8,(ImageView) findViewById(R.id.buttonBit6)));
        leds.add(new Led(9,(ImageView) findViewById(R.id.buttonBit7)));


        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.toogleLayout);

        ToggleListener listener = new ToggleListener();
        layout.setOnTouchListener(listener);
        //layout.setOnDragListener(listener);
    }


    class Led{

        private boolean on;
        private int id;
        private ImageView image;
        private Rect rect;

        public Led(int id, ImageView image) {
            this.id = id;
            this.image = image;
            this.rect = new Rect();
            this.on = false;
            this.image.setVisibility(View.INVISIBLE);
        }


        public void drag(boolean isOnMe){
            if (isOnMe){
                on();
            }
            else{
                off();
            }
        }

        public void touch(boolean isOnMe, boolean down){

            /*
            if(!isOnMe)return;
            Log.d(Connect.LOG_TAG,"WORKING !! TOUCH "+id);

            if(down){
                on();
            }
            else if(!down){
                off();
            }
            */
        }

        public void on(){
            if(on)return;
            this.image.setVisibility(View.VISIBLE);
            Pack p = new Pack(Pack.ON);
            p.getData().put(Pack.ID,id);
            Pack.sendPack(p,Connexion.output);
            on = true;
        }

        public void off(){
            if(!on)return;
            this.image.setVisibility(View.INVISIBLE);
            Pack p = new Pack(Pack.OFF);
            p.getData().put(Pack.ID,id);
            Pack.sendPack(p,Connexion.output);
            on = false;
        }

        public void toggle(){
            Pack p = new Pack(Pack.TOGGLE);
            p.getData().put(Pack.ID,id);
            Pack.sendPack(p,Connexion.output);
            on = !on;
        }



    }

    class ToggleListener implements View.OnDragListener, View.OnTouchListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {

            Log.d(Connect.LOG_TAG,"We are draging !");
            for(Led l : Toggle.leds){
                //l.drag(l.rect.contains((int)event.getX(),(int)event.getY()));
            }
            return true;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                for(Led l : Toggle.leds){
                    l.image.getHitRect(l.rect);
                    l.drag(l.rect.contains((int)event.getX(),(int)event.getY()));
                }
            } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                for(Led l : Toggle.leds){
                    l.image.getHitRect(l.rect);
                    l.drag(false);
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE){
                for(Led l : Toggle.leds){
                    l.image.getHitRect(l.rect);
                    l.drag(l.rect.contains((int)event.getX(),(int)event.getY()));
                }
            }


            return true;
        }
    }

    
}
