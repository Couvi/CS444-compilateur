// ----------------------------------------------------------------------------
// Testeur des regles de typage
// ----------------------------------------------------------------------------

package ProjetCompil.Verif.Src;

import ProjetCompil.Global.Src.*;
import ProjetCompil.Syntaxe.Src.*;

/** 
 * Classe qui permet de tester les regles de typage
 */

public class TestReglesTypage {


   /**
    * Methode de test des regles de typage.
    */
   public static void main(String args[]) throws Exception {
	System.out.println("Demarrage des tests des regles de typage");
	
	if(!TestAffectCompatible(new TypeInterval(0, 15), new TypeInterval(20, 42), new ResultatAffectCompatible(true, false)))
		System.out.println("Erreur Test Affect : TypeInterval - TypeInterval");
	if(!TestAffectCompatible(Type.Real,Type.Real, new ResultatAffectCompatible(true, false)))
		System.out.println("Erreur Test Affect : Real - Real");
	if(!TestAffectCompatible(Type.Boolean,Type.Boolean, new ResultatAffectCompatible(true, false)))
		System.out.println("Erreur Test Affect : Boolean - Boolean");
	if(!TestAffectCompatible(Type.Real,new TypeInterval(0, 15), new ResultatAffectCompatible(true, true)))
		System.out.println("Erreur Test Affect : Real - TypeInterval");
	if(!TestAffectCompatible(new TypeArray(new TypeInterval(0, 15), Type.Real),new TypeArray(new TypeInterval(0, 15), new TypeInterval(45, 77)), new ResultatAffectCompatible(true, true)))
		System.out.println("Erreur Test Affect : Array");
	if(!TestAffectCompatible(new TypeArray(new TypeInterval(1, 15), Type.Real),new TypeArray(new TypeInterval(0, 15), new TypeInterval(45, 77)), new ResultatAffectCompatible(false, false)))
		System.out.println("Erreur Test Affect : Array");
	
	
   }

    public static Boolean TestAffectCompatible (Type t1, Type t2, ResultatAffectCompatible result) {
	    ResultatAffectCompatible retour = ReglesTypage.affectCompatible(t1, t2);
	    if (retour.getOk() == result.getOk() && retour.getConv2() == result.getConv2())
		return true;
	    else 
		return false;
    }
    
    public static Boolean TestBinaireCompatible (Noeud noeud, Type t1, Type t2, ResultatBinaireCompatible result) {
	    ResultatBinaireCompatible retour = ReglesTypage.binaireCompatible(noeud, t1, t2);
	    if (retour.getOk() == result.getOk() && retour.getConv1() == result.getConv1() && retour.getConv2() == result.getConv2() && retour.getTypeRes().getNature() == result.getTypeRes().getNature())
		return true;
	    else 
		return false;
    }
    
    public static Boolean TestUnaireCompatible (Noeud noeud, Type t, ResultatUnaireCompatible result) {
	    ResultatUnaireCompatible retour = ReglesTypage.unaireCompatible(noeud, t);
	    if (retour.getOk() == result.getOk() && retour.getTypeRes().getNature() == result.getTypeRes().getNature())
		return true;
	    else 
		return false;
    }

}

