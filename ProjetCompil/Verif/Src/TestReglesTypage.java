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
   }

    public Boolean TestAffectCompatible (Type t1, Type t2, ResultatAffectCompatible result) {
	    ResultatAffectCompatible retour = ReglesTypage.affectCompatible(t1, t2);
	    if (retour.getOk() == result.getOk() && retour.getConv2() == result.getConv2())
		return true;
	    else 
		return false;
    }
    
    public Boolean TestBinaireCompatible (Noeud noeud, Type t1, Type t2, ResultatBinaireCompatible result) {
	    ResultatBinaireCompatible retour = ReglesTypage.binaireCompatible(noeud, t1, t2);
	    if (retour.getOk() == result.getOk() && retour.getConv1() == result.getConv1() && retour.getConv2() == result.getConv2() && retour.getTypeRes().getNature() == result.getTypeRes().getNature())
		return true;
	    else 
		return false;
    }
    
    public Boolean TestUnaireCompatible (Noeud noeud, Type t, ResultatUnaireCompatible result) {
	    ResultatUnaireCompatible retour = ReglesTypage.unaireCompatible(noeud, t);
	    if (retour.getOk() == result.getOk() && retour.getTypeRes().getNature() == result.getTypeRes().getNature())
		return true;
	    else 
		return false;
    }

}

