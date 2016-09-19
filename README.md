# CS444-compilateur
## Modifier le CLASSPATH
Executer la commande `source` avec le fichier setenv.sh a la racine

## Compiler
- cd ProjetCompil
- make

## Tester l'analyse lexicale : Fichiers dans dossier devlex
Dans le dossier Systaxe/Src
"make lextest-devlex"

## Tester l'analyse syntaxique : Fichiers dans dossier devsyn
Dans le dossier Systaxe/Src
"make syntest-devsyn"

# Passe 1 : Travail effectu�

## Analyse lexicale
- Dans le r�pertoire ProjetCompil/Syntaxe/Src, compl�ter le fichier lexical.flex (voir Lexicographie.txt)
- Pour tester, utiliser le script lexico dans le r�pertoire ProjetCompil/Syntaxe/Test.
	
## Analyse syntaxique
- Compl�ter le fichier syntaxe.cup (voir Syntaxe.txt et ArbreAbstrait.txt)
- Ecrire des programmes JCas de test dans ProjetCompil/Syntaxe/Test.
- Effectuer des tests avec le script syntaxe dans le r�pertoire ProjetCompil/Syntaxe/Test
- Ce script utilise le programme principal pr�sent dans la classe TestSynt.

