package Tcp;

import java.io.IOException;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
public class ServeurLILOC implements Logger{
    ThreadServeur threadServeur;

    public ServeurLILOC() {
        threadServeur = null;
        try
        {
            Protocole protocole = new LILOC();
            int port = 50000;
            int taillePool = 5;

            System.out.println("[SERVER] Lancement du pool");
            threadServeur = new ThreadServeurPool(port,protocole,taillePool);

            threadServeur.start();

        }
        catch (NumberFormatException ex)
        {
            System.out.println("ERREUR 1");
        }
        catch (IOException ex)
        {
            System.out.println("ERREUR 2");
        }
    }

    @Override
    public void Trace(String message) {

    }

    public static void main(String[] args) {


    }
}
