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
        leds.add(new Led(5,(ImageView) findViewById(R.id.buttonBit4)));
        leds.add(new Led(6,(ImageView) findViewById(R.id.buttonBit5)));
        leds.add(new Led(7,(ImageView) findViewById(R.id.buttonBit6)));
        leds.add(new Led(8,(ImageView) findViewById(R.id.buttonBit7)));


        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.toogleLayout);

        ToggleListener listener = new ToggleListener();
        layout.setOnDragListener(listener);
        layout.setOnTouchListener(listener);
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
            image.getHitRect(rect);


            int[] posXY = new int[2];
            image.getLocationOnScreen(posXY);
            int x = posXY[0];
            int y = posXY[1];

            rect.set(x,y,x+image.getWidth(),y+image.getHeight());

            Log.d(Connect.LOG_TAG,"Constructor : "+rect.toShortString());


        }


        public void drag(boolean isOnMe){
            if(Toggle.lock)return;
            if (isOnMe && !on){
                on();
            }
            else if(!isOnMe && on){
                off();
            }

        }

        public void touch(boolean isOnMe, boolean down){
            if(!isOnMe)return;

            if(down && Toggle.lock){
                toggle();
            }
            else if(!down && on){
                off();
            }

        }

        public void on(){
            this.image.setVisibility(View.VISIBLE);
        }

        public void off(){
            this.image.setVisibility(View.INVISIBLE);
        }

        public void toggle(){

        }



    }

    class ToggleListener implements View.OnDragListener, View.OnTouchListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            for(Led l : Toggle.leds){
                l.drag(l.rect.contains((int)event.getX(),(int)event.getY()));
            }
            return true;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                for(Led l : Toggle.leds){
                    l.touch(l.rect.contains((int)event.getX(),(int)event.getY()),true);
                }
            } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                for(Led l : Toggle.leds){
                    l.touch(l.rect.contains((int)event.getX(),(int)event.getY()),false);
                }
            }
            return true;
        }
    }

    
}
