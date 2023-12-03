package VESPAP;

import JDBC.DatabaseConnection;
import Tcp.FinConnexionException;
import Tcp.Interface.Protocole;
import Tcp.Interface.Reponse;
import Tcp.Interface.Requete;
import VESPAP.Reponse.*;
import VESPAP.Requete.*;
import crypto.MyCrypto;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.List;

public class VESPAPS implements Protocole {
    JdbcVESPAP db;
    private final HashMap<String, Socket> clientsConnectes;
    private final PrivateKey privateKey;
    private SecretKey keySession;

    public VESPAPS(DatabaseConnection dbc) throws UnrecoverableKeyException, CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException {
        clientsConnectes = new HashMap<>();
        db = new JdbcVESPAP(dbc);
        privateKey = RecupereClePriveeServeur();
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



        if (requete instanceof RequeteCrypte) {
            requete = TraiteRequeteCrypte((RequeteCrypte) requete);
            System.out.println("[SERVEUR] Requete cr de type " + requete.getClass() + " recu");
        }

        System.out.println("[SERVEUR] Requete de type " + requete.getClass() + " recu");
        if (requete instanceof RequeteLOGIN)
            reponse = TraiteRequeteLOGIN((RequeteLOGIN) requete, socket);
        if (requete instanceof RequeteLOGOUT)
            reponse = TraiteRequeteLOGOUT((RequeteLOGOUT) requete);
        if (requete instanceof RequeteGetFactures)
            reponse = TraiteRequeteGetFactures((RequeteGetFactures) requete);
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



    private synchronized ReponseLOGIN TraiteRequeteLOGIN(RequeteLOGIN requete, Socket socket)
    {
        if (db.checkLogin(requete.getLogin(), requete.getPassword()) == 0){
            String ipPortClient = socket.getInetAddress().getHostAddress() + "/" + socket.getPort();

            System.out.println("[SERVEUR] " + requete.getLogin() + " correctement loggé de " + ipPortClient);
            clientsConnectes.put(requete.getLogin(),socket);
            return new ReponseLOGIN(true);
        }

        // Si pas logge
        System.out.println("[SERVEUR] " + requete.getLogin() + " --> erreur de login");
        return new ReponseLOGIN(false);
    }

    private synchronized ReponseLogout TraiteRequeteLOGOUT(RequeteLOGOUT requete)//  throws FinConnexionException
    {
        clientsConnectes.remove(requete.getLogin());
        System.out.println("[SERVEUR] " + requete.getLogin() + " correctement déloggé");
        // throw new FinConnexionException(null);
        return new ReponseLogout(true);
    }

    private synchronized ReponseGetFactures TraiteRequeteGetFactures(RequeteGetFactures requete)
    {
        List<Facture> factures;

        factures = db.getFacture(requete.getIdClient());

        System.out.println("[SERVEUR] Factures : " + factures + "\n");

        return new ReponseGetFactures(factures);
    }

    private synchronized ReponsePayFactures TraiteRequetePayFactures(RequetePayFactures requete)
    {
        boolean result = false;
        if (isValidCreditCardNumber(requete.getNumVisa())){ // si la carte VISA est valide
            result = db.payFacture(requete.getIdFacture());
        }
        System.out.println("[SERVEUR] Reponse : " + result + "\n");
        return new ReponsePayFactures(result);
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
        System.out.println("HDNAD");
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
            System.out.println("Je decrypte");
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

    public static PrivateKey RecupereClePriveeServeur() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        // Récupération de la clé privée de Jean-Marc dans le keystore de Jean-Marc
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("serveur.jks"),"azerty".toCharArray());
        PrivateKey cle = (PrivateKey) ks.getKey("serveur","azerty".toCharArray());
        System.out.println("*** Cle privee generee = " + cle);

        return cle;
    }
}
