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
    case Mult: op=Operation.MUL; break;
    case DivReel: op=Operation.DIV; break;
    case Reste: op=Operation.MOD; break;
    case Quotient: op=Operation.DIV; break;
    default: break;
    }
    if(op!=null) {
      //actions communes à réaliser
      Registre rd;
      if((rd=Reg.Allouer_Reg())!=null) {
        coder_EXP(a.getFils1(), rc);
        coder_EXP(a.getFils2(), rd);
        Prog.ajouter(Inst.creation2(op, Operande.opDirect(rd), Operande.opDirect(rc)));
        Reg.Liberer(rd);
      }
      else {
        coder_EXP(a.getFils2(), rc);
        int temp = 0; //allouer un emplacement sur la pile
        Prog.ajouter(Inst.creation2(
          Operation.STORE, Operande.opDirect(rc), 
                           Operande.creationOpIndirect(temp,Registre.LB)));
        coder_EXP(a.getFils1(), rc);
        Prog.ajouter(Inst.creation2(
          op, Operande.creationOpIndirect(temp,Registre.LB), 
              Operande.opDirect(rc)));
        //libèrer temp
      }
      return;
    }

    //opérations binaires logiques
    switch (a.getNoeud()) {
    //remplir les cas (ne pas oublier le break)
    case Et: 
    case Ou: 
    case Egal: 
    case InfEgal: 
    case SupEgal: 
    case NonEgal: 
    case Inf: 
    case Sup: 
    default: break;
    }
    if(op!=null) {
      //actions communes à réaliser
      return;
    }

    //opérations unaires TODO
    switch (a.getNoeud()) {
    //remplir les cas (ne pas oublier le break)
    case PlusUnaire: 
    case MoinsUnaire: 
    case Non:
    default: break;
    }
    if(op!=null) {
      //actions communes à réaliser
      return;
    }

    //expressions feuilles TODO
    switch (a.getNoeud()) {
    //remplir les cas (ne pas oublier le break)
    case Chaine: 
    case Ident: 
    case Index: 
    case Entier:
    case Reel:
    default: break;
    }
    if(op!=null) {
      //actions communes à réaliser
      return;
    }
  }

  public void coder_INST(Arbre a) {
    //TODO
  }

  static Prog coder(Arbre a) {
    Prog.ajouterGrosComment("Programme généré par JCasc");
    Reg.init();
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



