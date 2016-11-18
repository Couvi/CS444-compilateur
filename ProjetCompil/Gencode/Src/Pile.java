package ProjetCompil.Gencode.Src;

import ProjetCompil.Global.Src.*;
import ProjetCompil.Global.Src3.*;
import java.util.HashMap;
import java.util.Map;

public class Pile {

	private static int sommet = 1; //Premier emplacement vide par rapport à LB
	private static int LB;
	private static boolean declaration = true;
	private static Map<String, Tuple<Integer, Type> >variables = new HashMap<String, Tuple<Integer, Type> >();

	public static void addGlobale(String nom, Type type) {
		if (declaration) {
			variables.put(nom, new Tuple(sommet, type));
			switch(type.getNature()){
				case Boolean:
				case Real:
				case Interval:
					sommet++;
					break;
				case Array: 
					//todo !					
					break;
			}
		}
	}

	public static Tuple<Integer,Type> getGlobale(String nom) {
		return variables.get(nom);
	}

	public static void finDeclaration() {
		declaration = false;
		LB = sommet-1;
		Prog.ajouter(Inst.creation2(Operation.ADD, Operande.creationOpEntier(LB), Operande.opDirect(Registre.LB)));
		//Ajouter déplacement de SP
	}


	public static int allouer() {
		//Augmenter SP
		sommet++;
		return sommet-LB;
	}

	public static void liberer(int ref) {
		if (ref == sommet) {
			sommet--;
			//Diminuer SP
		}

	  }
	  
}