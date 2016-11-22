package ProjetCompil.Gencode.Src;

import java.security.spec.ECFieldF2m;

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
    	      //actions communes à réaliser
    	      Registre rd;
    	      Etiq et1Faux = Etiq.nouvelle("et1faux");
    	      Etiq suite = Etiq.nouvelle("suite");
    	      if((rd=Reg.Allouer())!=null) {
    	        coder_EXP(a.getFils1(), rc);
    	        coder_EXP(a.getFils2(), rd);
    	        Prog.ajouter(Inst.creation2(Operation.CMP, Operande.opDirect(rc), Operande.creationOpEntier(-1))); //CMP rc, #-1
    	        Prog.ajouter(Inst.creation1(Operation.BEQ, Operande.creationOpEtiq(et1Faux))); //BEQ aF
    	        Prog.ajouter(Inst.creation2(Operation.CMP, Operande.opDirect(rd), Operande.creationOpEntier(1))); //CMP r3, #1 ; a est vrais
    	        Prog.ajouter(Inst.creation1(Operation.SEQ, Operande.opDirect(rc))); //SEQ r1 ; c prend la valeur de b
    	        Prog.ajouter(Inst.creation1(Operation.BRA, Operande.creationOpEtiq(suite)));//BRA suite
    	        Prog.ajouter(et1Faux);
    	        Prog.ajouter(Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), Operande.opDirect(rc)));//aF: 	LOAD #-1, R1 ; a est faux
    	        Prog.ajouter(suite);
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
    if(false) { //condition si on match une des cases au dessus
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
    if(false) { //condition si on match une des cases au dessus
      //actions communes à réaliser
      return;
    }
  }


  private Operande getOpFromPlace(Arbre a) {
    //retourne un objet OperandeDirect correspondant à l'emplacement global voulu
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



