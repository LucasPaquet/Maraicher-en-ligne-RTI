package ServeurWebHtmlCss.JDBC;

import java.sql.*;
import java.util.Hashtable;

public class DatabaseConnection {
    private Connection connection;

    public static final String MYSQL = "MySql";

    private static Hashtable<String,String> drivers;

    static
    {
        drivers = new Hashtable<>();
        drivers.put(MYSQL,"com.mysql.cj.jdbc.Driver");
    }

    /**
     * Constructeur qui permet de se connecter à la base de données SQL
     * @param type Type de base de données (DatabaseConnection.MYSQL)
     * @param server Ip du serveur
     * @param dbName Nom de la base de données à utiliser
     * @param user L'utilisateur
     * @param password Le mot de passe de l'utilisateur
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public DatabaseConnection(String type, String server, String dbName, String user, String password) throws ClassNotFoundException, SQLException{
        // Chargement du driver
        Class leDriver = Class.forName(drivers.get(type));

        // Création de l'URL
        String url = null;
        switch(type)
        {
            case MYSQL: url = "jdbc:mysql://" + server + "/" + dbName;
                break;
        }

        // Connexion à la BD

        connection = DriverManager.getConnection(url,user,password);

    }

    /**
     * Permet d'exécuter un SELECT dans la base de données
     * @param sql La requête SQL que l'on veut exécuter
     * @return Résultat de la requête SQL
     * @throws SQLException
     */
    public synchronized ResultSet executeQuery(String sql) throws SQLException
    {
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    /**
     * Permet d'exécuter un UPDATE dans la base de données
     * @param sql La requête SQL que l'on veut exécuter
     * @return Retourne le nombre de ligne affecté par la requête
     * @throws SQLException
     */
    public synchronized int executeUpdate(String sql) throws SQLException
    {
        Statement statement = connection.createStatement();
        return statement.executeUpdate(sql);
    }

    /**
     * Ferme la connexion au serveur SQL
     * @throws SQLException
     */
    public synchronized void close() throws SQLException
    {
        if (connection != null && !connection.isClosed())
            connection.close();
    }
}

