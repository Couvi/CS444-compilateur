package ProjetCompil.Gencode.Src;

import ProjetCompil.Global.Src.*;
import ProjetCompil.Global.Src3.*;
import java.util.HashMap;
import java.util.Map;

public class Pile {
	//Tout est exprimé par rapport à GB
	private static int SP = 0;
	private static boolean declaration = true;
	private static Library lib = Library.get_instance();
	private static Map<String, Integer> variables = new HashMap<String, Integer>();

	public static void addGlobale(Arbre a) {
		if (declaration) {
			switch(a.getDecor().getType().getNature()){
				case Boolean:
				case Real:
				case Interval:
					SP++;
					variables.put(a.getChaine(), SP);
					break;
				case Array: 
					variables.put(a.getChaine(), SP+1);
					Type temp = a.getDecor().getType();
					int taille = 1;
					while(temp.getNature() == NatureType.Array) {
						taille *= temp.getIndice().getBorneSup()-temp.getIndice().getBorneInf()+1;
						temp = temp.getElement();
					}
					SP += taille;				
					break;
			}
		}
	}
	
	public static int getGlobale(String nom) {
		return variables.get(a.getChaine());
	}

	public static void finDeclaration() {
		declaration = false;
		Prog.ajouter(Inst.creation1(Operation.TSTO, Operande.creationOpEntier(SP)));
		Prog.ajouter(Inst.creation1(Operation.BOV, Operande.creationOpEtiq(lib.get_StackOverflow())));
		Prog.ajouter(Inst.creation1(Operation.ADDSP, Operande.creationOpEntier(SP)));
	}


	public static int allouer() {
		if(!declaration) {
			SP++;
			Prog.ajouter(Inst.creation1(Operation.TSTO, Operande.creationOpEntier(1)));
			Prog.ajouter(Inst.creation1(Operation.BOV, Operande.creationOpEtiq(lib.get_StackOverflow())));
			Prog.ajouter(Inst.creation1(Operation.ADDSP, Operande.creationOpEntier(1)));
			return SP;
		}
		return -1;
	}

	public static void liberer(int ref) {
		if (!declaration && ref == SP) {
			SP--;
			Prog.ajouter(Inst.creation1(Operation.SUBSP, Operande.creationOpEntier(1)));
		}
	}
	  
}