package VESPAP.Reponse;

import Tcp.Interface.Reponse;

public class ReponseLOGINId implements Reponse
{
    private boolean valide;
    private int idClient;

    public ReponseLOGINId(boolean v, int id) {
        valide = v;
        idClient = id;
    }
    public boolean isValide() {
        return valide;
    }

    public int getIdClient() {
        return idClient;
    }
}
