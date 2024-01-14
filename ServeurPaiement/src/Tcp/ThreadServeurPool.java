package Tcp;

import Tcp.Interface.Protocole;

import java.io.IOException;
import java.net.*;
public class ThreadServeurPool extends ThreadServeur
{
    private FileAttente connexionsEnAttente;
    private ThreadGroup pool;
    private int taillePool;

    public ThreadServeurPool(int port, Protocole protocole, int taillePool) throws IOException
    {
        super(port, protocole);

        connexionsEnAttente = new FileAttente();
        pool = new ThreadGroup("POOL");
        this.taillePool = taillePool;
    }

    @Override
    public void run()
    {
        // Création du pool de threads
        try
        {
            System.out.println("[SERVEUR] Démarrage du thread Serveur (POOL)...");
            for (int i=0 ; i<taillePool ; i++)
                new ThreadClientPool(protocole,connexionsEnAttente,pool).start();
        }
        catch (IOException ex)
        {
            return;
        }

        // Attente des connexions
        while(!this.isInterrupted())
        {
            Socket csocket;
            try
            {
                ssocket.setSoTimeout(2000);
                csocket = ssocket.accept();
                connexionsEnAttente.addConnexion(csocket);
            }
            catch (Exception ex)
            {
               System.out.println("Erreur de ThreadServeurPool" + ex);
            }

        }
        pool.interrupt();
    }
}

