package Tcp;

import Tcp.Interface.Reponse;

/**
 * Celle-ci sera lancée lorsque le protocole décidera que la connexion doit se fermer après
 * le traitement de la requête → si une dernière réponse doit être envoyée au client avant
 * fermeture, celle-ci sera récupérée par le serveur générique dans l’exception catchée.
 */
public class FinConnexionException extends Exception{
    private Reponse reponse;

    public FinConnexionException(Reponse reponse)
    {
        super("Fin de Connexion décidée par protocole");
        this.reponse = reponse;
    }

    public Reponse getReponse()
    {
        return reponse;
    }

}
