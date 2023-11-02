package VESPAP;

import JDBC.DatabaseConnection;
import Tcp.FinConnexionException;
import Tcp.Interface.Protocole;
import Tcp.Interface.Reponse;
import Tcp.Interface.Requete;
import VESPAP.Reponse.*;
import VESPAP.Requete.*;

import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class VESPAP implements Protocole
{
    JdbcVESPAP db;
    private final HashMap<String,Socket> clientsConnectes;

    public VESPAP(DatabaseConnection dbc) {
        clientsConnectes = new HashMap<>();
        db = new JdbcVESPAP(dbc);
    }

    @Override
    public String getNom()
    {
        return "VESPAP";
    }
    @Override
    public synchronized Reponse TraiteRequete(Requete requete, Socket socket) throws FinConnexionException
    {
        System.out.println("[SERVEUR] Requete de type " + requete.getClass() + " recu");
        if (requete instanceof RequeteLOGIN)
            return TraiteRequeteLOGIN((RequeteLOGIN) requete, socket);
        if (requete instanceof RequeteLOGOUT)
            return TraiteRequeteLOGOUT((RequeteLOGOUT) requete);
        if (requete instanceof RequeteGetFactures)
            return TraiteRequeteGetFactures((RequeteGetFactures) requete);
        if (requete instanceof RequetePayFactures)
            return TraiteRequetePayFactures((RequetePayFactures) requete);
        if (requete instanceof RequeteGetVente)
            return TraiteRequeteGetVente((RequeteGetVente) requete);
        return null;
    }

    private synchronized ReponseLOGIN TraiteRequeteLOGIN(RequeteLOGIN requete, Socket socket)
    {
        if (db.checkLogin(requete.getLogin(), requete.getPassword()) == 0){
            String ipPortClient = socket.getInetAddress().getHostAddress() + "/"
                    + socket.getPort();
            System.out.println("[SERVEUR] " + requete.getLogin() + " correctement loggé de " +
                    ipPortClient);
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


}

