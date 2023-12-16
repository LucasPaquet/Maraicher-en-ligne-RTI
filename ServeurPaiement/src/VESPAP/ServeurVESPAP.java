package VESPAP;

import JDBC.DatabaseConnection;
import Tcp.*;
import Tcp.Interface.Protocole;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class ServeurVESPAP {
    ThreadServeur threadServeur;
    ThreadServeurTLS threadServeurTLS;
    ThreadServeur threadServeurSecure;
    DatabaseConnection dbConnect;
    private int port;
    private int taillePool;
    private int portTLS;
    private int taillePoolTLS;
    private int portSecure;



    public ServeurVESPAP() {
        threadServeur = null;

        initConfig();

        // Connexion MySql
        try {
            dbConnect = new DatabaseConnection(DatabaseConnection.MYSQL,
                    "192.168.0.26",
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
            Protocole protocoleSecure = new VESPAPS(dbConnect);

            System.out.println("[SERVER] Lancement des pools");

            // non-secure
            threadServeur = new ThreadServeurPool(port,protocole,taillePool);
            // TLS
            threadServeurTLS = new ThreadServeurPoolTLS(portTLS,protocole,taillePoolTLS);
            // crypte
            threadServeurSecure = new ThreadServeurDemande(portSecure, protocoleSecure);

            threadServeur.start();
            threadServeurTLS.start();
            threadServeurSecure.start();
        }
        catch (Exception ex)
        {
            System.out.println("ERREUR ServeruVESPAP : " + ex);
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
            portSecure = Integer.parseInt(properties.getProperty("PORT_PAIEMENT_SECURE"));
            portTLS = Integer.parseInt(properties.getProperty("PORT_PAIEMENT_TLS"));
            taillePool = Integer.parseInt(properties.getProperty("NB_THREAD_POOL"));
            taillePoolTLS = Integer.parseInt(properties.getProperty("NB_THREAD_POOL_TLS"));
        } catch (IOException e) {
            System.out.println("ERREUR IOException : " + e);
        }
    }
}
