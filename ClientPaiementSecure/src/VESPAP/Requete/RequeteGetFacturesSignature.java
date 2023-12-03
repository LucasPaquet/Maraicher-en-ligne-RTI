package VESPAP.Requete;

import Tcp.Interface.Requete;

public class RequeteGetFacturesSignature implements Requete {
    private int idClient;
    private byte[] signature;

    public RequeteGetFacturesSignature(int idClient) {

        this.idClient = idClient;
    }

    public int getIdClient() {
        return idClient;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }
}
