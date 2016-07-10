from threading import Thread
from queue import Queue
from TikiLedManager import TikiLedManager
from TikiClient import TikiClient
import socket
import select
import TikiDefs

class Server(Thread):

    def __init__(self, ip, port, maxQueue):
        Thread.__init__(self)
        self.ip = ip
        self.port = port
        self.maxQueue = maxQueue
        self.sock = None
        self.manager = None
        self.clients = []
        self.clientIdCount = 1
        self.terminated = False
        self.queue = Queue()

    def launchManager(self):
        print("Launching GPIO manager")
        self.manager = TikiLedManager()
        self.manager.start()
        print("GPIO manager running")

    def launchServer(self):
        try:
            print("Launching server ...")
            self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.sock.bind((self.ip, self.port))
            self.sock.listen(self.maxQueue)
        except socket.error:
            print("Server error")
            return False

        print("Server Online\n")
        print(("ip : "+str(self.ip)))
        print(("port : "+str(self.port)))
        return True

    def deleteClient(self, id):
        tmp = None
        for i in self.clients:
            if i.id == id:
                tmp = i
                break

        if tmp == None:
            print(("Client number "+str(id)+" has already been deleted"))

        else:
            self.clients.remove(tmp)
            print(("Client number " + str(id) + " deleted"))

    def quit(self):
        self.terminated = True
        self.closeServer()

    def closeManager(self):

        pass

    def closeServer(self):
        """
        When this function is called, the server is terminated, it will wait for all the clients
        to notice it and close their connexions, them it will shutdown the serveur socket and end the program
        """
        print("Closing server ...")
        for client in self.clients:
            print("Ending connexion with client : "+str(client.id))
            client.join()
            print("Done")

        self.sock.close()

        print("Server Offline")
        print("Closing led manager ...")
        self.closeManager()
        print("Done")


    def checkQueue(self):
    # This queue must receive tuples composed by a
    #function followed by an argument

        while not self.queue.empty() :

            try :
                tmp = self.queue.get()
                func, args = tmp[0], tmp[1]
                func(args)

            except :
                print("Queue error : bad argument format")




    def run(self):
        global TIMEOUT_SERVER

        self.launchManager()

        if not self.launchServer():
            self.manager.queue.put((self.manager.quit,None))
            self.terminated = True

        while not self.terminated:

            self.checkQueue()

            try :

                connexions = select.select([self.sock], [], [], TikiDefs.TIMEOUT_SERVER)

                if connexions[0]:

                    for connexion in connexions[0]:

                        if connexion == self.sock:
                            (client, info) = connexion.accept()  # select
                            print("A new client is connected")
                            print(("ip : "+str(info[0])+" port : "+str(info[1])))
                            tikiClient = TikiClient(client, self.clientIdCount, self)
                            self.clients.append(tikiClient)
                            self.clientIdCount += 1
                            tikiClient.start()
            except select.error:
                pass
            except socket.error:
                print("Socket error")
                break
        self.closeServer()
        print("Execution ended successfully")