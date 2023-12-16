package VESPAP;

import VESPAP.Reponse.*;
import VESPAP.Requete.*;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.util.List;
import java.util.Properties;

public class ClientVESPAP {
    private Socket socket;
    private String login;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private int port;
    private int portTLS;
    private String ip;

    public ClientVESPAP(boolean tls) {
        initConfig();
        oos = null;
        ois = null;

        try {
            if (tls){
                KeyStore ServerKs = KeyStore.getInstance("JKS");

                String FICHIER_KEYSTORE = "client.jks";

                char[] PASSWD_KEYSTORE = "azerty".toCharArray();

                FileInputStream ServerFK = new FileInputStream(FICHIER_KEYSTORE);

                ServerKs.load(ServerFK, PASSWD_KEYSTORE);

                SSLContext SslC = SSLContext.getInstance("TLSv1.2");

                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                char[] PASSWD_KEY = "azerty".toCharArray();
                kmf.init(ServerKs, PASSWD_KEY);

                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509"); tmf.init(ServerKs);


                SslC.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

                SSLSocketFactory SslSFac = SslC.getSocketFactory();
                socket = SslSFac.createSocket(ip, portTLS);
            }else
            {
                socket = new Socket(ip, port);
            }

            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

        } catch (Exception ex) {
            System.out.println("ERREUR IO : " + ex);
        }


    }

    public boolean VESPAP_Login(String log, String mdp){

        try {

            // Creation et envoie de la requete
            RequeteLOGIN requete = new RequeteLOGIN(log, mdp);
            oos.writeObject(requete);

            // Réception réponse
            ReponseLOGIN reponse = (ReponseLOGIN) ois.readObject();

            if (reponse.isValide()) {
                System.out.println("[CLIENT] Je suis connecté");
                this.login = log;
                return true;
            } else {
                System.out.println("[CLIENT] Je suis PAS connecté");
                return false;
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("ERREUR 2" + ex);
        }
        return false;
    }

    public void VESPAP_Logout(){
        try {
            RequeteLOGOUT requetes = new RequeteLOGOUT(login);
            oos.writeObject(requetes);

            // Réception réponse
            ois.readObject();
        }
        catch (IOException | ClassNotFoundException ex) {
            System.out.println("ERREUR : " + ex);
        }
    }

    public List<Facture> VESPAP_GetFactures(int idClient){

        try {

            // Creation et envoie de la requete
            RequeteGetFactures requete = new RequeteGetFactures(idClient);
            oos.writeObject(requete);

            // Réception réponse
            ReponseGetFactures reponse = (ReponseGetFactures) ois.readObject();
            return reponse.getFactures();

        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("ERREUR 2" + ex);
        }
        return null;
    }

    public boolean VESPAP_PayFactures(int numFacture, String nom, String numVisa){
        try {

            // Creation et envoie de la requete
            RequetePayFactures requete = new RequetePayFactures(numFacture, nom, numVisa);
            System.out.println("envoie requete");
            oos.writeObject(requete);

            // Réception réponse
            ReponsePayFactures reponse = (ReponsePayFactures) ois.readObject();

            return reponse.isPaid();

        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("ERREUR 2" + ex);
        }

        return false;
    }

    public List<Vente> VESPAP_GetVente(int idFacture){

        try {

            // Creation et envoie de la requete
            RequeteGetVente requete = new RequeteGetVente(idFacture);
            System.out.println("envoie requete RequeteGetVente");
            oos.writeObject(requete);

            // Réception réponse
            ReponseGetVente reponse = (ReponseGetVente) ois.readObject();
            return reponse.getVente();

        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("ERREUR IOException : " + ex);
        }
        return null;
    }

    public boolean IsOosNull() {
        return oos == null;
    }
    public void initConfig() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("src/config.properties")) {
            properties.load(fis);

            port = Integer.parseInt(properties.getProperty("PORT_PAIEMENT"));
            portTLS = Integer.parseInt(properties.getProperty("PORT_PAIEMENT_TLS"));
            ip = properties.getProperty("IP_PAIEMENT");
        } catch (IOException e) {
            System.out.println("Erreur d'init config : " + e);
        }
    }
}
