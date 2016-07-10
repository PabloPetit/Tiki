import TikiDefs
from time import *
try:
    import RPi.GPIO as GPIO

except:
    print("Rpi.GPIO not available, end of execution")
    #sys.exit()

class TikiLed:

    def __init__(self,type,pin,name,id):
        global LED_PWM
        self.type = type
        self.pin = pin
        self.name = name
        self.id = id
        self.inUsage = False

        GPIO.setup(pin, GPIO.OUT, initial = GPIO.LOW)

        if self.type == TikiDefs.LED_PWM :
            pass

    def stdTest(self):
        for i in range(3):
            for j in range(3):
                GPIO.output(self.pin, GPIO.HIGH)
                sleep(0.05)
                GPIO.output(self.pin, GPIO.LOW)
                sleep(0.05)
            sleep(0.07)

    def toogle(self):
        GPIO.output(self.pin,  not GPIO.input(self.pin))

    def on(self):
        GPIO.output(self.pin, GPIO.HIGH)

    def off(self):
        GPIO.output(self.pin, GPIO.LOW)

    def pwmTest(self):
        pass

    def test(self):
        print("Test of led number "+(str(self.id))+" pin : "+str(self.pin)+" name : "+str(self.name))
        if self.type == TikiDefs.LED_STD :
            self.stdTest()
        elif self.type == TikiDefs.LED_PWM :
            self.pwmTest()
        print("Test finished")

