#include <stdio.h>
#include <stdlib.h>
#include <mysql.h>
#include <time.h>
#include <string.h>

typedef struct
{
  int   id;
  char  intitule[20];
  float prix;
  int   stock;  
  char  image[20];
} ARTICLE;

ARTICLE Elm[] = 
{
  {-1,"carottes",2.16f,9,"carottes.jpg"},
  {-1,"cerises",9.75f,8,"cerises.jpg"},
  {-1,"artichaut",1.62f,15,"artichaut.jpg"},
  {-1,"bananes",2.6f,8,"bananes.jpg"},
  {-1,"champignons",10.25f,4,"champignons.jpg"},
  {-1,"concombre",1.17f,5,"concombre.jpg"},
  {-1,"courgette",1.17f,14,"courgette.jpg"},
  {-1,"haricots",10.82f,7,"haricots.jpg"},
  {-1,"laitue",1.62f,10,"laitue.jpg"},
  {-1,"oranges",3.78f,23,"oranges.jpg"},
  {-1,"oignons",2.12f,4,"oignons.jpg"},
  {-1,"nectarines",10.38f,6,"nectarines.jpg"},
  {-1,"peches",8.48f,11,"peches.jpg"},
  {-1,"poivron",1.29f,13,"poivron.jpg"},
  {-1,"pommes de terre",2.17f,25,"pommesDeTerre.jpg"},
  {-1,"pommes",4.00f,26,"pommes.jpg"},
  {-1,"citrons",4.44f,11,"citrons.jpg"},
  {-1,"ail",1.08f,14,"ail.jpg"},
  {-1,"aubergine",1.62f,17,"aubergine.jpg"},
  {-1,"echalotes",6.48f,13,"echalotes.jpg"},
  {-1,"tomates",5.49f,22,"tomates.jpg"}
};

int main(int argc,char *argv[])
{
  // Connection a MySql
  printf("Connection a la BD...\n");
  MYSQL* connexion = mysql_init(NULL);
  mysql_real_connect(connexion,"localhost","Student","PassStudent1_","PourStudent",0,0,0);

  // Creation d'une table UNIX_FINAL
  printf("Creation de la table articles...\n");

  // au cas ou elles existeraient deja
  mysql_query(connexion,"drop table articles;"); 
  mysql_query(connexion,"drop table clients;");
  mysql_query(connexion,"drop table factures;");
  mysql_query(connexion,"drop table ventes;");
  mysql_query(connexion,"drop table employes;");

  mysql_query(connexion,"create table articles (id INT(4) auto_increment primary key, intitule varchar(20),prix FLOAT(4),stock INT(4),image varchar(20));");
  mysql_query(connexion,"create table clients (id INT(4) auto_increment primary key,nom varchar(50),mdp varchar(50));");
  mysql_query(connexion,"create table factures (id INT(4) auto_increment primary key, idClient INT(4), prix FLOAT(4), date DATETIME, paye BOOLEAN);");
  mysql_query(connexion,"create table ventes (idFacture INT(4), idArticle INT(4), quantite INT(4));");
  mysql_query(connexion,"create table employes (id INT(4) auto_increment primary key,nom varchar(50),mdp varchar(50));");


  // Ajout de tuples dans la table UNIX_FINAL
  printf("Ajout de 21 articles la table articles...\n");
  char requete[256];
  for (int i=0 ; i<21 ; i++)
  {
	  sprintf(requete,"insert into articles values (NULL,'%s',%f,%d,'%s');",Elm[i].intitule,Elm[i].prix,Elm[i].stock,Elm[i].image);
	  mysql_query(connexion,requete);
  }

  printf("Ajout de quelques clients...\n");

  sprintf(requete,"insert into clients values (NULL, 'a', 'a');"); // on met NULL dans le premier champs car c'est l'id qui s'auto incremente
  mysql_query(connexion,requete);

  sprintf(requete,"insert into clients values (NULL, 'wagner', 'abc123');"); // on met NULL dans le premier champs car c'est l'id qui s'auto incremente
  mysql_query(connexion,requete);


  printf("Ajout de quelques factures\n");

  sprintf(requete,"insert into factures values (NULL, 1, 57.23, CURRENT_TIMESTAMP, false);"); // on met NULL dans le premier champs car c'est l'id qui s'auto incremente
  mysql_query(connexion,requete);

  printf("Ajout de quelques ventes\n");

  sprintf(requete,"insert into ventes values (1,6,7);"); 
  mysql_query(connexion,requete);

  printf("Ajout des employes\n");

  sprintf(requete,"insert into employes values (NULL, 'a', 'a');"); // on met NULL dans le premier champs car c'est l'id qui s'auto incremente
  mysql_query(connexion,requete);

  sprintf(requete,"insert into employes values (NULL, 'admin', 'admin');"); // on met NULL dans le premier champs car c'est l'id qui s'auto incremente
  mysql_query(connexion,requete);

  sprintf(requete,"insert into employes values (NULL, 'paquet', 'lucas');"); // on met NULL dans le premier champs car c'est l'id qui s'auto incremente
  mysql_query(connexion,requete);
  
  sprintf(requete,"insert into employes values (NULL, 'farine', 'nathan');"); // on met NULL dans le premier champs car c'est l'id qui s'auto incremente
  mysql_query(connexion,requete);

  // Deconnection de la BD
  mysql_close(connexion);
  exit(0);
}
