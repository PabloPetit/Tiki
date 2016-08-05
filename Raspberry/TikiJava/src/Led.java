/**
 * Created by Pablo on 01/08/2016.
 */

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class Led {

    public static int TEST_TIME_A = 50;
    public static int TEST_TIME_B = 75;
    public static int TEST_NMBR = 3;

    public static final GpioController gpio = GpioFactory.getInstance();

    private int id;
    private boolean on;
    private String name;
    private GpioPinDigitalOutput pin;


    public Led(int id,String name,Pin pin){
        this.id = id;
        this.name = name;
        this.on = false;
        this.pin = gpio.provisionDigitalOutputPin(pin, name, PinState.HIGH);
        pin.setShutdownOptions(true, PinState.LOW);
    }

    public void on(){
        pin.on();
    }

    public void off(){
        pin.off();
    }

    public void toggle(){
        pin.toggle();
    }

    public void pulse(int millis){
        pin.pulse(millis, true);
    }

    public void test(){
        try {
            for (int i = 0; i < TEST_NMBR; i++) {
                for (int j = 0; j < TEST_NMBR; j++) {
                    on();
                    Thread.sleep(TEST_TIME_A);
                    off();
                }
                Thread.sleep(TEST_TIME_B);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void shutdown(){
        gpio.shutdown();
    }



}
