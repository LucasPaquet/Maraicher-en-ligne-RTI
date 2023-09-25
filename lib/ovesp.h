#ifndef OVESP_H
#define OVESP_H
#define NB_MAX_CLIENTS 100


struct Article {
    int idArticle;
    char * intitule;
    int stock;
    double prix;
    char* image;
};

bool OVESP(char* requete, char* reponse,int socket);
bool OVESP_Login(const char* user,const char* password, int newClient);
int OVESP_Operation(char op,int a,int b);
void OVESP_Close();
Article OVESP_Consult(int idArticle);


#endif
