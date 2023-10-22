package VESPAP;

import java.sql.Timestamp;
import java.util.Objects;

public class Facture {
    private int idFacture;
    private int idClient;
    private float prix;
    private Timestamp date;
    private boolean paye;

    public Facture(int idFacture, int idClient, float prix, Timestamp date, boolean paye) {
        this.idFacture = idFacture;
        this.idClient = idClient;
        this.prix = prix;
        this.date = date;
        this.paye = paye;
    }

    public Facture() {
    }

    public int getIdFacture() {
        return idFacture;
    }

    public void setIdFacture(int idFacture) {
        this.idFacture = idFacture;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public boolean isPaye() {
        return paye;
    }

    public void setPaye(boolean paye) {
        this.paye = paye;
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
