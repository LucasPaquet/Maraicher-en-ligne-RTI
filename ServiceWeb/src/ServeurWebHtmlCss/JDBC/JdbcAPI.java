package ServeurWebHtmlCss.JDBC;

import ServeurWebHtmlCss.Article;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcAPI {
    DatabaseConnection dbConnect;

    public JdbcAPI(DatabaseConnection dbConnect) {
        this.dbConnect = dbConnect;
    }

    public int checkLogin(String login, String mdp){

        try {
            ResultSet rs = dbConnect.executeQuery("select * from employes where nom LIKE '" + login + "' AND mdp LIKE '" + mdp + "';");
            if (rs.next()) { // Si au moins une ligne correspond
                return 0;
            } else { // Aucune correspondance trouvée
                return -1;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public List<Article> getArticles(){
        List<Article> articles = new ArrayList<>();
        try {
            ResultSet rs = dbConnect.executeQuery("select * from articles;");
            while (rs.next()) { // Si au moins une ligne correspond
                articles.add(new Article(rs.getInt("id"),
                        rs.getString("intitule"),
                        rs.getDouble("prix"),
                        rs.getInt("stock"),
                        rs.getString("image")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return articles;
    }

    public boolean updateArticle(int idArticle, String prix, String stock) {

        try {
            int result = dbConnect.executeUpdate("update articles set prix = "+ prix +", stock = " + stock + " where id = " + idArticle + ";");
            return result == 1;

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return false;

    }


}
