# Sujet - TP Log4Shell
### BERTRAND Timothé - BLANC Olivier - PAYS Antoine - GENIN Fabien
### A rendre pour le jeudi 10 février 2022 23H59


## Exercice

### **Objectif** : Récuperer le contenu de la BDD avec les informations présentes dans le fichier .env de la victime.


**Schéma explicatif rapide :** 

Vous êtes un attaquant et vous souhaitez accéder à une BDD, sauf que vous n'avez pas les informations pour vous connectez à la BDD. Ces informations se trouvent dans le fichier .env de la victime.
Il faut savoir que le développeur du serveur victime a choisi de logger les identifiants d'un utilisateur lors de l'envoie du formulaire.



Pour utiliser les commandes docker vous devrez vous connecter à VDN à l'iut comme vous avez l'habitude de faire pour les TP précéndents

Si vous utilisez les machines de l'iut, lancer vdn docker-tmp.
Start la machine root@debian-1
Dans la console de la VM root@debian-1, créer un répertoire "log4shell" 
Faire `cd log4shell` et ensuite `git clone *lien github*`


Dans un premier temps, récupérer sur github le dépot du TP
    <lien github>

Après avoir importé le repertoire, faire `docker-compose up` dans le repertoire clonné.


Etapes d'initialisation & démarrage du serveur :
Docker-compose up
Et peut-etre faire un `make build` pour créer tous les DockerFiles

Etapes d'initialisation & démarrage du client : 

Ecrire dans le fichier.java le morceau de code manquant afin de récupérer et d'écrire le .env de la base de données dans les logs du serveur
Appeler votre class java dans une requete faite au serveur, grace à un lookup vu en cours.


### Question : 
    Fichier .env : Récupérer le fichier **.env** présent sur le serveur de la victime par le biais de la console

    BDD : Créer un utilisateur avec les informations présentes dans le fichier .env pour avoir accès au contenu de la BDD. 

    Quelles sont les informations présentes dans la BDD ? 
