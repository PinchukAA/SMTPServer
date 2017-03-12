import javax.swing.*;
import java.awt.*;

public class ServerFrame extends JFrame {

    private Server server;

    private static JTabbedPane tabbedPane;

    public ServerFrame(Server server){
        this.server = server;

        setSize(new Dimension(450, 665));
        setResizable(false);

        setTitle("AiPOSServer - log");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();

        add(tabbedPane, BorderLayout.NORTH);

        setVisible(true);
    }

    public static void addNewConnectionTab(JTextArea textArea, String title){
        JScrollPane scrollPane = new JScrollPane(textArea);
        tabbedPane.add(title, scrollPane);
    }

    public static JTextArea createNewTextArea(){
        JTextArea textArea = new JTextArea(38, 50);

        textArea.setEditable(false);

        addNewConnectionTab(textArea, "");

        return textArea;
    }
}
