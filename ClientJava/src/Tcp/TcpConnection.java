package Tcp;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
            System.out.println("ERREUR IOException : " + e);
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
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8); // pour pouvoir lire les accents correctement
            while (!endOfTransmission) {
                int b1 = reader.read();
                if (b1 == -1) {
                    break; // Si la lecture se passe mal
                }
                if (b1 == (byte) '#') {
                    int b2 = reader.read();
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

    public boolean IsOosNull(){
        return outputStream == null;
    }
}
