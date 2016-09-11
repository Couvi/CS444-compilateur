# CS444-compilateur
## Modifier le CLASSPATH
- Permanent via .bashrc
Dans le fichier .bash_profile , (ou .bashrc , ou .tcshrc...), modifier le CLASSPATH :
```export CLASSPATH=$CLASSPATH:$HOME:$HOME/ProjetCompil/Global/Bin/java-cup-11a-runtime.jar:$HOME/ProjetCompil/Global/Bin/JFlex.jar:.```
Dans le dossier $HOME, relire le fichier .bash_profile : source .bash_profile, (ou source .bashrc , ou .tcshrc...) 
- Temporaire via setenv.sh
Executer la commande `source` avec le fichier setenv.sh a la racine
 

## Compiler
- cd ProjetCompil
- make

## Tester l'analyse lexicale
- cd Syntaxe/Test
- ./lexico simple.cas

## Tester l'analyse syntaxique
- ./syntaxe simple.cas

# Travail à effectuer

## Analyse lexicale
- Dans le répertoire ProjetCompil/Syntaxe/Src, compléter le fichier lexical.flex (voir Lexicographie.txt)
- Pour tester, utiliser le script lexico dans le répertoire ProjetCompil/Syntaxe/Test.
	
## Analyse syntaxique
- Compléter le fichier syntaxe.cup (voir Syntaxe.txt et ArbreAbstrait.txt)
- Ecrire des programmes JCas de test dans ProjetCompil/Syntaxe/Test.
- Effectuer des tests avec le script syntaxe dans le répertoire ProjetCompil/Syntaxe/Test
- Ce script utilise le programme principal présent dans la classe TestSynt.

