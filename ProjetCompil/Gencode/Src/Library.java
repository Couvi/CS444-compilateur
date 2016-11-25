package ProjetCompil.Gencode.Src;

import ProjetCompil.Global.Src.*;
import ProjetCompil.Global.Src3.*;
import java.util.*;

class Library {
	static Library lib=new Library();
	public interface Codeur {
	  public void code();
	}
	HashMap<String, Codeur> mapfunc = new HashMap<String, Codeur>();

	public static Library get_instance() {
		return lib;
	}
	private void register_error(String nom, String message) {
		Codeur code = new Codeur() {
			public void code() { 
				Prog.ajouter(Etiq.lEtiq(nom));
				Prog.ajouter(Inst.creation1(Operation.WSTR, 
								 			Operande.creationOpChaine(message)));
				Prog.ajouter(Inst.creation0(Operation.HALT));
			}
		};
		mapfunc.put(nom, code);
	}
	public Etiq get_IndexOutOfBound() {
		register_error("IndexOutOfBound", "ERREUR Index Out Of Bound");
		return Etiq.lEtiq("IndexOutOfBound");
	}
	public Etiq get_StackOverflow() {
		register_error("StackOverflow", "ERREUR Stack Overflow");
		return Etiq.lEtiq("StackOverflow");
	}
	public void writeLib() {
		Iterator it = mapfunc.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        ((Codeur)pair.getValue()).code();
	        it.remove();
	    }
	}
}