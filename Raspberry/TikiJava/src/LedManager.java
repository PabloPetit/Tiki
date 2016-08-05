import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import com.pi4j.io.gpio.RaspiPin;

/**
 * Created by Pablo on 19/07/2016.
 */
public class LedManager{


    private Led LEFT_EYE;
    private Led RIGHT_EYE;

    private Led BIT_0;
    private Led BIT_1;
    private Led BIT_2;
    private Led BIT_3;
    private Led BIT_4;
    private Led BIT_5;
    private Led BIT_6;
    private Led BIT_7;

    private ArrayList<Led> allLeds;
    private ArrayList<Led> bits;
    private AtomicBoolean playing;

    public LedManager(){
        this.playing = new AtomicBoolean(false);
    }

    public boolean toggleLed(int id, boolean urgent){

        if (playing.get() && !urgent) return false;

        Led l = allLeds.get(id);
        if (l == null){
            return false;
        }
        l.toggle();
        return true;
    }

    public boolean onLed(int id, boolean urgent){

        if (playing.get() && !urgent) return false;

        Led l = allLeds.get(id);
        if (l == null){
            return false;
        }
        l.on();
        return true;
    }

    public boolean offLed(int id, boolean urgent){

        if (playing.get() && !urgent) return false;

        Led l = allLeds.get(id);
        if (l == null){
            return false;
        }
        l.off();
        return true;
    }

    public boolean pulseLed(int id, int millis, boolean urgent){

        if (playing.get() && !urgent) return false;

        Led l = allLeds.get(id);
        if (l == null){
            return false;
        }
        l.pulse(millis);
        return true;
    }

    public boolean writeNumber(int number){

        if (number < 0 || number > 255){
            return false;
        }

        String tmp = Integer.toBinaryString(number);
        for (int i = 0; i < 8;i++){
            if (i<tmp.length() && tmp.charAt(i)=='1'){
                bits.get(i).on();
            }else {
                bits.get(i).off();
            }
        }
        return true;
    }
    
    
    public boolean initLeds(){
        int id = 0;
        LEFT_EYE = new Led(id++,"LEFT_EYE",RaspiPin.GPIO_14);
        RIGHT_EYE = new Led(id++,"RIGHT_EYE",RaspiPin.GPIO_10);
        BIT_0 = new Led(id++,"BIT_0",RaspiPin.GPIO_7);
        BIT_1 = new Led(id++,"BIT_1",RaspiPin.GPIO_0);
        BIT_2 = new Led(id++,"BIT_2",RaspiPin.GPIO_1);
        BIT_3 = new Led(id++,"BIT_3",RaspiPin.GPIO_2);
        BIT_4 = new Led(id++,"BIT_4",RaspiPin.GPIO_3);
        BIT_5 = new Led(id++,"BIT_5",RaspiPin.GPIO_4);
        BIT_6 = new Led(id++,"BIT_6",RaspiPin.GPIO_5);
        BIT_7 = new Led(id++,"BIT_7",RaspiPin.GPIO_6);

        allLeds = new ArrayList<>();
        bits = new ArrayList<>();

        allLeds.add(LEFT_EYE);
        allLeds.add(RIGHT_EYE);
        allLeds.add(BIT_0);
        allLeds.add(BIT_1);
        allLeds.add(BIT_2);
        allLeds.add(BIT_3);
        allLeds.add(BIT_4);
        allLeds.add(BIT_5);
        allLeds.add(BIT_6);
        allLeds.add(BIT_7);

        bits.add(BIT_0);
        bits.add(BIT_1);
        bits.add(BIT_2);
        bits.add(BIT_3);
        bits.add(BIT_4);
        bits.add(BIT_5);
        bits.add(BIT_6);
        bits.add(BIT_7);

        return true; // TODO : check if everything is ok
    }

    public void close(){
        Led.shutdown();
    }



}
