import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Pablo on 11/07/2016.
 */
public class Proto implements Serializable{


    static final long serialVersionUID = 456789876567L;


    public static final int LOG_DATA = 1;
    public static final int DENIED = 2;
    public static final int ACCEPTED = 3;
    public static final int NEW_ID = 4;
    public static final int ACK = 5;
    public static final int SERVER_NAME = 6;
    public static final int QUIT = 7;
    public static final int SHUTDOWN = 8;
    public static final int LOG_ADMIN_DATA = 9;


    private int performative;
    private HashMap<String,Object> data;



    public Proto(int performative) {
        this.performative = performative;
        data = new HashMap<>();
    }



    public int getPerformative() {
        return performative;
    }

    public void setPerformative(int performative) {
        this.performative = performative;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }
}
