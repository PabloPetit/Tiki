from threading import Thread
from queue import Queue
import TikiDefs
from time import *

try:
    import RPi.GPIO as GPIO

except:
    print("Rpi.GPIO not available, end of execution")
    #sys.exit()

class TikiLedManager(Thread):

    def __init__(self):
        Thread.__init__(self)
        self.currentSequence = None
        self.LEDS = {}
        self.bits = []
        self.gpioInit()
        self.queue = Queue()
        self.directOn = False
        self.terminated = False



    def gpioInit(self):
        global LEDS
        self.LEDS = TikiDefs.LEDS
        self.initBits()
        self.testLeds(None)

    def initBits(self):
        self.bits.append(self.LEDS[4])
        self.bits.append(self.LEDS[5])
        self.bits.append(self.LEDS[6])
        self.bits.append(self.LEDS[7])
        self.bits.append(self.LEDS[8])
        self.bits.append(self.LEDS[9])
        self.bits.append(self.LEDS[10])
        self.bits.append(self.LEDS[11])

    def testLeds(self,arg):
        for led in self.LEDS.values():
            led.test()


    def gpioCleanup(self):
        GPIO.cleanup()

    def queueEmpty(self):
        sleep(1)

    def quit(self,arg):
        self.gpioCleanup()
        self.terminated = True

    def tikiSequence(self,sequence):
        if not self.direct and self.currentSequence == None :
            self.currentSequence = sequence
            sequence.start()
        else :
            self.queue.put((self.tikiSequence,sequence))

    def direct(self,id):

        led = None
        for tmp in self.LEDS.keys():
                if tmp == id :
                    led = self.LEDS[tmp]
                    break

        if led == None :
                print("Incorrect led id value")
                return

        led.toogle()

    def number(self,num):

        if num > 255:
            print(("Value received too high : "+str(num)))
            return
        elif num < 0:
            print(("Value received too low : "+str(num)))
            return

        tab = [int(digit) for digit in bin(num)[2:]]

        for i in range(8-len(tab)):
            tab.insert(0,0)

        for i in self.bits:
            i.off()

        for i in range(7,-1,-1):
            if tab[i]==1:
                self.bits[i].on()



    def run(self):

        while not self.terminated :

            if self.queue.empty() :
                self.queueEmpty()

            tmp = self.queue.get() #Tmp should be a tuple (func,arg)

            func, arg = tmp[0],tmp[1]

            func(arg)










