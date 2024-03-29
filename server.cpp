#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <signal.h>
#include <pthread.h>

#include "tcp.h"
#include "ovesp.h"


void HandlerSIGINT(int s);
void TraitementConnexion(int sService);
void* FctThreadClient(void* p);
void lireConfig(const char* fichierConfig, int* nbThreadsPool, int* tailleFileAttente, int* portAchat);
int sEcoute;

// Gestion du pool de threads
int NB_THREADS_POOL = 0;
int TAILLE_FILE_ATTENTE = 0;
int PORT_ACHAT = 0;
int* socketsAcceptees = NULL; // on alloue dynamiquement la memoire apres car sinon on a pas le temps de lire le fichier de configuration

int indiceEcriture = 0, indiceLecture = 0;
pthread_mutex_t mutexSocketsAcceptees;
pthread_cond_t condSocketsAcceptees;

// Connexion SQL
MYSQL* connexion;

int main(int argc, char* argv[])
{
    lireConfig("config.txt", &NB_THREADS_POOL, &TAILLE_FILE_ATTENTE, &PORT_ACHAT); // permet de lire le fichier de configuration "config.txt"

    socketsAcceptees = (int*)malloc(TAILLE_FILE_ATTENTE * sizeof(int)); 
    if (socketsAcceptees == NULL) { // on verifie que la memoire a bien ete allouer
        perror("Erreur d'allocation memoire");
    }

    // Initialisation socketsAcceptees
    pthread_mutex_init(&mutexSocketsAcceptees, NULL);
    pthread_cond_init(&condSocketsAcceptees, NULL);

    for (int i = 0; i < TAILLE_FILE_ATTENTE; i++)
        socketsAcceptees[i] = -1;

    // Armement des signaux
    struct sigaction A;
    A.sa_flags = 0;
    sigemptyset(&A.sa_mask);
    A.sa_handler = HandlerSIGINT;

    if (sigaction(SIGINT, &A, NULL) == -1)
    {
        perror("Erreur de sigaction");
        exit(1);
    }

    // Creation de la socket d'écoute
    if ((sEcoute = ServerSocket(PORT_ACHAT)) == -1)
    {
        perror("Erreur de ServeurSocket");
        exit(1);
    }

    // Connection à la base de données SQL
    connexion = mysql_init(NULL);
    if (mysql_real_connect(connexion,"localhost","Student","PassStudent1_","PourStudent",0,0,0) == NULL)
    {
        fprintf(stderr,"[SQL] Erreur de connexion à la base de données...\n");
        exit(1);  
    }
    else
    {
        fprintf(stderr,"[SQL] Connexion a sql reussi...\n");
    }

    // Creation du pool de threads
    printf("Création du pool de threads.\n");
    pthread_t th;

    for (int i = 0; i < NB_THREADS_POOL; i++)
        pthread_create(&th, NULL, FctThreadClient, NULL);

    // Mise en boucle du serveur
    int sService;
    char ipClient[50];
    printf("Demarrage du serveur.\n");

    while (1)
    {
        printf("Attente d'une connexion...\n");

        if ((sService = Accept(sEcoute, ipClient)) == -1)
        {
            perror("Erreur de Accept");
            close(sEcoute);
            OVESP_Close();
            exit(1);
        }

        printf("Connexion acceptée : IP=%s socket=%d\n", ipClient, sService);

        // Insertion en liste d'attente et réveil d'un thread du pool
        // (Production d'une tâche)
        pthread_mutex_lock(&mutexSocketsAcceptees);
        socketsAcceptees[indiceEcriture] = sService;
        indiceEcriture++;

        if (indiceEcriture == TAILLE_FILE_ATTENTE)
            indiceEcriture = 0;

        pthread_mutex_unlock(&mutexSocketsAcceptees);
        pthread_cond_signal(&condSocketsAcceptees);
    }
}

void* FctThreadClient(void* p)
{
    int sService;

    while (1)
    {
        printf("\t[THREAD %p] Attente socket...\n", pthread_self());

        // Attente d'une tâche
        pthread_mutex_lock(&mutexSocketsAcceptees);

        while (indiceEcriture == indiceLecture)
            pthread_cond_wait(&condSocketsAcceptees, &mutexSocketsAcceptees);

        sService = socketsAcceptees[indiceLecture];
        socketsAcceptees[indiceLecture] = -1;
        indiceLecture++;

        if (indiceLecture == TAILLE_FILE_ATTENTE)
            indiceLecture = 0;

        pthread_mutex_unlock(&mutexSocketsAcceptees);

        // Traitement de la connexion (consommation de la tâche)
        printf("\t[THREAD %p] Je m'occupe de la socket %d\n", pthread_self(), sService);
        TraitementConnexion(sService);
    }
}

void HandlerSIGINT(int s)
{
    printf("\nArret du serveur.\n");
    close(sEcoute);
    pthread_mutex_lock(&mutexSocketsAcceptees);

    for (int i = 0; i < TAILLE_FILE_ATTENTE; i++)
    {
        if (socketsAcceptees[i] != -1)
            close(socketsAcceptees[i]);
    }

    pthread_mutex_unlock(&mutexSocketsAcceptees);
    OVESP_Close(); // fermer toute les socket client
    mysql_close(connexion); // fermer la connexion sql
    exit(0);
}

void TraitementConnexion(int sService)
{
    char requete[200], reponse[200];
    int nbLus, nbEcrits;
    bool onContinue = true;
    CaddieArticle articles[10]; // Caddie du Client

    for (int i = 0; i < 10; ++i) 
        articles[i].idArticle = -1;
        

    for(;;) // boucle infinie (jusqu'a l'arret du serveur ou SIGINT)
    {
        printf("\t[THREAD %p] Attente requete...\n", pthread_self());

        // ***** Reception Requete ******************
        if ((nbLus = Receive(sService, requete)) < 0)
        {
            perror("Erreur de Receive");
            close(sService);
            HandlerSIGINT(0);
        }

        // ***** Fin de connexion ? *****************
        if (nbLus == 0)
        {
            printf("\t[THREAD %p] Fin de connexion du client.\n", pthread_self());
            close(sService);
            return;
        }

        requete[nbLus] = 0;
        printf("\t[THREAD %p] Requete recue = %s\n", pthread_self(), requete);

        // ***** Traitement de la requete ***********
        OVESP(requete, reponse, sService, connexion, articles);

        // ***** Envoi de la reponse ****************
        if ((nbEcrits = Send(sService, reponse, strlen(reponse))) < 0)
        {
            perror("Erreur de Send");
            close(sService);
            HandlerSIGINT(0);
        }

        printf("[THREAD %p] Reponse envoyee = %s\n", pthread_self(), reponse);
    }
}

void lireConfig(const char* fichierConfig, int* nbThreadsPool, int* tailleFileAttente, int* portAchat) {
    FILE* fichier = fopen(fichierConfig, "r");

    if (!fichier) {
        perror("Erreur : Impossible d'ouvrir le fichier de configuration");
        return;
    }

    char ligne[100];
    while (fgets(ligne, sizeof(ligne), fichier)) {
        char* cle = strtok(ligne, "=");
        char* valeur = strtok(NULL, "\n");

        if (cle && valeur) { // verifie  qu'on a pas lu du vide (donc cle ou valeur = NULL)
            if (strcmp(cle, "NB_THREADS_POOL") == 0) {
                *nbThreadsPool = atoi(valeur);
            } else if (strcmp(cle, "TAILLE_FILE_ATTENTE") == 0) {
                *tailleFileAttente = atoi(valeur);
            
            } else if (strcmp(cle, "PORT_ACHAT") == 0) {
                *portAchat = atoi(valeur);
            }
        }
    }

    fclose(fichier);
}