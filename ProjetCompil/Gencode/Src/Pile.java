package ProjetCompil.Gencode.Src;

import ProjetCompil.Global.Src.*;
import ProjetCompil.Global.Src3.*;
import java.util.HashMap;
import java.util.Map;

public class Pile {
	//Tout est exprimé par rapport à LB (qui se trouve entre la zone TEXT et la pile
	private static int SP = 0;
	private static int GB = 0;
	private static boolean declaration = true;

	public static void addGlobale(Arbre a) {
		if (declaration) {
			switch(a.getNoeud()){
				/*case Boolean:
				case Real:
				case Interval:
					SP++; GB++;
					//Ajouter l'information dans le décor (SP)
					break;
				case Array: 
					//todo !					
					break;*/
			}
		}
	}

	public static void finDeclaration() {
		declaration = false;
		Prog.ajouter(Inst.creation2(Operation.ADD, Operande.creationOpEntier(GB), Operande.opDirect(Registre.GB)));
		Prog.ajouter(Inst.creation1(Operation.ADDSP, Operande.creationOpEntier(SP)));
		SP = 0;
	}


	public static int allouer() {
		if(!declaration) {
			SP++;
			Prog.ajouter(Inst.creation1(Operation.ADDSP, Operande.creationOpEntier(1)));
			return SP;
		}
		return -1;
	}

	public static void liberer(int ref) {
		if (!declaration && ref == SP) {
			SP--;
			Prog.ajouter(Inst.creation1(Operation.ADDSP, Operande.creationOpEntier(-1)));
		}
	}
	  
}