package VESPAP.Reponse;

import java.util.List;
import Tcp.Interface.Reponse;
import VESPAP.Facture;

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
