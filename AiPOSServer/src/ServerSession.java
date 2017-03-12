import java.io.*;
import java.net.Socket;

public class ServerSession implements Runnable{
    private Thread thread;
    private Socket socket;
    private Server server;
    private Request request;

    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;
    private OutputStreamWriter outputStreamWriter;
    private PrintWriter printWriter;

    private Logger logger;
    private AttachmentParser attachmentParser;

    private boolean running;

    public ServerSession(Socket socket, Server server) {
        this.server = server;
        this.socket = socket;

        logger = new Logger();
        attachmentParser = new AttachmentParser(this);

        request = new Request(this, attachmentParser);

        initStreams();

        thread = new Thread(this);
        thread.start();
    }

    public void initStreams(){
        try {
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);

            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            printWriter = new PrintWriter(outputStreamWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeStreams(){
        try {
            bufferedReader.close();
            printWriter.close();

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String message;
        String command;
        sendResponse("220 AiPOSServer is ready!\r\n");
        System.out.println("run session...");
        logger.writeToLog("run session...\r\n");

        running = true;
        while (running){
            message = getRequest();

            if(message == null) {
                System.out.println("failed\r\n");
                break;
            }
            if(message.length() < 4) {
                sendResponse("500 Command too short\r\n");
                break;
            } else command = message.substring(0, 4);

            switch (command.toUpperCase()){
                case RequestConstants.EHLO:
                    request.executeEHLO();
                    break;
                case RequestConstants.HELO:
                    request.executeEHLO();
                    break;
                case RequestConstants.MAIL:
                    if (message.length() > 10 && RequestConstants.MAIL_FROM.equals(message.substring(0, 10).toUpperCase()))
                        request.executeMAIL(message);
                    else sendResponse("500 Command not recognized\r\n");
                    break;
                case RequestConstants.RCPT:
                    if (message.length() > 8 && RequestConstants.RCPT_TO.equals(message.substring(0, 8).toUpperCase()))
                        request.executeRCPT(message);
                    else sendResponse("500 Command not recognized\r\n");
                    break;
                case RequestConstants.DATA:
                    request.executeDATA();
                    break;
                case RequestConstants.RSET:
                    request.executeRSET();
                    break;
                case RequestConstants.NOOP:
                    request.executeNOOP();
                    break;
                case RequestConstants.EXPN:
                    request.executeEXPN();
                    break;
                case RequestConstants.VRFY:
                    request.executeVRFY();
                    break;
                case RequestConstants.HELP:
                    request.executeHELP();
                    break;
                case RequestConstants.QUIT:
                    request.executeQUIT();
                    logger.writeLogToFile(attachmentParser.getMessageID());
                    running = false;
                    closeStreams();
                    break;
                case RequestConstants.EXIT:
                    running = false;
                    closeStreams();
                    server.stopServer();
                    break;
                default: sendResponse("500 Command not recognized\r\n");
            }
        }
    }

    public String getRequest(){
        try {
            String request = bufferedReader.readLine();
            logger.writeToLog("Message from client: " + request + "\r\n");
            System.out.println(request);

            return request;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendResponse(String response){
        printWriter.print(response);
        printWriter.flush();

        logger.writeToLog("Response from server: " + response + "\r\n");
        System.out.println(response);

    }

}
