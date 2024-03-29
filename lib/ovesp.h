#ifndef OVESP_H
#define OVESP_H
#define NB_MAX_CLIENTS 100
#include <mysql.h>

struct Article {
    char idArticle[100];
    char intitule[100];
    char stock[100];
    char prix[100];
    char image[100];
};

struct CaddieArticle {
    int idArticle;
    char intitule[100];
    int stock;
    float prix;
    char image[100];
    int idClient;
};

void OVESP(char* requete, char* reponse,int socket, MYSQL* connexion, CaddieArticle caddie[10]);
int OVESP_Login(const char* user,const char* password, int newClient, MYSQL* connexion);
Article OVESP_Consult(int idArticle, MYSQL* connexion);
int OVESP_Achat(int idArticle, MYSQL* connexion, int quantite, CaddieArticle caddie[10]);
char* OVESP_Caddie(CaddieArticle caddie[10]);
bool OVESP_Cancel(int indArticle, MYSQL* connexion, CaddieArticle caddie[10]);
bool OVESP_CancelAll(MYSQL* connexion, CaddieArticle caddie[10]);
int OVESP_Confirmer(MYSQL* connexion, CaddieArticle caddie[10], int idClient);
void OVESP_Close();



#endif
