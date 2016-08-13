

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Pablo on 10/07/2016.
 */
public class Server extends Thread{

    public static final int TIMEOUT = 1000;

    public static final String DEFAULT_IP = "192.168.1.38";//"192.168.1.113";
    public static final int DEFAULT_PORT = 4200;
    public static final int QUEUE_SIZE = 20;

    private String ip;
    private int port;

    private ServerInfo info;

    private String resPath;

    private ServerSocket socket;

    private AtomicBoolean terminated;

    private ArrayList<Client> clients;

    private LedManager ledManager;


    public Server(String ip, int port,ServerInfo info,String ressourcePath){

        this.ip = ip;
        this.port = port;
        this.info = info;
        this.socket = null;
        this.terminated = new AtomicBoolean(false);
        this.clients = new ArrayList<>();
        this.resPath = ressourcePath;
        this.ledManager = new LedManager();

    }

    public boolean initLedManager(){
        System.out.println("Preparing leds ...");
        if(!ledManager.initLeds()){
            System.out.println("Failed to init leds");
            return false;
        }
        System.out.println("Testing leds ...");
        ledManager.testAllLeds();
        System.out.println("Leds ok");
        return true;
    }



    public boolean openServer(){
        try {
            System.out.println("Opening server ...");
            InetAddress addr = InetAddress.getByName(ip);
            socket = new ServerSocket(port,QUEUE_SIZE,addr);
            System.out.println("The server is open on :\n\nip : "
                    +socket.getInetAddress().getHostName()
                    +"\nport : " +socket.getLocalPort() + "\n");

            return true;

        }catch (BindException e){
            System.err.println("Wrong ip, cannot open server");
            return false;
        }
        catch (IOException e){
            System.err.println("Failed to open the server");
            e.printStackTrace();
            return false;
        }
    }

    public void quit(){
        terminated.set(true);
    }

    public void shutdown(){

        System.out.println("Server shutting down ...");
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Failed to properly close socket");
            e.printStackTrace();
        }
        System.out.println("The server is now offline");

        for (Client c : clients){
            c.quit();
        }

        System.out.println("Waiting for clients to end connexion... ["+ clients.size()+"]");
        int i = 0;
        for (Client c : clients){
            try {
                c.join(TIMEOUT);
                System.out.println(""+ ++i);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }

        closeLeds();

        System.out.println("Server successfully closed");
    }

    public void closeLeds(){
        System.out.println("Shuting down leds...");
        ledManager.close();
    }

    public synchronized boolean removeClient(Client c){
        return clients.remove(c);
    }


    @Override
    public void run(){
        System.out.println("The server is now online");

        while (!terminated.get()){
            try {

                Socket comSock = socket.accept();
                System.out.println("New connection established with [ " +
                        comSock.getInetAddress().getHostName() +
                        " ; " + comSock.getPort() + " ]");

                Client newClient = new Client(comSock,this);
                clients.add(newClient);
                newClient.start();
                System.out.println("Back to the server");
            }catch (IOException e){
                System.err.println("The server failed to accept a client");
                e.printStackTrace();
            }
        }
        shutdown();
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

    public AtomicBoolean getTerminated() {
        return terminated;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public LedManager getLedManager() {
        return ledManager;
    }
}
