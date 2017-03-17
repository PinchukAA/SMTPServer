import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final int DEFAULT_SMTP_PORT = 25;
    private static final int TIMEOUT = 10000;

    private boolean running;

    private Socket socket;
    private ServerSocket serverSocket;

    public Server(){
        ConfigFileReader.parseFile();
        AttachmentSaver.setPath(ConfigFileReader.getPath());
        AttachmentSaver.setEncoding(ConfigFileReader.getEncoding());
    }

    public void startServer(){
        try {
            serverSocket = new ServerSocket(ConfigFileReader.getPort());
            running = true;
            while (running){
                socket = serverSocket.accept();
                System.out.println("connection...");
                new ServerSession(socket, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void stopServer(){
        try {
            running = false;
            serverSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        Server server = new Server();
        new ServerFrame(server);
        server.startServer();
    }
}
