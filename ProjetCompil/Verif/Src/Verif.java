package ProjetCompil.Verif.Src;

import ProjetCompil.Global.Src.*;
 
/**
 * Cette classe permet de réaliser la vérification et la décoration 
 * de l'arbre abstrait d'un programme.
 */
public class Verif {

   private Environ env; // L'environnement des identificateurs

   /**
    * Constructeur.
    */
   public Verif() {
      env = new Environ();
   }

   /**
    * Vérifie les contraintes contextuelles du programme correspondant à 
    * l'arbre abstrait a, qui est décoré et enrichi. 
    * Les contraintes contextuelles sont décrites 
    * dans Context.txt.
    * En cas d'erreur contextuelle, un message d'erreur est affiché et 
    * l'exception ErreurVerif est levée.
    */
   public void verifierDecorer(Arbre a) throws ErreurVerif {
      verifier_PROGRAMME(a);
   }

   /**
    * Initialisation de l'environnement avec les identificateurs prédéfinis.
    */
   private void initialiserEnv() {
      Defn def;
      // integer
      def = Defn.creationType(Type.Integer);
      def.setGenre(Genre.PredefInteger);
      env.enrichir("integer", def);
      // ------------
      // A COMPLETER
      // ------------
      // string
      def = Defn.creationType(Type.String);
      //pas de genre...
      env.enrichir("string", def);
      // real
      def = Defn.creationType(Type.Real);
      def.setGenre(Genre.PredefReal);
      env.enrichir("real", def);
      // boolean
      def = Defn.creationType(Type.Boolean);
      def.setGenre(Genre.PredefBoolean);
      env.enrichir("boolean", def);
   }

   /**************************************************************************
    * PROGRAMME
    **************************************************************************/
   private void verifier_PROGRAMME(Arbre a) throws ErreurVerif {
      initialiserEnv();
      switch(a.getNoeud()) {
        case Programme: {
          verifier_LISTE_DECL(a.getFils1());
          verifier_LISTE_INST(a.getFils2());
          return;
        }
        default: {
          throw new ErreurVerif();
        }
      }
   }

   /**************************************************************************
    * LISTE_DECL
    **************************************************************************/
   private void verifier_LISTE_DECL(Arbre a) throws ErreurVerif {
      switch(a.getNoeud()) {
        case Vide: {
          return;
        }
        case ListeDecl: {
          verifier_LISTE_DECL(a.getFils1());
          verifier_DECL(a.getFils2());
          return;
        }
        default: {
          throw new ErreurVerif();
        }
      }
   }

   /**************************************************************************
    * LISTE_INST
    **************************************************************************/
   private void verifier_LISTE_INST(Arbre a) throws ErreurVerif {
      // A COMPLETER
   }  

  private void verifier_DECL(Arbre a) throws ErreurVerif {
    switch(a.getNoeud()) {
      case Decl: {
        Type type = verifier_TYPE(a.getFils2());
        verifier_LISTE_IDF(a.getFils1(), type);
        return;
      }
      default: {
        throw new ErreurVerif();
      }
    }
  }

  private void verifier_LISTE_IDF(Arbre a, Type t) throws ErreurVerif {
    switch(a.getNoeud()) {
      case Vide: {
        return;
      }
      case ListeIdent: {
        verifier_LISTE_IDF(a.getFils1(),t);
        verifier_IDF(a.getFils2());
        env.enrichir(a.getFils2().getChaine(), Defn.creationVar(t));
      }
      default: {
        throw new ErreurVerif();
      }
    }
  }

  private void verifier_IDF(Arbre a) {

  }

  private Type verifier_TYPE(Arbre a) throws ErreurVerif {
    switch(a.getNoeud()) {
      case Ident: {
        verifier_IDF(a);
        return null; //TODO
      }
      case Intervalle: {
        Type t = verifier_INTERVALLE(a);
        return t;
      }
      case Tableau: {
        Type t = verifier_TABLEAU(a);
        return t;
      }
      default: {
        throw new ErreurVerif();
      }
    }
  }

  private Type verifier_INTERVALLE(Arbre a) {
    return null; //TODO
  }

  private Type verifier_TABLEAU(Arbre a) {
    return null; //TODO
  }
   // ------------------------------------------------------------------------
   // COMPLETER les operations de vérifications et de décoration pour toutes 
   // les constructions d'arbres
   // ------------------------------------------------------------------------
/*
//patron:
      switch(a.getNoeud()) {
        case : {
          
        }
        default: {
          throw new ErreurVerif();
        }
      }
*/
}
