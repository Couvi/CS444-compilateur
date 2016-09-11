package ProjetCompil.Global.Src;

/**
 * Exception levée lorsqu'une précondition concernant les defns n'est 
 * pas respectée.
 */

public class ErreurDefn extends RuntimeException { 
   public ErreurDefn(String message) {
      super(message);
   }
}


