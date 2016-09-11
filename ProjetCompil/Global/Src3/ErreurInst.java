package ProjetCompil.Global.Src3;

/**
 * Exception levée lorsqu'une précondition concernant les instructions n'est 
 * pas respectée.
 */

public class ErreurInst extends RuntimeException {
   public ErreurInst(String message) {
      super(message);
   }
}

