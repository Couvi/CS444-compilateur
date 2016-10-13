/**
 * Classe Verif
 * Cette classe permet de verifier et de décorer contextuellement 
 * l'arbre de syntaxe d'un programme. 
 * le parcours de l'arbre se fait de manière descendante, il y a
 * une fonction de vérification pour chaque élément de grammaire 
 * de la syntaxe. dans certains cas, ces fonctions de verification
 * prennent en paramètre des informations sur leur context pour mieux
 * décorer l'arbre (voir verifier_TYPE())
 *
 * les verifications redondantes à l'analyseur syntaxique ne sont pas
 * systématiquement réalisés, sauf dans les cas où cela peux être fait
 * sans difficulté: 
 * Dans un switch sur le type de noeud d'un arbre,
 * le cas default est théoriquement non necessaire, car l'arbre est correct
 * du point de vu de la syntaxe mais une double verification ne fais pas de mal
 * et n'alterne pas les performances du compilateur car on est déja dans un switch.
 *
 * 
 */

package ProjetCompil.Verif.Src;

import ProjetCompil.Global.Src.*;

/**
 * Cette classe permet de réaliser la vérification et la décoration de l'arbre
 * abstrait d'un programme.
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
	 * l'arbre abstrait a, qui est décoré et enrichi. Les contraintes
	 * contextuelles sont décrites dans Context.txt. En cas d'erreur
	 * contextuelle, un message d'erreur est affiché et l'exception ErreurVerif
	 * est levée.
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
		// pas de genre...
		env.enrichir("string", def);
		// real
		def = Defn.creationType(Type.Real);
		def.setGenre(Genre.PredefReal);
		env.enrichir("real", def);
		// boolean
		def = Defn.creationType(Type.Boolean);
		def.setGenre(Genre.PredefBoolean);
		env.enrichir("boolean", def);
		//true
		def = Defn.creationConstBoolean(true);
		def.setGenre(Genre.PredefBoolean);
		env.enrichir("true", def);
		//false
		def = Defn.creationConstBoolean(false);
		def.setGenre(Genre.PredefBoolean);
		env.enrichir("false", def);
		//max_int
		def = Defn.creationConstInteger(java.lang.Integer.MAX_VALUE);
		def.setGenre(Genre.PredefInteger);
		env.enrichir("max_int", def);

	}

	private void verifier_PROGRAMME(Arbre a) throws ErreurVerif {
		initialiserEnv();
		verifier_LISTE_DECL(a.getFils1());
		verifier_LISTE_INST(a.getFils2());
	}


	private void verifier_LISTE_DECL(Arbre a) throws ErreurVerif {
		switch (a.getNoeud()) {
		case Vide: 
			return;
		case ListeDecl: 
			verifier_LISTE_DECL(a.getFils1());
			verifier_DECL(a.getFils2());
			return;
		default: 
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Liste Decl", a.getFils2().getNumLigne());
			return;
		}
	}

	private void verifier_DECL(Arbre a) throws ErreurVerif {
		Type type = verifier_TYPE(a.getFils2());
		verifier_LISTE_IDF(a.getFils1(), type);
	}

	private void verifier_LISTE_IDF(Arbre a, Type t) throws ErreurVerif {
		switch (a.getNoeud()) {
		case Vide: 
			return;
		case ListeIdent: 
			verifier_LISTE_IDF(a.getFils1(), t);
			boolean isPresent = env.enrichir(a.getFils2().getChaine(), Defn.creationVar(t));
			if(isPresent) {//TODO testfail
				ErreurContext err = ErreurContext.RedeclarationIdent;
				err.leverErreurContext(a.getFils2().getChaine(), a.getFils2().getNumLigne());
			}
			a.getFils2().setDecor(new Decor(Defn.creationVar(t),t));
			verifier_IDF(a.getFils2(),NatureDefn.Var);
			return;
		default: 
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Liste Idf", a.getFils2().getNumLigne());
			return;
		}
	}

	private void verifier_IDF(Arbre a, NatureDefn reqNat) throws ErreurVerif{
		Defn def = env.chercher(a.getChaine());
		if(def == null) {//TODO testfail
			ErreurContext err = ErreurContext.IdentificateurInconnu;
			err.leverErreurContext(a.getChaine(), a.getNumLigne());
			return;
		}
		a.setDecor(new Decor(def,def.getType()));	
		NatureDefn nat = a.getDecor().getDefn().getNature();
		if (nat != reqNat) {
			ErreurContext err = ErreurContext.IdentBadNature;
			err.leverErreurContext(nat+" au lieu de "+reqNat+" ", a.getNumLigne());
		}

	}

	private Type verifier_TYPE(Arbre a) throws ErreurVerif {
		switch (a.getNoeud()) {
		case Ident: 
			verifier_IDF(a,NatureDefn.Type);
			return trouverType(a.getChaine(), a.getNumLigne());
		case Intervalle: 
			Type t = verifier_INTERVALLE(a);
			return t;
		case Tableau: 
			Type t2 = verifier_TABLEAU(a);
			return t2;
		default: 
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Type", a.getNumLigne());
			return null;
		}
	}

	private void verifier_CONSTANTE(Arbre a) throws ErreurVerif{
		switch (a.getNoeud()) {
		case PlusUnaire: 
			verifier_CONSTANTE(a.getFils1());
			a.setDecor(new Decor(a.getFils1().getDecor().getType()));
			return;
		case MoinsUnaire: 
			verifier_CONSTANTE(a.getFils1());
			a.setDecor(new Decor(a.getFils1().getDecor().getType()));
			return;
		case Ident: 
			verifier_IDF(a,NatureDefn.ConstInteger);
			NatureDefn nat = a.getDecor().getDefn().getNature();
			if (nat != NatureDefn.ConstInteger) {
				ErreurContext err = ErreurContext.IdentBadNature;
				err.leverErreurContext(nat+"", a.getNumLigne());
			}
			return;
		case Entier: 
			a.setDecor(new Decor(Type.Integer));
			return;
		default: 
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Constante", a.getNumLigne());
			return;
		}
	}


	private Type verifier_INTERVALLE(Arbre a) throws ErreurVerif{
		verifier_CONSTANTE(a.getFils1());
		verifier_CONSTANTE(a.getFils2());
		int min, max;
		if (a.getFils1().getNoeud() == Noeud.PlusUnaire) {
			min = a.getFils1().getFils1().getEntier();
		} 
		else if (a.getFils1().getNoeud() == Noeud.MoinsUnaire) {
			min = -a.getFils1().getFils1().getEntier();
		} 
		else {
			min = a.getFils1().getEntier();
		}
		if (a.getFils2().getNoeud() == Noeud.PlusUnaire) {
			max = a.getFils2().getFils1().getEntier();
		} 
		else if (a.getFils2().getNoeud() == Noeud.MoinsUnaire) {
			max = -a.getFils2().getFils1().getEntier();
		} 
		else {
			max = a.getFils2().getEntier();
		}

		Type temp = Type.creationInterval(min, max);
		a.setDecor(new Decor(temp));
		return temp;
	}

	private Type verifier_TABLEAU(Arbre a) throws ErreurVerif {
		Type t1 = verifier_INTERVALLE(a.getFils1());
		Type t2 = verifier_TYPE(a.getFils2());
		Type arr = Type.creationArray(t1,t2);
		a.setDecor(new Decor(arr));
		return arr;
	}

	/**************************************************************************
	 * LISTE_INST
	 **************************************************************************/
	private void verifier_LISTE_INST(Arbre a) throws ErreurVerif {

		switch (a.getNoeud()) {
		case Vide: 
			return;
		case ListeInst: 
			verifier_LISTE_INST(a.getFils1());
			verifier_INST(a.getFils2());
			return;
		default: 
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Liste Inst", a.getFils2().getNumLigne());
			return;
		}
	}

	private void verifier_INST(Arbre a) throws ErreurVerif {
		switch (a.getNoeud()) {
		case Nop:
			return;
		case Affect:
			verifier_PLACE(a.getFils1());
			verifier_EXP(a.getFils2());
			Type t1= a.getFils1().getDecor().getType();
			Type t2= a.getFils2().getDecor().getType();
			ResultatAffectCompatible res = ReglesTypage.affectCompatible(t1, t2);
			if(res.getOk()== true){ 
				if(res.getConv2()==true){
					a.setFils2(Arbre.creation1(Noeud.Conversion, a.getFils2(), a.getFils2().getNumLigne()));
					a.getFils2().setDecor(new Decor(Type.Real));
				}
				a.setDecor(new Decor(t1));
			}
			else{
				ErreurContext err = ErreurContext.TypesNonCompatible;
				err.leverErreurContext(a.getNoeud()+"=>"+"("+t1.toString()+","+t2.toString()+")", a.getNumLigne());
			}
			return;
		case Pour:
			verifier_PAS(a.getFils1());
			verifier_LISTE_INST(a.getFils2());
			return;
		case TantQue :
			verifier_EXP(a.getFils1());
			Type exp = a.getFils1().getDecor().getType();
  			if (!(exp == Type.Boolean)) {
  				ErreurContext err = ErreurContext.TypesNonCompatible;
				err.leverErreurContext("("+exp+") au lieu de Boolean", a.getFils1().getNumLigne());
  			}
			verifier_LISTE_INST(a.getFils2());
			return;
		case Si :
			verifier_EXP(a.getFils1());
			Type cond = a.getFils1().getDecor().getType();
  			if (!(cond == Type.Boolean)) {
  				ErreurContext err = ErreurContext.TypesNonCompatible;
				err.leverErreurContext("("+cond+") au lieu de Boolean", a.getFils1().getNumLigne());
  			}
			verifier_LISTE_INST(a.getFils2());
			verifier_LISTE_INST(a.getFils3());
			return;
		case Ecriture :
			verifier_LISTE_EXP(a.getFils1());
			Arbre temp = a.getFils1();
			while (temp.getNoeud() != Noeud.Vide) {
				Type expWrite = temp.getFils2().getDecor().getType();
	  			if (!(expWrite instanceof TypeInterval) && expWrite != Type.Real && expWrite != Type.String) {
	  				ErreurContext err = ErreurContext.TypesNonCompatible;
					err.leverErreurContext("("+expWrite+") au lieu de TypeInterval, Real ou String", temp.getFils2().getNumLigne());
	  			}
	  			temp = temp.getFils1();
	  		}
			return;
		case Lecture :
			verifier_PLACE(a.getFils1());
			Type expRead = a.getFils1().getDecor().getType();
  			if (!(expRead instanceof TypeInterval) && expRead != Type.Real) {
  				ErreurContext err = ErreurContext.TypesNonCompatible;
				err.leverErreurContext("("+expRead+") au lieu de TypeInterval ou Real", a.getFils1().getNumLigne());
  			}
			return;
		case Ligne :
			return;
		default:
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Inst", a.getFils1().getNumLigne());
			return;
		}
	}

	private void verifier_PAS(Arbre a) throws ErreurVerif {
		switch (a.getNoeud()) {
  		case Increment :
  		case Decrement :
  			verifier_IDF(a.getFils1(),NatureDefn.Var);
  			Type idf = a.getFils1().getDecor().getType();
  			if (!(idf instanceof TypeInterval)) {
  				ErreurContext err = ErreurContext.TypesNonCompatible;
				err.leverErreurContext("("+idf+") au lieu de TypeInterval", a.getFils1().getNumLigne());
  			}
  			verifier_EXP(a.getFils2());
  			Type exp1 = a.getFils2().getDecor().getType();
  			if (!(exp1 instanceof TypeInterval)) {
  				ErreurContext err = ErreurContext.TypesNonCompatible;
				err.leverErreurContext("("+exp1+") au lieu de TypeInterval", a.getFils2().getNumLigne());
  			}
  			verifier_EXP(a.getFils3());
  			Type exp2 = a.getFils3().getDecor().getType();
  			if (!(exp2 instanceof TypeInterval)) {
  				ErreurContext err = ErreurContext.TypesNonCompatible;
				err.leverErreurContext("("+exp2+") au lieu de TypeInterval", a.getFils3().getNumLigne());
  			}
  			return;
	  	default:
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Pas", a.getFils1().getNumLigne());
			return;
		}
	}
	
	private void verifier_PLACE(Arbre a) throws ErreurVerif {
		switch (a.getNoeud()) {
		case Ident: 
			verifier_IDF(a,NatureDefn.Var);
			return;
		case Index: 
			verifier_PLACE(a.getFils1());
			if (!(a.getFils1().getDecor().getType() instanceof TypeArray)) {
				ErreurContext err = ErreurContext.IndexationNonArray;
				err.leverErreurContext("", a.getFils1().getNumLigne());
			}
			Type elem = ((TypeArray)(a.getFils1().getDecor().getType())).getElement();
			a.setDecor(new Decor(elem));
			verifier_EXP(a.getFils2());
			Type exp = a.getFils2().getDecor().getType();
			if(!(exp instanceof TypeInterval)){
				ErreurContext err = ErreurContext.TypeIndex;
				err.leverErreurContext(exp.toString(), a.getFils2().getNumLigne());
			}
			return;
		default: 
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Place", a.getFils1().getNumLigne());
			return;
		}
	}
		
	private void verifier_LISTE_EXP(Arbre a) throws ErreurVerif {
		switch (a.getNoeud()) {
		case Vide: 
			return;
		case ListeExp: 
			verifier_LISTE_EXP(a.getFils1());
			verifier_EXP(a.getFils2());
			return;
		default: 
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Liste Exp", a.getFils2().getNumLigne());
			return;
		}
	}

	private void verifier_EXP(Arbre a) throws ErreurVerif{
		switch (a.getNoeud()) {
		case Et: 
		case Ou: 
		case Egal: 
		case InfEgal: 
		case SupEgal: 
		case NonEgal: 
		case Inf: 
		case Sup: 
		case Plus: 
		case Moins: 
		case Mult: 
		case DivReel: 
		case Reste: 
		case Quotient: 
			verifier_EXP(a.getFils1());
			verifier_EXP(a.getFils2());
			Type t1= a.getFils1().getDecor().getType();
			Type t2= a.getFils2().getDecor().getType();
			ResultatBinaireCompatible res = ReglesTypage.binaireCompatible(a.getNoeud(), t1, t2);
			if(res.getOk()== true){
				if(res.getConv1()==true){

					a.setFils1(Arbre.creation1(Noeud.Conversion, a.getFils1(), a.getFils1().getNumLigne()));
					a.getFils1().setDecor(new Decor(Type.Real));
				}
				if(res.getConv2()==true){
					a.setFils2(Arbre.creation1(Noeud.Conversion, a.getFils2(), a.getFils2().getNumLigne()));
					a.getFils2().setDecor(new Decor(Type.Real));
				}
				a.setDecor(new Decor(res.getTypeRes()));
			}
			else{
				ErreurContext err = ErreurContext.TypesNonCompatible;
				err.leverErreurContext(a.getNoeud()+"=>"+"("+t1.toString()+","+t2.toString()+")", a.getNumLigne());
			}
			return;
		case Chaine: 
		case Ident: 
		case Index: 
		case Entier:
		case Reel:
			verifier_FACTEUR(a);
			return;
		case PlusUnaire: 
		case MoinsUnaire: 
		case Non: 
			verifier_FACTEUR(a.getFils1());
			Type t= a.getFils1().getDecor().getType();
			ResultatUnaireCompatible res2 = ReglesTypage.unaireCompatible(a.getNoeud(), t);
			if(res2.getOk()== true){ 
				a.setDecor(new Decor(res2.getTypeRes()));
			}
			else {
				ErreurContext err = ErreurContext.TypesNonCompatible;
				err.leverErreurContext(a.getNoeud()+"=>"+"("+t.toString()+")", a.getNumLigne());
			}
			
			return;
		default: 
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Exp", a.getNumLigne());
			return;
		}
	}

	

	private void verifier_FACTEUR(Arbre a) throws ErreurVerif {
		switch (a.getNoeud()) {
		case Entier: 
			a.setDecor(new Decor(Type.Integer));
			return;
		case Reel: 
			a.setDecor(new Decor(Type.Real));
			return;
		case Chaine: 
			a.setDecor(new Decor(Type.String));
			return;
		case Index: 
			verifier_PLACE(a);
			return;
		case Ident: 
			Defn def = env.chercher(a.getChaine());
			if(def == null) {
				throw new ErreurInterneVerif("identificateur "+a.getChaine()+" inconnu");
			}
			NatureDefn nat = def.getNature();
			if (nat != NatureDefn.Var && nat != NatureDefn.ConstInteger && nat != NatureDefn.ConstBoolean) {
				ErreurContext err = ErreurContext.IdentBadNature;
				err.leverErreurContext(nat+" au lieu de Var, ConstInteger ou ConstBoolean", a.getNumLigne());
			}
			a.setDecor(new Decor(def,def.getType()));	
			return;
		default: 
			ErreurContext err = ErreurContext.ProblemeCompilateur; 
			err.leverErreurContext("Facteur", a.getNumLigne());
			return;
		}
	}

	
	private Type trouverType(String s, int numLigne) throws ErreurVerif{
		Defn t= env.chercher(s);
		if(t!= null){
			return t.getType();
		}
		else {
			ErreurContext err = ErreurContext.ProblemeCompilateur; 
			err.leverErreurContext("pas de type sur l'identificateur ("+s+")", numLigne);
		}
		if(t.getNature() != NatureDefn.Type) {
			ErreurContext err = ErreurContext.IdentBadNature; 
			err.leverErreurContext(s, numLigne);
		}
		return null;
	}
	
	
	@SuppressWarnings("unused")
	private void verifier_SAMPLE(Arbre a) throws ErreurVerif {
		switch (a.getNoeud()) {
	  		case Vide :
	  	default: 
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Sample", a.getNumLigne());
			return;
		}
	}
}
