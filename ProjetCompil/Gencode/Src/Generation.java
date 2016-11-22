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

		// opérations binaires logiques
		switch (a.getNoeud()) {
		// on note que les commentaires ne correspondent pas exactement au code,
		// ce sont plus des aides qu'autre chose
		case Et: {
			Etiq suite = Etiq.nouvelle("suite");
			Etiq etOp1Faux = Etiq.nouvelle("etOp1Faux");
			// debut: %calculer a dans r1
			coder_EXP(a.getFils1(), rc);
			// CMP rc, #0
			Prog.ajouter(Inst.creation2(Operation.CMP, Operande.opDirect(rc),
					Operande.creationOpEntier(0)));
			// BEQ aF
			Prog.ajouter(Inst.creation1(Operation.BEQ,
					Operande.creationOpEtiq(etOp1Faux)));
			// %calculer b dans rc
			coder_EXP(a.getFils2(), rc);
			// CMP r1, #1 ; a est vrais
			Prog.ajouter(Inst.creation2(Operation.CMP, Operande.opDirect(rc),
					Operande.creationOpEntier(1)));
			// SEQ r1 ; c prend la valeur de b
			Prog.ajouter(Inst.creation1(Operation.SEQ, Operande.opDirect(rc)));
			// BRA suite
			Prog.ajouter(Inst.creation1(Operation.BRA,
					Operande.creationOpEtiq(suite)));
			// aF: LOAD #0, R1 ; a est faux
			Prog.ajouter(etOp1Faux);
			Prog.ajouter(Inst.creation2(Operation.LOAD,
					Operande.creationOpEntier(0), Operande.opDirect(rc)));
			Prog.ajouter(suite);
			return;
		}
		case Ou: {
			Etiq suite = Etiq.nouvelle("suite");
			Etiq ouOp1Faux = Etiq.nouvelle("ouOp1Faux");
			// debut: %calculer a dans r1
			coder_EXP(a.getFils1(), rc);
			// CMP rc, #0
			Prog.ajouter(Inst.creation2(Operation.CMP, Operande.opDirect(rc),
					Operande.creationOpEntier(0)));
			// BEQ aF
			Prog.ajouter(Inst.creation1(Operation.BEQ,
					Operande.creationOpEtiq(ouOp1Faux)));
			// aT: LOAD #1, r1 ; a est vrais
			Prog.ajouter(Inst.creation2(Operation.LOAD,
					Operande.creationOpEntier(1), Operande.opDirect(rc)));
			// BRA suite
			Prog.ajouter(Inst.creation1(Operation.BRA,
					Operande.creationOpEtiq(suite)));
			// %calculer b dans rc
			coder_EXP(a.getFils2(), rc);
			// aF : CMP r1, #1 ; a est faux
			Prog.ajouter(ouOp1Faux);
			Prog.ajouter(Inst.creation2(Operation.CMP, Operande.opDirect(rc),
					Operande.creationOpEntier(1)));
			// SEQ r1 ; c prend la valeur de b
			Prog.ajouter(Inst.creation1(Operation.SEQ, Operande.opDirect(rc)));
			Prog.ajouter(suite);
		}
		case Egal:
			op = Operation.SEQ;
			break;
		case InfEgal:
			op = Operation.SLE;
			break;
		case SupEgal:
			op = Operation.SGE;
			break;
		case NonEgal:
			op = Operation.SNE;
			break;
		case Inf:
			op = Operation.SLT;
			break;
		case Sup:
			op = Operation.SGT;
			break;
		default:
			break;
		}
		if (op != null) {
			// actions communes à réaliser
			Registre rd;
			if ((rd = Reg.allouer()) != null) {
				coder_EXP(a.getFils1(), rc);
				coder_EXP(a.getFils2(), rd);
				Prog.ajouter(Inst.creation2(Operation.CMP, Operande.opDirect(rd),
						Operande.opDirect(rc)));
				Prog.ajouter(Inst.creation1(op, Operande.opDirect(rc)));
				Reg.liberer(rd);
			} else {
				coder_EXP(a.getFils2(), rc);
				int temp = Pile.allouer(); // allouer un emplacement sur la pile
				Prog.ajouter(Inst.creation2(Operation.STORE,
						Operande.opDirect(rc),
						Operande.creationOpIndirect(temp, Registre.LB)));
				coder_EXP(a.getFils1(), rc);
				Prog.ajouter(Inst.creation2(Operation.CMP,
						Operande.creationOpIndirect(temp, Registre.LB),
						Operande.opDirect(rc)));
				Prog.ajouter(Inst.creation1(op, Operande.opDirect(rc)));
				Pile.liberer(temp);// libèrer temp
			}
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


 /* private Operande getOpFromPlace(Arbre a) {
    //retourne un objet OperandeDirect??? correspondant à l'emplacement global voulu
	  
	   -d=identificateur/variable dans une liste de declaration
	   -d.getIndex servirait de déplacement
	  if (d.getNomVar().equals(a.getChaine()){
		  Operande destination =  Operande.creationOpIndirect(d.getIndex, Registre.GB);
	   }
	   return destination;
	   
    return null;
  }*/
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



