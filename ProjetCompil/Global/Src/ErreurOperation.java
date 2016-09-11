package ProjetCompil.Global.Src;

/**
* Exception levée lorsqu'une opération est incorrecte.
*/


class ErreurOperation extends RuntimeException { 
  public ErreurOperation(String message) {
     super(message);
  }
}