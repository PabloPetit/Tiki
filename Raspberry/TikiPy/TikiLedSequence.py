from threading import Thread
import TikiDefs
from time import *
try:
    import RPi.GPIO as GPIO

except:
    print("Rpi.GPIO not available, end of execution")
    #sys.exit()

class TikiLedSequence(Thread):  # This class will managed only one led

    def __init__(self, led, sequence,manager):
        Thread.__init__(self)
        self.led = led
        self.type = led.type
        self.sequence = sequence  # in milliseconds
        self.manager = manager
        self.it = 0

    def str(self):  # LED_ID : LENGHT : (MILLIS : )*
        global LED_STD
        global LED_PWM
        global SEQUENCE_SIZE

        st = str(self.led.id) + ':'

        if self.type == TikiDefs.LED_STD:
            st += str(len(self.sequence))

            for i in self.sequence:
                st += ':' + str(int(i))

            if len(self.sequence) == 0:
                st += ':'

        elif self.type == TikiDefs.LED_PWM :
            st += "Not implemented yet"

        for i in range(len(st), TikiDefs.SEQUENCE_SIZE):
                st += '@'

        return st

    def getBytes(self):

        return bytes(self.str(),'utf8')

    def runSTD(self):
        self.led.inUsage = True

        state = False

        for t in self.sequence :

            if state:
                GPIO.output(self.led.pin,GPIO.LOW)
            else :
                GPIO.output(self.led.pin,GPIO.HIGH)
            state = not state

            sleep(t/1000)

        GPIO.output(self.led.pin,GPIO.LOW)
        self.led.inUsage = False

    def runPWM(self):
        pass

    def run(self):
        global LED_STD
        global LED_PWM

        if self.led.type == TikiDefs.LED_STD:
            self.runSTD()
        elif self.led.type == TikiDefs.LED_PWM:
            self.runPWM()

        self.manager.currentSequence = None


def TikiSequenceFromPacket(packet,manager):
    global LED_STD
    global LED_PWM

    tab = packet.split(':')

    try :
        ledId = tab[0]
        lenght = tab[1]
        led = None
        sequence = []

        for tmp in manager.LEDS.keys() :
                if tmp.id == ledId :
                    led = manager.LEDS[tmp]
                    break

        if led == None :
                print("Incorrect led id value")
                return None

        if led.type == TikiDefs.LED_STD :

            for i in range(3,lenght):
                sequence.append(int(packet[i]))

            if len(sequence) == 0 :
                print("The packet contains an empty sequence")

            return TikiLedSequence(led,sequence,manager)

        elif led.type == TikiDefs.LED_PWM :
            pass

    except :
        print("Unreadable packet : \n"+packet+"\n")

    return None


