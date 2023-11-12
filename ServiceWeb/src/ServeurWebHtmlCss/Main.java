package ServeurWebHtmlCss;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
public class Main
{
    public static void main(String[] args)
    {
        HttpServer serveur;
        try
        {
            serveur = HttpServer.create(new InetSocketAddress(8080),0);
            serveur.createContext("/",new HandlerHtml());
            serveur.createContext("/css",new HandlerCss());
            serveur.createContext("/js", new HandlerJs());
            serveur.createContext("/api/tasks", new MaraicherAPI());
            /*
            serveur.createContext("/images",new HandlerImages());
            serveur.createContext("/pdfs",new HandlerPdfs());
             */
            System.out.println("Demarrage du serveur HTTP...");
            serveur.start();
        }
        catch (IOException e)
        {
            System.out.println("Erreur: " + e.getMessage());
        }
    }
}
