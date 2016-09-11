
package ProjetCompil.Verif.Src;

/**
 * Exception levée en cas d'erreur interne lors des vérifications contextuelles.
 */

public class ErreurInterneVerif extends RuntimeException {

   public ErreurInterneVerif(String s) {
      super("===========================================================\n" + 
            "                ERREUR INTERNE VERIF                       \n" + 
            "===========================================================\n" + 
            s + "\n" + 
            "===========================================================\n");
   }

}


