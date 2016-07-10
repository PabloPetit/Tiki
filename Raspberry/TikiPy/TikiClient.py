from threading import Thread
from TikiLedSequence import *
import socket
import select
import TikiDefs

class TikiClient(Thread):

    def __init__(self, sock, id, server):
        Thread.__init__(self)
        self.sock = sock
        self.server = server
        self.manager = server.manager
        self.id = id
        self.terminated = False
        self.initPerfs()

    def quit(self, packet):
        self.closeConnexion()

    def shutdown(self,packet):
        self.server.queue.put((self.server.quit, None))
        self.closeConnexion()

    def tikiSequence(self, packet):
        self.manager.queueSequence.put(TikiSequenceFromPacket(packet, self.manager))
        print("Client number "+str(self.id)+" received TKSQ message")

    def stat(self, packet):
        print(("Client number "+str(self.id)+" received STAT message"))

    def direct(self, packet):
        try:
            ledId = int(packet[4:])
            self.manager.queue.put((self.manager.direct, ledId))
        except:
            print("Client number : "+str(self.id)+" DRCT : wrong packet format")

    def test(self, packet):
        self.manager.queue.put((self.manager.testLeds, None))
        print("Client number "+str(self.id)+" received TEST message")

    def number(self,packet):
        print("Client number "+str(self.id)+" received NMBR message")

        try:
            num = int(packet[4:])
            self.manager.queue.put((self.manager.number, num))
        except:
            print("Client number : "+str(self.id)+" NMBR : wrong packet format")


    def initPerfs(self):
        global PERFS
        self.perfs = {}

        self.perfs["QUIT"] = self.quit
        self.perfs["SHUT"] = self.shutdown
        self.perfs["TKSQ"] = self.tikiSequence
        self.perfs["STAT"] = self.stat
        self.perfs["DRCT"] = self.direct
        self.perfs["TEST"] = self.test
        self.perfs["NMBR"] = self.number

    def closeConnexion(self):
        self.sock.close()
        self.terminated = True
        print(("Connexion with client " + str(self.id) + " ended"))
        self.server.queue.put((self.server.deleteClient,self.id))

    def run(self):

        self.sock.send(bytes("You are now connected on  a Tiki server as client number "+str(self.id)+"\n",'utf-8'))

        while not self.terminated :

            if self.server.terminated :
                self.terminated = True
                continue

            try:
                ready = select.select([self.sock],[], [], TikiDefs.TIMEOUT_CLIENT)

                if ready[0] :

                    packet = self.sock.recv(TikiDefs.PACKET_SIZE)
                    packet = packet.decode("utf8")
                    performative = packet[0:4]
                    self.perfs[performative](packet) #if this works it's awesome !

            except KeyError :
                print("The performative send by the client is incorrect\nReceived : "+performative+"\n")

            except socket.error:
                self.closeConnexion()

        self.closeConnexion()











