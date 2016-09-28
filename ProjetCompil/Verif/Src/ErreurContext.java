/**
 * Type énuméré pour les erreurs contextuelles.
 * Ce type énuméré définit toutes les erreurs contextuelles possibles et 
 * permet l'affichage des messages d'erreurs pour la passe 2.
 */

// -------------------------------------------------------------------------
// A COMPLETER, avec les différents types d'erreur et les messages d'erreurs 
// correspondants
// -------------------------------------------------------------------------

package ProjetCompil.Verif.Src;

public enum ErreurContext {
   
   ErreurNonRepertoriee, TypeInconnu, Erreurchiante;

   void leverErreurContext(String s, int numLigne) throws ErreurVerif {
      System.err.println("Erreur contextuelle : ");
      switch (this) {
	      case TypeInconnu :
			System.err.println("Type inconnu ("+s+") ");
			break;
         default:
            System.err.print("non repertoriee");
	 
      }
      System.err.println(" ... ligne " + numLigne);
      throw new ErreurVerif();
   }

}


//*** Pour utiliser tout ce merdier !!! ****/
// ErreurContext err = ErreurContext.Erreurdemerde;
// err.leverErreurContext(String s, int numLigne)