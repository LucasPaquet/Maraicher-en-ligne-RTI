package VESPAP;

import Tcp.Interface.Reponse;
import Tcp.Interface.Requete;
import VESPAP.Reponse.*;
import VESPAP.Requete.*;
import Crypto.MyCrypto;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

public class ClientVESPAPS {
    private Socket socket;
    private String login;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private final PublicKey publicKey;
    private final PrivateKey privateKey;
    private SecretKey keySession;

    public ClientVESPAPS(String ip, int port) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
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
        privateKey = RecupereClePriveeClient();
    }

    // *********************** METHODE VESPAPS **********************************

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
            RequeteLOGINDigest requete = new RequeteLOGINDigest(log);

            byte[] digest = MakeDigest(requete, mdp);
            requete.setDigest(digest);



            // Cryptage de la requete
            RequeteCrypte requeteCrypte = ConvertToRequeteCrypte(requete);

            // Envoie de la requete crypte
            oos.writeObject(requeteCrypte);

            // Réception réponse
            ReponseCrypte reponseCrypte = (ReponseCrypte) ois.readObject();
            if (reponseCrypte == null)
                System.out.println("je suis null");

            // Decrypte la Reponse
            ReponseLOGIN reponse = (ReponseLOGIN) TraiteReponseCrypte(reponseCrypte);


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

    public void VESPAPS_Logout(){
        try {
            // Creation et envoie de la requete
            RequeteLOGOUT requete = new RequeteLOGOUT(login);

            // Cryptage de la requete
            RequeteCrypte requeteCrypte = ConvertToRequeteCrypte(requete);

            // Envoie de la requete crypte
            oos.writeObject(requeteCrypte);

            // Réception réponse
            ReponseCrypte reponseCrypte = (ReponseCrypte) ois.readObject();

        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("ERREUR 2" + ex);
        }
    }

    public List<Facture> VESPAPS_GetFactures(int idClient){

        try {
            // Creation et envoie de la requete
            RequeteGetFacturesSignature requete = new RequeteGetFacturesSignature(idClient);
            requete.setSignature(SignFacture(requete.getIdClient()));

            // Cryptage de la requete
            RequeteCrypte requeteCrypte = ConvertToRequeteCrypte(requete);

            // Envoie de la requete crypte
            oos.writeObject(requeteCrypte);

            // Réception reponse
            ReponseCrypte reponseCrypte = (ReponseCrypte) ois.readObject();

            // Decryptage de la reponse
            ReponseGetFactures reponse = (ReponseGetFactures) TraiteReponseCrypte(reponseCrypte);

            return reponse.getFactures();


        } catch (Exception ex) {
            System.out.println("ERREUR 2" + ex);
        }
        return null;
    }

    public boolean VESPAPS_PayFacture(int numFacture, String nom, String numVisa){
        try {
            // Creation et envoie de la requete
            RequetePayFactures requete = new RequetePayFactures(numFacture, nom, numVisa);

            // Cryptage de la requete
            RequeteCrypte requeteCrypte = ConvertToRequeteCrypte(requete);

            // Envoie de la requete crypte
            oos.writeObject(requeteCrypte);

            // Réception réponse
            ReponseCrypte reponseCrypte = (ReponseCrypte) ois.readObject();
            if (reponseCrypte == null)
                System.out.println("je suis null");

            // Decrypte la Reponse
            ReponsePayFacturesHMAC reponse = (ReponsePayFacturesHMAC) TraiteReponseCrypte(reponseCrypte);

            if (VerifyHmac(reponse))
                return reponse.isPaid();
            else
                return false;

        } catch (Exception ex) {
            System.out.println("ERREUR : " + ex);
        }
        return false;
    }

    public List<Vente> VESPAP_GetVente(int idFacture) {
        try {
            // Creation et envoie de la requete
            RequeteGetVente requete = new RequeteGetVente(idFacture);

            // Cryptage de la requete
            RequeteCrypte requeteCrypte = ConvertToRequeteCrypte(requete);

            // Envoie de la requete crypte
            oos.writeObject(requeteCrypte);

            // Réception reponse
            ReponseCrypte reponseCrypte = (ReponseCrypte) ois.readObject();

            // Decryptage de la reponse
            ReponseGetVente reponse = (ReponseGetVente) TraiteReponseCrypte(reponseCrypte);

            return reponse.getVente();


        } catch (Exception ex) {
            System.out.println("ERREUR : " + ex);
        }
        return null;
    }

    // ***************************** METHOD DECRYPT AND CRYPT ********************************
    private Reponse TraiteReponseCrypte(ReponseCrypte reponse)
    {
        try {
            // Décryptage du message
            byte[] messageDecrypte;
            messageDecrypte = MyCrypto.DecryptSymDES(keySession,reponse.getData());

            // Récupération des données claires
            ByteArrayInputStream bais = new ByteArrayInputStream(messageDecrypte);
            ObjectInputStream dis = new ObjectInputStream(bais);
            Reponse reponseDecrypt = (Reponse) dis.readObject();
            return reponseDecrypt;

        }catch (Exception e){
            System.out.println("Erreur : " + e);
        }
        return null;
    }
    private RequeteCrypte ConvertToRequeteCrypte(Requete requete){
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream dos = new ObjectOutputStream(baos);

            dos.writeObject(requete);
            dos.flush();
            byte[] requeteClaire = baos.toByteArray();

            // Cryptage de la requete (qui est en byte[])
            RequeteCrypte requeteCrypte = new RequeteCrypte(MyCrypto.CryptSymDES(keySession,requeteClaire));

            return requeteCrypte;
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    // *************************** METHODE D'AUTH et INTEGRITE *******************************
    private byte[] MakeDigest(RequeteLOGINDigest requete, String mdp) {


        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1","BC");

            md.update(requete.getLogin().getBytes());
            md.update(mdp.getBytes());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeLong(requete.getTemps());
            dos.writeDouble(requete.getNbRandom());

            md.update(baos.toByteArray());

            return md.digest();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] SignFacture(int idClient){
        try{
            // Construction de la signature
            Signature s = Signature.getInstance("SHA1withRSA","BC");
            s.initSign(privateKey);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeInt(idClient);
            s.update(baos.toByteArray());
            return s.sign();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
    private boolean VerifyHmac(ReponsePayFacturesHMAC reponse) {
        try {
            // Construction du HMAC local
            Mac hm = Mac.getInstance("HMAC-MD5","BC");
            hm.init(keySession);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeBoolean(reponse.isPaid());
            hm.update(baos.toByteArray());

            byte[] hmacLocal = hm.doFinal();

            // Comparaison HMAC reçu et HMAC local
            return MessageDigest.isEqual(reponse.getHmac(),hmacLocal);
        }catch (Exception e){
            System.out.println("Erreur : " + e);
            return false;
        }
    }

    // *************************** METHODE RECUPERATION CLE ******************************************

    public static PublicKey RecupereClePubliqueServeur() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        // Récupération de la clé publique de Jean-Marc dans le keystore de Christophe
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("client.jks"),"azerty".toCharArray());
        X509Certificate certif = (X509Certificate)ks.getCertificate("serveur");
        PublicKey cle = certif.getPublicKey();
        return cle;
    }

    public static PrivateKey RecupereClePriveeClient() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        // Récupération de la clé publique de Jean-Marc dans le keystore de Christophe
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("client.jks"),"azerty".toCharArray());
        PrivateKey cle = (PrivateKey) ks.getKey("client","azerty".toCharArray());
        return cle;
    }

    public boolean IsOosNull() {
        return oos == null;
    }


}
