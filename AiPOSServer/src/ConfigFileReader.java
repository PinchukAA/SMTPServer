import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ConfigFileReader {
    private static final String PORT = "Port: ";
    private static final String PATH = "Path: ";
    private static final String ENCODING = "Encoding: ";
    private static final String CONFIG_PATH = "../config.txt";

    private static String path;
    private static String encoding;
    private static int port;

    private static FileReader fileReader;

    public static void parseFile(){
        String configData = "";

        try {
            fileReader = new FileReader(CONFIG_PATH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            int c;
            while((c = fileReader.read()) != -1){
                configData += (char) c;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        path = configData.substring(configData.indexOf(PATH) + PATH.length(), configData.indexOf(ENCODING) - 2);
        encoding = configData.substring(configData.indexOf(ENCODING) + ENCODING.length(), configData.indexOf(PORT) - 2);
        port = Integer.valueOf(configData.substring(configData.indexOf(PORT) + PORT.length()));

        System.out.println(path);
        System.out.println(encoding);
        System.out.println(port);
    }


    public static int getPort() {
        return port;
    }

    public static String getPath() {
        return path;
    }

    public static String getEncoding() {
        return encoding;
    }
}
