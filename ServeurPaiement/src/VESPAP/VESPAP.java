package VESPAP;

import JDBC.DatabaseConnection;
import Tcp.FinConnexionException;
import Tcp.Interface.Protocole;
import Tcp.Interface.Reponse;
import Tcp.Interface.Requete;

import java.net.Socket;
import java.util.HashMap;

public class VESPAP implements Protocole
{
    JdbcVESPAP db;
    private HashMap<String,Socket> clientsConnectes;

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
    public synchronized Reponse TraiteRequete(Requete requete, Socket socket) throws
            FinConnexionException
    {
        if (requete instanceof RequeteLOGIN) return TraiteRequeteLOGIN((RequeteLOGIN) requete, socket);
        if (requete instanceof RequeteLOGOUT) TraiteRequeteLOGOUT((RequeteLOGOUT) requete);
        return null;
    }

    private synchronized ReponseLOGIN TraiteRequeteLOGIN(RequeteLOGIN requete, Socket socket) throws FinConnexionException
    {
        if (db.checkLogin(requete.getLogin(), requete.getPassword()) == 0){
            String ipPortClient = socket.getInetAddress().getHostAddress() + "/"
                    + socket.getPort();
            System.out.println(requete.getLogin() + " correctement loggé de " +
                    ipPortClient);
            clientsConnectes.put(requete.getLogin(),socket);
            return new ReponseLOGIN(true);
        }

        // Si pas logge
        System.out.println(requete.getLogin() + " --> erreur de login");
        return new ReponseLOGIN(false);
    }

    private synchronized void TraiteRequeteLOGOUT(RequeteLOGOUT requete)//  throws FinConnexionException
    {
        System.out.println("RequeteLOGOUT reçue de " + requete.getLogin());
        clientsConnectes.remove(requete.getLogin());
        System.out.println(requete.getLogin() + " correctement déloggé");
        // throw new FinConnexionException(null);
    }


}

