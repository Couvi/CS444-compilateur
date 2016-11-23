package ProjetCompil.Gencode.Src;

import ProjetCompil.Global.Src.*;
import ProjetCompil.Global.Src3.*;
import java.util.HashMap;
import java.util.Map;

public class Pile {
	//Tout est exprimé par rapport à LB (qui se trouve entre la zone TEXT et la pile
	private static int SP = 0;
	private static int LB = 0;
	private static boolean declaration = true;

	public static void addGlobale(Arbre a) {
		if (declaration) {
			switch(a.getNoeud()){
				case Entier:
				case Intervalle:
					SP++; LB++;
					a.getDecor().setInfoCode(SP);
					break;
				case Tableau: 
					//todo !					
					break;
			}
		}
	}

	public static void finDeclaration() {
		declaration = false;
		Prog.ajouter(Inst.creation2(Operation.ADD, Operande.creationOpEntier(LB), Operande.opDirect(Registre.LB)));
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