package VESPAP.Reponse;

import Tcp.Interface.Reponse;

public class ReponseHandshake implements Reponse {
    private boolean valide;

    public ReponseHandshake(boolean v) {
        valide = v;
    }
    public boolean isValide() {
        return valide;
    }
}
