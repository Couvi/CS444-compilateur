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

	/**************************************************************************
	 * PROGRAMME
	 **************************************************************************/
	private void verifier_PROGRAMME(Arbre a) throws ErreurVerif {
		initialiserEnv();
		verifier_LISTE_DECL(a.getFils1());
		verifier_LISTE_INST(a.getFils2());
	}

	/**************************************************************************
	 * LISTE_DECL
	 **************************************************************************/
	private void verifier_LISTE_DECL(Arbre a) throws ErreurVerif {
		switch (a.getNoeud()) {
		case Vide: {
			return;
		}
		case ListeDecl: {
			verifier_LISTE_DECL(a.getFils1());
			verifier_DECL(a.getFils2());
			return;
		}
		default: {
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Liste Decl", a.getFils2
				().getNumLigne());
			return;
		}
		}
	}

	private void verifier_DECL(Arbre a) throws ErreurVerif {
		Type type = verifier_TYPE(a.getFils2());
		verifier_LISTE_IDF(a.getFils1(), type);
	}

	private void verifier_LISTE_IDF(Arbre a, Type t) throws ErreurVerif {
		switch (a.getNoeud()) {
		case Vide: {
			return;
		}
		case ListeIdent: {
			verifier_LISTE_IDF(a.getFils1(), t);
			boolean isPresent = env.enrichir(a.getFils2().getChaine(), Defn.creationVar(t));
			if(isPresent) {
				ErreurContext err = ErreurContext.RedeclarationIdent;
				err.leverErreurContext(a.getFils2().getChaine(), a.getFils2().getNumLigne());
			}
			a.getFils2().setDecor(new Decor(Defn.creationVar(t),t));
			verifier_IDF(a.getFils2());
			return;
		}
		default: {
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Liste Idf", a.getFils2().getNumLigne());
			return;
		}
		}
	}

	private void verifier_IDF(Arbre a) throws ErreurVerif{
		Defn def = env.chercher(a.getChaine());
		if(def == null) {
			ErreurContext err = ErreurContext.IdentificateurInconnu;
			err.leverErreurContext(a.getChaine(), a.getNumLigne());
			return;
		}
		a.setDecor(new Decor(def,def.getType()));	

	}

	private Type verifier_TYPE(Arbre a) throws ErreurVerif {
		switch (a.getNoeud()) {
		case Ident: {
			verifier_IDF(a);
			return trouverType(a.getChaine(), a.getNumLigne());
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
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Type", a.getNumLigne());
			return null;
		}
		}
	}

	private void verifier_CONSTANTE(Arbre a) throws ErreurVerif{
		switch (a.getNoeud()) {
		case PlusUnaire: {
			verifier_CONSTANTE(a.getFils1());
			return;
		}
		case MoinsUnaire: {
			verifier_CONSTANTE(a.getFils1());
			return;
		}
		case Ident: {
			verifier_IDF(a);
			a.setDecor(new Decor(Type.Integer));
			return;
		}
		case Entier: {
			a.setDecor(new Decor(Type.Integer));
			return;
		}
		default: {
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Constante", a.getNumLigne());
			return;
		}
		}
	}


	private Type verifier_INTERVALLE(Arbre a) throws ErreurVerif{
		verifier_CONSTANTE(a.getFils1());
		verifier_CONSTANTE(a.getFils2());
		Type t1 = a.getFils1().getDecor().getType();
		Type t2 = a.getFils2().getDecor().getType();
		if(!(t1 instanceof TypeInterval)) {
			ErreurContext err = ErreurContext.BorneNonEntier;
			err.leverErreurContext("", a.getFils1().getNumLigne());
		}
		if(!(t2 instanceof TypeInterval)) {
			ErreurContext err = ErreurContext.BorneNonEntier;
			err.leverErreurContext("", a.getFils2().getNumLigne());
		}
		Type temp = Type.creationInterval(a.getFils1().getEntier(), a.getFils2().getEntier());
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
		case Vide: {
			return;
		}
		case ListeInst: {
			verifier_LISTE_INST(a.getFils1());
			verifier_INST(a.getFils2());
			return;
		}
		default: {
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Liste Inst", a.getFils2().getNumLigne());
			return;
		}
		}
	}

	private void verifier_INST(Arbre a) throws ErreurVerif {
		switch (a.getNoeud()) {
		case Nop:
			return;
		case Affect:
			verifier_PLACE(a.getFils1());
			verifier_EXP(a.getFils2());
			return;
		case Pour:
			verifier_PAS(a.getFils1());
			verifier_LISTE_INST(a.getFils2());
			return;
		case TantQue :
			verifier_EXP(a.getFils1());
			verifier_LISTE_INST(a.getFils2());
			return;
		case Si :
			verifier_EXP(a.getFils1());
			verifier_LISTE_INST(a.getFils2());
			verifier_LISTE_INST(a.getFils3());
			return;
		case Ecriture :
			verifier_LISTE_EXP(a.getFils1());
			return;
		case Lecture :
			verifier_PLACE(a.getFils1());
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
	  			verifier_IDF(a.getFils1());
	  			verifier_EXP(a.getFils2());
	  			verifier_EXP(a.getFils3());
	  			return;
	  	default:
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Pas", a.getFils1().getNumLigne());
			return;
		}
	}
	
	private void verifier_PLACE(Arbre a) throws ErreurVerif {
		switch (a.getNoeud()) {
		case Ident: {
			verifier_IDF(a);
			return;
		}
		case Index: {
			verifier_PLACE(a.getFils1());
			if (!(a.getFils1().getDecor().getType() instanceof TypeArray)) {
				ErreurContext err = ErreurContext.IndexationNonArray;
				err.leverErreurContext("", a.getFils1().getNumLigne());
			}
			Type elem = ((TypeArray)(a.getFils1().getDecor().getType())).getElement();
			Type index = ((TypeArray)(a.getFils1().getDecor().getType())).getIndice();
			a.setDecor(new Decor(elem));
			verifier_EXP(a.getFils2());
			if(!(index instanceof TypeInterval)){
				ErreurContext err = ErreurContext.TypeIndex;
				err.leverErreurContext(index.toString(), a.getFils2().getNumLigne());
			}
			return;
		}
		default: {
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Place", a.getFils1().getNumLigne());
			return;
		}
		}
	}
		
	private void verifier_LISTE_EXP(Arbre a) throws ErreurVerif {
		switch (a.getNoeud()) {
		case Vide: {
			return;
		}
		case ListeExp: {
			verifier_LISTE_EXP(a.getFils1());
			verifier_EXP(a.getFils2());
			return;
		}
		default: {
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Liste Exp", a.getFils2().getNumLigne());
			return;
		}
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
		case Quotient: {
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
				err.leverErreurContext(t1.toString()+","+t2.toString(), a.getNumLigne());
			}
			return;
		}
		case Chaine: 
		case Ident: 
		case Index: 
		case Entier:
			verifier_FACTEUR(a);
			return;
		case PlusUnaire: 
		case MoinsUnaire: 
		case Non: 
			verifier_FACTEUR(a.getFils1());
			a.setDecor(a.getFils1().getDecor());
			return;
		default: 
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Exp", a.getNumLigne());
			return;
		}
	}

	

	private void verifier_FACTEUR(Arbre a) throws ErreurVerif {
		switch (a.getNoeud()) {
		case Entier: {
			a.setDecor(new Decor(Type.Integer));
			return;
		}	
		case Reel: {
			a.setDecor(new Decor(Type.Real));
			return;
		}
		case Chaine: {
			a.setDecor(new Decor(Type.String));
			return;
		}
		case Index: {
			verifier_PLACE(a);
			return;
		}
		case Ident: {
			verifier_IDF(a);
			return;
		}


		default: {
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Facteur", a.getNumLigne());
			return;
		}
		}
	}

	
	private Type trouverType(String s, int numLigne) throws ErreurVerif{
		
	
		Defn t= env.chercher(s);
		if(t!= null){
			return t.getType();
		}
		else {
			ErreurContext err = ErreurContext.TypeInconnu;
			err.leverErreurContext(s, numLigne);
		}
		return null;
	}
	
	
	@SuppressWarnings("unused")
	private void verifier_SAMPLE(Arbre a) throws ErreurVerif {
		switch (a.getNoeud()) {
	  		case Vide :

	  	default: {
			ErreurContext err = ErreurContext.ProblemeCompilateur;
			err.leverErreurContext("Sample", a.getNumLigne());
			return;
		}
		}
	}
}
