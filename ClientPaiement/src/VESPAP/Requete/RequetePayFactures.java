package VESPAP.Requete;

import Tcp.Interface.Requete;

public class RequetePayFactures implements Requete {
    private int idFacture;
    private String nom;
    private String numVisa; // String car int ou long trop petit pour le nombre

    public RequetePayFactures(int idClient, String nom, String numVisa) {
        this.idFacture = idClient;
        this.nom = nom;
        this.numVisa = numVisa;
    }

    public int getIdFacture() {
        return idFacture;
    }

    public String getNom() {
        return nom;
    }

    public String getNumVisa() {
        return numVisa;
    }
}
