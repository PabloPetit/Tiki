import java.io.*;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by Pablo on 10/07/2016.
 */
public class Client extends Thread {

    private static String DEFAULT_NAME = "NO_NAME";
    private static String DEFAULT_ID = "-1";
    private static String DEFAULT_PASSWORD = "";

    private Server server;
    private Socket socket;
    private ClientInfo info;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    private boolean admin;
    private boolean terminated;


    public Client(Socket socket,Server server){
        this.socket = socket;
        this.server = server;
        this.info = null;
        this.input = null;
        this.output = null;
        this.admin = false;
        this.terminated = false;
    }

    private boolean setClientInfo(String id, String name, String path){

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
            input = new ObjectInputStream(socket.getInputStream());
        }catch (IOException e){
            System.err.println("Socket unreadable");
            e.printStackTrace();
            return false;
        }
        return login();
    }

    private boolean login(){
        String id = DEFAULT_ID;
        String name = DEFAULT_NAME;
        String password = DEFAULT_PASSWORD;

        try {

            //Step 1 : send the server name
            Proto server_name = new Proto(Proto.SERVER_NAME);
            server_name.getData().put("NAME",server.getServerInfo().getName());

            output.writeObject(server_name);
            output.flush();

            //Step 2 : received log info and check password

            Proto logData = (Proto)input.readObject();

            password = (String)(logData.getData().get("PASS"));
            id = (String)(logData.getData().get("ID"));
            name = (String)(logData.getData().get("NAME"));

            System.out.println("Log data received from client : "+getClientName()+" : \n" +
                    "Id : "+id+"\n+" +
                    "Name : "+"\n"+
                    "Password : "+password+"\n");

            if(password.equals(server.getServerInfo().getPassword())){
                System.out.println("Password is correct "+getClientName());
                //Step 3 : Match id, if new, send new id

                boolean newIdNeeded = setClientInfo(id,name,server.getResPath());

                if (newIdNeeded){
                    System.out.println("A new id is requested by client "+getClientName());
                    Proto newId = new Proto(Proto.NEW_ID);
                    int clientId = server.getNewId();
                    newId.getData().put("ID",clientId);
                    output.writeObject(newId);
                    output.flush();
                    System.out.println("New id set for client "+getClientName()+" : "+clientId);
                    info.setId(clientId);
                    info.save(server.getResPath());
                }

                //Step 4 :

                Proto accepted = new Proto(Proto.ACCEPTED);
                output.writeObject(accepted);
                output.flush();

                Proto ack = (Proto) input.readObject();

                if(ack.getPerformative() != Proto.ACK){
                    System.err.println("Client "+getClientName()+" didn't sent ACK message");
                    return false;
                }

            }else {
                System.out.println("Wrong Password "+getClientName());
                Proto denied = new Proto(Proto.DENIED);
                output.writeObject(denied);
                output.flush();
                return false;
            }

        } catch (Exception e) {
            System.err.println("An error occured while login "+getClientName());
            e.printStackTrace();
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

        server.getClients().remove(this);

        if (info != null){
            System.out.println("Connexion with client "+getClientName()+" ended");
            info.save(server.getResPath());
        }
        else {
            System.out.println("Connexion with anonymous client ended");
        }
    }

    public void checkAdminPassword(String password){

        Proto p;

        if (password == server.getServerInfo().getAdminPassword()){
            p = new Proto(Proto.ACCEPTED);
            admin = true;
            System.out.println("Client "+getClientName()+" is now connected as admin");
        }else {
            p = new Proto(Proto.DENIED);
        }
        try{
            output.writeObject(p);
            output.flush();
        } catch (IOException e) {
            System.err.println("An error occured while login as admin "+getClientName());
            e.printStackTrace();
        }
    }

    public String getClientName(){
        if (info != null){
            return "["+info.getName()+" ; "+info.getId()+" ]";
        }
        else {
            return "NOT_SET";
        }
    }

    @Override
    public void run(){

        terminated = !init();

        while (!terminated){

            try {

                Proto incoming = (Proto)input.readObject();

                System.out.println("New message received from "+getClientName()+" : "+incoming.getPerformative());

                switch (incoming.getPerformative()){
                    case Proto.LOG_ADMIN_DATA :
                        checkAdminPassword((String) incoming.getData().get("ADMIN_PASSWORD"));
                        break;

                    default:
                        System.out.println("Wrong performative received from client : "+getClientName());
                        break;
                }
            } catch (IOException e) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        terminate();
    }


}
