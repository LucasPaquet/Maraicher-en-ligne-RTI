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

// Prototypes de fonctions
int estPresent(int socket);
void ajoute(int socket);
void retire(int socket);

//***** Parsing de la requete et creation de la reponse *************
bool OVESP(char* requete, char* reponse, int socket, MYSQL* connexion)
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
        if (strcmp(art.idArticle, "-1") == 0)
            sprintf(reponse, "CONSULT#ko");
        else
            sprintf(reponse, "CONSULT#ok#%s#%s#%s#%s#%s", art.idArticle, art.intitule, art.stock, art.prix, art.image);


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
    char requete[200];
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
