package VESPAP;

import JDBC.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;

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
}
