package ServeurWebHtmlCss;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyStore;

public class Main
{
    public static void main(String[] args)
    {
        HttpServer serveur;
        HttpsServer serverHTTPS;

        try // ONLY HTTP
        {
            serveur = HttpServer.create(new InetSocketAddress(8080),0);
            serveur.createContext("/",new HandlerHtml());
            serveur.createContext("/css",new HandlerCss());
            serveur.createContext("/js", new HandlerJs());
            serveur.createContext("/api/tasks", new MaraicherAPI());
            serveur.createContext("/images",new HandlerImages());
            /*

            serveur.createContext("/pdfs",new HandlerPdfs());
             */
            System.out.println("Demarrage du serveur HTTP...");
            serveur.start();
        }
        catch (IOException e)
        {
            System.out.println("Erreur: HTTP" + e.getMessage());
        }

        try {   // ONLY HTTPS
            // Chargement du keystore
            char[] keystorePassword = "azerty".toCharArray();
            KeyStore keyStore = KeyStore.getInstance("JKS");
            FileInputStream fileInputStream = new FileInputStream("serveur.jks");
            keyStore.load(fileInputStream, keystorePassword);

            // Config gestionnaire de clés
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, keystorePassword);

            // Config gestion de confiance
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            serverHTTPS = HttpsServer.create(new InetSocketAddress(8443), 0);
            serverHTTPS.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                public void configure(HttpsParameters params) {
                    try {
                        // SSL pour le serveur
                        SSLContext context = getSSLContext();
                        SSLParameters sslParams = context.getDefaultSSLParameters();
                        sslParams.setNeedClientAuth(false);
                        params.setSSLParameters(sslParams);
                    } catch (Exception ex) {
                        System.out.println("Erreur de configuration SSL : " + ex);
                    }
                }
            });

            // Gestion des différentes routes de votre serveur HTTPS
            serverHTTPS.createContext("/", new HandlerHtml());
            serverHTTPS.createContext("/css", new HandlerCss());
            serverHTTPS.createContext("/js", new HandlerJs());
            serverHTTPS.createContext("/api/tasks", new MaraicherAPI());
            serverHTTPS.createContext("/images", new HandlerImages());

            System.out.println("Démarrage du serveur HTTPS...");
            serverHTTPS.start();
        } catch (Exception e) {
            System.out.println("Erreur de HTTPS: " + e.getMessage());
        }
    }
}
