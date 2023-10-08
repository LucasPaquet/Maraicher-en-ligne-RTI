package Tcp;

import java.io.*;
import java.net.Socket;

public class TcpConnection {

    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    public TcpConnection(String host, int port) {
        try {
            socket = new Socket(host, port); // connexion au serveur
            outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la création de la connexion au serveur", e);
        }
    }

    /**
     * Permet d'envoyer des données au serveur
     * @param data Chaîne de caractère que l'on veut envoyer au serveur
     */
    public void send(String data) {
        data += "#)";
        System.out.println("Client send : " + data);
        try {
            outputStream.write(data.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'envoi des données", e);
        }
    }

    /**
     * Permet de recevoir une réponse du serveur
     * @return Retourne la chaîne de caractère reçu par le serveur
     */
    public String receive() {
        try {
            StringBuilder buffer = new StringBuilder();
            boolean endOfTransmission = false;
            while (!endOfTransmission) {
                byte b1 = inputStream.readByte();
                if (b1 == (byte) '#') {
                    byte b2 = inputStream.readByte();
                    if (b2 == (byte) ')') {
                        endOfTransmission = true;
                    } else {
                        buffer.append((char) b1);
                        buffer.append((char) b2);
                    }
                } else {
                    buffer.append((char) b1);
                }
            }
            return buffer.toString();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la réception des données", e);
        }
    }

    /**
     * Permet de fermer la connection avec le serveur
     */
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la fermeture de la connexion", e);
        }
    }
}
