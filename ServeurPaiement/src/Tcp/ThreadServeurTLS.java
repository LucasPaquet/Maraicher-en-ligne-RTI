package Tcp;

import Tcp.Interface.Protocole;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.KeyStore;

/**
 * Au sein du processus serveur, un thread sera dédié à l’acception des demandes de
 * connexion par les clients et ces « connexions établies » (les sockets de service) seront
 * transmises aux threads clients
 */
public abstract class ThreadServeurTLS extends Thread
{
    // private final javax.net.ssl.SSLSocket SSLSocket;
    protected int port;
    protected Protocole protocole;
    protected SSLServerSocket ssocket;

    public ThreadServeurTLS(int port, Protocole protocole) {

        super("TH Serveur (port=" + port + ",protocole=" + protocole.getNom() + ")");

        this.port = port;
        this.protocole = protocole;

        try {
            System.out.println("DEBUG : " + "TH Serveur (port=" + port + ",protocole=" + protocole.getNom() + ")");

            KeyStore ServerKs = KeyStore.getInstance("JKS");

            String FICHIER_KEYSTORE = "serveurTLS.jks";

            char[] PASSWD_KEYSTORE = "azerty".toCharArray();

            FileInputStream ServerFK = new FileInputStream (FICHIER_KEYSTORE);

            ServerKs.load(ServerFK, PASSWD_KEYSTORE);


            SSLContext SslC = SSLContext.getInstance("TLSv1.2");

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            char[] PASSWD_KEY = "azerty".toCharArray();
            kmf.init(ServerKs, PASSWD_KEY);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509"); tmf.init(ServerKs);


            SslC.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            SSLServerSocketFactory SslSFac= SslC.getServerSocketFactory();

            ssocket = (SSLServerSocket) SslSFac.createServerSocket(port);
        }catch (Exception e){
            System.out.println("Erreur d : " + e);
        }
        // Version sécurisée
        // ssocket = (SSLSocket) SslSSocket.accept();

        //ssocket = new ServerSocket(port);
    }
}
