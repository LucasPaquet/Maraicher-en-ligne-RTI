package Tcp;

import Tcp.Interface.Protocole;

import java.io.IOException;
import java.net.Socket;

public class ThreadServeurPoolTLS extends ThreadServeurTLS{
    private FileAttente connexionsEnAttente;
    private ThreadGroup pool;
    private int taillePool;

    public ThreadServeurPoolTLS(int port, Protocole protocole, int taillePool)
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
            catch (Exception ex) {
                System.out.println("Erreur de ThreadServeurPoolTLS" + ex);
            }
        }
        pool.interrupt();
    }
}
