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
};

bool OVESP(char* requete, char* reponse,int socket, MYSQL* connexion, CaddieArticle caddie[10]);
bool OVESP_Login(const char* user,const char* password, int newClient);
Article OVESP_Consult(int idArticle, MYSQL* connexion);
int OVESP_Achat(int idArticle, MYSQL* connexion, int quantite, CaddieArticle caddie[10]);
char* OVESP_Caddie(CaddieArticle caddie[10]);
int OVESP_Operation(char op,int a,int b);
void OVESP_Close();



#endif
