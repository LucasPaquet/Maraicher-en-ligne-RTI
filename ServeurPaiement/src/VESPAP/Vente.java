package VESPAP;

import java.io.Serializable;
import java.util.Objects;

public class Vente implements Serializable {
    private int idArticle;
    private int Quantite;

    public Vente(int idArticle, int quantite) {
        this.idArticle = idArticle;
        Quantite = quantite;
    }

    public int getIdArticle() {
        return idArticle;
    }

    public void setIdArticle(int idArticle) {
        this.idArticle = idArticle;
    }

    public int getQuantite() {
        return Quantite;
    }

    public void setQuantite(int quantite) {
        Quantite = quantite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vente vente = (Vente) o;
        return idArticle == vente.idArticle && Quantite == vente.Quantite;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idArticle, Quantite);
    }

    @Override
    public String toString() {
        return "Vente{" +
                "idArticle=" + idArticle +
                ", Quantite=" + Quantite +
                '}';
    }
}
