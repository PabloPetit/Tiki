import java.io.*;

/**
 * Created by Pablo on 10/07/2016.
 */
public class ServerInfo implements Serializable{

    private static final String DEFAULT_NAME = "Tahuiti";
    private static final String DEFAULT_PASSWORD = "password";
    private static final String DEFAULT_MASTER_PASS = "superPassword";

    private String name;
    private String password;
    private int idCount;


    public ServerInfo(String name, String password){
        this.name = name;
        this.password = password;
        this.idCount = 0;
    }

    public boolean save(String path){
        try {

            File old = new File(path+"/server.tki");

            if(old!=null){
                old.delete();
            }

            FileOutputStream streamOut = new FileOutputStream(path+"/server.tki");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(streamOut);
            objectOutputStream.writeObject(this);

            objectOutputStream.close();
            streamOut.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }



    public static ServerInfo findServerInfo(String path) {

        File root = new File(path);

        for (File f : root.listFiles()) {

            String tmp = f.getName().replace(".tki", "");

            if (tmp.equals("server")) {
                try {
                    FileInputStream streamIn = new FileInputStream(f.getPath());
                    ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);

                    ServerInfo info = (ServerInfo) objectinputstream.readObject();

                    objectinputstream.close();
                    streamIn.close();

                    return info;

                } catch (IOException e) {
                    System.out.println("Server info recovery failed");
                    e.printStackTrace();

                } catch (ClassNotFoundException e) {
                    System.out.println("Server info recovery failed");
                    e.printStackTrace();
                }
            }
        }

        ServerInfo info = new ServerInfo(DEFAULT_NAME,DEFAULT_PASSWORD);

        info.save(path);

        return info;

    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getIdCount() {
        return idCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIdCount(int idCount) {
        this.idCount = idCount;
    }
}
