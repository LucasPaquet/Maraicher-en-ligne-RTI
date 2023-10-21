package Tcp;

import java.io.IOException;

public class ServeurLILOC {
    ThreadServeur threadServeur;

    public ServeurLILOC() {
        threadServeur = null;
        try
        {
            Protocole protocole = new LILOC();
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
