package ProjetCompil.Gencode.Src;

import ProjetCompil.Global.Src.*;
import ProjetCompil.Global.Src3.*;
/**
 * Génération de code pour un programme JCas à partir d'un arbre décoré.
 */


class Generation {
  
  private static Library lib = new Library();
  private Registre rx = Registre.R1; //registre réservé pour les instructions
  private Registre ry = Registre.R2;
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

  private void coder_verif_borne_interval(Type interv, Registre rc) {
    int inf = interv.getBorneInf();
    int sup = interv.getBorneSup();
    Prog.ajouter(Inst.creation2(Operation.CMP, 
                                Operande.creationOpEntier(inf), 
                                Operande.opDirect(rc)));
    Prog.ajouter(Inst.creation1(Operation.BGT, 
                                Operande.creationOpEtiq(lib.get_IndexOutOfBound())));
  }

  private void coder_PLACE(Arbre a, Registre rc) {
    switch (a.getNoeud()) {
    case Ident: //cas identificateur
      Prog.ajouter(Inst.creation2(Operation.LOAD, 
                                  Operande.creationOpEntier(a.getDecor().getInfoCode()), 
                                  Operande.opDirect(rc)));
      return;
    case Index: //cas identificateur[expr][expr]...
      coder_PLACE(a.getFils1(), rc); //offset de base ex: t[42][43] -> l'offset de t[42] est l'offset de base
      Type interval = a.getFils2().getDecor().getType();
      //TOTO verifier les bornes
      if(interval.getBorneInf() == -java.lang.Integer.MAX_VALUE &&
         interval.getBorneSup() == java.lang.Integer.MAX_VALUE) {
        //cas particulier des entiers de base, pas de soustraction de la borne inf
      }
      else {
        //cas général des TypeInterval, il faut soustraire la borne inf
        Prog.ajouter(Inst.creation2(Operation.SUB, 
                                    Operande.creationOpEntier(interval.getBorneInf()), 
                                    Operande.opDirect(rc)));
      }
      Registre rd;
      if((rd=Reg.allouer())!=null) {
        coder_EXP(a.getFils2(), rd);
        coder_verif_borne_interval(interval, rd);
        Prog.ajouter(Inst.creation2(Operation.ADD, 
                                    Operande.opDirect(rd), 
                                    Operande.opDirect(rc)));
        Reg.liberer(rd);
        return;
      }
      else {
        int temp = Pile.allouer(); //allouer un emplacement sur la pile
        Prog.ajouter(Inst.creation2(
          Operation.STORE, Operande.opDirect(rc), 
                           Operande.creationOpIndirect(temp,Registre.LB)));
        coder_EXP(a.getFils2(), rc);
        coder_verif_borne_interval(interval, rc);
        Prog.ajouter(Inst.creation2(Operation.ADD, 
                                    Operande.creationOpIndirect(temp,Registre.LB), 
                                    Operande.opDirect(rc)));
        Pile.liberer(temp);//libèrer temp
      }
      return;
    default: 
    }
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
    //utilisation des deux registres libres
    int valFin = Pile.allouer();
    int addrCompteur = Pile.allouer();
    coder_PLACE(a.getFils1().getFils1(), rx);
    coder_store_reg(rx,addrCompteur);
    coder_EXP(a.getFils1().getFils2(), rx);
    coder_load_reg(ry,addrCompteur);
    coder_store_reg_index(rx,ry);
    coder_EXP(a.getFils1().getFils3(), rx);
    coder_store_reg(rx,valFin);
    Prog.ajouter(boucle);
    coder_LISTE_INST(a.getFils2());
    //incrément/décrément
    coder_load_reg(ry,addrCompteur);
    coder_load_reg_index(rx,ry);
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
    coder_store_reg_index(rx,ry);
    //comparaison
    Prog.ajouter(Inst.creation2(Operation.CMP, 
                                Operande.creationOpIndirect(valFin,Registre.GB), 
                                Operande.opDirect(rx)));
    if(incrementer) {
      Prog.ajouter(Inst.creation1(Operation.BGT, 
                                  Operande.creationOpEtiq(boucle)));
    }
    else {
      Prog.ajouter(Inst.creation1(Operation.BLT, 
                                  Operande.creationOpEtiq(boucle)));
    }
    Pile.liberer(addrCompteur);
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


  public void coder_INST(Arbre a) {
    
    switch (a.getNoeud()) {
    case Nop: break;//TODO mettre un commentaire?
    case Affect: 
      coder_PLACE(a.getFils1(),ry);
      coder_EXP(a.getFils2(),rx);
      coder_store_reg_index(rx,ry);
      break;
    case Pour: coder_boucle_for(a); break;
    case TantQue: {
      Etiq expBool = Etiq.nouvelle("condTantQue");
      Etiq finBoucle = Etiq.nouvelle("finTantQue");
      Prog.ajouter(expBool);
      coder_if(a.getFils1(),finBoucle);
      coder_LISTE_INST(a.getFils2());
      Prog.ajouter(Inst.creation1(Operation.BRA, 
                                  Operande.creationOpEtiq(expBool)));
      Prog.ajouter(finBoucle);
    } break;
    case Si: {
      Etiq faux = Etiq.nouvelle("faux");
      coder_if(a.getFils1(),faux);
      coder_LISTE_INST(a.getFils2());
      Prog.ajouter(faux);
      coder_LISTE_INST(a.getFils3());
    } break;
    case Ecriture: {
      Arbre temp = a.getFils1();
      while (temp.getNoeud() != Noeud.Vide) {
        NatureType natureExp = temp.getFils2().getDecor().getType().getNature();
        switch (natureExp) {
        case String:
          Prog.ajouter(Inst.creation1(Operation.WSTR,
                                      Operande.creationOpChaine(temp.getFils2().getChaine())));
          break;
        case Interval:
          coder_EXP(temp.getFils2(),rx);
          Prog.ajouter(Inst.creation0(Operation.WINT));
          break;
        case Real:
          coder_EXP(temp.getFils2(),rx);
          Prog.ajouter(Inst.creation0(Operation.WFLOAT));
          break;
        }
        temp = temp.getFils1();
      }
      break;
    }
    case Lecture: {
      NatureType natureExp = a.getFils1().getDecor().getType().getNature();
      coder_PLACE(a.getFils1(),ry);
      if(natureExp == NatureType.Interval)
        Prog.ajouter(Inst.creation0(Operation.RINT));
      else 
        Prog.ajouter(Inst.creation0(Operation.RFLOAT));
      coder_store_reg_index(rx,ry);
    }
    case Ligne:
      Prog.ajouter(Inst.creation0(Operation.WNL));
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
    lib.writeLib();

    // Fin du programme
    // L'instruction "HALT"
    Inst inst = Inst.creation0(Operation.HALT);
    // On ajoute l'instruction à la fin du programme
    Prog.ajouter(inst);

    // On retourne le programme assembleur généré
    return Prog.instance(); 
  }
}



