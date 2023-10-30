package VESPAP.Reponse;

import Tcp.Interface.Reponse;

public class ReponseLogout implements Reponse {
    private boolean valide;

    public ReponseLogout(boolean v) {
        valide = v;
    }
    public boolean isValide() {
        return valide;
    }
}
