package ProjetCompil.Gencode.Src;

import ProjetCompil.Global.Src.*;
import ProjetCompil.Global.Src3.*;
import java.util.HashMap;
import java.util.Map;

public class Pile {
	//Tout est exprimé par rapport à GB
	private static int SP = 0;
	private static boolean declaration = true;

	public static void addGlobale(Arbre a) {
		if (declaration) {
			switch(a.getDecor().getType().getNature()){
				case Boolean:
				case Real:
				case Interval:
					SP++;
					a.getDecor().setInfoCode(SP);
					break;
				case Array: 
					a.getDecor().setInfoCode(SP+1);
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

	public static void finDeclaration() {
		declaration = false;
		Prog.ajouter(Inst.creation1(Operation.ADDSP, Operande.creationOpEntier(SP)));
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