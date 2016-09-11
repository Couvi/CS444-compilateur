package ProjetCompil.Syntaxe.Src;

/**
 * Exception levée en cas d'erreur interne dans l'analyseur lexical.
 */

public class ErreurInterneLexical extends RuntimeException {
   ErreurInterneLexical(String message) {
      super(message);
   }
}

