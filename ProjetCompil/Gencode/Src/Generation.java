package ProjetCompil.Gencode.Src;

import ProjetCompil.Global.Src.*;
import ProjetCompil.Global.Src3.*;

/**
 * Génération de code pour un programme JCas à partir d'un arbre décoré.
 */

class Generation {
   
   /**
    * Méthode principale de génération de code.
    * Génère du code pour l'arbre décoré a.
    */
  public void coder_EXP(Arbre a, Registre rc) {
    Operation op = null;

    switch (a.getNoeud()) {
    //opérations binaires arithmétiques
    case Plus: op=Operation.ADD; break;
    case Moins: op=Operation.SUB; break;
    case Mult: op=Operation.SUB; break;
    case DivReel: op=Operation.SUB; break;
    case Reste: op=Operation.SUB; break;
    case Quotient: op=Operation.SUB; break;
    default: break;
    }

    //if(il reste des registres?) { TODO
      coder_EXP(a.getFils1(), rc);
      Registre rd = null; //allouer un registre TODO
      coder_EXP(a.getFils2(), rd);
      Prog.ajouter(Inst.creation2(op, new OperandeDirect(rd), new OperandeDirect(rc)));
      //libèrer rd
    //} fin du if
    //else {
      coder_EXP(a.getFils2(), rc);
      int temp = 0; //allouer un emplacement sur la pile
      Prog.ajouter(Inst.creation2(
        Operation.STORE, new OperandeDirect(rc), 
                         new OperandeIndirect(temp,Registre.LB)));
      coder_EXP(a.getFils1(), rc);
      Prog.ajouter(Inst.creation2(
        op, new OperandeIndirect(temp,Registre.LB), 
            new OperandeDirect(rc)));
      //libèrer temp
    //}fin else
    
  }

  static Prog coder(Arbre a) {
    Prog.ajouterGrosComment("Programme généré par JCasc");

    // -----------
    // A COMPLETER
    // -----------
    

    // Fin du programme
    // L'instruction "HALT"
    Inst inst = Inst.creation0(Operation.HALT);
    // On ajoute l'instruction à la fin du programme
    Prog.ajouter(inst);

    // On retourne le programme assembleur généré
    return Prog.instance(); 
  }
}



