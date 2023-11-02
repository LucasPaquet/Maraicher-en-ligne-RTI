package VESPAP;

import VESPAP.Reponse.*;
import VESPAP.Requete.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientVESPAP {
    private Socket socket;
    private String login;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public ClientVESPAP(String ip, int port) {
        oos = null;
        ois = null;

        try {
            socket = new Socket(ip, port);

            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

        } catch (IOException ex) {
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
                System.out.println("[CLIENT] Je suis PAS connecté :(");
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
            ReponseLogout reponse = (ReponseLogout) ois.readObject();
        }
        catch (IOException | ClassNotFoundException ex) {
            System.out.println("ERREUR : " + ex);
        }
    }

    public List<Facture> VESPAP_GetFactures(int idClient){

        try {

            // Creation et envoie de la requete
            RequeteGetFactures requete = new RequeteGetFactures(idClient);
            System.out.println("envoie requete");
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

    public boolean IsOosNull() {
        return oos == null;
    }
}
