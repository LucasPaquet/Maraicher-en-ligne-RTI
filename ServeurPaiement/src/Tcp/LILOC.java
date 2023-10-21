package Tcp;

import java.net.Socket;
import java.util.HashMap;

public class LILOC implements Protocole
{
    private HashMap<String,String> passwords;
    private HashMap<String,Socket> clientsConnectes;

    private Logger logger;

    public LILOC(Logger log)
    {
        passwords = new HashMap<>();
        passwords.put("wagner","abcd");
        passwords.put("charlet","1234");
        passwords.put("calmant","azerty");

        logger = log;

        clientsConnectes = new HashMap<>();
    }

    public LILOC() {
        passwords = new HashMap<>();
        passwords.put("wagner","abcd");
        passwords.put("charlet","1234");
        passwords.put("calmant","azerty");
        logger = null;
        clientsConnectes = new HashMap<>();
    }

    @Override
    public String getNom()
    {
        return "LILOC";
    }
    @Override
    public synchronized Reponse TraiteRequete(Requete requete, Socket socket) throws
            FinConnexionException
    {
        if (requete instanceof RequeteLOGIN) return TraiteRequeteLOGIN((RequeteLOGIN)
                requete, socket);
        return null;
    }

    private synchronized ReponseLOGIN TraiteRequeteLOGIN(RequeteLOGIN requete, Socket
            socket) throws FinConnexionException
    {
        System.out.println("RequeteLOGIN reçue de " + requete.getLogin());
        String password = passwords.get(requete.getLogin());
        if (password != null)
            if (password.equals(requete.getPassword()))
            {
                String ipPortClient = socket.getInetAddress().getHostAddress() + "/"
                        + socket.getPort();
                System.out.println(requete.getLogin() + " correctement loggé de " +
                        ipPortClient);
                clientsConnectes.put(requete.getLogin(),socket);
                return new ReponseLOGIN(true);
            }
        System.out.println(requete.getLogin() + " --> erreur de login");
        throw new FinConnexionException(new ReponseLOGIN(false));
    }


}

