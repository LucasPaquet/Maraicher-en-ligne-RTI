package VESPAP;

import Tcp.Interface.Reponse;
import Tcp.Interface.Requete;
import VESPAP.Reponse.ReponseCrypte;
import VESPAP.Reponse.ReponseLOGIN;
import VESPAP.Requete.RequeteCrypte;
import VESPAP.Requete.RequeteHandshake;
import VESPAP.Requete.RequeteLOGIN;
import Crypto.MyCrypto;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class ClientVESPAPS {
    private Socket socket;
    private String login;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private final PublicKey publicKey;
    private SecretKey keySession;

    public ClientVESPAPS(String ip, int port) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException {
        oos = null;
        ois = null;

        try {
            socket = new Socket(ip, port);

            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

        } catch (IOException ex) {
            System.out.println("ERREUR IO : " + ex);
        }

        publicKey = RecupereClePubliqueServeur();
    }

    public boolean VESPAPS_Handshake() throws NoSuchAlgorithmException, NoSuchProviderException, CertificateException, IOException, KeyStoreException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        // Génération d'une clé de session
        Security.addProvider(new BouncyCastleProvider());
        KeyGenerator cleGen = KeyGenerator.getInstance("DES","BC");
        cleGen.init(new SecureRandom());
        SecretKey cleSession = cleGen.generateKey();
        System.out.println("Génération d'une clé de session : " + cleSession);

        keySession = cleSession; // sauvegarder dans la classe la cle de session


        // Cryptage asymétrique de la clé de session
        byte[] cleSessionCrypte;
        cleSessionCrypte = MyCrypto.CryptAsymRSA(publicKey,cleSession.getEncoded());
        System.out.println("Cryptage asymétrique de la clé de session : " + new String(cleSessionCrypte));

        // Construction de la requête
        RequeteHandshake requete = new RequeteHandshake();
        requete.setDataSession(cleSessionCrypte);

        oos.writeObject(requete);

        try {
            Object reponseCrypte = ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public boolean VESPAPS_Login(String log, String mdp){

        try {
            VESPAPS_Handshake();

            // Creation et envoie de la requete
            RequeteLOGIN requete = new RequeteLOGIN(log, mdp);

            // Convertion de la requete en byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream dos = new ObjectOutputStream(baos);

            dos.writeObject(requete);
            dos.flush();
            byte[] requeteClaire = baos.toByteArray();

            // Cryptage de la requete (qui est en byte[])
            RequeteCrypte requeteCrypte = new RequeteCrypte(MyCrypto.CryptSymDES(keySession,requeteClaire));

            // Envoie de la requete crypte
            oos.writeObject(requeteCrypte);

            // Réception réponse
            ReponseCrypte reponseCrypte = (ReponseCrypte) ois.readObject();
            if (reponseCrypte == null)
                System.out.println("je suis null");

            ReponseLOGIN reponse = (ReponseLOGIN) TraiteRequeteCrypte(reponseCrypte);


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
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private synchronized Reponse TraiteRequeteCrypte(ReponseCrypte requete)
    {
        try {
            // Décryptage du message
            byte[] messageDecrypte;
            messageDecrypte = MyCrypto.DecryptSymDES(keySession,requete.getData());

            // Récupération des données claires
            ByteArrayInputStream bais = new ByteArrayInputStream(messageDecrypte);
            ObjectInputStream dis = new ObjectInputStream(bais);
            Reponse requeteDecrypt = (Reponse) dis.readObject();
            return requeteDecrypt;

        }catch (Exception e){
            System.out.println("Erreur : " + e);
        }
        return null;
    }

    public static PublicKey RecupereClePubliqueServeur() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        // Récupération de la clé publique de Jean-Marc dans le keystore de Christophe
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("client.jks"),"azerty".toCharArray());
        X509Certificate certif = (X509Certificate)ks.getCertificate("serveur");
        PublicKey cle = certif.getPublicKey();
        return cle;
    }

    public boolean IsOosNull() {
        return oos == null;
    }
}
