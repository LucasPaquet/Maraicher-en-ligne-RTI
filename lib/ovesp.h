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

bool OVESP(char* requete, char* reponse,int socket, MYSQL* connexion);
bool OVESP_Login(const char* user,const char* password, int newClient);
int OVESP_Operation(char op,int a,int b);
void OVESP_Close();
Article OVESP_Consult(int idArticle, MYSQL* connexion);


#endif
