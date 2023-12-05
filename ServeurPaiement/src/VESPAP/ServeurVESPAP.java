package VESPAP;

import JDBC.DatabaseConnection;
import Tcp.Interface.Protocole;
import Tcp.ThreadServeur;
import Tcp.ThreadServeurDemande;
import Tcp.ThreadServeurPool;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.util.Properties;

public class ServeurVESPAP {
    ThreadServeur threadServeur;
    ThreadServeur threadServeurSecure;
    DatabaseConnection dbConnect;
    private int port;
    private int taillePool;
    private int portSecure;


    public ServeurVESPAP() {
        threadServeur = null;

        initConfig();

        // Connexion MySql
        try {
            dbConnect = new DatabaseConnection(DatabaseConnection.MYSQL,
                    "192.168.122.1",
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
            threadServeur = new ThreadServeurPool(port,protocole,taillePool);
            threadServeurSecure = new ThreadServeurDemande(portSecure, protocoleSecure);

            threadServeur.start();
            threadServeurSecure.start();
        }
        catch (NumberFormatException ex)
        {
            System.out.println("ERREUR NumberFormatException : " + ex);
        }
        catch (IOException ex)
        {
            System.out.println("ERREUR IOException : " + ex);
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
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
            taillePool = Integer.parseInt(properties.getProperty("NB_THREAD_POOL"));
        } catch (IOException e) {
            System.out.println("ERREUR IOException : " + e);
            e.printStackTrace();
        }
    }
}
