package VESPAP.Requete;

import Tcp.Interface.Requete;

import java.util.Date;

public class RequeteLOGINDigest implements Requete
{
    private String login;
    private byte[] digest;
    long temps ;
    double nbRandom;
    public RequeteLOGINDigest(String l) {
        login = l;
        temps = new Date().getTime();
        nbRandom = Math.random();
    }



    public String getLogin() {
        return login;
    }
    public byte[] getPassword() {
        return digest;
    }

    public long getTemps() {
        return temps;
    }

    public double getNbRandom() {
        return nbRandom;
    }
    public void setDigest(byte[] digest) {
        this.digest = digest;
    }
}

