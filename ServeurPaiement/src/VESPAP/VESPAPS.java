package VESPAP;

import JDBC.DatabaseConnection;
import Tcp.FinConnexionException;
import Tcp.Interface.Protocole;
import Tcp.Interface.Reponse;
import Tcp.Interface.Requete;
import VESPAP.Reponse.*;
import VESPAP.Requete.*;
import crypto.MyCrypto;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;

public class VESPAPS implements Protocole {
    JdbcVESPAP db;
    private final HashMap<String, Socket> clientsConnectes;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private SecretKey keySession;

    public VESPAPS(DatabaseConnection dbc) throws UnrecoverableKeyException, CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException {
        clientsConnectes = new HashMap<>();
        db = new JdbcVESPAP(dbc);
        privateKey = RecupereClePriveeServeur();
        publicKey = RecupereClePubliqueClient();
    }

    @Override
    public String getNom()
    {
        return "VESPAP";
    }
    @Override
    public synchronized Reponse TraiteRequete(Requete requete, Socket socket) throws FinConnexionException
    {
        Reponse reponse = null;
        System.out.println("[SERVEUR] Requete de type " + requete.getClass() + " recu");

        if (requete instanceof RequeteCrypte) {
            requete = TraiteRequeteCrypte((RequeteCrypte) requete);
        }

        if (requete instanceof RequeteLOGINDigest)
            reponse = TraiteRequeteLOGIN((RequeteLOGINDigest) requete, socket);
        if (requete instanceof RequeteLOGOUT)
            reponse = TraiteRequeteLOGOUT((RequeteLOGOUT) requete);
        if (requete instanceof RequeteGetFacturesSignature)
            reponse = TraiteRequeteGetFactures((RequeteGetFacturesSignature) requete);
        if (requete instanceof RequetePayFactures)
            reponse = TraiteRequetePayFactures((RequetePayFactures) requete);
        if (requete instanceof RequeteGetVente)
            reponse = TraiteRequeteGetVente((RequeteGetVente) requete);
        if (requete instanceof RequeteHandshake)
            return TraiteRequeteHandshake((RequeteHandshake) requete); // TODO : repondre ok avec le message crypte

        reponse = CrypteReponse(reponse); // crypte la reponse clair

        if (reponse == null)
        {
            System.out.println("je usi snyu");
        }
        return reponse;
    }



    private synchronized ReponseLOGIN TraiteRequeteLOGIN(RequeteLOGINDigest requete, Socket socket)
    {
        try {
            String mdp = db.getMdp(requete.getLogin());
            System.out.println("MDP : " + mdp);
            // Construction du digest local
            MessageDigest md = MessageDigest.getInstance("SHA-1","BC");

            md.update(requete.getLogin().getBytes());
            md.update(mdp.getBytes());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeLong(requete.getTemps());
            dos.writeDouble(requete.getNbRandom());

            md.update(baos.toByteArray());
            byte[] digestLocal = md.digest();

            if (MessageDigest.isEqual(requete.getPassword(), digestLocal)){
                return new ReponseLOGIN(true);
            }
            else
                return new ReponseLOGIN(false);


        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }

    }

    private synchronized ReponseLogout TraiteRequeteLOGOUT(RequeteLOGOUT requete)//  throws FinConnexionException
    {
        clientsConnectes.remove(requete.getLogin());
        System.out.println("[SERVEUR] " + requete.getLogin() + " correctement déloggé");
        // throw new FinConnexionException(null);
        return new ReponseLogout(true);
    }

    private synchronized ReponseGetFactures TraiteRequeteGetFactures(RequeteGetFacturesSignature requete)
    {
        List<Facture> factures;

        if (VerifySignature(requete)) {
            factures = db.getFacture(requete.getIdClient());

            System.out.println("[SERVEUR] Factures : " + factures + "\n");

            return new ReponseGetFactures(factures);
        }
        return null;
    }

    private synchronized ReponsePayFacturesHMAC TraiteRequetePayFactures(RequetePayFactures requete)
    {
        boolean result = false;
        if (isValidCreditCardNumber(requete.getNumVisa())){ // si la carte VISA est valide
            result = db.payFacture(requete.getIdFacture());
        }
        System.out.println("[SERVEUR] Reponse : " + result + "\n");

        ReponsePayFacturesHMAC res = new ReponsePayFacturesHMAC(result);
        res.setHmac(GenerateHmac(res));

        return res;
    }

    private synchronized ReponseGetVente TraiteRequeteGetVente(RequeteGetVente requete)
    {
        List<Vente> ventes;

        ventes = db.getVente(requete.getIdFacture());

        System.out.println("[SERVEUR] Ventes : " + ventes + "\n");

        return new ReponseGetVente(ventes);
    }

    private synchronized ReponseLogout TraiteRequeteHandshake(RequeteHandshake requete)
    {
        try {
            byte[] cleSessionDecryptee;

            System.out.println("Clé session cryptée reçue = ");
            System.out.println(new String(requete.getDataSession()));

            cleSessionDecryptee = MyCrypto.DecryptAsymRSA(privateKey,requete.getDataSession());
            SecretKey cleSession = new SecretKeySpec(cleSessionDecryptee,"DES");
            System.out.println("Decryptage asymétrique de la clé de session...");
            System.out.println(cleSession);
            keySession = cleSession;
            System.out.println("Crypte : " + requete.getDataSession());
        }catch (Exception e){
            System.out.println("Erreur : " + e);
        }
        return new ReponseLogout(true);
    }

    private synchronized Requete TraiteRequeteCrypte(RequeteCrypte requete)
    {
        try {
            // Décryptage du message
            byte[] messageDecrypte;
            messageDecrypte = MyCrypto.DecryptSymDES(keySession,requete.getData());

            // Récupération des données claires
            ByteArrayInputStream bais = new ByteArrayInputStream(messageDecrypte);
            ObjectInputStream dis = new ObjectInputStream(bais);
            Requete requeteDecrypt = (Requete) dis.readObject();
            System.out.println("Je viens de le tranformer en : " + requeteDecrypt.getClass());
            return requeteDecrypt;

        }catch (Exception e){
            System.out.println("Erreur : " + e);
        }
        return null;
    }

    private Reponse CrypteReponse(Reponse reponseClaire) {
        try {
            // Convertion de la requete en byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream dos = new ObjectOutputStream(baos);

            dos.writeObject(reponseClaire);
            dos.flush();
            byte[] requeteClaire = baos.toByteArray();

            // Cryptage de la requete (qui est en byte[])
            ReponseCrypte reponseCrypte = new ReponseCrypte(MyCrypto.CryptSymDES(keySession,requeteClaire));

            return reponseCrypte;
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
    /**
     * Vérifie si le string passé en paramètre est un numéro de carte Visa valide (basé sur l'algorithme de Luhn)
     * @author Modifié par Lucas Paquet : <a href="https://www.buildingjavaprograms.com/code_files/5ed/gui/CreditCardGUI.java">source</a>
     * @param numVisa Chaîne de caractère que l'on veut vérifier
     * @return True = valide, false invalide
     */
    public boolean isValidCreditCardNumber(String numVisa) {
        if (!numVisa.startsWith("4") || numVisa.length() != 16) { // toute les cartes visa commence par 1 et ont 16 chiffres
            return false;
        }

        int sum = 0;
        for (int i = 0; i < numVisa.length(); i++) {
            int digit = Integer.parseInt(numVisa.substring(i, i + 1));
            if (i % 2 == 0) {
                digit *= 2;
                sum += (digit / 10) + (digit % 10);
            } else {
                sum += digit;
            }
        }
        return (sum % 10 == 0);
    }
    // *************************** METHODE RECUPERATION CLE ******************************************
    public static PrivateKey RecupereClePriveeServeur() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        // Récupération de la clé privée de Jean-Marc dans le keystore de Jean-Marc
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("serveur.jks"),"azerty".toCharArray());
        PrivateKey cle = (PrivateKey) ks.getKey("serveur","azerty".toCharArray());
        System.out.println("*** Cle privee generee = " + cle);

        return cle;
    }

    public static PublicKey RecupereClePubliqueClient() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        // Récupération de la clé publique de Jean-Marc dans le keystore de Christophe
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("serveur.jks"),"azerty".toCharArray());
        X509Certificate certif = (X509Certificate)ks.getCertificate("client");
        PublicKey cle = certif.getPublicKey();
        return cle;
    }

    // *************************** METHODE D'AUTH et INTEGRITE *******************************

    public boolean VerifySignature(RequeteGetFacturesSignature requete)
    {
        try {
            // Construction de l'objet Signature
            Signature s = Signature.getInstance("SHA1withRSA","BC");
            s.initVerify(publicKey);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeInt(requete.getIdClient());
            s.update(baos.toByteArray());

            // Vérification de la signature reçue
            return s.verify(requete.getSignature());

        } catch (SignatureException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
    private byte[] GenerateHmac(ReponsePayFacturesHMAC requete){
        try {
            // Construction du HMAC
            Mac hm = Mac.getInstance("HMAC-MD5","BC");
            hm.init(keySession);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeBoolean(requete.isPaid());
            hm.update(baos.toByteArray());

            return hm.doFinal();
        }
        catch (Exception e){
            System.out.println("Erreur :" + e);
        }
        return null;
    }
}
