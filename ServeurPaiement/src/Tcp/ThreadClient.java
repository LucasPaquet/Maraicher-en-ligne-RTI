package Tcp;

import Tcp.Interface.Protocole;
import Tcp.Interface.Reponse;
import Tcp.Interface.Requete;

import java.io.*;
import java.net.Socket;

/**
 * Thread (lancé dans le processus serveur) chargé de s’occuper entièrement de la connexion de
 * ce client
 */
public abstract class ThreadClient extends Thread
{
    protected Protocole protocole;
    protected Socket csocket;
    private int numero;

    private static int numCourant = 1;

    public ThreadClient(Protocole protocole, Socket csocket) throws
            IOException
    {
        super("TH Client " + numCourant + " (protocole=" + protocole.getNom() + ")");
        this.protocole = protocole;
        this.csocket = csocket;
        this.numero = numCourant++;
    }

    public ThreadClient(Protocole protocole, ThreadGroup groupe)
            throws IOException
    {
        super(groupe,"TH Client " + numCourant + " (protocole=" + protocole.getNom()
                + ")");
        this.protocole = protocole;
        this.csocket = null;
        this.numero = numCourant++;
    }

    @Override
    public void run()
    {
        try
        {
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;

            try
            {
                ois = new ObjectInputStream(csocket.getInputStream());
                oos = new ObjectOutputStream(csocket.getOutputStream());

                while (true)
                {
                    Requete requete = (Requete) ois.readObject();
                    Reponse reponse = protocole.TraiteRequete(requete,csocket);
                    oos.writeObject(reponse);
                }
            }
            catch (FinConnexionException ex)
            {
                System.out.println("Fin connexion demandée par protocole");
                if (oos != null && ex.getReponse() != null)
                    oos.writeObject(ex.getReponse());
            }
        }
        catch (IOException ex) { System.out.println("Erreur I/O"); }
        catch (ClassNotFoundException ex) { System.out.println("Erreur requete invalide");
        }
        finally
        {
            try { csocket.close(); }
            catch (IOException ex) { System.out.println("Erreur fermeture socket"); }
        }
    }
}
