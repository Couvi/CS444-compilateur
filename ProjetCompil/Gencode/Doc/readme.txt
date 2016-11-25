========== GESTION DES REGISTRES ==========
Nous avons crée une classe Registre qui tiendra à jour l'occupation des registres. 
Tout d'abord, certains registres sont reservé, et donc ne sont pas allouable.
La méthode allouer() permet d'obtenir un registre qui est libre. Elle return null si plus aucun registre n'est libre et qu'il faut utiliser la pile.
La méthode liberer() permet de rendre un registre qui a été demandé précédemment. Il va donc être libéré et pourra être utilisé ultérieurement.


========== GESTION DE LA PILE ==========
Pour la pile, nous avons décidé d'exprimer tous nos déplacements à partir de GB qui est situé juste avant la stack. 1(GB) est donc le premier mot de la pile.

Au début de la génération, la méthode addGlobale() permet d'ajouter une variable dans le programme, de réserver la place pour cette variable dans la stack et de noter dans une HashMap, la position dans la stack de cette variable. La HashMap est composé de 2 élément : le nom de la variable (la clé) et la position dans la stack par rapport à GB. Une fois toutes les déclarations terminées, la méthode finDeclaration() permet d'indiquer que les déclarations sont terminée, et donc que l'on peut écrire dans le programme la réservation de ses variables dans la stack. Avec la méthode getGlobale(), on peut alors récupèrer la position de ces varibles dans la stack.

Ensuite la pile peut être utile si tout les registres sont occupés. On peut alors demander un espace dans la stack avec allouer() puis le libérer quand on en a plus besoin avec liberer().

Le fait de centraliser la gestion de la pile, permet de faire systèmatiquement des vérifications sur les débordements, par exemple.


========== ALGORITHME DU GÉNÉRATION DU CODE ==========
A compléter !


========== GESTION DES ERREURS POSSIBLES A L'EXECUTION ==========
Afin de gérer de manière simple les différentes erreurs potentielles lors de l'exécution, nous avons crée une librarie, en charge de la gestion de ces erreurs.
Ainsi, lorsque l'on fait des vérifications lors de l'exécution, s'il y a un problème, on peut drectement appeler le code correspondant à l'aide d'un label.

Lors de la génération du code du programme, on ajoute du code afin d'effectuer des vérifications à l'exécution (borne intervalle, ...). Ces vérification peuvent conduire à une erreur. Il faut donc afficher l'erreur et arrêter le programme.
Cette librairie permet de centraliser les messages d'erreurs. Durant la génération de code, on fait appel à ces erreurs pour récupèrer le label correspondant.
Lorsque la génération de code est terminée, elle place à la fin les code à exécuter concernant les différentes erreurs qui peuvent se produire avec ce programme (avec les labels).
La liste des erreurs détaillé est disponible dans la Javadoc (Html/ProjetCompil/Gencode/Src/Library.html)


===================================================================
Pour plus de détails concernant les différentes méthodes utilisées, se référerer à la JavaDoc du projets dans Html/ProjetCompil/Gencode/Src/*
