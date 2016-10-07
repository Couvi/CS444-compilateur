// ----------------------------------------------------------------------------
// Testeur pour la passe de vérifications contextuelles
// ----------------------------------------------------------------------------

package ProjetCompil.Verif.Src;

import ProjetCompil.Global.Src.*;
import ProjetCompil.Syntaxe.Src.*;

/** 
 * Classe qui permet de tester la passe de vérifications contextuelles.
 */

public class TestVerif {


   /**
    * Méthode de test de la passe de vérifications contextuelles.
    */
   public static void main(String args[]) throws Exception {

      try {
         // Appel de l'analyseur syntaxique et récupération de l'arbre résultat
         Arbre arbre = parser.analyseSyntaxique(args);
         //arbre.afficher(0);
         //arbre.decompiler(0);
         
         // On construit un verificateur de passe 2
         Verif passe2 = new Verif();
         passe2.verifierDecorer(arbre); 
         arbre.afficher(1);
         // Décompilation de l'arbre
         arbre.decompiler(1);
         
      } catch (ErreurLexicale e) {
         // Recuperation de l'exception ErreurLexicale
         System.exit(-1);
      } catch (ErreurSyntaxe e) {
         // Recuperation de l'exception ErreurSyntaxe
         System.exit(-1);
      } catch (ErreurVerif e) {
         // Recuperation de l'exception ErreurVerif
         System.exit(-1);
      }
   }

}

