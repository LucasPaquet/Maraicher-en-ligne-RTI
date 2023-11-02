package VESPAP;

import JDBC.DatabaseConnection;
import Tcp.Interface.Protocole;
import Tcp.ThreadServeur;
import Tcp.ThreadServeurPool;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class ServeurVESPAP {
    ThreadServeur threadServeur;
    DatabaseConnection dbConnect;
    private int port;
    private int taillePool;

    public ServeurVESPAP() {
        threadServeur = null;

        initConfig();

        // Connexion MySql
        try {
            dbConnect = new DatabaseConnection(DatabaseConnection.MYSQL,
                    "192.168.28.128",
                    "PourStudent",
                    "Student",
                    "PassStudent1_");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

        // Lancement des threads
        try
        {
            Protocole protocole = new VESPAP(dbConnect);

            System.out.println("[SERVER] Lancement du pool");
            threadServeur = new ThreadServeurPool(port,protocole,taillePool);

            threadServeur.start();
        }
        catch (NumberFormatException ex)
        {
            System.out.println("ERREUR NumberFormatException : " + ex);
        }
        catch (IOException ex)
        {
            System.out.println("ERREUR IOException : " + ex);
        }
    }

    /**
     * Permet de lire le fichier de configuration pour le nombre de thread dans le pool et le numéro de port
     */
    private void initConfig() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("src/config.properties")) {
            properties.load(fis);

            port = Integer.parseInt(properties.getProperty("PORT_PAIEMENT"));
            taillePool = Integer.parseInt(properties.getProperty("NB_THREAD_POOL"));
        } catch (IOException e) {
            System.out.println("ERREUR IOException : " + e);
            e.printStackTrace();
        }
    }
}
