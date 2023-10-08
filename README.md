# Maraicher-en-ligne-RTI

L’application à réaliser est une application 
d’achats en ligne de fruits/légumes intitulée 
« Le Maraicher en ligne ». 
Les clients du « Maraicher en ligne » doivent 
se logger à partir d’une application « desktop »
avant de pouvoir naviguer dans le catalogue du 
magasin en ligne et faire leurs achats. Ces 
achats sont stockés dans un panier virtuel
avant d’être validé par le client. 
Afin d’obtenir sa commande, le client devra 
tout d’abord payer la facture générée lors de sa 
session d’achat :

* Une première possibilité pour lui est de se rendre physiquement au comptoir du magasin où il s’adressera à un employé. Il pourra alors s’acquitter de sa facture auprès de cet employé avant de repartir avec sa commande de fruits et légumes. 

* Une seconde possibilité sera de réaliser le paiement en ligne en utilisant une autre application « desktop » permettant un traitement sécurisé de sa facture. Il pourra ensuite se rendre physiquement au magasin pour récupérer sa commande. La gestion et le réapprovisionnement du stock de marchandises seront réalisés par un gérant qui utilisera soit son PC, soit une tablette via une application web dédiée.

## Schéma global de l’application

L’application globale comporte plusieurs serveurs et clients écrits dans différents langages de
programmation et paradigmes imposés. Voici le schéma global de l’application :

![image](https://cdn.discordapp.com/attachments/1156623456029380701/1160543941100314685/image.png?ex=65350bc5&is=652296c5&hm=c63b8c5a2badaea5536364f9b98312d5521595ec8c15841b17edafb692731813&)

## Protocole de communication

### Online Vegetables Shopping Protocol (OVESP) : 

Protocol pour les communications entre le client (C ou Java) et le serveur (C) pour faire une commande sur le Maraîcher en ligne.

| Commande    | Requête                                | Réponse                                                                                 | Actions / Explications                                                                                                                                                                                                                                      |
|-------------|----------------------------------------|-----------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Login       | Login, password, nouveau client ou pas | Oui ou non, message ou raison                                                           | Vérification de l’existence et du mot de passe du client / Création d’un nouveau client dans la table clients                                                                                                                                               |
| Consult     | idArticle                              | idArticle ou -1, intitule, stock,prix, image                                            | Consultation d’un article en BD → si article non trouvé, retour -1 au client                                                                                                                                                                                |
| Achat       | idArticle, quantité                    | idArticle ou -1, quantité ou 0, prix                                                    | Si article non trouvé, retour -1. Si trouvé mais que stock insuffisant, retour d’une quantité 0 → Si ok, le stock est mis à jour en BD et le contenu du caddie est mémorisé au niveau du serveur → actuellement aucune action sur tables factures et ventes |
| Caddie      | /                                      | Contenu du panier : (idArticle, intitulé, quantité, prix) × nombre d’articles du panier | Retourne l’entièreté du contenu du caddie au client                                                                                                                                                                                                         |
| Cancel      | idArticle                              | Oui ou non                                                                              | Supprime un article du caddie et met à jour à la BD                                                                                                                                                                                                         |
| Cancel All  | /                                      | /                                                                                       | Supprime tous les articles du caddie et met à jour la BD                                                                                                                                                                                                    |
| Confirmer   | /                                      | Numéro de facture générée                                                               | Création d’une facture et BD et ajout des éléments du caddie dans la BD                                                                                                                                                                                     |
| Logout      | /                                      | /                                                                                       | Si Caddie en cours, vide le caddie et met à jour la BD                                                                                                                                                                                                      |


### « VEgetables Shopping PAyment Protocol (VESPAP) : 

Protocol pour les communications entre le client (Java) et serveur (Java) pour faire payer une commande du Maraîcher en ligne.

| Commande    | Requête                                   | Réponse                                                               | Actions / Explications                                                                                              |
|-------------|-------------------------------------------|-----------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------|
| Login       | Login, password, (d'un emmployé)          | Oui ou non                                                            | Vérification du login et du mot passe dans la table des employés                                                    |
| Get Factures| idClient (fournie par le client sur place)| Liste des factures (idFacture, date, montant, payé)                   | On récupère simplement les factures du client dans la table factures (sans le contenu détaillé de la commande donc) |   
| Pay Factures| idFacture, nom et numéro de la carte VISA | Oui ou non (carte VISA invalide)                                      | Le serveur se contente de vérifier la validité du numéro de carte → si ok, on considère que le paiement est réalisé |       
| Logout      | /                                         | /                                                                     | /                                                                                                                   |               


---

# Partie 1
Cette partie comporte les éléments suivants :
* la construction d’une libraire de sockets en C/C++
* le serveur « Achat » multi-threads C/C++ tournant sur une machine Linux (et utilisant 
la librairie de sockets développée)
* le client C/C++/Qt capable de communiquer avec ce serveur
*  la mise en place de la base de données MySQL

Sur le schéma global de l’application, cela correspond à :

![image](https://cdn.discordapp.com/attachments/1156623456029380701/1160543997798912021/image.png?ex=65350bd2&is=652296d2&hm=22a8549f3ebb06b8ca7c904985a4519515a71ee2185bd742bec5f67cb30a48f1&)

## Élement associé
:file_folder:: BD_Maraicher, ClientQT, lib
<br>
:page_facing_up: : config.txt, makefile, server.cpp

## L’application « Client Achat »

C’est grâce à l’application « Client Achat » (tournant sur une machine Linux) qu’un nouveau
client (ou client existant) pourra parcourir les pages du magasin en ligne, remplir son panier et 
ensuite confirmer sa commande. Tout ceci se fera bien sûr via un dialogue avec le serveur 
Achat.

## La base de données

Il s’agira d’une base de données MySQL. Vous pouvez la créer vous-même ou alors vous 
pouvez utiliser le programme CreationBD fourni. Ce programme (à utiliser sur la VM Oracle 
Linux fournie) créera la table articles et la pré-remplira. Les autres tables sont à votre charge.

## La librairie de sockets

Dans un premier temps, on vous demande de développer une petite librairie C ou C++ de 
sockets (fournie sous forme de fichiers .cpp et .h) dont les caractéristiques sont : 

* elle doit générique : on ne doit pas voir apparaître la notion d’ « articles » ou de 
« clients » dans le prototype des fonctions. Elle doit pouvoir être réutilisée telle quelle 
dans une autre application
* elle doit abstraite : l’utilisation de votre librairie doit permettre d’éviter de voir 
apparaître les structures systèmes du genre « sockaddr_in » dans les programmes qui 
utilisent votre librairie

## Le serveur Achat
Le « Serveur Achat » devra être un serveur 
* multi-threads écrit en C (threads POSIX),
* implémentant le modèle « pool de threads »
* attendant sur le port PORT_ACHAT
* tournant sur une machine Linux (pouvant être la VM Oracle Linux dont vous 
disposez déjà)

Le nombre de threads du pool ainsi que le PORT_ACHAT pourront être lus dans un fichier 
(texte) de configuration du serveur.
Pour la construction du protocole (trame, …), vous devez savoir que dans la second partie, un 
client Java similaire devra être développé. Ce protocole s’appellera « Online VEgetables 
Shopping Protocol » (OVESP).

# Partie 2 
Cette partie comporte les éléments suivants : 
* le client Java pour le serveur Achat 
* le Java Bean d’accès à la base de données construit en utilisant JDBC 
* le serveur « Paiement » multi-threads Java, utilisant le bean développé 
* le client Java « Paiement » capable de communiquer avec ce serveur

## Élement associé
:file_folder:: ClientJava, JavaLib

## L’application « Client Achat » Java

Sur le schéma global de l’application, cela correspond à :

![image](https://cdn.discordapp.com/attachments/1156623456029380701/1160544053767721072/image.png?ex=65350be0&is=652296e0&hm=0fec6c6a77ac2f89d44a2244d8a2aecaad8bd2f6b77097d288f97479c62fb819&)

Vous devez donc développer ici une application fenêtrée en Java (Swing) capable d’interagir 
avec le « Serveur Achat » déjà développé et similaire visuellement à celle fournie en Qt. Le 
serveur ne doit en aucune façon faire de différence entre le client C et le client Java, et ne doit 
se rendre compte de rien. Cette application sera utilisée par les clients du magasin ne 
disposant que d’une machine Windows avec Java installé.

### Faire communiquer le serveur C et le client Java
Pour faire communiquer le serveur C et le client Java, il ne faut pas oublier d'ouvrir les ports de la machine sur lequel tourne le serveur C. 
<br><br>
Pour ouvrir les ports sur linux :
<br>
`sudo firewall-cmd --permanent --zone=public --add-port=50000/tcp`
<br>
et puis pour recharger :
<br>
`sudo firewall-cmd --reload`

## Le Java Bean d’accès à la base de données

L'accès à la base de données ne devra pas se faire avec les primitives JDBC utilisées telles
quelles, mais plutôt au moyen d'objets métiers encapsulant le travail (typiquement de type
Java Beans, mais **sans mécanisme d'events**).

On demande donc de construire une petite librairie constituée d’un ensemble de telles classes
permettant l'accès (c'est-à-dire à tout le moins la connexion et les opérations de
base SELECT, INSERT, UPDATE) le plus simple possible. On pourrait imaginer : 

* **un bean générique** (indépendant de l’application) encapsulant la connexion à la BD et
le traitement de requêtes SQL simples via les primitives JDBC
* **un bean « métier »** héritant (ou utilisant) le bean précédent mais dédié à la logique
métier du serveur paiement. Ce bean pourrait alors s’utiliser sans aucune connaissance
de JDBC ou même du langage SQL.

Attention qu’une seule instance de ce(s) bean(s) ne sera utilisée dans le serveur Paiement et
que ce serveur sera un serveur multi-threads : attention donc aux <ins>accès concurrents<ins> !

## Le serveur paiement et son application cliente

Sur le schéma global de l’application, cela correspond à :

![image](https://cdn.discordapp.com/attachments/1156623456029380701/1160544098038587462/image.png?ex=65350bea&is=652296ea&hm=88ed61e0f1ac27c3bac2299ac965c1f45870447ae38526ee5f0481e6bc48fcc8&)

L’application Java « Client Paiement » est utilisée par un employé qui accueille les clients
venant payer une facture et récupérer la commande correspondante. Grâce au numéro du
client donné de vive voix par le client, l’employé peut alors se connecter sur le serveur
paiement et obtenir toutes les factures impayées par le client. Le client précise alors quelle
facture (et donc quelle commande) il veut payer. Ensuite, il fournit à l’employé les références
de sa carte VISA (nom + numéro de carte). L’employé envoie alors ces informations au
serveur paiement qui se contentera de vérifier si le numéro de carte est valide.

A des fins de simplifications, si le numéro de carte est valide, on considérera que le paiement
est réalisé sans problème. NB : Pour les bacheliers en informatique orientation « Réseaux et
Télécommunications », le paiement se fera de manière sécurisée dans le laboratoire de
« Complément Réseau » de Mr. C.

Etant donné, que l’application Java « Client Paiement » et le « serveur Paiement » se situent
tous les deux dans même réseau local, aucun mécanisme de sécurisation (cryptage, signature
électronique, …) n’a été envisagé ici.

### Consignes d’implémentation
Tout d’abord, le pont JDBC entre le serveur paiement et la base de données se fera en utilisant
le bean d’accès développé à la section précédente.

Le « Serveur Paiement » devra être un serveur : 
* multi-threads écrit en Java (utilisant le package java.net)
* implémentant le modèle « pool de threads »
* attendant sur le port PORT_PAIEMENT

Le nombre de threads du pool ainsi que le PORT_PAIEMENT pourront être lus dans un
fichier (properties) de configuration du serveur.

L’application Java cliente sera développée en Swing et permettra au client de communiquer
avec le serveur paiement selon le protocole décrit ci-dessous.

Pour la construction du protocole (trame, objets sérialisés, …), vous devez savoir que tous les
intervenants seront écrits en Java. Ce protocole s’appellera « Vegetables Shopping
Payment Protocol » (VESPAP). Les différentes commandes sont en haut du README.MD.

En bonus, on pourrait compléter le protocole avec une commande « Get Facture » qui
permettrait de récupérer l’ensemble des articles concernant une facture dont on fournirait l’id
au serveur.

L’interface graphique de votre client devra permettre à l’utilisateur d’encoder les données
nécessaires aux requêtes et d’en afficher les résultats, tout en étant le plus ergonomique
possible.

Pour la validation du numéro de carte VISA, vous pouvez vous contenter d’un « choix
aléatoire » du serveur. Pour les puristes qui voudraient faire mieux, l’algorithme de Luhn peut
vous intéresser (voir par exemple https://ma-petite-encyclopedie.org/accueil?lex_id=1939 )

