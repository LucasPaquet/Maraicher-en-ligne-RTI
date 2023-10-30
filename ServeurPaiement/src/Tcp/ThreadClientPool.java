package Tcp;

import Tcp.Interface.Protocole;

import java.io.IOException;
public class ThreadClientPool extends ThreadClient
{
    private FileAttente connexionsEnAttente;

    public ThreadClientPool(Protocole protocole, FileAttente file, ThreadGroup groupe) throws IOException
    {
        super(protocole, groupe);
        connexionsEnAttente = file;
    }

    @Override
    public void run()
    {
        boolean interrompu = false;
        while(!interrompu)
        {
            try
            {
                csocket = connexionsEnAttente.getConnexion();
                System.out.println("Connexion prise en charge.");
                super.run();
            }
            catch (InterruptedException ex)
            {
                System.out.println("Demande d'interruption...");
                interrompu = true;
            }
        }
        System.out.println("TH Client (Pool) se termine.");
    }
}

