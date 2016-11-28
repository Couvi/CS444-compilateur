========== GESTION DES REGISTRES ==========
Nous avons crée une classe Registre qui tiendra à jour l'occupation des registres. 
Tout d'abord, certains registres sont reservés, et donc ne sont pas allouables.
La méthode allouer() permet d'obtenir un registre qui est libre. Elle retourne null si plus aucun registre n'est libre et qu'il faut utiliser la pile.
La méthode liberer() permet de rendre un registre qui a été demandé précédemment. Il va donc être libéré et pourra être utilisé ultérieurement.


========== GESTION DE LA PILE ==========
Pour la pile, nous avons décidé d'exprimer tous nos déplacements à partir de GB qui est situé juste avant la stack. 1(GB) est donc le premier mot de la pile.

Au début de la génération, la méthode addGlobale() permet d'ajouter une variable dans le programme, de réserver la place pour cette variable dans la stack, et de noter dans une HashMap la position dans la stack de cette variable. La HashMap est composé de 2 éléments : le nom de la variable (la clé) et la position dans la stack par rapport à GB. Une fois toutes les déclarations terminées, la méthode finDeclaration() permet d'indiquer que les déclarations sont terminée, et donc que l'on peut écrire dans le programme les instruction de réservation de ces variables dans la stack. Avec la méthode getGlobale(), on peut alors récupèrer la position de ces variables dans la stack.

Ensuite la pile peut être utile si tout les registres sont occupés. On peut alors demander un espace dans la stack avec allouer() puis le libérer quand on en a plus besoin avec liberer().

Le fait de centraliser la gestion de la pile, permet de faire systèmatiquement des vérifications sur les débordements, par exemple.


========== ALGORITHME DU GÉNÉRATION DU CODE ==========
 La génération de code pour un programme JCas se fait à partir d'un arbre décoré valide.
 la classe Génération classe fonctionne en parcourant l'arbre de manière décendante
 comme lors de la verification. Cependant, aucune verification n'est réalisé sur l'arbre.
 Les verifications lors de l'execution qui sont réalisées: 
 - verification des bornes lors de l'affectation
 - verification d'overflow lors des opérations arithmétiques
 - verification de stack overflow lors de l'allocation de variables sur la pile
 
 Utilisation des registres:
 les 3 premiers registres R0, R1 et R2 sont libres d'accès à toutes les fonctions à tout moment
 Aucune garantie n'est faite sur la durée de vie de ces derniers, il servent donc principalement de manière 
 locale à une fonction pour la manipulation de valeurs.
 Les autres registres servent de variables locales lors de l'évaluation des expressions.
 Ces derniers sont alloués et leur durée de vie est donc garanti par la fonction de génération qui l'utilise.
 Quand il n'y a plus de registres libres, on utilise la pile (cela arrive donc rarement pour une évaluation d'expression).
  
 Utilisation de la pile:
 Certaines instructions complexes (copie de tableau) necessitent plusieurs variables temporaires, les 3 registres libres ne suffisent pas,
 dans ce cas il aurait été necessaire de recoder plusieurs fois la même instruction avec des allocations de registres/pile 
 ou de créer plus de registres libres, ce qui est censé être une exception...
 pour simplifier le code, nous avons seulement utilisé la pile et les 3 registres libres dans ce cas.
 Enfin la pile peut servir à allouer des variables locales pour l'évaluation des expressions quand il n'y a plus de registres de libre.
 
========== GESTION DES ERREURS POSSIBLES A L'EXECUTION ==========
Afin de gérer de manière simple les différentes erreurs potentielles lors de l'exécution, nous avons crée une librarie, en charge de la gestion de ces erreurs.
Ainsi, lorsque l'on fait des vérifications lors de l'exécution, s'il y a un problème, on peut drectement appeler le code correspondant à l'aide d'un label.

Lors de la génération du code du programme, on ajoute du code afin d'effectuer des vérifications à l'exécution (borne intervalle, ...). Ces vérification peuvent conduire à une erreur. Il faut donc afficher l'erreur et arrêter le programme.
Cette librairie permet de centraliser les messages d'erreurs. Durant la génération de code, on fait appel à ces erreurs pour récupèrer le label correspondant.
Lorsque la génération de code est terminée, elle place à la fin les code à exécuter concernant les différentes erreurs qui peuvent se produire avec ce programme (avec les labels).
La liste des erreurs détaillé est disponible dans la Javadoc (Html/ProjetCompil/Gencode/Src/Library.html)


===================================================================
Pour plus de détails concernant les différentes méthodes utilisées, se référerer à la JavaDoc du projets dans Html/ProjetCompil/Gencode/Src/*
