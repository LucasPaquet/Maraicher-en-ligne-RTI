package VESPAP.Requete;

import Tcp.Interface.Requete;

public class RequeteGetVente implements Requete {
    private int idFacture;

    public RequeteGetVente(int idFact) {

        this.idFacture = idFact;
    }

    public int getIdFacture() {
        return idFacture;
    }
}
