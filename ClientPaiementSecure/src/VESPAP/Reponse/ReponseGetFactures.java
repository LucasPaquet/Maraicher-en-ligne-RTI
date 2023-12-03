package VESPAP.Reponse;

import Tcp.Interface.Reponse;
import VESPAP.Facture;

import java.util.List;

public class ReponseGetFactures implements Reponse{
    private List<Facture> factures;

    public ReponseGetFactures(List<Facture> facturesList) {
        factures = null;
        this.factures = facturesList;
    }

    public List<Facture> getFactures() {
        return factures;
    }
}
