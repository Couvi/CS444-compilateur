package ProjetCompil.Gencode.Src;

import java.security.spec.ECFieldF2m;

import ProjetCompil.Global.Src.*;
import ProjetCompil.Global.Src3.*;
/**
 * Génération de code pour un programme JCas à partir d'un arbre décoré.
 */


public class Generation {
  
  private static Library lib = Library.get_instance();
  private Registre rx = Registre.R0; //registre réservé pour les instructions
  private Registre ry = Registre.R1;
  private Registre rz = Registre.R2;
  private boolean noVerif = false;
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
        Prog.ajouter(Inst.creation2(op, 
                                    Operande.opDirect(rd), 
                                    Operande.opDirect(rc)));
        Reg.liberer(rd);
      }
      else {
        coder_EXP(a.getFils2(), rc);
        int temp = Pile.allouer(); //allouer un emplacement sur la pile
        Prog.ajouter(Inst.creation2(Operation.STORE, 
                                    Operande.opDirect(rc), 
                                    Operande.creationOpIndirect(temp,Registre.LB)));
        coder_EXP(a.getFils1(), rc);
        Prog.ajouter(Inst.creation2(op, 
                                    Operande.creationOpIndirect(temp,Registre.LB), 
                                    Operande.opDirect(rc)));
        Pile.liberer(temp);//libèrer temp
      }
      if(a.getDecor().getType().getNature() == NatureType.Interval) {
        coder_verif_borne_interval(a.getDecor().getType(), rc);
      }
      //coder_verif_borne_interval(a.getDecor().getType(), rc);
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
			Prog.ajouter(Inst.creation2(Operation.CMP, 
                                  Operande.creationOpEntier(0),
                                  Operande.opDirect(rc)));
			// BEQ aF
			Prog.ajouter(Inst.creation1(Operation.BEQ,
			                         		Operande.creationOpEtiq(etOp1Faux)));
			// %calculer b dans rc
			coder_EXP(a.getFils2(), rc);
			// CMP r1, #1 ; a est vrais
			Prog.ajouter(Inst.creation2(Operation.CMP, 
                                  Operande.creationOpEntier(1),
                                  Operande.opDirect(rc)));
			// SEQ r1 ; c prend la valeur de b
			Prog.ajouter(Inst.creation1(Operation.SEQ, Operande.opDirect(rc)));
			// BRA suite
			Prog.ajouter(Inst.creation1(Operation.BRA,
				                        	Operande.creationOpEtiq(suite)));
			// aF: LOAD #0, R1 ; a est faux
			Prog.ajouter(etOp1Faux);
			Prog.ajouter(Inst.creation2(Operation.LOAD,
					                        Operande.creationOpEntier(0), 
                                  Operande.opDirect(rc)));
			Prog.ajouter(suite);
			return;
		}
		case Ou: {
			Etiq suite = Etiq.nouvelle("suite");
			Etiq ouOp1Faux = Etiq.nouvelle("ouOp1Faux");
			// debut: %calculer a dans r1
			coder_EXP(a.getFils1(), rc);
			// CMP rc, #0
			Prog.ajouter(Inst.creation2(Operation.CMP, 
                                  Operande.creationOpEntier(0),
                                  Operande.opDirect(rc)));
			// BEQ aF
			Prog.ajouter(Inst.creation1(Operation.BEQ,
					                        Operande.creationOpEtiq(ouOp1Faux)));
			// aT: LOAD #1, r1 ; a est vrais
			Prog.ajouter(Inst.creation2(Operation.LOAD,
					                        Operande.creationOpEntier(1), 
                                  Operande.opDirect(rc)));
			// BRA suite
			Prog.ajouter(Inst.creation1(Operation.BRA,
			                            Operande.creationOpEtiq(suite)));
			// %calculer b dans rc
			coder_EXP(a.getFils2(), rc);
			// aF : CMP r1, #1 ; a est faux
			Prog.ajouter(ouOp1Faux);
			Prog.ajouter(Inst.creation2(Operation.CMP, 
                                  Operande.creationOpEntier(1),
                                  Operande.opDirect(rc)));
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
				Prog.ajouter(Inst.creation2(Operation.CMP, 
                                    Operande.opDirect(rd),
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

		// opérations unaires
		switch (a.getNoeud()) {
		// remplir les cas (ne pas oublier le break)
		case PlusUnaire:
			coder_EXP(a.getFils1(), rc);
			return;
		case Non:
			coder_EXP(a.getFils1(), rc);
			Prog.ajouter(Inst.creation2(Operation.CMP, 
                                  Operande.creationOpEntier(0),
                                  Operande.opDirect(rc)));
			Prog.ajouter(Inst.creation1(Operation.SEQ, Operande.opDirect(rc)));
			return;
		case MoinsUnaire:
			coder_EXP(a.getFils1(), rc);
			Prog.ajouter(Inst.creation2(Operation.OPP, 
                                  Operande.opDirect(rc),
                                  Operande.opDirect(rc)));
      if(a.getDecor().getType().getNature() == NatureType.Interval) {
        coder_verif_borne_interval(a.getDecor().getType(), rc);
      }
			return;
		default:
			break;
		}
    //expressions feuilles TODO
    switch (a.getNoeud()) {
    //remplir les cas (ne pas oublier le break)
    case Chaine: return;
    case Ident:
      switch (a.getChaine().toLowerCase()) {
      case "true": 
        Prog.ajouter(Inst.creation2(Operation.LOAD,
                                    Operande.creationOpEntier(1),
                                    Operande.opDirect(rc)));
        return;
      case "false": 
        Prog.ajouter(Inst.creation2(Operation.LOAD,
                                    Operande.creationOpEntier(0),
                                    Operande.opDirect(rc)));
        return;
      case "max_int": 
        Prog.ajouter(Inst.creation2(Operation.LOAD,
                                    Operande.creationOpEntier(java.lang.Integer.MAX_VALUE),
                                    Operande.opDirect(rc)));
        return;
      default:
        Type t = coder_PLACE(a, rc);
        if(t.getNature() != NatureType.Array) {
          coder_load_reg_index(rc,rc);
        }
        return;
      }
    case Index: {
      Type t = coder_PLACE(a, rc);
      if(t.getNature() != NatureType.Array) {
        coder_load_reg_index(rc,rc);
      }
      return;
    }
    case Entier:
      Prog.ajouter(Inst.creation2(Operation.LOAD, 
                                  Operande.creationOpEntier(a.getEntier()), 
                                  Operande.opDirect(rc)));
      coder_verif_borne_interval(a.getDecor().getType(), rc);
      
      return;
    case Reel:
      Prog.ajouter(Inst.creation2(Operation.LOAD, 
                                  Operande.creationOpReel(a.getReel()), 
                                  Operande.opDirect(rc)));
      return;
    default: break;
    }
  }

  private void coder_verif_borne_interval(Type interv, Registre rc) {
    if(noVerif) {
      return;
    }
    int inf = interv.getBorneInf();
    int sup = interv.getBorneSup();
    Prog.ajouter(Inst.creation2(Operation.CMP, 
                                Operande.creationOpEntier(inf), 
                                Operande.opDirect(rc)));
    Prog.ajouter(Inst.creation1(Operation.BLT, 
                                Operande.creationOpEtiq(lib.get_IntervalOutOfBound())));
    Prog.ajouter(Inst.creation2(Operation.CMP, 
                                Operande.creationOpEntier(sup), 
                                Operande.opDirect(rc)));
    Prog.ajouter(Inst.creation1(Operation.BGT, 
                                Operande.creationOpEtiq(lib.get_IntervalOutOfBound())));
  }


  private Type coder_PLACE(Arbre a, Registre rc) {
    Type t=null;
    switch (a.getNoeud()) {
    case Ident: //cas identificateur
      Prog.ajouter(Inst.creation2(Operation.LOAD, 
                                  Operande.creationOpEntier(Pile.getGlobale(a.getChaine())), 
                                  Operande.opDirect(rc)));
      t = a.getDecor().getType();
      break;
    case Index: //cas identificateur[expr][expr]...
      coder_PLACE(a.getFils1(), rc); //offset de base ex: t[42][43] -> l'offset de t[42] est l'offset de base
      Type interval = a.getFils1().getDecor().getType().getIndice();
      t = a.getDecor().getType();
      int elemLen = totalLenCounter(a.getFils1().getDecor().getType().getElement());
      Registre rd;
      if((rd=Reg.allouer())!=null) {
        coder_EXP(a.getFils2(), rd);
        coder_verif_borne_interval(interval, rd);
        Prog.ajouter(Inst.creation2(Operation.SUB, 
                                    Operande.creationOpEntier(interval.getBorneInf()), 
                                    Operande.opDirect(rd)));
        if(elemLen > 1) {
          Prog.ajouter(Inst.creation2(Operation.MUL,
                                      Operande.creationOpEntier(elemLen),
                                      Operande.opDirect(rd)));
        }
        Prog.ajouter(Inst.creation2(Operation.ADD, 
                                    Operande.opDirect(rd), 
                                    Operande.opDirect(rc)));
        Reg.liberer(rd);
      }
      else {
        int temp = Pile.allouer(); //allouer un emplacement sur la pile
        Prog.ajouter(Inst.creation2(Operation.STORE, 
                                    Operande.opDirect(rc), 
                                    Operande.creationOpIndirect(temp,Registre.LB)));
        coder_EXP(a.getFils2(), rc);
        coder_verif_borne_interval(interval, rc);
        Prog.ajouter(Inst.creation2(Operation.SUB, 
                                    Operande.creationOpEntier(interval.getBorneInf()), 
                                    Operande.opDirect(rc)));
        if(elemLen > 1) {
          Prog.ajouter(Inst.creation2(Operation.MUL,
                                      Operande.creationOpEntier(elemLen),
                                      Operande.opDirect(rc)));
        }
        Prog.ajouter(Inst.creation2(Operation.ADD, 
                                    Operande.creationOpIndirect(temp,Registre.LB), 
                                    Operande.opDirect(rc)));
        Pile.liberer(temp);//libèrer temp
      }
      break;
    default: 
    }
    return t;
  }
  private void coder_store_reg(Registre reg, int pile) {
    Prog.ajouter(Inst.creation2(Operation.STORE,
                                Operande.opDirect(reg),
                                Operande.creationOpIndirect(pile,Registre.LB)));
  }
  private void coder_load_reg(Registre reg, int pile) {
    Prog.ajouter(Inst.creation2(Operation.LOAD,
                                Operande.creationOpIndirect(pile,Registre.LB),
                                Operande.opDirect(reg)));
  }
  private void coder_store_reg_index(Registre reg, Registre index) {
    Prog.ajouter(Inst.creation2(Operation.STORE,
                                Operande.opDirect(reg),
                                Operande.creationOpIndexe(0,Registre.GB,index)));
  }
  private void coder_load_reg_index(Registre reg, Registre index) {
    Prog.ajouter(Inst.creation2(Operation.LOAD,
                                Operande.creationOpIndexe(0,Registre.GB,index),
                                Operande.opDirect(reg)));
  }


  public void coder_boucle_for(Arbre a) {
    boolean incrementer = (a.getFils1().getNoeud() == Noeud.Increment);
    Etiq boucle = Etiq.nouvelle("boucle");
    int valFin = Pile.allouer();
    int compteur = Pile.getGlobale(a.getFils1().getFils1().getChaine());
    coder_EXP(a.getFils1().getFils2(), rx);
    coder_store_reg(rx,compteur);
    coder_EXP(a.getFils1().getFils3(), rx);
    coder_store_reg(rx,valFin);
    Prog.ajouter(boucle);
    coder_LISTE_INST(a.getFils2());
    //incrément/décrément
    coder_load_reg(rx,compteur);
    if(incrementer) {
      Prog.ajouter(Inst.creation2(Operation.ADD,
                                  Operande.creationOpEntier(1),
                                  Operande.opDirect(rx)));
    }
    else {
      Prog.ajouter(Inst.creation2(Operation.SUB,
                                  Operande.creationOpEntier(1),
                                  Operande.opDirect(rx)));
    }
    coder_store_reg(rx,compteur);
    //comparaison
    Prog.ajouter(Inst.creation2(Operation.CMP, 
                                Operande.creationOpIndirect(valFin,Registre.GB), 
                                Operande.opDirect(rx)));
    if(incrementer) {
      Prog.ajouter(Inst.creation1(Operation.BLE, 
                                  Operande.creationOpEtiq(boucle)));
    }
    else {
      Prog.ajouter(Inst.creation1(Operation.BGE, 
                                  Operande.creationOpEtiq(boucle)));
    }
    Pile.liberer(valFin);
  }
  public void coder_if(Arbre a, Etiq faux) {
    coder_EXP(a,rx);
    Prog.ajouter(Inst.creation2(Operation.CMP, 
                                Operande.creationOpEntier(1), 
                                Operande.opDirect(rx)));
    Prog.ajouter(Inst.creation1(Operation.BNE, 
                                Operande.creationOpEtiq(faux)));
  }

  public void coder_liste_ecriture(Arbre a) {
    Arbre temp = a;
    
    if (temp.getNoeud() != Noeud.Vide) {
      coder_liste_ecriture(temp.getFils1());
      NatureType natureExp = temp.getFils2().getDecor().getType().getNature();
      switch (natureExp) {
      case String:
        Prog.ajouter(Inst.creation1(Operation.WSTR,
                                    Operande.creationOpChaine(temp.getFils2().getChaine())));
        break;
      case Interval:
        coder_EXP(temp.getFils2(),ry);
        Prog.ajouter(Inst.creation0(Operation.WINT));
        break;
      case Real:
        coder_EXP(temp.getFils2(),ry);
        Prog.ajouter(Inst.creation0(Operation.WFLOAT));
        break;
      }
    }
  }
  private int totalLenCounter(Type t) {
    if(t.getNature() == NatureType.Array) {
      int len = t.getIndice().getBorneSup() - t.getIndice().getBorneInf()+1;
      return totalLenCounter(t.getElement())*len;
    }
    else {
      return 1;
    }
  }
  public void coder_copy_type(Type type) {
    int totalLen = totalLenCounter(type);
    Etiq boucleCopy = Etiq.nouvelle("boucleCopy");
    Etiq finBoucleCopy = Etiq.nouvelle("finBoucleCopy");
    int fin = Pile.allouer();
    Prog.ajouter(Inst.creation2(Operation.LOAD,
                                Operande.creationOpEntier(totalLen),
                                Operande.opDirect(rz)));
    Prog.ajouter(Inst.creation2(Operation.ADD,
                                Operande.opDirect(rx),
                                Operande.opDirect(rz)));
    coder_store_reg(rz,fin);
    Prog.ajouter(boucleCopy);
    Prog.ajouter(Inst.creation2(Operation.CMP, 
                                Operande.creationOpIndirect(fin,Registre.GB), 
                                Operande.opDirect(rx)));
    Prog.ajouter(Inst.creation1(Operation.BLT, 
                                Operande.creationOpEtiq(finBoucleCopy)));
    coder_load_reg_index(rz,rx);
    coder_store_reg_index(rz,ry);
    Prog.ajouter(Inst.creation2(Operation.ADD,
                                Operande.creationOpEntier(1),
                                Operande.opDirect(rx)));
    Prog.ajouter(Inst.creation2(Operation.ADD,
                                Operande.creationOpEntier(1),
                                Operande.opDirect(ry)));
    Prog.ajouter(Inst.creation1(Operation.BRA, 
                                  Operande.creationOpEtiq(boucleCopy)));
    Prog.ajouter(finBoucleCopy);
  }

  public void coder_INST(Arbre a) {
    switch (a.getNoeud()) {
    case Nop: Prog.ajouterComment("NOOP"+" Ligne :"+a.getNumLigne()); break;//TODO mettre un commentaire?
    case Affect: 
      Prog.ajouterComment("Affect"+" Ligne :"+a.getNumLigne());
      Type typePlace = coder_PLACE(a.getFils1(),ry);
      coder_EXP(a.getFils2(),rx);
      if(typePlace.getNature() == NatureType.Array) {
        coder_copy_type(typePlace); //rx et ry doivent être initialisés
        break;
      }
      else {
        if(typePlace.getNature() == NatureType.Interval) {
          coder_verif_borne_interval(typePlace,rx);
        }
        coder_store_reg_index(rx,ry);
        Prog.ajouterComment("Fin Affect"+" Ligne :"+a.getNumLigne());
      }
      break;
    case Pour: 
      Prog.ajouterComment("Pour"+" Ligne :"+a.getNumLigne());
      coder_boucle_for(a); 
      Prog.ajouterComment("Fin Pour"+" Ligne :"+a.getNumLigne());
      break;
    case TantQue: {
      Prog.ajouterComment("TantQue"+" Ligne :"+a.getNumLigne());
      Etiq expBool = Etiq.nouvelle("condTantQue");
      Etiq finBoucle = Etiq.nouvelle("finTantQue");
      Prog.ajouter(expBool);
      coder_if(a.getFils1(),finBoucle);
      coder_LISTE_INST(a.getFils2());
      Prog.ajouter(Inst.creation1(Operation.BRA, 
                                  Operande.creationOpEtiq(expBool)));
      Prog.ajouter(finBoucle);
      Prog.ajouterComment("Fin TantQue"+" Ligne :"+a.getNumLigne());
    } break;
    case Si: {
      Prog.ajouterComment("Si"+" Ligne :"+a.getNumLigne());
      Etiq faux = Etiq.nouvelle("faux");
      Etiq finsi = Etiq.nouvelle("finsi");
      coder_if(a.getFils1(),faux);
      coder_LISTE_INST(a.getFils2());
      Prog.ajouter(Inst.creation1(Operation.BRA, 
                                  Operande.creationOpEtiq(finsi)));
      Prog.ajouter(faux);
      coder_LISTE_INST(a.getFils3());
      Prog.ajouter(finsi);
      Prog.ajouterComment("Fin Si"+" Ligne :"+a.getNumLigne());
    } break;
    case Ecriture:
      Prog.ajouterComment("Ecriture"+" Ligne :"+a.getNumLigne());
      coder_liste_ecriture(a.getFils1());
      Prog.ajouterComment("Fin Ecriture"+" Ligne :"+a.getNumLigne());
      break;
    case Lecture: {
      Prog.ajouterComment("Lecture"+" Ligne :"+a.getNumLigne());
      NatureType natureExp = a.getFils1().getDecor().getType().getNature();
      coder_PLACE(a.getFils1(),rx);
      if(natureExp == NatureType.Interval)
        Prog.ajouter(Inst.creation0(Operation.RINT));
      else 
        Prog.ajouter(Inst.creation0(Operation.RFLOAT));
      coder_store_reg_index(ry,rx);
      Prog.ajouterComment("Fin Lecture"+" Ligne :"+a.getNumLigne());
    }
    case Ligne:
      Prog.ajouterComment("newline"+" Ligne :"+a.getNumLigne());
      Prog.ajouter(Inst.creation0(Operation.WNL));
      Prog.ajouterComment("Fin newline"+" Ligne :"+a.getNumLigne());
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
    case ListeDecl: 
      coder_LISTE_DECL(a.getFils1());
      coder_DECL(a.getFils2());
      return;
    default: 
    }
  }
  public void coder_DECL(Arbre a) {
    coder_LISTE_IDF(a.getFils1());
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
    lib.writeLib();


    // On retourne le programme assembleur généré
    return Prog.instance(); 
  }
}



