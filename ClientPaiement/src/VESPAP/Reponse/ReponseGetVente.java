package VESPAP.Reponse;

import Tcp.Interface.Reponse;
import VESPAP.Vente;

import java.util.List;

public class ReponseGetVente implements Reponse{
    private List<Vente> ventes;

    public ReponseGetVente(List<Vente> ventesList) {
        ventes = null;
        this.ventes = ventesList;
    }

    public List<Vente> getVente() {
        return ventes;
    }
}
