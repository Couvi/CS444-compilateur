package ProjetCompil.Gencode.Src;

import ProjetCompil.Global.Src.*;
import ProjetCompil.Global.Src3.*;
import java.util.*;

/**Classe qui permet de gérer les erreurs d'exécution
Lors de la génération du code du programme, on ajoute du code afin d'effectuer des vérifications à l'exécution (borne intervalle, ...)
Ces vérification peuvent conduire à une erreur. Il faut donc afficher l'erreur et arrêter le programme.
Cette librairie permet de centraliser les messages d'erreurs. Durant la génération de code, on fait appel à ces erreurs pour récupèrer le label correspondant.
Lorsque la génération de code est terminée, elle place à la fin les code à exécuter concernant les différentes erreurs qui peuvent se produire avec ce programme (avec les labels)
**/
public class Library {
	static Library lib=new Library(); //singleton
	//interface des fonctions de codage des erreurs, permet de manipuler ces fonction comme des objets
	//et des les stocker dans la hashmap
	public interface Codeur {
	  public void code();
	}
	
	HashMap<String, Codeur> mapfunc = new HashMap<String, Codeur>(); /**Contient la liste des erreurs utiles à ce programme**/

	public static Library get_instance() { //pour opbtenir l'instance de la librairie
		return lib;
	}
	private void register_error(String nom, String message) { //fonction d'ordre superieur qui génère des fonctions de codage d'erreur
		Codeur code = new Codeur() { //on utilise une classe annonyme qui contient la fonction de codage
			public void code() { 
				Prog.ajouter(Etiq.lEtiq(nom));
				Prog.ajouter(Inst.creation1(Operation.WSTR, 
								 			Operande.creationOpChaine(message)));
				Prog.ajouter(Inst.creation0(Operation.HALT));
			}
		};
		mapfunc.put(nom, code); //on ajoute cette fonction de codage à notre map des fonctions utilisés
	}
	
	//ici on code les erreurs necessaires
	//remarque: on aurait aussi pu crééer des procédures plus complexes pour factiriser le code généré (but premier de cette classe) 
	//mais nous en avons pas eu l'utilité

	/**Erreur qui intervient lorsque un nombre n'est plus pas compris dans l'intervalle défini à la délaration de la variable**/
	public Etiq get_IntervalOutOfBound() {
		register_error("IntervalOutOfBound", "ERREUR Interval Out Of Bound");
		return Etiq.lEtiq("IntervalOutOfBound");
	}
	/**Erreur qui intervient lors d'un overflow sur un registre**/
	public Etiq get_ArithmeticOverflow() {
		register_error("ArithmeticOverflow", "ERREUR  Arithmetic Overflow");
		return Etiq.lEtiq("ArithmeticOverflow");
	}
	
	/**Erreur de débordement de Stack**/
	public Etiq get_StackOverflow() {
		register_error("StackOverflow", "ERREUR Stack Overflow");
		return Etiq.lEtiq("StackOverflow");
	}
	
	/**Ecriture de la library en fin du programme**/
	public void writeLib() {
		Iterator it = mapfunc.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        ((Codeur)pair.getValue()).code();
	        it.remove();
	    }
	}
}