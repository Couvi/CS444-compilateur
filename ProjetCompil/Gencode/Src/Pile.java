package ProjetCompil.Gencode.Src;

import ProjetCompil.Global.Src.*;
import ProjetCompil.Global.Src3.*;
import java.util.HashMap;
import java.util.Map;

/**Classe permettant de gérer la pile (variables globales et temporaire**/
public class Pile {
	private static int SP = 0; /** Exprimé par rapport à GB**/
	private static boolean declaration = true; /**Ce booleen permet d'éviter de mettre des valeurs au somet de la stack si on n'a pas fini les déclarations **/
	private static Library lib = Library.get_instance();
	private static Map<String, Integer> variables = new HashMap<String, Integer>(); /** HashMap qui contient les référence entre le nom des variables et les positions**/

	/** Permet de déclarer les variables du programme, on reservant l'espace nécésaire dans la pile.
	Pour tout les types exceptés les tableaux, on réserve une taille de un.
	Pour les tableaux c'est plus compliqué car il faut déterminer la taille du tableau (qui peut lui même être composé d'un tableau...). On utilise donc une boucle qui nous donne la taille.**/
	public static void addGlobale(Arbre a) {
		if (declaration) {
			switch(a.getDecor().getType().getNature()){
				case Boolean:
				case Real:
				case Interval:
					SP++;
					variables.put(a.getChaine().toLowerCase(), SP);
					break;
				case Array: 
					variables.put(a.getChaine().toLowerCase(), SP+1);
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
	
	/**Permet de récupèrer la position dans la pile, d'une varible (par son nom)**/
	public static int getGlobale(String nom) {
		return variables.get(nom.toLowerCase());
	}

	/** Indiquer que les déclaration sont terminées. On va donc écrire les instruction permettant de réserver l'espace requis dans la pile **/
	public static void finDeclaration() {
		declaration = false;
		Prog.ajouter(Inst.creation1(Operation.TSTO, Operande.creationOpEntier(SP)));
		Prog.ajouter(Inst.creation1(Operation.BOV, Operande.creationOpEtiq(lib.get_StackOverflow())));
		Prog.ajouter(Inst.creation1(Operation.ADDSP, Operande.creationOpEntier(SP)));
	}


	/** Demander un emplacement dans la pile **/
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

	/** Libérer un emplacement préalablement pris dans la pile **/
	public static void liberer(int ref) {
		if (!declaration && ref == SP) {
			SP--;
			Prog.ajouter(Inst.creation1(Operation.SUBSP, Operande.creationOpEntier(1)));
		}
	}
	  
}