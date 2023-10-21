package Tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientLILOC {
    private Socket socket;
    private String login;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public ClientLILOC() {
        oos = null;
        ois = null;

        String ipServeur = "127.0.0.1";
        int portServeur = 50000;
        String login = "wagner";
        String password = "abcd";

        try
        {
            System.out.println("[CLIENT] Connexion au serveur");
            socket = new Socket(ipServeur,portServeur);
            RequeteLOGIN requete = new RequeteLOGIN(login,password);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            oos.writeObject(requete);
            ReponseLOGIN reponse = (ReponseLOGIN) ois.readObject();
            if (reponse.isValide())
            {
                System.out.println("[CLIENT] Je suis connecté");
                this.login = login;
            }
            else
            {
                System.out.println("[CLIENT] Je suis PAS connecté :(");
                socket.close();
            }
        }
        catch (IOException | ClassNotFoundException ex)
        {
            System.out.println("ERREUR 2");
        }
    }

    public static void main(String[] args) {


    }

}
