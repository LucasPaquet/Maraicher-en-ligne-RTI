package VESPAP.Reponse;

import Tcp.Interface.Reponse;

public class ReponsePayFacturesHMAC implements Reponse {
    private boolean isPaid;
    private byte[] hmac;

    public ReponsePayFacturesHMAC(boolean isPaid) {
        this.isPaid = isPaid;
    }

    public byte[] getHmac() {
        return hmac;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setHmac(byte[] hmac) {
        this.hmac = hmac;
    }
}
