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

# Travail � effectuer

## Analyse lexicale
- Dans le r�pertoire ProjetCompil/Syntaxe/Src, compl�ter le fichier lexical.flex (voir Lexicographie.txt)
- Pour tester, utiliser le script lexico dans le r�pertoire ProjetCompil/Syntaxe/Test.
	
## Analyse syntaxique
- Compl�ter le fichier syntaxe.cup (voir Syntaxe.txt et ArbreAbstrait.txt)
- Ecrire des programmes JCas de test dans ProjetCompil/Syntaxe/Test.
- Effectuer des tests avec le script syntaxe dans le r�pertoire ProjetCompil/Syntaxe/Test
- Ce script utilise le programme principal pr�sent dans la classe TestSynt.

