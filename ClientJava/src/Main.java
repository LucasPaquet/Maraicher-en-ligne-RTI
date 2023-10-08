import GUI.WindowClient;
import com.formdev.flatlaf.FlatLightLaf;

import Tcp.TcpConnection;

public class Main {
    public static void main(String[] args) {
        /*
        TcpConnection serverConnection = new TcpConnection("192.168.28.128", 50000);

        // Envoi de données
        String reponse = "LOGIN#a#a#0";

        serverConnection.send(reponse);
        System.out.println("Réponse envoyée.");

        // Réception de données
        String requete = serverConnection.receive();
        System.out.println("Reçu : " + requete);

        // Fermeture de la connexion
        serverConnection.close();
        */

        FlatLightLaf.setup(); // pour mettre un look and feel plus moderne
        WindowClient gui = new WindowClient();

        gui.setVisible(true);


    }
}