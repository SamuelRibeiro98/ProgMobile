# Fonctionnalités

L'ensemble de l'application possède des layouts verticaux et horizontaux pour la rendre plus agréable à utiliser.

##Page d'accueil
Permet de :
-se connecter
-accéder à la page d'inscription

peut se connecter automatiquement si un token à été sauvegardé d'une précédente connexion

Gère les différents cas d'erreur des réponses API avec un message d'avertissement à l'écran

##Page d'inscription
Permet de s'inscrire

Gère les différents cas d'erreur des réponses API avec un message d'avertissement à l'écran

##Page du menu
Permet de choisir les différentes fonctionnalités de l'application.
L'id de votre maison est affiché
Dans le cas ou le token sauvegardé est éxpiré, l'utilisateur est renvoyé sur la page de connexion

##Contrôler une maison
Par défaut si on choisit "controler ma maison" l'id de votre maison est transmit a la page du choix de type d'appareil

#Page du choix du type d'appareil à contrôler
L'utilisateur peut choisir le type d'appareil qu'il souhaite contrôler parmis:
-fenêtres
-volets
-portes de garage

Attention: Si la maison n'est pas ouverte dans un navigateur l'utilisateur est renvoyé au menu avec un message pour le prévenir dans un pop-up

#Page du contrôle des appareils
La page est divisée en deux section:
-La première permet de contrôler l'ensemble des appareils du type choisis
-La seconde contient la liste des appareils filrée sur le type choisit avec des boutons individuels pour les contrôler.

##Gérer les droits
Cette page permet:
-de voir qui à des droits d'accès à votre maison
-de donner des droits sur sa maison à d'autres utilisateurs
-de leur retirer les droits

#Contrôler d'autres maison
Cette page permet de voir les maisons sur lequelles vous avez des droits d'accès.
En appuyant sur l'une d'entre elle vous êtes envoyé sur la page du choix du type d'appareil à contrôler en transmettant directement l'id de cette maison
