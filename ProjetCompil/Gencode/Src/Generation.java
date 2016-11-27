package ProjetCompil.Gencode.Src;

import java.security.spec.ECFieldF2m;

import ProjetCompil.Global.Src.*;
import ProjetCompil.Global.Src3.*;
/**
 * Génération de code pour un programme JCas à partir d'un arbre décoré.
 * Cette classe fonctionne en parcourant l'arbre de manière décendante
 * comme lors de la verification. Cependant, aucune verification n'est réalisé sur l'arbe.
 * Les verifications lors de l'execution sont réalisés: 
 * - verification des bornes lors de l'affectation
 * - verification d'overflow lors des opérations arithmétiques
 * - verification de stack overflow lors de l'allocation de variables sur la pile
 *
 * Utilisation des registres:
 * les 3 premiers registres R0, R1 et R2 sont libres d'accès à toutes les fonctions à tout moment
 * Aucune garanti n'est faite sur la durée de vie de ces derniers, il servent donc principalement de manière 
 * local à une fonction pour la manipulation de valeurs.
 * Les autres registres servent de variables locales lors de l'évaluation des expressions.
 * Ces derniers sont alloués et leur durrée de vie est donc garanti par la fonction de grénération qui l'utilise.
 * Quand il n'y a plus de registres libres, on utilise la pile (cela arrive donc rarement pour une evaluation d'expression)
 *
 * Utilisation de la pile:
 * Certaines instructions complexes (boucle for) necessitent plusieurs variables temporaires, les 3 registres libres ne suffisent pas,
 * dans ce cas il aurait été necessaire de recoder plusieurs fois la même instruction avec des allocations de registres/pile 
 * ou de créer plus de registres libres, ce qui est censé être une exception...
 * pour simplifier le code, nous avons seulement utilisé la pile et les 3 registres libres dans ce cas.
 * Enfin la pile peux servir à allouer des variables locales pour l'evaluation des expressions quand il n'y a plus de registres de libre
 * 
 */

public class Generation {
  
  private static Library lib = Library.get_instance();
  private Registre rx = Registre.R0; //registre réservé pour les instructions
  private Registre ry = Registre.R1;
  private Registre rz = Registre.R2;
  private boolean noVerif = false;
   /**
    * Génère du code pour calculer l'expresion représenté par a et stock le résultat dans le registre rc
    * Ne modifie pas les registres libres R0,R1 et R2
    * la génération de code est récursive
    * Utilise l'allocation de registre quand c'est possible, ou la pile sinon
    * Remarque, quand l'expression retourné est un tableau, rc contient l'offset de ce tableau
    */
  public void coder_EXP(Arbre a, Registre rc) {
    Operation op = null;
    boolean divReel = false;
    switch (a.getNoeud()) {
    //opérations binaires arithmétiques
    case Plus: op=Operation.ADD; break;
    case Moins: op=Operation.SUB; break;
    case Mult: op=Operation.MUL; break;
    case DivReel: op=Operation.DIV; divReel=true; break;
    case Reste: op=Operation.MOD; break;
    case Quotient: op=Operation.DIV; break;
    default: break;
    }
    if(op!=null) {
      //actions communes à réaliser
      Registre rd;
      if((rd=Reg.allouer())!=null) {
        if(divReel && a.getFils1().getDecor().getType().getNature() == NatureType.Interval) {
          //on assume que les 2 opérandes sont de nature type interval 
          //(il y aura un neud conversion pour s'en assurer)
          coder_EXP(a.getFils1(), rc);
          Prog.ajouter(Inst.creation2(Operation.FLOAT, 
                                      Operande.opDirect(rc), 
                                      Operande.opDirect(rc)));
          coder_EXP(a.getFils2(), rd);
          Prog.ajouter(Inst.creation2(Operation.FLOAT, 
                                      Operande.opDirect(rd), 
                                      Operande.opDirect(rd)));

        }
        else {
          coder_EXP(a.getFils1(), rc);
          coder_EXP(a.getFils2(), rd);
        }
        Prog.ajouter(Inst.creation2(op, 
                                    Operande.opDirect(rd), 
                                    Operande.opDirect(rc)));
        coder_verif_overflow();
        Reg.liberer(rd);
      }
      else {
        int temp = Pile.allouer();
        if(divReel && a.getFils1().getDecor().getType().getNature() == NatureType.Interval) {
          //on assume que les 2 opérandes sont de nature type interval 
          //(il y aura un neud conversion pour s'en assurer)
          coder_EXP(a.getFils2(), rc);
          Prog.ajouter(Inst.creation2(Operation.FLOAT, 
                                      Operande.opDirect(rc), 
                                      Operande.opDirect(rc)));
          Prog.ajouter(Inst.creation2(Operation.STORE, 
                                      Operande.opDirect(rc), 
                                      Operande.creationOpIndirect(temp,Registre.LB)));
          coder_EXP(a.getFils1(), rc);
          Prog.ajouter(Inst.creation2(Operation.FLOAT, 
                                      Operande.opDirect(rc), 
                                      Operande.opDirect(rc)));
        }
        else {
          coder_EXP(a.getFils2(), rc);
          Prog.ajouter(Inst.creation2(Operation.STORE, 
                                      Operande.opDirect(rc), 
                                      Operande.creationOpIndirect(temp,Registre.LB)));
          coder_EXP(a.getFils1(), rc);
        }
        Prog.ajouter(Inst.creation2(op, 
                                    Operande.creationOpIndirect(temp,Registre.LB), 
                                    Operande.opDirect(rc)));
        coder_verif_overflow();
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
      coder_verif_overflow();
			return;
    case Conversion:
      coder_EXP(a.getFils1(), rc);
      if(a.getFils1().getDecor().getType().getNature() != NatureType.Array) {
        Prog.ajouter(Inst.creation2(Operation.FLOAT,
                                    Operande.opDirect(rc),
                                    Operande.opDirect(rc)));
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
  /**fonction de codage de la verification d'overflow*/
  private void coder_verif_overflow() {
    Prog.ajouter(Inst.creation1(Operation.BOV, 
                                Operande.creationOpEtiq(lib.get_ArithmeticOverflow())));
  }
  /**fonction de codage de la verification des bornes d'interval
   * Compare le registre rc aux bornes de l'interval passé en paramètre,
   * déroute l'execution du programme vers l'erreur IntervalOutOfBound de la librairie en cas de valeur non valide*/
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

  /**
   * Codage de l'évaluation d'une place (identificateur T, T[i], T[i][j]...)
   * cette fonction génère un code qui stock dans rc l'offset de l'emplacement mémoire désigné par la place
   * une place peut être un simple identificateur ou une indexation de tableau (avec une ou plusieurs dimensions)
   * Remarque: dans le cas d'un identificateur, l'offset est constant et aurait pu être utilisé sans registre, 
   * mais dans un souci de généralisation, on utilise toujours un registre (de la même manière que coder_EXP())
   * La vérification des bornes lors de l'indexation est réalisé
   */
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
  /**fonction de sauvegarde d'un registre dans un emplacement de la pile (simplifie la lecture du code)*/
  private void coder_store_reg(Registre reg, int pile) {
    Prog.ajouter(Inst.creation2(Operation.STORE,
                                Operande.opDirect(reg),
                                Operande.creationOpIndirect(pile,Registre.LB)));
  }
  /**fonction de chargement d'un registre dans un emplacement de la pile (simplifie la lecture du code)*/
  private void coder_load_reg(Registre reg, int pile) {
    Prog.ajouter(Inst.creation2(Operation.LOAD,
                                Operande.creationOpIndirect(pile,Registre.LB),
                                Operande.opDirect(reg)));
  }
  /**fonction de sauvegarde d'un registre dans un emplacement de la pile avec l'offset contenu dans un registre (simplifie la lecture du code)*/
  private void coder_store_reg_index(Registre reg, Registre index) {
    Prog.ajouter(Inst.creation2(Operation.STORE,
                                Operande.opDirect(reg),
                                Operande.creationOpIndexe(0,Registre.GB,index)));
  }
  /**fonction de chargement d'un registre dans un emplacement de la pile avec l'offset contenu dans un registre (simplifie la lecture du code)*/
  private void coder_load_reg_index(Registre reg, Registre index) {
    Prog.ajouter(Inst.creation2(Operation.LOAD,
                                Operande.creationOpIndexe(0,Registre.GB,index),
                                Operande.opDirect(reg)));
  }

  /**Codage d'une boucle for
   * Utilise R0 et deux variables de la pile.
   * L'utilisation des 2 autres registres libres à la places de la pile n'est pas possible car leur valeur n'est pas garantie
   * et les instructions dans le for risquent de les modifier
   * cependant, on aurait pu faire une version avec allocation de registres mais on aurait du recoder 3 fois cette instruction
   * (avec 2 registres alloués, avec 1 registre alloué et un emplacement sur la pile, avec 2 emplacemente sur la pile)
   * Seul la dernière solution à été choisi car elle fonctionne tout le temps bien que ce soit la plus lente
   */ 
  public void coder_boucle_for(Arbre a) {
    boolean incrementer = (a.getFils1().getNoeud() == Noeud.Increment);
    Etiq boucle = Etiq.nouvelle("boucle");
    int valFin = Pile.allouer();
    int compteur = Pile.getGlobale(a.getFils1().getFils1().getChaine()); //ici le compteur ne peux pas être une indexation, l'offset est donc constant
    coder_EXP(a.getFils1().getFils2(), rx);
    coder_verif_borne_interval(a.getFils1().getFils1().getDecor().getType(),rx);
    coder_store_reg(rx,compteur);
    coder_EXP(a.getFils1().getFils3(), rx);
    coder_verif_borne_interval(a.getFils1().getFils1().getDecor().getType(),rx);
    coder_store_reg(rx,valFin);
    Prog.ajouter(boucle);
    coder_LISTE_INST(a.getFils2());
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

  /**simple fonction d'e factorisation de code, voir coder_INST pour le "si" complet*/
  private void coder_if(Arbre a, Etiq faux) {
    coder_EXP(a,rx);
    Prog.ajouter(Inst.creation2(Operation.CMP, 
                                Operande.creationOpEntier(1), 
                                Operande.opDirect(rx)));
    Prog.ajouter(Inst.creation1(Operation.BNE, 
                                Operande.creationOpEtiq(faux)));
  }
  /**parcours la liste d'expression d'un write pour les afficher*/
  private void coder_liste_ecriture(Arbre a) {
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
  /**Permet de calculer la longeur total d'un élément indexé dans un tableau,
   * si cet élément est un interval ou un reel, renvoi 1
   * si c'est un sous tableau, renvoi la taille de ce dernier en templacement mémoire */
  private int totalLenCounter(Type t) {
    if(t.getNature() == NatureType.Array) {
      int len = t.getIndice().getBorneSup() - t.getIndice().getBorneInf()+1;
      return totalLenCounter(t.getElement())*len;
    }
    else {
      return 1;
    }
  }
  /**Permet de ronvoyer le type final d'un tableau (interval ou réel)*/
  private Type finalTypeArray(Type t) {
    if(t.getNature() == NatureType.Array) {
      return finalTypeArray(t.getElement());
    }
    else {
      return t;
    }
  }
  /**Dans le cas d'une affectation tableau-tableau, permet de copier tous les éléments du premier tableau vers le deuxième,
   * et réalise les conversions et les vérifications necessaires sur chaque élément */
  private void coder_copy_type(Type type, boolean convertion) {
    int totalLen = totalLenCounter(type);
    Etiq boucleCopy = Etiq.nouvelle("boucleCopy");
    Etiq finBoucleCopy = Etiq.nouvelle("finBoucleCopy");
    int fin = Pile.allouer();
    Type finaltype = finalTypeArray(type);
    Prog.ajouter(Inst.creation2(Operation.LOAD,
                                Operande.creationOpEntier(totalLen-1),
                                Operande.opDirect(rz)));
    Prog.ajouter(Inst.creation2(Operation.ADD,
                                Operande.opDirect(rx),
                                Operande.opDirect(rz)));
    coder_store_reg(rz,fin);
    Prog.ajouter(boucleCopy);
    Prog.ajouter(Inst.creation2(Operation.CMP, 
                                Operande.creationOpIndirect(fin,Registre.GB), 
                                Operande.opDirect(rx)));
    Prog.ajouter(Inst.creation1(Operation.BGT, 
                                Operande.creationOpEtiq(finBoucleCopy)));
    coder_load_reg_index(rz,rx);
    if(finaltype.getNature() == NatureType.Interval) {
      coder_verif_borne_interval(finaltype,rz);
    }
    if(convertion) {
      Prog.ajouter(Inst.creation2(Operation.FLOAT,
                                  Operande.opDirect(rz),
                                  Operande.opDirect(rz)));
    }
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
  /**Fonction de codage des instructions.
   * afficher pour chaque instruction un commentaire avec le numéro de ligne correspondant dans le fichier source
   */
  public void coder_INST(Arbre a) {
    switch (a.getNoeud()) {
    case Nop: Prog.ajouterComment("NOOP"+" Ligne :"+a.getNumLigne()); break;//TODO mettre un commentaire?
    case Affect: 
      Prog.ajouterComment("Affect"+" Ligne :"+a.getNumLigne());
      Type typePlace = coder_PLACE(a.getFils1(),ry);
      coder_EXP(a.getFils2(),rx);
      if(typePlace.getNature() == NatureType.Array) {
        coder_copy_type(typePlace,(a.getFils2().getNoeud() == Noeud.Conversion)); //rx et ry doivent être initialisés
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
      if(natureExp == NatureType.Interval) {
        Prog.ajouter(Inst.creation0(Operation.RINT));
        coder_verif_borne_interval(a.getFils1().getDecor().getType(),ry);
      }
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
  /**Codage d'une liste d'instructions*/
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
  /**codage d'une liste de déclarations*/
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
  /**Codage d'une déclaration*/
  public void coder_DECL(Arbre a) {
    coder_LISTE_IDF(a.getFils1());
  }
  /**Codage d'une liste d'identificateur
   * alloue un emplacement sur la pile pour chaque variable déclaré*/
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
  
  
  /**fonction qui permet de coder un arbre représentant un programme jcas valide*/
  static Prog coder(Arbre a) {
    Prog.ajouterGrosComment("Programme généré par JCasc");
    Reg.init();//initialisation de la pool de registres
    Generation gen = new Generation(); //instanciation de cette classe
    gen.coder_LISTE_DECL(a.getFils1()); //analyse des déclarations
    Pile.finDeclaration(); //écriture des déclarations (réservation de place dans la pile)
    gen.coder_LISTE_INST(a.getFils2()); //codage des instructions du programme
    
    // Fin du programme
    // L'instruction "HALT"
    Inst inst = Inst.creation0(Operation.HALT);
    // On ajoute l'instruction à la fin du programme
    Prog.ajouter(inst);
    lib.writeLib();// écriture des librairies utilisés
    // On retourne le programme assembleur généré
    return Prog.instance(); 
  }
}



