public class Main {


    public static void main(String[] args) {

        String ip = Server.DEFAULT_IP;
        int port = Server.DEFAULT_PORT;
        String resPath = "/home/pi/Documents/TikiRes";

        //Parsing arguments

        ServerInfo info = ServerInfo.findServerInfo(resPath);

        if (info == null){
            System.err.println("Server info recovery failed");
            return;
        }

        Server server = new Server(ip,port,info,resPath);

        server.openServer();

        server.run();

    }
}
