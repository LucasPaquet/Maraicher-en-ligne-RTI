package VESPAP;

import JDBC.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    public List<Facture> getFacture(int idClient){
        List<Facture> rep = null;
        try {
            ResultSet rs = dbConnect.executeQuery("select * from factures where id = " + idClient + ";");
            while (rs.next()) { // Si au moins une ligne correspond
                rep.add(new Facture(rs.getInt("id"),
                        rs.getInt("idClient"),
                        rs.getFloat("prix"),
                        rs.getTimestamp("date"),
                        rs.getBoolean("paye")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return rep;
    }


}
