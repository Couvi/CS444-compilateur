# CS444-compilateur
## Modifier le CLASSPATH
Executer la commande `source` avec le fichier setenv.sh a la racine

## Compiler
- cd ProjetCompil
- make

## Tester 
La commande 'make test' permet de lancer les tests de :
- l'analyse lexicale : Fichiers dans dossier devlex
- l'analyse syntaxique : Fichiers dans dossier devsyn

# Passe 1 : Travail effectué

## Analyse lexicale
- Dans le répertoire ProjetCompil/Syntaxe/Src, compléter le fichier lexical.flex (voir Lexicographie.txt)
- Pour tester, utiliser le script lexico dans le répertoire ProjetCompil/Syntaxe/Test.
	
## Analyse syntaxique
- Compléter le fichier syntaxe.cup (voir Syntaxe.txt et ArbreAbstrait.txt)
- Ecrire des programmes JCas de test dans ProjetCompil/Syntaxe/Test.
- Effectuer des tests avec le script syntaxe dans le répertoire ProjetCompil/Syntaxe/Test
- Ce script utilise le programme principal présent dans la classe TestSynt.

