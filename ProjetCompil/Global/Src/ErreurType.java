package ProjetCompil.Global.Src;

/**
 * Exception levée lorsqu'une précondition concernant les types n'est 
 * pas respectée.
 */

public class ErreurType extends RuntimeException { 
   public ErreurType(String message) {
      super(message);
   }
}


