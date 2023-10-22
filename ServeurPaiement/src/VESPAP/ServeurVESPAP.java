package VESPAP;

import JDBC.DatabaseConnection;
import Tcp.Interface.Protocole;
import Tcp.ThreadServeur;
import Tcp.ThreadServeurPool;

import java.io.IOException;
import java.sql.SQLException;

public class ServeurVESPAP {
    ThreadServeur threadServeur;
    DatabaseConnection dbConnect;

    public ServeurVESPAP() {
        threadServeur = null;

        // Connexion MySql
        try {
            dbConnect = new DatabaseConnection(DatabaseConnection.MYSQL,
                    "192.168.28.128",
                    "PourStudent",
                    "Student",
                    "PassStudent1_");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Lancement des threads
        try
        {
            Protocole protocole = new VESPAP(dbConnect);
            int port = 50000;
            int taillePool = 5;

            System.out.println("[SERVER] Lancement du pool");
            threadServeur = new ThreadServeurPool(port,protocole,taillePool);

            threadServeur.start();

        }
        catch (NumberFormatException ex)
        {
            System.out.println("ERREUR 1");
        }
        catch (IOException ex)
        {
            System.out.println("ERREUR 2");
        }
    }
}
