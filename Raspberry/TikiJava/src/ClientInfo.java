import java.io.*;

/**
 * Created by Pablo on 10/07/2016.
 */
public class ClientInfo implements Serializable {

    private int id;
    private String name;
    private int nbConnexions;


    public ClientInfo(int id, String name){
        this.id = id;
        this.name = name;
        this.nbConnexions = 0;
    }


    public boolean save(String path){
        try {
            File old = new File(path+"/"+id+".tki");

            if(old!=null){
                old.delete();
            }

            FileOutputStream streamOut = new FileOutputStream(path+"/"+id+".tki");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(streamOut);
            objectOutputStream.writeObject(this);

            objectOutputStream.close();
            streamOut.close();

        } catch (Exception e) {
            System.err.println("Failed to save client info in file : "+path);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static ClientInfo findClientInfo(int id, String path){

        File root = new File(path);

        for (File f : root.listFiles()){

            String tmp = f.getName().replace(".tki","");

            String idString = id+"";

            if(tmp.equals(idString)){
                try {
                    FileInputStream streamIn = new FileInputStream(f.getPath());
                    ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);

                    ClientInfo info = (ClientInfo) objectinputstream.readObject();

                    return info;

                }catch (IOException e){
                    System.err.println("Failed to find client info for id : "+id+" in file : "+path);
                    e.printStackTrace();

                }catch (ClassNotFoundException e){
                    e.printStackTrace();
                }
            }
        }

        System.err.println("Client info not found");

        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNbConnexions() {
        return nbConnexions;
    }

    public void setNbConnexions(int nbConnexions) {
        this.nbConnexions = nbConnexions;
    }
}
