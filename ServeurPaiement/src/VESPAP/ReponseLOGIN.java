package VESPAP;

import Tcp.Interface.Reponse;

public class ReponseLOGIN implements Reponse
{
    private boolean valide;

    ReponseLOGIN(boolean v) {
        valide = v;
    }
    public boolean isValide() {
        return valide;
    }
}