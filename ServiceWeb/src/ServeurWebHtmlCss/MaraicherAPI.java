package ServeurWebHtmlCss;

import ServeurWebHtmlCss.JDBC.DatabaseConnection;
import ServeurWebHtmlCss.JDBC.JdbcAPI;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaraicherAPI implements HttpHandler{
    private static List<Article> articles = new ArrayList<>();
    private final JdbcAPI db;

    public MaraicherAPI() {
        DatabaseConnection dbConnect;
        try {
            dbConnect = new DatabaseConnection(DatabaseConnection.MYSQL,
                    "10.222.23.184",
                    "PourStudent",
                    "Student",
                    "PassStudent1_");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        db = new JdbcAPI(dbConnect);
        System.out.println("MaraicherAPI : API demarre");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        // CORS (Cross-Origin Resource Sharing), autoriser les verbe
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, PUT");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "ContentType");

        articles = db.getArticles();
        String requestMethod = exchange.getRequestMethod();
        System.out.println("DEBUG : " + requestMethod);
        if (requestMethod.equalsIgnoreCase("GET"))
        {
            System.out.println("--- Requête GET reçue (obtenir la liste) ---");
            // Récupérer la liste des tâches au format JSON
            String response = convertTasksToJson();
            sendResponse(exchange, 200, response);
        }
        else if (requestMethod.equalsIgnoreCase("PUT"))
        {
            System.out.println("--- Requête PUT reçue (mise a jour) ---");
            // Mettre à jour une tâche existante
            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());

            if (queryParams.containsKey("id"))
            {
                int taskId = Integer.parseInt(queryParams.get("id"));
                System.out.println("Mise a jour tache id=" + taskId);

                String requestBody = readRequestBody(exchange);
                System.out.println("requestBody = " + requestBody);
                String[] values = requestBody.split("&"); // on parse sur le '&' car dans js on ajoute un '&' entre les deux valeur

                if (!updateTask(taskId, values[0], values[1])){
                    sendResponse(exchange, 500, "Erreur interne au serveur");
                    return;
                }


                sendResponse(exchange, 200, "Article mise a jour avec succes");
            }
            else sendResponse(exchange, 400, "ID de tache manquant dans les parametres");
        }
        else if (requestMethod.equalsIgnoreCase("OPTIONS")) { // les browser envoie d'abord une requette options pour savoir ce qui est autorisé ou pas donc oblige de handle
            // Répondre aux requêtes OPTIONS pour permettre CORS
            exchange.sendResponseHeaders(200, -1);
        }
        else sendResponse(exchange, 405, "Methode non autorisee");
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException
    {
        System.out.println("Envoi de la réponse (" + statusCode + ") : --" + response
                + "--");
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    private static String readRequestBody(HttpExchange exchange) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
        {
            requestBody.append(line);
        }
        reader.close();
        return requestBody.toString();
    }
    private static Map<String, String> parseQueryParams(String query)
    {
        Map<String, String> queryParams = new HashMap<>();
        if (query != null)
        {
            String[] params = query.split("&");
            for (String param : params)
            {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2)
                {
                    queryParams.put(keyValue[0], keyValue[1]);
            }
            }
        }
        return queryParams;
    }
    private static String convertTasksToJson()
    {
        // Convertir la liste des tâches en format JSON
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < articles.size(); i++)
        {
            json.append("{\"id\": ")
                    .append(i + 1)
                    .append(", \"intitule\":\"").append(articles.get(i).getNomArticle())
                    .append("\", \"prix\": ").append(articles.get(i).getPrix())
                    .append(", \"stock\":").append(articles.get(i).getQuantite())
                    .append(", \"image\":\"").append(articles.get(i).getImage())
                    .append("\"}");
            if (i < articles.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }
    private boolean updateTask(int taskId, String prix, String stock)
    {
        if (taskId >= 1 && taskId <= articles.size())
        {
            return db.updateArticle(taskId, prix, stock);
        }
        return false;
    }

}
