#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h> // pour memset
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


int Accept(int sEcoute,char *ipClient)
{
	// Construction de l'adresse
	struct addrinfo hints;
	struct addrinfo *results;
	memset(&hints,0,sizeof(struct addrinfo));
	hints.ai_family = AF_INET;
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_flags = AI_PASSIVE | AI_NUMERICSERV; // pour une connexion passive

	if (getaddrinfo(NULL,"50000",&hints,&results) != 0)
		exit(1);

	// Affichage du contenu de l'adresse obtenue
	char host[NI_MAXHOST];
	char port[NI_MAXSERV];
	getnameinfo(results->ai_addr,results->ai_addrlen,
	host,NI_MAXHOST,port,NI_MAXSERV,NI_NUMERICSERV | NI_NUMERICHOST);
	printf("Mon Adresse IP: %s -- Mon Port: %s\n",host,port);

	

	// Mise à l'écoute de la socket
	if (listen(sEcoute,SOMAXCONN) == -1)
	{
		perror("Erreur de listen()");
		exit(1);
	}
	printf("listen() reussi !\n");

	// Attente d'une connexion
	int sService;
	if ((sService = accept(sEcoute,NULL,NULL)) == -1)
	{
		perror("Erreur de accept()");
		exit(1);
	}
	printf("accept() reussi !");
	printf("socket de service = %d\n",sService);

	// Recuperation d'information sur le client connecte
	struct sockaddr_in adrClient;
	socklen_t adrClientLen;
	getpeername(sService,(struct sockaddr*)&adrClient,&adrClientLen);
	getnameinfo((struct sockaddr*)&adrClient,adrClientLen,
	host,NI_MAXHOST,
	port,NI_MAXSERV,
	NI_NUMERICSERV | NI_NUMERICHOST);
	printf("Client connecte --> Adresse IP: %s -- Port: %s\n",host,port);


	return 0;
}