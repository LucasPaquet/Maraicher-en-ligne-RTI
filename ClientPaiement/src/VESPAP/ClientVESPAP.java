package VESPAP;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
            System.out.println("ERREUR 2");
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
        }
        catch (IOException ex) {
            System.out.println("ERREUR : " + ex);
        }


    }

}
