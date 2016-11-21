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
      if((rd=Reg.allouer())!=null) {
        coder_EXP(a.getFils1(), rc);
        coder_EXP(a.getFils2(), rd);
        Prog.ajouter(Inst.creation2(op, Operande.opDirect(rd), Operande.opDirect(rc)));
        Reg.liberer(rd);
        return;
      }
      else {
        coder_EXP(a.getFils2(), rc);
        int temp = Pile.allouer(); //allouer un emplacement sur la pile
        Prog.ajouter(Inst.creation2(
          Operation.STORE, Operande.opDirect(rc), 
                           Operande.creationOpIndirect(temp,Registre.LB)));
        coder_EXP(a.getFils1(), rc);
        Prog.ajouter(Inst.creation2(
          op, Operande.creationOpIndirect(temp,Registre.LB), 
              Operande.opDirect(rc)));
        Pile.liberer(temp);//libèrer temp
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
    if(false) { //condition si on match une des cases au dessus
      //actions communes à réaliser
      return;
    }

    //opérations unaires TODO
    switch (a.getNoeud()) {
    //remplir les cas (ne pas oublier le break)
    case PlusUnaire: return;
    case Non:
    case MoinsUnaire: 
    	coder_EXP(a.getFils1(), rc);
        Prog.ajouter(Inst.creation2(Operation.OPP, Operande.opDirect(rc), Operande.opDirect(rc)));
      return;
    default: break;
    }
    

    //expressions feuilles TODO
    switch (a.getNoeud()) {
    //remplir les cas (ne pas oublier le break)
    case Chaine: break;
    case Ident: break;
    case Index: break;
    case Entier:
      Prog.ajouter(Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getEntier()), Operande.opDirect(rc)));
      break;
    case Reel:
      Prog.ajouter(Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getReel()), Operande.opDirect(rc)));
      break;

    default: break;
    }
    if(false) { //condition si on match une des cases au dessus
      //actions communes à réaliser
      return;
    }
  }


  private Operande getOpFromPlace(Arbre a) {
    //retourne un objet OperandeDirect correspondant à l'emplacement global voulu
	  /*a =noeud chaine ou ident
	   d=identificateur/variable
	  if (d.getNomVar().equals(a.getChaine()){
	  
	   }
	    */
    return null;
  }
  public void coder_INST(Arbre a) {
    Registre rc = Registre.R15; //registre réservé pour les instructions
    switch (a.getNoeud()) {
    case Nop: break;//TODO mettre un commentaire?
    case Affect: {
      //Operande destination = getOpFromPlace(a.getFils1());
      coder_EXP(a.getFils2(), rc);
      //Prog.ajouter(Inst.creation2(Operation.STORE, Operande.opDirect(rc), destination);
      break;
    }
    case Pour:
    case TantQue:
    case Si:
    case Ecriture:
    case Lecture:
    case Ligne:
    default: break;
    }
  }

  private void coder_LISTE_INST(Arbre a) {
		switch (a.getNoeud()) {
		case Vide: 
			return;
		case ListeInst: 
			coder_LISTE_INST(a.getFils1());
			coder_INST(a.getFils2());
			return;
		default: 
		}
	}

  public void coder_LISTE_DECL(Arbre a) {
    switch (a.getNoeud()) {
    case Vide: 
      return;
    case ListeIdent: 
      coder_LISTE_DECL(a.getFils1());
      coder_LISTE_IDF(a.getFils1());
      return;
    default: 
    }
  }
  public void coder_LISTE_IDF(Arbre a) {
    switch (a.getNoeud()) {
    case Vide: 
      return;
    case ListeIdent: 
      coder_LISTE_IDF(a.getFils1());
      Pile.addGlobale(a.getFils2());
      return;
    default:
    }
  }
  
  

  static Prog coder(Arbre a) {
    Prog.ajouterGrosComment("Programme généré par JCasc");
    Reg.init();
    Generation gen = new Generation();
    gen.coder_LISTE_DECL(a.getFils1());
    Pile.finDeclaration();
    gen.coder_LISTE_INST(a.getFils2());


    // Fin du programme
    // L'instruction "HALT"
    Inst inst = Inst.creation0(Operation.HALT);
    // On ajoute l'instruction à la fin du programme
    Prog.ajouter(inst);

    // On retourne le programme assembleur généré
    return Prog.instance(); 
  }
}



