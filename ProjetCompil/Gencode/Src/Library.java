package ProjetCompil.Gencode.Src;

import ProjetCompil.Global.Src.*;
import ProjetCompil.Global.Src3.*;

class Library {

	private void code_IndexOutOfBound() {
		Prog.ajouter(new Ligne(Etiq.lEtiq("IndexOutOfBound"), 
						 Inst.creation1(Operation.WSTR, 
						 				Operande.creationOpChaine("ERREUR Index Out Of Bound")), 
						 ""));
		Prog.ajouter(Inst.creation0(Operation.HALT));
	}

	public Etiq get_IndexOutOfBound() {
		return Etiq.lEtiq("IndexOutOfBound");
	}

	public void writeLib() {
		code_IndexOutOfBound();
	}
}