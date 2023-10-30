import JDBC.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try
        {
            DatabaseConnection dbConnect;
            dbConnect = new DatabaseConnection(DatabaseConnection.MYSQL,
                    "192.168.28.128",
                    "PourStudent",
                    "Student",
                    "PassStudent1_");
            System.out.println("Connecté");


            // Exécution d'une requête de sélection
            String requete = "select * from clients;";
            ResultSet rs = dbConnect.executeQuery(requete);



            while(rs.next())
                System.out.println("Nom = " + rs.getString("nom"));

            // Exécution d'une requête de mise à jour
            requete = "UPDATE clients set mdp = \"oui\" where id = 3;";
            int nbLignes = dbConnect.executeUpdate(requete);

            System.out.println("nbLignes = " + nbLignes);

            // Fermeture de la connexion
            rs.close();
            dbConnect.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException ex)
        {
            System.out.println("Erreur DatabaseConnection: " + ex.getMessage());
        }

    }
}