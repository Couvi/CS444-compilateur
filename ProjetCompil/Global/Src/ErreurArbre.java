package ProjetCompil.Global.Src;

/**
 * Exception levée lorsqu'une précondition concernant les arbres n'est 
 * pas respectée.
 */

public class ErreurArbre extends RuntimeException { 
   public ErreurArbre(String message) {
      super(message);
   }
}


