package VESPAP;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

public class Facture implements Serializable {
    private final int idFacture;
    private final int idClient;
    private final float prix;
    private final Timestamp date;
    private final boolean paye;

    public Facture(int idFacture, int idClient, float prix, Timestamp date, boolean paye) {
        this.idFacture = idFacture;
        this.idClient = idClient;
        this.prix = prix;
        this.date = date;
        this.paye = paye;
    }



    public int getIdFacture() {
        return idFacture;
    }

    public int getIdClient() {
        return idClient;
    }

    public float getPrix() {
        return prix;
    }

    public Timestamp getDate() {
        return date;
    }

    public boolean isPaye() {
        return paye;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Facture facture = (Facture) o;
        return idFacture == facture.idFacture && idClient == facture.idClient && Float.compare(facture.prix, prix) == 0 && paye == facture.paye && Objects.equals(date, facture.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFacture, idClient, prix, date, paye);
    }

    @Override
    public String toString() {
        return "Facture{" +
                "idFacture=" + idFacture +
                ", idClient=" + idClient +
                ", prix=" + prix +
                ", date=" + date +
                ", paye=" + paye +
                '}';
    }
}
