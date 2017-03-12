import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class AttachmentParser {

    private ServerSession serverSession;

    private String date;

    private String messageID;
    private String rcptAddress;
    private String sndrAddress;
    private String message;
    private String contentType;


    public AttachmentParser(ServerSession serverSession){
        this.serverSession = serverSession;
    }

    public void parse(){
        String command;

        while (true){
            command = serverSession.getRequest();

            if(command == null) {
                System.out.println("failed");
                break;
            }

            if (command.startsWith(ParserConstants.DATE)) date = command;
            else if (command.startsWith(ParserConstants.FROM)) sndrAddress = command;
            else if (command.startsWith(ParserConstants.MESSAGE_ID)) {
                messageID = command.substring(ParserConstants.MESSAGE_ID.length() + 1, command.indexOf(">"));
                AttachmentSaver.createFolder(messageID);
            }
            else if (command.startsWith(ParserConstants.TO)) rcptAddress = command;
            else if (command.startsWith(ParserConstants.CONTENT_TYPE)) {
                String contentType = command.substring(ParserConstants.CONTENT_TYPE.length(), command.indexOf(";"));
                String boundary;
                switch (contentType){
                    case ParserConstants.MIXED_TYPE:
                        boundary = serverSession.getRequest();
                        boundary = "--".concat(boundary.substring(ParserConstants.BOUNDARY.length() + 2, boundary.length() - 1));

                     //   System.out.println("================================================================    " + boundary);

                        parseMessage(boundary);
                        parseAttachments(boundary);
                        return;
                    case ParserConstants.PLAIN_TYPE:
                        parseMessage(ParserConstants.DEFAULT_BOUNDARY);
                        return;
                }
            }
        }
    }

    private void parseAttachments(String boundary){
        String command;
        while (true){
            byte[] attachmentData;
            byte[] decodedData;

            command = serverSession.getRequest();
            if(ParserConstants.DEFAULT_BOUNDARY.equals(command)) break;

            if(command.startsWith(ParserConstants.FILE_NAME)){
                String fileName = command.substring(ParserConstants.FILE_NAME.length() + 1,  command.length() - 1);

                attachmentData = getAttachmentBytes(boundary);

                decodedData = Base64.getMimeDecoder().decode(attachmentData);
                AttachmentSaver.writeToFile(messageID, fileName, decodedData);
            }
        }
    }

    public byte[] getAttachmentBytes(String boundary){
        byte[] attachmentBytes = new byte[]{};

        while (true){
            byte[] temp = attachmentBytes;
            String part = serverSession.getRequest();

            if(part.startsWith(boundary)){
                break;
            }

            attachmentBytes = new byte[attachmentBytes.length + part.getBytes().length];
            System.arraycopy(temp, 0, attachmentBytes, 0, temp.length);
            System.arraycopy(part.getBytes(), 0, attachmentBytes, temp.length, part.getBytes().length);
        }

        return attachmentBytes;
    }

    private void parseMessage(String boundary){
        String command;
        message = "";
        while(true){
            command = serverSession.getRequest();
            if(command.startsWith(ParserConstants.CONTENT_TRANSFER_ENCODING)){
                while (true){
                    command = serverSession.getRequest();
                    if(command != null) {
                        if (boundary.equals(command)) break;
                        message = new StringBuffer(message).append(command + "\r\n").toString();
                    }
                }
                break;
            }
        }
/*
        try {
            message = new String(message.getBytes("Windows-1251"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
*/
        AttachmentSaver.writeToFile(messageID, "message.txt", date + "\r\n" + sndrAddress + "\r\n" + message);
    }

    public String getMessageID() {
        return messageID;
    }
}
