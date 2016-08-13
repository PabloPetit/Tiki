import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Pablo on 10/07/2016.
 */
public class Client extends Thread {

    private static String DEFAULT_NAME = "NO_NAME";
    private static int DEFAULT_ID = -1;
    private static String DEFAULT_PASSWORD = "";


    private Server server;
    private Socket socket;
    private ClientInfo info;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private LedManager ledManger;

    private boolean admin;
    private AtomicBoolean terminated;


    public Client(Socket socket,Server server){
        this.socket = socket;
        this.server = server;
        this.info = null;
        this.input = null;
        this.output = null;
        this.admin = false;
        this.terminated = new AtomicBoolean(false);
        this.ledManger = server.getLedManager();
    }

    private boolean setClientInfo(int id, String name, String path){

        ClientInfo tmp = ClientInfo.findClientInfo(id,path);

        if (tmp != null){
            info = tmp;
            return true;
        }

        info = new ClientInfo(server.getNewId(),name);
        return false;
    }

    private boolean init(){

        try{
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
        }catch (IOException e){
            System.err.println("Socket unreadable");
            e.printStackTrace();
            return false;
        }
        return login();
    }
    

    

    private boolean login(){
        int id = DEFAULT_ID;
        String name = DEFAULT_NAME;
        String password = DEFAULT_PASSWORD;


        //Step 1 : send the server name
        Pack server_name = new Pack(Pack.SERVER_NAME);
        server_name.getData().put(Pack.NAME,server.getServerInfo().getName());
        Pack.sendPack(server_name,output);

        //Step 2 : received log info and check password
        Pack logData = Pack.readPack(input);

        if(logData == null){
            System.err.println("Client "+getClientName()+" did not sent login data");
            return false;
        }

        password = (String)(logData.getData().get(Pack.PASSWORD));
        id = (int)(logData.getData().get(Pack.ID));
        name = (String)(logData.getData().get(Pack.NAME));

        System.out.println("Log data received from client : "+getClientName()+"\n" +
                "Id : "+id+"\n" +
                "Name : "+name+"\n"+
                "Password : "+password+"\n");
        // Checking the password
        if(password.equals(server.getServerInfo().getPassword())){
            System.out.println("Password is correct "+getClientName());
            //Step 3 : Match id, if new, send new id
            System.out.println("Looking for client info ...");

            boolean newIdNeeded = !setClientInfo(id,name,server.getResPath());

            if (newIdNeeded){
                System.out.println("A new id is requested by client "+getClientName());
                Pack newId = new Pack(Pack.NEW_ID);
                int clientId = server.getNewId();
                newId.getData().put(Pack.ID,clientId);
                Pack.sendPack(newId,output);

                System.out.println("New id sent to client "+getClientName());
                System.out.println("Waiting fo acknolegde");

                Pack p = Pack.readPack(input);

                if(p == null || p.getPerformative() != Pack.ACK){
                    System.out.println((p==null)?"ACK not received":"Wrong performative");
                }

                System.out.println("New id set for client "+getClientName()+" : "+clientId);
                info.setId(clientId);
                info.save(server.getResPath());


            }else{
                System.out.println("Info found : \n" +
                        "Id : "+info.getId()+"\n" +
                        "Name : "+info.getName()+"\n" +
                        "Connexions : "+info.getNbConnexions());
            }

            //Step 4 :

            Pack accepted = new Pack(Pack.ACCEPTED);
            Pack.sendPack(accepted,output);

            Pack ack = Pack.readPack(input);
            if(ack == null || ack.getPerformative() != Pack.ACK){
                System.err.println("Client "+getClientName()+" didn't sent ACK message");
                return false;
            }
        }else {
            System.out.println("Wrong Password "+getClientName());
            Pack denied = new Pack(Pack.DENIED);
            Pack.sendPack(denied,output);
            return false;
        }
        return true;
    }



    private void terminate(){
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Failed to close socket "+getClientName());
            e.printStackTrace();
        }

        server.removeClient(this);

        if (info != null){
            System.out.println("Connexion with client "+getClientName()+" ended");
            info.save(server.getResPath());
        }
        else {
            System.out.println("Connexion with anonymous client ended");
        }
    }

    public void checkAdminPassword(String password){

        Pack p;

        System.out.println("Client "+getClientName()+" is trying to log as admin");

        System.out.println("Pass received : "+password+" Actual pass : "+server.getServerInfo().getAdminPassword());

        if (password.equals(server.getServerInfo().getAdminPassword())){
            p = new Pack(Pack.ACCEPTED);
            admin = true;
            System.out.println("Client "+getClientName()+" is now connected as admin");
        }else {
            p = new Pack(Pack.DENIED);
            System.out.println("Admin login denied for client "+getClientName());
        }

        Pack.sendPack(p,output);
    }

    public String getClientName(){
        if (info != null){
            return info.getName()+":"+info.getId();
        }
        else {
            return "NOT_SET";
        }
    }

    public synchronized void quit(){
        terminated.set(true);
    }

    public void shutdown(){
        System.out.println("Client "+getClientName()+" asked for shutdown");
        if (!admin) {
            System.out.println("Client "+getClientName()+" is not admin");
            return;
        }
        System.out.println("Permission granted");
        server.quit();
        quit();
    }

    public boolean checkConnexionOk(){
        if(socket.isClosed() || !socket.isConnected() || socket.isInputShutdown() || socket.isOutputShutdown()){
            terminated.set(true);
            return false;
        }
        return true;
    }

    @Override
    public void run(){

        if(!init()){
            System.err.println("An error occured while login "+getClientName());
            terminated.set(true);
        }

        while (!terminated.get()){

            if(!checkConnexionOk()) break;

            Pack incoming = Pack.readPack(input);

            if(incoming == null){
                try {
                    Thread.sleep(Pack.LITTLE_SLEEP);
                    continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("New message received from "+getClientName()+" : "+incoming.getPerformative());

            switchPerformative(incoming);

        }
        terminate();
    }


    public void switchPerformative(Pack incoming){
        switch (incoming.getPerformative()){
            case Pack.QUIT:
                quit();
                break;

            case Pack.SHUTDOWN:
                shutdown();
                break;

            case Pack.LOG_ADMIN_DATA :
                checkAdminPassword((String) incoming.getData().get(Pack.ADMIN_PASSWORD));
                break;

            case Pack.TOGGLE :
                ledManger.toggleLed((Integer) incoming.getData().get(Pack.ID),admin);
                break;

            case Pack.SEQUENCE :
                break;

            case Pack.ON :
                ledManger.onLed((Integer) incoming.getData().get(Pack.ID),admin);
                break;

            case Pack.OFF :
                ledManger.offLed((Integer) incoming.getData().get(Pack.ID),admin);
                break;

            case Pack.BLOCK_ACCESS:
                break;

            case Pack.RANDOM :
                break;

            case Pack.CLIENT_LIST:
                break;

            default:
                System.out.println("Wrong performative received from client : "+getClientName());
                break;
        }
    }

}
