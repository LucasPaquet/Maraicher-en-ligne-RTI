package Tcp;

import Tcp.Interface.Protocole;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Au sein du processus serveur, un thread sera dédié à l’acception des demandes de
 * connexion par les clients et ces « connexions établies » (les sockets de service) seront
 * transmises aux threads clients
 */
public abstract class ThreadServeur extends Thread
{
    protected int port;
    protected Protocole protocole;
    protected ServerSocket ssocket;

    public ThreadServeur(int port, Protocole protocole) throws
            IOException
    {
        super("TH Serveur (port=" + port + ",protocole=" + protocole.getNom() + ")");
        this.port = port;
        this.protocole = protocole;

        ssocket = new ServerSocket(port);
    }
}
