package VESPAP;

import Tcp.Interface.Requete;

public class RequeteLOGOUT implements Requete
{
    private String login;

    public RequeteLOGOUT(String l) {
        login = l;
    }
    public String getLogin() {
        return login;
    }
}

