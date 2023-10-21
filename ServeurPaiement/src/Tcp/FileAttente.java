package Tcp;

import java.net.Socket;
import java.util.LinkedList;

/**
 * Classe qui permet de stocker les connexions en attentes
 */
public class FileAttente {
    private LinkedList<Socket> fileAttente;

    public FileAttente() {
        fileAttente = new LinkedList<>();
    }

    public synchronized void addConnexion(Socket socket) {
        fileAttente.addLast(socket);
        notify();
    }

    public synchronized Socket getConnexion() throws InterruptedException {
        while (fileAttente.isEmpty()) wait();
        return fileAttente.remove();
    }
}