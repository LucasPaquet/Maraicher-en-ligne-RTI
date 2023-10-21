package VESPAP;

import Tcp.Interface.Protocole;
import Tcp.ThreadServeur;
import Tcp.ThreadServeurPool;

import java.io.IOException;

public class ServeurVESPAP {
    ThreadServeur threadServeur;

    public ServeurVESPAP() {
        threadServeur = null;
        try
        {
            Protocole protocole = new VESPAP();
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
