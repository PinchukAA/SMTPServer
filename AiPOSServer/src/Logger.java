import javax.swing.*;

public class Logger {
    private JTextArea textArea;

    public Logger(){
        textArea = ServerFrame.createNewTextArea();
    }

    public void writeToLog(String message){
        textArea.append(message);
    }

    public void writeLogToFile(String folderName){
        AttachmentSaver.writeToFile(folderName, "log.txt", textArea.getText());
    }
}
