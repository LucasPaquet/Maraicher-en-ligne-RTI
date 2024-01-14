package Tcp;

import Tcp.Interface.Protocole;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ThreadServeurDemande extends ThreadServeur{
    public ThreadServeurDemande(int port, Protocole protocole) throws IOException
    {
        super(port, protocole, false);
    }

    @Override
    public void run()
    {
        System.out.println("[SERVEUR] Démarrage du thread Serveur (Demande)...");
        while(!this.isInterrupted())
        {
            Socket csocket;
            try
            {
                ssocket.setSoTimeout(2000);
                csocket = ssocket.accept();
                System.out.println("[SERVEUR] Connexion acceptée, création thread Client");
                Thread th = new ThreadClientDemande(protocole,csocket);
                th.start();
            }
            catch (SocketTimeoutException ex)
            {
                // Pour vérifier si le thread a été interrompu
            }
            catch (IOException ex)
            {
                System.out.println("Erreur I/O");
            }
        }
        System.out.println("[SERVEUR] Thread Serveur (Demande) interrompu.");
        try { ssocket.close(); }
        catch (IOException ex) { System.out.println("Erreur I/O"); }
    }

}
