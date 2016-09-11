package ProjetCompil.Global.Src;

/**
* Exception levée lorsqu'une ligne de code de machine abstraite n'est pas correcte.
*/

class ErreurLigne extends RuntimeException { 
  public ErreurLigne(String message) {
     super(message);
  }
}
