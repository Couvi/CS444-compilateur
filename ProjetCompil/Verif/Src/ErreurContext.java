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
   * Problemecompilateur est généré lorsque le compilateur est en défault
   * (cela ne devrait jamais ce produire ...)
   **/
   Problemecompilateur,
   
   /**
   * TypesNonCompatible signifie que l'opération n'est pas possible car les types des variables 
   * dans l'opération ne sont pas compatible
   */
   TypesNonCompatible;
   
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
			System.err.println("Type inconnu ("+s+") ");
			break;
		case RedeclarationIdent :
			System.out.println("L'identificateur "+s+" a déjà été déclaré ou est reservé ");
			break;
		case IdentificateurInconnu :
			System.out.println("L'identificateur "+s+" n'a pas été déclaré et est inconnu ");
			break;		
		case BorneNonEntier :
			System.out.println("Les bornes de l'Interval ne sont pas des entier ");
			break;	
		case Problemecompilateur : 
			System.out.println("Erreur du compilateur (il n'y a rien à faire...)";
			break;
		case TypesNonCompatible :
			System.out.println("Types non compatible ( "+s+") ");
			break;
         default:
        	 System.err.print("non repertoriee");
	 
      }
      System.err.println(" ... ligne " + numLigne);
      throw new ErreurVerif();
   }

}
