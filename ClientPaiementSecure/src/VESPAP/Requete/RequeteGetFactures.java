package VESPAP.Requete;

import Tcp.Interface.Requete;

public class RequeteGetFactures implements Requete {
    private int idClient;

    public RequeteGetFactures(int idClient) {

        this.idClient = idClient;
    }

    public int getIdClient() {
        return idClient;
    }
}
