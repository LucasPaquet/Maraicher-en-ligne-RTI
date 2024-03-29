#include "windowclient.h"
#include "ui_windowclient.h"
#include <QMessageBox>
#include <string>

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include "../lib/tcp.h"

using namespace std;

extern WindowClient *w;

#define REPERTOIRE_IMAGES "ClientQt/images/"

void HandlerSIGINT(int s);

void Echange(char* requete, char* reponse);

// Function Protocol OVESP
bool OVESP_Login(const char* user, const char* password, int newClient);
void OVESP_Logout();
void OVESP_Consult(int article);
void OVESP_Achat(int article, int quantite);
void OVESP_Caddie();
void OVESP_Cancel(int indArticle);
void OVESP_CancelAll();
void OVESP_Confirmer();

int sClient;
int articleEnCour = 1; // changer en Struct Article
int idClient = 0;

float prixTotal = 0; 

WindowClient::WindowClient(QWidget *parent) : QMainWindow(parent), ui(new Ui::WindowClient)
{

    ui->setupUi(this);

    // Configuration de la table du panier (ne pas modifer)
    ui->tableWidgetPanier->setColumnCount(3);
    ui->tableWidgetPanier->setRowCount(0);
    QStringList labelsTablePanier;
    labelsTablePanier << "Article" << "Prix à l'unité" << "Quantité";
    ui->tableWidgetPanier->setHorizontalHeaderLabels(labelsTablePanier);
    ui->tableWidgetPanier->setSelectionMode(QAbstractItemView::SingleSelection);
    ui->tableWidgetPanier->setSelectionBehavior(QAbstractItemView::SelectRows);
    ui->tableWidgetPanier->horizontalHeader()->setVisible(true);
    ui->tableWidgetPanier->horizontalHeader()->setDefaultSectionSize(160);
    ui->tableWidgetPanier->horizontalHeader()->setStretchLastSection(true);
    ui->tableWidgetPanier->verticalHeader()->setVisible(false);
    ui->tableWidgetPanier->horizontalHeader()->setStyleSheet("background-color: lightyellow");

    ui->pushButtonPayer->setText("Confirmer achat");
    setPublicite("!!! Bienvenue sur le Maraicher en ligne !!!");

    // Armement des signaux
    struct sigaction A;
    A.sa_flags = 0;
    sigemptyset(&A.sa_mask);
    A.sa_handler = HandlerSIGINT;

    if (sigaction(SIGINT, &A, NULL) == -1)
    {
        perror("Erreur de sigaction");
        exit(1);
    }

    // Connexion sur le serveur
    if ((sClient = ClientSocket("127.0.0.1", 50000)) == -1)
    {
        perror("Erreur de ClientSocket");
        exit(1);
    }

    printf("Connecté sur le serveur.\n");
}

WindowClient::~WindowClient()
{
    delete ui;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Fonctions utiles : ne pas modifier /////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::setNom(const char* Text)
{
  if (strlen(Text) == 0 )
  {
    ui->lineEditNom->clear();
    return;
  }
  ui->lineEditNom->setText(Text);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
const char* WindowClient::getNom()
{
  strcpy(nom,ui->lineEditNom->text().toStdString().c_str());
  return nom;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::setMotDePasse(const char* Text)
{
  if (strlen(Text) == 0 )
  {
    ui->lineEditMotDePasse->clear();
    return;
  }
  ui->lineEditMotDePasse->setText(Text);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
const char* WindowClient::getMotDePasse()
{
  strcpy(motDePasse,ui->lineEditMotDePasse->text().toStdString().c_str());
  return motDePasse;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::setPublicite(const char* Text)
{
  if (strlen(Text) == 0 )
  {
    ui->lineEditPublicite->clear();
    return;
  }
  ui->lineEditPublicite->setText(Text);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::setImage(const char* image)
{
  // Met à jour l'image
  char cheminComplet[80];
  sprintf(cheminComplet,"%s%s",REPERTOIRE_IMAGES,image);
  QLabel* label = new QLabel();
  label->setSizePolicy(QSizePolicy::Ignored, QSizePolicy::Ignored);
  label->setScaledContents(true);
  QPixmap *pixmap_img = new QPixmap(cheminComplet);
  label->setPixmap(*pixmap_img);
  label->resize(label->pixmap()->size());
  ui->scrollArea->setWidget(label);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
int WindowClient::isNouveauClientChecked()
{
  if (ui->checkBoxNouveauClient->isChecked()) return 1;
  return 0;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::setArticle(const char* intitule,float prix,int stock,const char* image)
{
  ui->lineEditArticle->setText(intitule);
  if (prix >= 0.0)
  {
    char Prix[20];
    sprintf(Prix,"%.2f",prix);
    ui->lineEditPrixUnitaire->setText(Prix);
  }
  else ui->lineEditPrixUnitaire->clear();
  if (stock >= 0)
  {
    char Stock[20];
    sprintf(Stock,"%d",stock);
    ui->lineEditStock->setText(Stock);
  }
  else ui->lineEditStock->clear();
  setImage(image);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
int WindowClient::getQuantite()
{
  return ui->spinBoxQuantite->value();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::setTotal(float total)
{
  if (total >= 0.0)
  {
    char Total[20];
    sprintf(Total,"%.2f",total);
    ui->lineEditTotal->setText(Total);
  }
  else ui->lineEditTotal->clear();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::loginOK()
{
  ui->pushButtonLogin->setEnabled(false);
  ui->pushButtonLogout->setEnabled(true);
  ui->lineEditNom->setReadOnly(true);
  ui->lineEditMotDePasse->setReadOnly(true);
  ui->checkBoxNouveauClient->setEnabled(false);

  ui->spinBoxQuantite->setEnabled(true);
  ui->pushButtonPrecedent->setEnabled(true);
  ui->pushButtonSuivant->setEnabled(true);
  ui->pushButtonAcheter->setEnabled(true);
  ui->pushButtonSupprimer->setEnabled(true);
  ui->pushButtonViderPanier->setEnabled(true);
  ui->pushButtonPayer->setEnabled(true);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::logoutOK()
{
  ui->pushButtonLogin->setEnabled(true);
  ui->pushButtonLogout->setEnabled(false);
  ui->lineEditNom->setReadOnly(false);
  ui->lineEditMotDePasse->setReadOnly(false);
  ui->checkBoxNouveauClient->setEnabled(true);

  ui->spinBoxQuantite->setEnabled(false);
  ui->pushButtonPrecedent->setEnabled(false);
  ui->pushButtonSuivant->setEnabled(false);
  ui->pushButtonAcheter->setEnabled(false);
  ui->pushButtonSupprimer->setEnabled(false);
  ui->pushButtonViderPanier->setEnabled(false);
  ui->pushButtonPayer->setEnabled(false);

  setNom("");
  setMotDePasse("");
  ui->checkBoxNouveauClient->setCheckState(Qt::CheckState::Unchecked);

  setArticle("",-1.0,-1,"");

  w->videTablePanier();
  w->setTotal(-1.0);
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Fonctions utiles Table du panier (ne pas modifier) /////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::ajouteArticleTablePanier(const char* article,float prix,int quantite)
{
    char Prix[20],Quantite[20];

    sprintf(Prix,"%.2f",prix);
    sprintf(Quantite,"%d",quantite);

    // Ajout possible
    int nbLignes = ui->tableWidgetPanier->rowCount();
    nbLignes++;
    ui->tableWidgetPanier->setRowCount(nbLignes);
    ui->tableWidgetPanier->setRowHeight(nbLignes-1,10);

    QTableWidgetItem *item = new QTableWidgetItem;
    item->setFlags(Qt::ItemIsSelectable|Qt::ItemIsEnabled);
    item->setTextAlignment(Qt::AlignCenter);
    item->setText(article);
    ui->tableWidgetPanier->setItem(nbLignes-1,0,item);

    item = new QTableWidgetItem;
    item->setFlags(Qt::ItemIsSelectable|Qt::ItemIsEnabled);
    item->setTextAlignment(Qt::AlignCenter);
    item->setText(Prix);
    ui->tableWidgetPanier->setItem(nbLignes-1,1,item);

    item = new QTableWidgetItem;
    item->setFlags(Qt::ItemIsSelectable|Qt::ItemIsEnabled);
    item->setTextAlignment(Qt::AlignCenter);
    item->setText(Quantite);
    ui->tableWidgetPanier->setItem(nbLignes-1,2,item);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::videTablePanier()
{
    ui->tableWidgetPanier->setRowCount(0);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
int WindowClient::getIndiceArticleSelectionne()
{
    QModelIndexList liste = ui->tableWidgetPanier->selectionModel()->selectedRows();
    if (liste.size() == 0) return -1;
    QModelIndex index = liste.at(0);
    int indice = index.row();
    return indice;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Fonctions permettant d'afficher des boites de dialogue (ne pas modifier ////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::dialogueMessage(const char* titre,const char* message)
{
   QMessageBox::information(this,titre,message);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::dialogueErreur(const char* titre,const char* message)
{
   QMessageBox::critical(this,titre,message);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////// CLIC SUR LA CROIX DE LA FENETRE /////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::closeEvent(QCloseEvent *event)
{
    OVESP_Logout();
    OVESP_CancelAll();

    exit(0);
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Fonctions clics sur les boutons ////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonLogin_clicked()
{
    if (strcmp(getNom(), "") == 0 || strcmp(getMotDePasse(), "") == 0)
    {
        dialogueErreur("Erreur de connexion", "Remplisez les champs !");
    }
    else
    {
        if (OVESP_Login(getNom(), getMotDePasse(), isNouveauClientChecked()))
        {
            loginOK();
            OVESP_Consult(articleEnCour); // pour avoir le premier article quand on se connecte
        }
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonLogout_clicked()
{
  OVESP_Logout();
  OVESP_CancelAll();
  articleEnCour = 1;
  logoutOK();
  setMotDePasse("");
  setNom("");
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonSuivant_clicked()
{
    OVESP_Consult(articleEnCour+1);

}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonPrecedent_clicked()
{
    OVESP_Consult(articleEnCour-1);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonAcheter_clicked()
{
    if (getQuantite() > 0) // on verifie que le client n'achete pas 0 article
    {
        OVESP_Achat(articleEnCour, getQuantite());
        OVESP_Caddie(); // On met a jour le caddie pour le GUI
        OVESP_Consult(articleEnCour); // pour mettre a jour le stock sur le GUI
    }
    else
        w->dialogueErreur("Erreur d'achat", "Mettez une valeur au dessus de 0");
    
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonSupprimer_clicked()
{
    if (getIndiceArticleSelectionne() == -1)
    {
        dialogueErreur("Erreur de supression", "Veillez à sélectionner un article");
    }
    else
    {
        OVESP_Cancel(getIndiceArticleSelectionne());
        OVESP_Caddie(); // On met a jour le caddie pour le GUI
        OVESP_Consult(articleEnCour); // pour mettre a jour le stock sur le GUI
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonViderPanier_clicked()
{
    OVESP_CancelAll();
    OVESP_Caddie(); // On met a jour le caddie pour le GUI
    OVESP_Consult(articleEnCour); // pour mettre a jour le stock sur le GUI
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonPayer_clicked()
{
    OVESP_Confirmer();
}


// ***** Fin de connexion ********************************************
void HandlerSIGINT(int s)
{
    printf("\nArrêt du client.\n");
    OVESP_Logout();
    close(sClient);
    exit(0);
}

// ***** Gestion du protocole OVESP ***********************************
bool OVESP_Login(const char* user, const char* password, int newClient)
{
    char requete[200], reponse[200];
    char* message;

    // ***** Construction de la requête *********************
    sprintf(requete, "LOGIN#%s#%s#%d", user, password, newClient);

    // ***** Envoi requête + réception réponse **************
    Echange(requete, reponse);

    // ***** Parsing de la réponse **************************

    char* ptr = strtok(reponse, "#"); // entête = LOGIN (normalement...)
    ptr = strtok(NULL, "#");          // statut = ok ou ko
    message = strtok(NULL, "#");      // Message de retour

    if (strcmp(ptr, "ok") == 0)
    {
        idClient = atoi(strtok(NULL, "#"));
        w->dialogueMessage("Connexion réussi !", message);
        return true;
    }
    else
    {
        w->dialogueErreur("Erreur de connexion", message);
        printf("Erreur de login: %s\n", ptr);
        return false;
    }

}

//*******************************************************************
void OVESP_Logout()
{
    char requete[200], reponse[200];


    // ***** Construction de la requête *********************
    sprintf(requete, "LOGOUT");

    // ***** Envoi requête + réception réponse **************
    Echange(requete, reponse);

    // ***** Parsing de la réponse **************************
    // Pas vraiment utile...
}

//*******************************************************************


void OVESP_Consult(int article)
{
    char requete[200], reponse[200];
    int stock;
    char intitule[200];
    char image[200];
    char prix[200];


    // ***** Construction de la requête *********************
    sprintf(requete, "CONSULT#%d", article);

    // ***** Envoi requête + réception réponse **************
    Echange(requete, reponse);

    char* ptr = strtok(reponse, "#"); // entête = CONSULT (normalement...)
    ptr = strtok(NULL, "#");          // statut = ok ou ko
    if (strcmp(ptr, "ok") == 0)
    {
        articleEnCour = atoi(strtok(NULL, "#"));
        strcpy(intitule, strtok(NULL, "#"));
        stock = atoi(strtok(NULL, "#"));
        strcpy(prix, strtok(NULL, "#"));
        strcpy(image, strtok(NULL, "#"));

        // pour convertir le "."" en "," pour le prix
        for (char* p = prix; *p; ++p) {
        if (*p == '.') {
            *p = ','; // Remplacez la virgule par un point
        }
    }

        w->setArticle(intitule, atof(prix), stock, image); // stof() = convertir un string en float
    }
    
}

//*******************************************************************

void OVESP_Achat(int article, int quantite)
{
    char requete[200], reponse[200], msg[200];
    int qtt, idArticle;


    // ***** Construction de la requête *********************
    sprintf(requete, "ACHAT#%d#%d", article, quantite);

    // ***** Envoi requête + réception réponse **************
    Echange(requete, reponse);

    char* ptr = strtok(reponse, "#"); // entête = CONSULT (normalement...)
    ptr = strtok(NULL, "#");          // statut = ok ou ko

    qtt = atoi(strtok(NULL, "#")); // recuperer la quantite de l'article
    idArticle = atoi(strtok(NULL, "#")); // recuperer l'id de l'article
    
    if (strcmp(ptr, "ok") == 0)
    {
        sprintf(msg, "%d articles[%d] ont été ajoutés à votre panier", quantite, idArticle);
        w->dialogueMessage("Achat réussi", msg);
    }
    else
    {
        switch(qtt)
        {
            case 0: w->dialogueErreur("Erreur d'achat", "Stock insufisant");
                    break;
            case -1: w->dialogueErreur("Erreur d'achat", "Article non trouvé");
                    break;
            case -2: w->dialogueErreur("Erreur d'achat", "Votre panier est plein !");
                    break;
        }
    }
}

//*******************************************************************

void OVESP_Caddie()
{
    char requete[200], reponse[2000];
    int n;
    char intitule[100], image[100], prix[10];
    int quantite, idArticle;

    // ***** Construction de la requête *********************
    sprintf(requete, "CADDIE");

    // ***** Envoi requête + réception réponse **************
    Echange(requete, reponse);

     // ***** Parsing de la réponse **************************

    w->videTablePanier(); // on vide tout car on va tout remplir juste après

    char* ptr = strtok(reponse, "#"); // entête = CONSULT (normalement...)
    n = atoi(strtok(NULL, "#"));  

    prixTotal = 0; // on le remet a zero parce que on va tout recalculer

    for (int i = 0; i < n; ++i)
    {
        idArticle = atoi(strtok(NULL, "#"));
        strcpy(intitule, strtok(NULL, "#"));
        quantite = atoi(strtok(NULL, "#")); 
        strcpy(prix, strtok(NULL, "#"));
        strcpy(image, strtok(NULL, "#"));

        // pour convertir le "."" en "," pour le prix
        for (char* p = prix; *p; ++p) {
        if (*p == '.') {
            *p = ','; // Remplacez la virgule par un point
        }
    }

        prixTotal += atof(prix) * quantite;

        w->ajouteArticleTablePanier(intitule, atof(prix), quantite);
    }

    w->setTotal(prixTotal);
   
}

//*******************************************************************

void OVESP_Cancel(int indArticle)
{
    char requete[200], reponse[200];


    // ***** Construction de la requête *********************
    sprintf(requete, "CANCEL#%d", indArticle);

    // ***** Envoi requête + réception réponse **************
    Echange(requete, reponse);

    // ***** Parsing de la requête **************************

    char* ptr = strtok(reponse, "#"); // entête = CANCEL (normalement...)
    ptr = strtok(NULL, "#");          // statut = ok ou ko

    if (strcmp(ptr, "ko") == 0)
        w->dialogueErreur("Erreur de supression", "Une erreur est survenue lors de la supression de l'article");
}

//*******************************************************************
void OVESP_CancelAll()
{
    char requete[200], reponse[200];


    // ***** Construction de la requête *********************
    sprintf(requete, "CANCELALL");

    // ***** Envoi requête + réception réponse **************
    Echange(requete, reponse);

    // ***** Parsing de la réponse **************************
    char* ptr = strtok(reponse, "#"); // entête = CANCELALL (normalement...)
    ptr = strtok(NULL, "#");          // statut = ok ou ko

    if (strcmp(ptr, "ko") == 0)
        w->dialogueErreur("Erreur de supression", "Une erreur est survenue lors de la supression de votre panier");
}

//*******************************************************************

void OVESP_Confirmer()
{
    char requete[200], reponse[200], msg[200];
    int numFacture;

    // ***** Construction de la requête *********************
    sprintf(requete, "CONFIRMER#%d", idClient);

    // ***** Envoi requête + réception réponse **************
    Echange(requete, reponse);

    // ***** Parsing de la réponse **************************
    char* ptr = strtok(reponse, "#"); // entête = CONFIRMER (normalement...)
    ptr = strtok(NULL, "#");          // statut = ok ou ko

    if (strcmp(ptr, "ok") == 0)
    {
        numFacture = atoi(strtok(NULL, "#")); // recuperer le num de facture
        sprintf(msg, "La commande à bien été envoyé au Maraîcher. Numéro de facture : %d", numFacture);
        w->dialogueMessage("Commande réussi", msg);
        w->videTablePanier();        
        w->setTotal(0);
    }
    else
        w->dialogueErreur("Commande échoué", "Une erreur est survenue lors du passage de la commande");

}


//***** Échange de données entre client et serveur ******************
void Echange(char* requete, char* reponse)
{
    int nbEcrits, nbLus;

    // ***** Envoi de la requête ****************************
    if ((nbEcrits = Send(sClient, requete, strlen(requete))) == -1)
    {
        perror("Erreur de Send");
        close(sClient);
        exit(1);
    }

    // ***** Attente de la réponse **************************
    if ((nbLus = Receive(sClient, reponse)) < 0)
    {
        perror("Erreur de Receive");
        close(sClient);
        exit(1);
    }

    if (nbLus == 0)
    {
        printf("Serveur arrêté, pas de réponse reçue...\n");
        close(sClient);
        exit(1);
    }

    reponse[nbLus] = 0;
}
