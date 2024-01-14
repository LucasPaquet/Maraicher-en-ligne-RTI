package Tcp;

import Tcp.Interface.Protocole;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.KeyStore;

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

    public ThreadServeur(int port, Protocole protocole, boolean secure) throws IOException
    {
        super("TH Serveur (port=" + port + ",protocole=" + protocole.getNom() + ")");
        this.port = port;
        this.protocole = protocole;

        if (secure) {
            try {

                KeyStore ServerKs = KeyStore.getInstance("JKS");
                String FICHIER_KEYSTORE = "serveurTLS.jks";
                char[] PASSWD_KEYSTORE = "azerty".toCharArray();
                FileInputStream ServerFK = new FileInputStream(FICHIER_KEYSTORE);
                ServerKs.load(ServerFK, PASSWD_KEYSTORE);

                SSLContext SslC = SSLContext.getInstance("TLSv1.3");

                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                char[] PASSWD_KEY = "azerty".toCharArray();
                kmf.init(ServerKs, PASSWD_KEY);

                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                tmf.init(ServerKs);

                SslC.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                SSLServerSocketFactory SslSFac = SslC.getServerSocketFactory();

                ssocket = SslSFac.createServerSocket(port);
            } catch (Exception e) {
                System.out.println("Erreur d : " + e);
            }
        }
        else {
            ssocket = new ServerSocket(port);
        }
    }
}
