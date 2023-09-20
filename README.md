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

# Partie 1
Cette partie comporte les éléments suivants :
* la construction d’une libraire de sockets en C/C++
* le serveur « Achat » multi-threads C/C++ tournant sur une machine Linux (et utilisant 
la librairie de sockets développée)
* le client C/C++/Qt capable de communiquer avec ce serveur
*  la mise en place de la base de données MySQL

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

## L’application « Client Achat » Java
Vous devez donc développer ici une application fenêtrée en Java (Swing) capable d’interagir 
avec le « Serveur Achat » déjà développé et similaire visuellement à celle fournie en Qt. Le 
serveur ne doit en aucune façon faire de différence entre le client C et le client Java, et ne doit 
se rendre compte de rien. Cette application sera utilisée par les clients du magasin ne 
disposant que d’une machine Windows avec Java installé.

## Le Java Bean d’accès à la base de données