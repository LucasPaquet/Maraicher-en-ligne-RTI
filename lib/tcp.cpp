#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h> 
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>

#include "tcp.h"

int ServerSocket(int portTemp)
{
    int s;

    printf("pid = %d\n", getpid());

    if ((s = socket(AF_INET, SOCK_STREAM, 0)) == -1)
    {
        perror("Erreur de socket()");
        exit(1);
    }
    printf("socket creee = %d\n", s);

    // Pour la recherche
    struct addrinfo hints;
    struct addrinfo *results;

    // Pour l'affichage des resultats
    char host[NI_MAXHOST];
    char port[NI_MAXSERV];

    struct addrinfo* info;

    // On fournit l'hote et le service
    char portStr[6]; // Pour stocker le numéro de port sous forme de chaîne
    snprintf(portStr, sizeof(portStr), "%d", portTemp); // Convertit le portTemp en chaîne

    memset(&hints, 0, sizeof(struct addrinfo));
    hints.ai_family = AF_INET;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE; // Pour permettre la liaison sur toutes les interfaces
    printf("Pour le port %d :\n", portTemp);

    if (getaddrinfo(NULL, portStr, &hints, &results) != 0)
        printf("Erreur de getaddrinfo");
    else
    {
        // Affichage du contenu des adresses obtenues au format numérique
        for (info = results; info != NULL; info = info->ai_next)
        {
            getnameinfo(info->ai_addr, info->ai_addrlen,
                        host, NI_MAXHOST,
                        port, NI_MAXSERV,
                        NI_NUMERICSERV | NI_NUMERICHOST);
            printf("Adresse IP: %s -- Port: %s\n", host, port);
        }

        freeaddrinfo(results);
    }

    // Liaison de la socket à l'adresse et au port
    if (bind(s, results->ai_addr, results->ai_addrlen) == -1)
    {
        perror("Erreur de bind()");
        exit(1);
    }

    // Écoute sur la socket
    if (listen(s, SOMAXCONN) == -1)
    {
        perror("Erreur de listen()");
        exit(1);
    }

    return s; // Retourne la socket d'écoute
}


int Accept(int sEcoute, char *ipClient)
{
    char host[NI_MAXHOST];
    char port[NI_MAXSERV];

    // Attente d'une connexion
    int sService;
    struct sockaddr_in adrClient;
    socklen_t adrClientLen = sizeof(adrClient);

    printf("Attente de service\n");

    if ((sService = accept(sEcoute, (struct sockaddr*)&adrClient, &adrClientLen)) == -1)
    {
        perror("Erreur de accept()");
        exit(1);
    }
    printf("accept() reussi !\n");
    printf("socket de service = %d\n", sService);

    // Récupération d'information sur le client connecté
    getnameinfo((struct sockaddr*)&adrClient, adrClientLen,
                host, NI_MAXHOST,
                port, NI_MAXSERV,
                NI_NUMERICSERV | NI_NUMERICHOST);
    
    if (host != NULL)
    {
        strcpy(ipClient, host); // Copie l'adresse IP dans ipClient
    }


    printf("Client connecte --> Adresse IP: %s -- Port: %s\n", host, port);

    return sService; // Retourne la socket de service
}

int ClientSocket(char* ipServeur, int portServeur)
{
    int sClient;
    printf("PID du processus : %d\n", getpid());

    // Crée une socket de type TCP/IP (IPv4)
    if ((sClient = socket(AF_INET, SOCK_STREAM, 0)) == -1)
    {
        perror("Erreur lors de la création de la socket");
        exit(1);
    }
    printf("Socket créée avec succès, descripteur de socket = %d\n", sClient);

    // Prépare les informations d'adresse du serveur
    struct addrinfo hints;
    struct addrinfo *results;
    memset(&hints, 0, sizeof(struct addrinfo));
    hints.ai_family = AF_INET;           // Utilise IPv4
    hints.ai_socktype = SOCK_STREAM;     // Utilise le protocole TCP
    hints.ai_flags = AI_NUMERICSERV;    // Le port est en format numérique

    // Convertit le portServeur en une chaîne de caractères
    char portStr[6]; // Assez grand pour contenir un numéro de port à 5 chiffres
    sprintf(portStr, "%d", portServeur);

    // Récupère les informations d'adresse pour le serveur
    if (getaddrinfo(ipServeur, portStr, &hints, &results) != 0)
        exit(1);

    // Demande une connexion au serveur
    if (connect(sClient, results->ai_addr, results->ai_addrlen) == -1)
    {
        perror("Erreur lors de la connexion au serveur");
        exit(1);
    }
    printf("Connexion réussie !");


    return sClient;
}

int Send(int sSocket,char* data,int taille)
{
	return 0;
}

int Receive(int sSocket,char* data)
{
	return 0;
}