package VESPAP;

import JDBC.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class JdbcVESPAP {
    DatabaseConnection dbConnect;

    public JdbcVESPAP(DatabaseConnection dbConnect) {
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
    public String getMdp(String login){
        try {
            ResultSet rs = dbConnect.executeQuery("select mdp from clients where nom LIKE '" + login + "';");
            while (rs.next()) { // Si au moins une ligne correspond

                return rs.getString("mdp");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    public int getIdClient(String login){
        try {
            ResultSet rs = dbConnect.executeQuery("select id from clients where nom LIKE '" + login + "';");
            while (rs.next()) { // Si au moins une ligne correspond

                return rs.getInt("id");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    public List<Facture> getFacture(int idClient){
        List<Facture> factures = new ArrayList<>();
        try {
            ResultSet rs = dbConnect.executeQuery("select * from factures where idClient = " + idClient + " and paye = false;");
            while (rs.next()) { // Si au moins une ligne correspond

                factures.add(new Facture(rs.getInt("id"),
                        rs.getInt("idClient"),
                        rs.getFloat("prix"),
                        rs.getTimestamp("date"),
                        rs.getBoolean("paye")));


            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return factures;
    }

    public boolean payFacture(int idFacture){

        try {
            int result = dbConnect.executeUpdate("update factures set paye = true where id = " + idFacture + ";");
            return result == 1;

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return false;

    }

    public List<Vente> getVente(int idFacture){
        List<Vente> ventes = new ArrayList<>();
        try {
            ResultSet rs = dbConnect.executeQuery("select * from ventes where idFacture = " + idFacture + ";");
            while (rs.next()) { // Si au moins une ligne correspond
                ventes.add(new Vente(rs.getInt("idArticle"), rs.getInt("quantite")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ventes;
    }


}
