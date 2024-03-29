package Tcp;

import Tcp.Interface.Protocole;

import java.io.IOException;
import java.net.Socket;

public class ThreadClientDemande extends ThreadClient {
    public ThreadClientDemande(Protocole protocole, Socket csocket) throws IOException
    {
        super(protocole, csocket);
    }

    @Override
    public void run()
    {
        System.out.println("[SERVEUR] Thread Client (Demande) démarre...");
        super.run();
        System.out.println("[SERVEUR] Thread Client (Demande) se termine.");
    }

}
