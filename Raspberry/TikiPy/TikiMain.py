from TikiServer import Server
import sys
import subprocess

# Script :

opt = sys.argv

#p = subprocess.Popen(['curl','ipecho.net/plain'],stdout=subprocess.PIPE,stderr=subprocess.PIPE)
#ip = p.communicate()[0].decode()

ip = "192.168.1.113"
port = 4200
password = "password"
maxQueue = 5



try :
    for i in range(len(opt)):
            if opt[i] in ["-ip"] :
                ip = opt[i+1]

            elif opt[i] in ["-p"] :
                port = opt[i+1]

except :
    print("Arguments incorrects")


print("ip : "+str(ip))
print("port : "+str(port))

tikiServer = Server(ip,port,maxQueue)

tikiServer.start()


#Idea : a welcoming sound when a new connection on the local network is detected with arp
#Tiki could speak in a foreign language

