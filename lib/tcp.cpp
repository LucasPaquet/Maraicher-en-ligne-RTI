#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h> // pour memset
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>

#include "tcp.h"

int ServerSocket(int port)
{
	int s;

	printf("pid = %d\n",getpid());

	if ((s = socket(AF_INET, SOCK_STREAM, 0)) == -1)
	{
		perror("Erreur de socket()");
	 	exit(1);
	}
	printf("socket creee = %d\n",s);


	// Pour la recherche
	struct addrinfo hints;
	struct addrinfo *results;

	// Pour l'affichage des resultats
	char host[NI_MAXHOST];
	char portBuffer[NI_MAXSERV];

	struct addrinfo* info;

	// On fournit l'hote et le service
	memset(&hints,0,sizeof(struct addrinfo)); // initialisation à 0
	hints.ai_family = AF_INET;
	hints.ai_socktype = SOCK_STREAM;
	printf("Pour www.google.be avec le service http :\n");

	if (getaddrinfo("www.google.be","http",&hints,&results) != 0)
		printf("Erreur de getaddrinfo");
	else
	{
		// Affichage du contendu des adresses obtenues au format numérique
		for (info = results ; info != NULL ; info = info->ai_next)
		{
			getnameinfo(info->ai_addr,info->ai_addrlen,
			host,NI_MAXHOST,
			portBuffer,NI_MAXSERV,
			NI_NUMERICSERV | NI_NUMERICHOST);
			printf("Adresse IP: %s -- Port: %s\n",host,portBuffer);
		}

		freeaddrinfo(results);
	}

	// On fournit l'adresse IP et le port directement
	memset(&hints,0,sizeof(struct addrinfo)); // initialisation à 0
	hints.ai_family = AF_INET;
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_flags = AI_NUMERICSERV | AI_NUMERICHOST;
	printf("Pour 192.168.228.167 avec le port 80 :\n");

	if (getaddrinfo("192.168.228.167","80",&hints,&results) != 0)
		printf("Erreur de getaddrinfo");
	else
	{
		// Affichage du contendu des adresses obtenues au format "hote" et "service"
		for (info = results ; info != NULL ; info = info->ai_next)
		{
			getnameinfo(info->ai_addr,info->ai_addrlen,
			host,NI_MAXHOST,
			portBuffer,NI_MAXSERV,
			0);
			printf("Hote: %s -- Service: %s\n",host,portBuffer);
		}

		freeaddrinfo(results);
	}
	return 0;
}