package VESPAP.Reponse;

import Tcp.Interface.Reponse;

public class ReponseCrypte implements Reponse {
    private byte[] data;

    public ReponseCrypte(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
