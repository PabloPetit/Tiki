import java.io.*;

/**
 * Created by Pablo on 10/07/2016.
 */
public class ServerInfo implements Serializable{



    private static final String DEFAULT_NAME = "Tahuiti";
    private static final String DEFAULT_PASSWORD = "password";
    private static final String DEFAULT_ADMIN_PASSWORD = "superPassword";

    private String name;
    private String password;
    private String adminPassword;
    private int idCount;


    public ServerInfo(String name, String password, String adminPassword){
        this.name = name;
        this.password = password;
        this.adminPassword = adminPassword;
        this.idCount = 1;
    }

    public boolean save(String path){
        try {

            File old = new File(path+"/server.tki");

            if(old!=null){ // supposedly useless
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

        System.out.println("Looking for server info ...");

        File root = new File(path);

        if (root==null){
            return null;
        }

        for (File f : root.listFiles()) {

            String tmp = f.getName().replace(".tki", "");

            if (tmp.equals("server")) {
                try {
                    FileInputStream streamIn = new FileInputStream(f.getPath());
                    ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);

                    ServerInfo info = (ServerInfo) objectinputstream.readObject();

                    objectinputstream.close();
                    streamIn.close();

                    System.out.println("Server info found");

                    return info;

                } catch (IOException e) {
                    System.err.println("Server info recovery failed");
                    e.printStackTrace();

                } catch (ClassNotFoundException e) {
                    System.err.println("Server info recovery failed");
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Server info not found, creating new one");

        ServerInfo info = new ServerInfo(DEFAULT_NAME,DEFAULT_PASSWORD,DEFAULT_ADMIN_PASSWORD);

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

    public String getAdminPassword() {
        return adminPassword;
    }
}
