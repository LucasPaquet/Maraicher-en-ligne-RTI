#include "ovesp.h"
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <mysql.h>

//***** Etat du protocole : liste des clients loggés ****************
int clients[NB_MAX_CLIENTS];
int nbClients = 0;
pthread_mutex_t mutexClients = PTHREAD_MUTEX_INITIALIZER;
char requete[200];

// Prototypes de fonctions
int estPresent(int socket);
void ajoute(int socket);
void retire(int socket);

// Fonction de debug
void printCaddie(CaddieArticle caddie[10]);


//***** Parsing de la requete et creation de la reponse *************
bool OVESP(char* requete, char* reponse, int socket, MYSQL* connexion, CaddieArticle caddie[10])
{
    int idArticle = 0;
    // ***** Récupération nom de la requete *****************
    char *ptr = strtok(requete, "#");
    Article art;

    // ***** LOGIN ******************************************
    if (strcmp(ptr, "LOGIN") == 0)
    {
        char user[50], password[50];
        int newClient = 0;

        strcpy(user, strtok(NULL, "#"));
        strcpy(password, strtok(NULL, "#"));

        printf("\t[THREAD %p] LOGIN de %s\n", pthread_self(), user);

        if (estPresent(socket) >= 0) // client déjà loggé
        {
            sprintf(reponse, "LOGIN#ko#Client déjà loggé !");
            return true;
        }
        else
        {
            if (OVESP_Login(user, password, newClient))
            {
                sprintf(reponse, "LOGIN#ok");
                ajoute(socket);
                return true;
            }
            else
            {
                sprintf(reponse, "LOGIN#ko#Mauvais identifiants !");
                return true;
            }
        }
    }

    // ***** CONSULT ******************************************
    if (strcmp(ptr, "CONSULT") == 0)
    {
        idArticle = atoi(strtok(NULL, "#"));

        printf("\t[THREAD %p] CONSULT de %d\n", pthread_self(), idArticle);

        art = OVESP_Consult(idArticle, connexion);
        // faire si id = -1 faire ko
        if (strcmp(art.idArticle, "-1") == 0) // si on ne trouve pas l'article
            sprintf(reponse, "CONSULT#ko");
        else
            sprintf(reponse, "CONSULT#ok#%s#%s#%s#%s#%s", art.idArticle, art.intitule, art.stock, art.prix, art.image);


        return true;
    }

    // ***** ACHAT *******************************************
    if (strcmp(ptr, "ACHAT") == 0)
    {
        int idArticle, quantite, rep;

        idArticle = atoi(strtok(NULL, "#"));
        quantite = atoi(strtok(NULL, "#"));

        printf("\t[THREAD %p] ACHAT de %d\n", pthread_self(), idArticle);

        rep = OVESP_Achat(idArticle,connexion,quantite,caddie);

        switch(rep)
        {
            case 0: sprintf(reponse, "ACHAT#ok#Votre a bien ete enregistrer");
                    break;
            case 1: sprintf(reponse, "ACHAT#ko#Votre caddie est plein");
                    break;
            case 2: sprintf(reponse, "ACHAT#ko#Il n'y a pas asser de stock");
                    break;
            case 3: sprintf(reponse, "ACHAT#ko#Erreur");
                    break;
        }

        return true;
    }

    // ***** CADDIE *****************************************
    if (strcmp(ptr, "CADDIE") == 0)
    {
        printf("\t[THREAD %p] CADDIE\n", pthread_self());

        sprintf(reponse, "CADDIE#%s",OVESP_Caddie(caddie)); 

        return true;
    }

    // ***** CANCEL *****************************************
    if (strcmp(ptr, "CANCEL") == 0)
    {
        int indArticle = 0;

        printf("\t[THREAD %p] CANCEL\n", pthread_self());
        
        indArticle = atoi(strtok(NULL, "#")); // recuperer l'indice du panier

        if (OVESP_Cancel(indArticle,connexion,caddie))
            sprintf(reponse, "CANCEL#ok"); 
        else
            sprintf(reponse, "CANCEL#ko");

        return true;
    }

    // ***** CANCELALL *****************************************
    if (strcmp(ptr, "CANCELALL") == 0)
    {
        int indArticle = 0;

        printf("\t[THREAD %p] CANCELALL\n", pthread_self());

        if (OVESP_CancelAll(connexion,caddie))
            sprintf(reponse, "CANCELALL#ok"); 
        else
            sprintf(reponse, "CANCELALL#ko");

        return true;
    }

    // ***** LOGOUT *****************************************
    if (strcmp(ptr, "LOGOUT") == 0)
    {
        printf("\t[THREAD %p] LOGOUT\n", pthread_self());
        retire(socket);
        sprintf(reponse, "LOGOUT#ok");
        return true;
    }

    // ***** OPER *******************************************
    if (strcmp(ptr, "OPER") == 0)
    {
        char op;
        int a, b;
        ptr = strtok(NULL, "#");
        op = ptr[0];
        a = atoi(strtok(NULL, "#"));
        b = atoi(strtok(NULL, "#"));
        printf("\t[THREAD %p] OPERATION %d %c %d\n", pthread_self(), a, op, b);

        if (estPresent(socket) == -1)
        {
            sprintf(reponse, "OPER#ko#Client non loggé !");
            return true;
        }
        else
        {
            try
            {
                int resultat = OVESP_Operation(op, a, b);
                sprintf(reponse, "OPER#ok#%d", resultat);
                return true;
            }
            catch (int)
            {
                sprintf(reponse, "OPER#ko#Division par zéro !");
                return true;
            }
        }
    }
    
    return false; // Ajustez le retour en fonction de la logique de votre application
}


//***** Traitement des requetes *************************************
bool OVESP_Login(const char* user, const char* password, int newClient)
{
    if (strcmp(user, "wagner") == 0 && strcmp(password, "abc123") == 0) return true;
    if (strcmp(user, "a") == 0 && strcmp(password, "a") == 0) return true;
    return false;
}

int OVESP_Operation(char op, int a, int b)
{
    if (op == '+') return a + b;
    if (op == '-') return a - b;
    if (op == '*') return a * b;

    if (op == '/')
    {
        if (b == 0) throw 1;
        return a / b;
    }

    return 0;
}

Article OVESP_Consult(int idArticle, MYSQL* connexion)
{
    Article response;
    MYSQL_RES  *resultat;
    MYSQL_ROW  tuple;


  // Acces BD
  sprintf(requete,"select * from articles where id = %d",idArticle); // pour recuperer les infos sur le produit en fonciton de son id
  
  mysql_query(connexion,requete); // execution de la requete
  resultat = mysql_store_result(connexion);
  if (resultat && idArticle > 0 && idArticle < 22)
  {
    tuple = mysql_fetch_row(resultat); 
    printf("(ACCESBD) RESULTAT : %s, %s, %s, %s, %s\n", tuple[0], tuple[1], tuple[2], tuple[3], tuple[4]);

    strcpy(response.idArticle, tuple[0]);
    strcpy(response.intitule, tuple[1]);
    strcpy(response.prix, tuple[2]);
    strcpy(response.stock, tuple[3]);
    strcpy(response.image, tuple[4]);   
    
  }
  else
  {
    strcpy(response.idArticle, "-1");
  }
  return response;
}

int OVESP_Achat(int idArticle, MYSQL* connexion, int quantite, CaddieArticle caddie[10])
{
    int caddieFree, i;
    MYSQL_ROW  tuple;
    MYSQL_RES  *resultat;
    char requeteSql[200];
    char requete[200];

    for (i = 0; i < 10; ++i) {
        if (caddie[i].idArticle == -1) {
            caddieFree = i; // Retourne le numéro de la première place libre
            break;
        }
    }

    if (i == 10)
        return 1;

    sprintf(requete,"select * from articles where id = %d",idArticle);
                        
    mysql_query(connexion,requete);
    resultat = mysql_store_result(connexion);
  
    if (resultat && idArticle > 0 && idArticle < 22)
    {
        tuple = mysql_fetch_row(resultat);
    
        if (atoi(tuple[3]) - quantite < 0)
        {
          printf("PAS ASSER DE STOCK\n");
          return 2;
        }
        else
        {
            // si assez de stock
            sprintf(requeteSql, "update articles SET stock = stock - %d where id = %d",quantite,idArticle); // mise a jour du stock
            mysql_query(connexion,requeteSql);

            caddie[caddieFree].idArticle = atoi(tuple[0]);
            strcpy(caddie[caddieFree].intitule, tuple[1]);
            caddie[caddieFree].prix = atof(tuple[2]);
            caddie[caddieFree].stock =  quantite;
            strcpy(caddie[caddieFree].image, tuple[4]); 

            printCaddie(caddie); // Fct de debug qui permet de print le caddie

            return 0;
        }
    }

    printCaddie(caddie);
    return 3;
    
}

char* OVESP_Caddie(struct CaddieArticle caddie[10]) {
    char* article = (char*)malloc(2000); // Allouer de la mémoire pour la chaine avec les articles
    char* rep = (char*)malloc(2000); // Allouer de la mémoire pour la chaine résultante
    article[0] = '\0'; // Initialiser la chaîne résultante comme une chaine vide
    const char separator = '#'; // Caractere séparateur
    int n = 0; // nb d'article dans la panier

    for (int i = 0; i < 10; ++i) {
        if (caddie[i].idArticle != -1) {
            
            char temp[200]; 
            sprintf(temp, "%c%d%c%s%c%d%c%.2f%c%s", separator,
                caddie[i].idArticle, separator,
                caddie[i].intitule, separator,
                caddie[i].stock, separator,
                caddie[i].prix, separator,
                caddie[i].image
            );
            strcat(article, temp);

            n++;
        }
    }

    if (n > 0) // si il y a plus d'un article dans le panier
       sprintf(rep, "%d%s", n, article);
    else
        sprintf(rep, "0");
    

    printf("%s\n", rep); // debug
    return rep;
}

bool OVESP_Cancel(int indArticle, MYSQL* connexion, CaddieArticle caddie[10])
{
    char requete[200];
    int quantite = caddie[indArticle].stock;
    int idArticle = caddie[indArticle].idArticle;
    int i;

    sprintf(requete, "update articles SET stock = stock + %d where id = %d",quantite,idArticle); // mise a jour du stock
    
    if (mysql_query(connexion,requete) != 0) // Si la requete ne s'est pas bien passer
        return false;

    for(i = indArticle; i < 10; i++) // pour decaller tout les articles pour la coherence entre le panier du serveur et celui du client
    {
        if (caddie[i+1].idArticle == -1) // si l'article dans le panier suivant n'existe pas
        {
            caddie[i].idArticle = -1; // mettre id = -1. Cela permet d'aviter de copié du vide et d'optimiser le code
            break; // se termine plus car le reste du panier doit etre vide
        }
        else // sinon copie tout les elements de l'article
        { 
            caddie[i].idArticle = caddie[i+1].idArticle;
            strcpy(caddie[i].intitule,caddie[i+1].intitule);
            caddie[i].prix = caddie[i+1].prix;
            caddie[i].stock = caddie[i+1].stock;
            strcpy(caddie[i].image,caddie[i+1].image);
        }
    }

    printCaddie(caddie); // debug

    return true;
}

bool OVESP_CancelAll(MYSQL* connexion, CaddieArticle caddie[10])
{
    while(caddie[0].idArticle != -1) // on garde le 0 car tout va se decaller vers le 0 quand on les surprimme 1 a 1
    {
        if (OVESP_Cancel(0,connexion,caddie) == false) // Nonzero if an error occurred
            return false;
    }
    return true;
}


//***** Gestion de l'état du protocole ******************************

int estPresent(int socket)
{
    int indice = -1;
    pthread_mutex_lock(&mutexClients);

    for(int i = 0; i < nbClients; i++)
    {
        if (clients[i] == socket)
        {
            indice = i;
            break;
        }
    }

    pthread_mutex_unlock(&mutexClients);
    return indice;
}

void ajoute(int socket)
{
    pthread_mutex_lock(&mutexClients);
    clients[nbClients] = socket;
    nbClients++;
    pthread_mutex_unlock(&mutexClients);
}

void retire(int socket)
{
    int pos = estPresent(socket);

    if (pos == -1) return;

    pthread_mutex_lock(&mutexClients);

    for (int i = pos; i <= nbClients - 2; i++)
    {
        clients[i] = clients[i + 1];
    }

    nbClients--;
    pthread_mutex_unlock(&mutexClients);
}

//***** Fin prématurée **********************************************
void OVESP_Close()
{
    pthread_mutex_lock(&mutexClients);

    for (int i = 0; i < nbClients; i++)
    {
        close(clients[i]);
    }

    pthread_mutex_unlock(&mutexClients);
}

//*****Fonction pour DEBUG ******************************************

void printCaddie(CaddieArticle caddie[10])
{
    for (int i = 0; i < 10; ++i)
    {
        if (caddie[i].idArticle != -1)
            printf("ID : %d \nQT: %d \nPrix: %f\n intitule : %s\n", caddie[i].idArticle, caddie[i].stock, caddie[i].prix, caddie[i].intitule);
        else
            printf("VIDE\n");
    }
}