

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Pablo on 10/07/2016.
 */
public class Server extends Thread{

    public static final String DEFAULT_IP = "192.168.1.113";
    public static final int DEFAULT_PORT = 4200;
    public static final int QUEUE_SIZE = 20;

    private String ip;
    private int port;

    private ServerInfo info;

    private String resPath;

    private ServerSocket socket;

    private boolean terminated;

    private ArrayList<Client> clients;


    public Server(String ip, int port,ServerInfo info,String ressourcePath){

        this.ip = ip;
        this.port = port;
        this.info = info;
        this.socket = null;
        this.terminated = false;
        this.clients = new ArrayList<>();
        this.resPath = ressourcePath;

    }



    public boolean openServer(){
        try {
            System.out.println("Opening server ...");
            InetAddress addr = InetAddress.getByName(ip);
            socket = new ServerSocket(port,QUEUE_SIZE,addr);
            System.out.println("The server is now open on :\n\nip : "
                    +socket.getInetAddress().getHostName()
                    +"\nport : " +socket.getLocalPort() + ".\n");

            return true;

        }catch (IOException e){
            System.err.println("Failed to open the server");
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public void run(){
        System.out.println("The server is now online");

        while (!terminated){
            try {

                Socket comSock = socket.accept();
                System.out.println("New connection established with [ " +
                        comSock.getInetAddress().getHostName() +
                        " ; " + comSock.getPort() + " ]");

                Client newClient = new Client(comSock,this);
                clients.add(newClient);
                newClient.run();

            }catch (IOException e){
                System.err.println("The server failed to accept a client");
                e.printStackTrace();
            }
        }

    }

    public int getNewId(){

        int id = info.getIdCount();

        info.setIdCount(info.getIdCount()+1);
        info.save(resPath);

        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public ServerInfo getServerInfo() {
        return info;
    }


    public String getResPath() {
        return resPath;
    }

    public ServerSocket getSocket() {
        return socket;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }
}
