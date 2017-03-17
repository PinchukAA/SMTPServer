
import java.io.*;

public class AttachmentSaver {
    private static String path;
    private static String encoding;

    public static void setPath(String path) {
        AttachmentSaver.path = path;
    }

    public static void setEncoding(String encoding) {
        AttachmentSaver.encoding = encoding;
    }

    public static void createFolder(String folderName){
        new File(path + folderName).mkdirs();
    }

    public static void initStream(String folderName, String fileName){

    }

    public static void writeToFile(String folderName, String fileName, String data){
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path + folderName + "/" + fileName, true), encoding));
            bw.write(data);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToFile(String folderName, String fileName, byte[] data){
        try {
            FileOutputStream fos = new FileOutputStream(path + folderName + "/" + fileName, true);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

