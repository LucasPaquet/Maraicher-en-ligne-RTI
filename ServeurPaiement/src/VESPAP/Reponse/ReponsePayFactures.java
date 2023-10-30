package VESPAP.Reponse;

import Tcp.Interface.Reponse;

public class ReponsePayFactures implements Reponse {
    private boolean isPaid;

    public ReponsePayFactures(boolean isPaid) {
        this.isPaid = isPaid;
    }

    public boolean isPaid() {
        return isPaid;
    }
}
