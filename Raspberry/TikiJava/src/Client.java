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

            if(password.equals(server.getServerInfo().getPassword())){

                //Step 3 : Match id, if new, send new id

                boolean newIdNeeded = setClientInfo(id,name,server.getResPath());

                if (newIdNeeded){
                    Proto newId = new Proto(Proto.NEW_ID);
                    newId.getData().put("ID",server.getNewId());
                    output.writeObject(newId);
                    output.flush();
                }

                //Step 4 :

                Proto accepted = new Proto(Proto.ACCEPTED);
                output.writeObject(accepted);
                output.flush();

                Proto ack = (Proto) input.readObject();

                if(ack.getPerformative() != Proto.ACK){
                    return false;
                }

            }else {
                Proto denied = new Proto(Proto.DENIED);
                output.writeObject(denied);
                output.flush();
                return false;
            }

        } catch (Exception e) {
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
            info.save(server.getResPath()); // Maybe not necessary
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.getClients().remove(this);

        if (info != null){
            System.out.println("Connexion with client "+getClientName());
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

        terminated = init();

        while (!terminated){

            try {

                Proto incoming = (Proto)input.readObject();

                switch (incoming.getPerformative()){
                    case Proto.LOG_ADMIN_DATA :
                        checkAdminPassword((String) incoming.getData().get("ADMIN_PASSWORD"));
                        break;

                    default:
                        System.out.println("Wrong performative received from client : "+getClientName());
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } 
        }
        terminate();
    }


}
