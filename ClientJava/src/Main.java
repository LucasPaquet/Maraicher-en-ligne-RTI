import GUI.WindowClient;
import Tcp.TcpConnection;
import com.formdev.flatlaf.FlatLightLaf;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {

        String ip = "";
        int port= 0;

        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("src/config.properties")) {
            properties.load(fis);

            port = Integer.parseInt(properties.getProperty("PORT_PAIEMENT"));
            ip = properties.getProperty("IP_PAIEMENT");
        } catch (IOException e) {
            e.printStackTrace();
        }

        FlatLightLaf.setup(); // pour mettre un look and feel plus moderne
        TcpConnection connection = new TcpConnection(ip, port);
        WindowClient gui = new WindowClient(connection);

        gui.setVisible(true);


    }
}