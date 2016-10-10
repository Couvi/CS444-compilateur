/**
 * Type énuméré pour les erreurs contextuelles.
 * Ce type énuméré définit toutes les erreurs contextuelles possibles et 
 * permet l'affichage des messages d'erreurs pour la passe 2.
 */

package ProjetCompil.Verif.Src;

public enum ErreurContext {
   
   /**
   * ErreurNonRepertoriee signifie qu'aucune autre erreur ne correspond au problème
   * (cela ne devrait jamais ce produire ...)
   */
   ErreurNonRepertoriee,
   
   /**
   * TypeInconnu est déclanché lorsque l'on cherche un type qui n'existe pas
   */
   TypeInconnu, 
   
   /**
   * RedeclarationIdent est généré lorsque un identificateur à déjà été déclaré ou
   * est réservé.
   **/
   RedeclarationIdent,
   
   /**
   * IdentificateurInconnu est généré lorsque un identificateur n'a pas été déclaré préalablement
   **/
   IdentificateurInconnu,
   
   /**
   * BorneNonEntier est généré lorsque l'on veut créer un type Interval mais les bornes ne sont pas des entiers
   **/
   BorneNonEntier,

   /**
   * ProblemeCompilateur est généré lorsque le compilateur est en défault
   * (cela ne devrait jamais ce produire ...)
   **/
   ProblemeCompilateur,

   /**
   * TypeIndex est généré lorsque l'index donné pour un type Array n'est pas du bon interval
   **/
   TypeIndex,
   
   /**
   * TypesNonCompatible signifie que l'opération n'est pas possible car les types des variables 
   * dans l'opération ne sont pas compatibles
   */
   TypesNonCompatible,
   
   /**
   * IndexationNonArray signifie que l'on essaie d'indexer un type qui n'est pas un tableau
   */
   IndexationNonArray;
   
   /**
   * Cette méthode permet de lever une exception en précisant l'erreur (type et ligne)
   * 
   * <b>Pour l'utiliser : </b> <br>
   * ErreurContext err = ErreurContext.Nomdelerreur;
   * err.leverErreurContext(String detail, int numLigne);
   **/
   void leverErreurContext(String s, int numLigne) throws ErreurVerif {
      System.err.println("Erreur contextuelle : ");
      switch (this) {
	case TypeInconnu :
		System.err.print("Type inconnu ("+s+") ");
		break;
	case RedeclarationIdent :
		System.err.print("L'identificateur "+s+" a déjà été déclaré ou est reservé ");
		break;
	case IdentificateurInconnu :
		System.err.print("L'identificateur "+s+" n'a pas été déclaré et est inconnu ");
		break;		
	case BorneNonEntier :
		System.err.print("Les bornes de l'Interval ne sont pas des entier ");
		break;	
	case ProblemeCompilateur : 
		System.err.print("Erreur du compilateur ("+s+")");
		break;
	case TypesNonCompatible :
		System.err.print("Types non compatible ( "+s+") ");
		break;
	case TypeIndex :
		System.err.print("Index de type non valide ( "+s+") ");
		break;
	case IndexationNonArray :
		System.err.print("Un type qui n'est pas un tableau ne peut pas être indexé ");
		break;
      	default:
        	System.err.print("non repertoriee");	 
      }
      System.err.println(" ... ligne " + numLigne);
      StackTraceElement[] stack = Thread.currentThread().getStackTrace();
      for (int i=0; i< stack.length; i++)
         System.err.println(stack[i]);
      throw new ErreurVerif();
   }

}
