package VESPAP.Requete;

import Tcp.Interface.Requete;

public class RequeteHandshake implements Requete {
    private byte[] dataSession; // clé de session cryptée asymétriquement

    public void setDataSession(byte[] d) { dataSession = d; }
    public byte[] getDataSession() { return dataSession; }
}
