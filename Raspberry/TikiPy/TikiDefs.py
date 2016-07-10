from TikiLed import TikiLed
try:
    import RPi.GPIO as GPIO

except:
    print("Rpi.GPIO not available, end of execution")
    #sys.exit()

# This list contains all the performatives used in this protocol
# When a new client is client created, it will associate each one of the performatives
# with a self.fonction

PERFS = ["QUIT","SHUT","TKSQ","STAT","DRCT","EDDR","TEST","NMBR"]

# QUIT : Close connexion with client
# SHUT : Shutdown server
# TKSQ : Tiki Sequence
# STAT : Stats of the server
# DRCT : Direct order for led usage, first priority
# EDDR : End direct transmission
# TEST : Test all the leds
# NMBR : Number : print a number on the leds

LEDS = {}


LED_STD = 1
LED_PWM = 2

TIMEOUT_CLIENT = 2
TIMEOUT_SERVER = 0.1

PACKET_SIZE = 512
PERFS_SIZE = 4

SEQUENCE_SIZE = PACKET_SIZE - PERFS_SIZE - 2 #Id plus lenght

GPIO.setmode(GPIO.BOARD)

left = TikiLed(LED_STD, 23, "LEFT_EYE", 1)
right = TikiLed(LED_STD, 24, "RIGHT_EYE", 2)
#pwm = TikiLed(LED_PWM, 18, "PWM", 3)

bit0 = TikiLed(LED_STD, 7, "BIT_0", 4)
bit1 = TikiLed(LED_STD, 11, "BIT_1", 5)
bit2 = TikiLed(LED_STD, 12, "BIT_2", 6)
bit3 = TikiLed(LED_STD, 13, "BIT_3", 7)

bit4 = TikiLed(LED_STD, 15, "BIT_4", 8)
bit5 = TikiLed(LED_STD, 16, "BIT_5", 9)
bit6 = TikiLed(LED_STD, 18, "BIT_6", 10)
bit7 = TikiLed(LED_STD, 22, "BIT_7", 11)

LEDS[left.id] = left
LEDS[right.id] = right


LEDS[bit0.id] = bit0
LEDS[bit1.id] = bit1
LEDS[bit2.id] = bit2
LEDS[bit3.id] = bit3
LEDS[bit4.id] = bit4
LEDS[bit5.id] = bit5
LEDS[bit6.id] = bit6
LEDS[bit7.id] = bit7


