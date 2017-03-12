
public class Request {
    private ServerSession serverSession;
    private String state;
    private AttachmentParser attachmentParser;

    public Request(ServerSession serverSession, AttachmentParser attachmentParser){
        this.serverSession = serverSession;
        this.attachmentParser = attachmentParser;
    }

    public void executeNOOP(){
        serverSession.sendResponse("250 OK\r\n");
    }

    public void executeVRFY(){
        serverSession.sendResponse("252 Not supported\r\n");
    }

    public void executeEXPN(){
        serverSession.sendResponse("252 Not supported\r\n");
    }

    public void executeHELP(){
        serverSession.sendResponse("211 Help yourself out\r\n");
    }

    public void executeRSET(){
        serverSession.sendResponse("250 Flushed\r\n");
        state = null;
    }

    public void executeQUIT(){
        serverSession.sendResponse("221 AiPOSServer closing connection\r\n");
    }

    public void executeEHLO(){
        serverSession.sendResponse("250 OK\r\n");
        state = RequestConstants.EHLO;
    }

    public void executeMAIL(String message){
        String senderAddress;
        if(RequestConstants.EHLO.equals(state)){
            senderAddress = message.substring(10, message.length());
           // if(senderAddress.matches("<" + "[a-zA-Z0-9]+@[a-zA-Z]+\\.[a-zA-Z]+" + ">")){
                serverSession.sendResponse("250 Sender OK\r\n");
                state = RequestConstants.MAIL_FROM;
        //    } else serverSession.sendResponse("501 Syntax error in address\r\n");

        } else executeBadRequest();
    }

    public void executeRCPT(String message){
        String rcptAddress;
        if(RequestConstants.MAIL_FROM.equals(state)){
            rcptAddress = message.substring(8, message.length());
           // if(rcptAddress.matches("<" + "[a-zA-Z0-9]+@[a-zA-Z]+\\.[a-zA-Z]+" + ">")){
                serverSession.sendResponse("250 Recipient OK\r\n");
                state = RequestConstants.RCPT_TO;
       //     } else serverSession.sendResponse("501 Syntax error in address\r\n");
        } else executeBadRequest();
    }

    public void executeDATA(){
        if(RequestConstants.RCPT_TO.equals(state)){
            serverSession.sendResponse("354 Start mail input; end with <CRLF>.<CRLF>\r\n");
            attachmentParser.parse();
            serverSession.sendResponse("250 Message accepted for delivery\r\n");
            state = RequestConstants.HELO;
        } else executeBadRequest();
    }

    public void executeBadRequest(){
        serverSession.sendResponse("503 Bad sequence of commands \n\r");
    }
}
