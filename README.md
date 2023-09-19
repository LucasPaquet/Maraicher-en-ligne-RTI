# Maraicher-en-ligne-RTI

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