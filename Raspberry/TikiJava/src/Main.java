public class Main {


    public static void main(String[] args) {

        String ip = Server.DEFAULT_IP;
        int port = Server.DEFAULT_PORT;
        String resPath = "~/Documents/TikiRes";

        //Parsing arguments

        ServerInfo info = ServerInfo.findServerInfo(resPath);

        Server server = new Server(ip,port,info,resPath);

        server.openServer();

        server.run();

    }
}
