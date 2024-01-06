package VESPAP.Requete;

import Tcp.Interface.Requete;

import java.io.Serializable;

public class RequeteCrypte implements Requete {
    private byte[] data;

    public RequeteCrypte(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
