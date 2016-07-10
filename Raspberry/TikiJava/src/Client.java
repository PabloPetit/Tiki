import java.io.*;
import java.net.Socket;

/**
 * Created by Pablo on 10/07/2016.
 */
public class Client extends Thread {

    private static String DEFAULT_NAME = "NO_NAME";
    private static String DEFAULT_ID = "-1";


    private Server server;
    private Socket socket;
    private ClientInfo info;
    private BufferedReader br;
    private PrintWriter pw;

    private boolean terminated;


    public Client(Socket socket,Server server){
        this.socket = socket;
        this.server = server;
        this.info = null;
        this.br = null;
        this.pw = null;
        this.terminated = false;
    }

    private void setClientInfo(String id, String name, String path){

        ClientInfo tmp = ClientInfo.findClientInfo(id,path);

        if (tmp != null){
            info = tmp;
            return;
        }

        info = new ClientInfo(server.getNewId(),name);
    }

    private boolean login(){

        String id = DEFAULT_ID;
        String name = DEFAULT_NAME;






        setClientInfo(id,name,server.getResPath());
        return true;
    }

    private boolean init(){

        try{

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

        }catch (IOException e){
            System.out.println("Socket unreadable");
            e.printStackTrace();
            return false;
        }

        return login();

    }

    @Override
    public void run(){


        terminated = init();

        while (!terminated){

        }
        
    }

}
