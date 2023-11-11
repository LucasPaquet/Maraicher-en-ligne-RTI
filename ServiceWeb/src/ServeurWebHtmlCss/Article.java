package ServeurWebHtmlCss;

public class Article {
    private int idArticle;
    private String nomArticle;
    private double prix;
    private int quantite;
    private String image;

    public Article() {
    }

    public Article(int idArticle, String nomArticle, double prix, int quantite, String image) {
        this.idArticle = idArticle;
        this.nomArticle = nomArticle;
        this.prix = prix;
        this.quantite = quantite;
        this.image = image;
    }

    public int getIdArticle() {
        return idArticle;
    }

    public void setIdArticle(int idArticle) {
        this.idArticle = idArticle;
    }

    public String getNomArticle() {
        return nomArticle;
    }

    public void setNomArticle(String nomArticle) {
        this.nomArticle = nomArticle;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Article{" +
                "idArticle=" + idArticle +
                ", nomArticle='" + nomArticle + '\'' +
                ", prix=" + prix +
                ", quantite=" + quantite +
                ", image='" + image + '\'' +
                '}';
    }
}
