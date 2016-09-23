package ProjetCompil.Verif.Src;

import ProjetCompil.Global.Src.*;

/**
 * La classe ReglesTypage permet de définir les différentes règles 
 * de typage du langage JCas.
 */

public class ReglesTypage {

   /**
    * Teste si le type t1 et le type t2 sont compatibles pour l'affectation, 
    * c'est à dire si on peut affecter un objet de t2 à un objet de type t1.
    */

   static ResultatAffectCompatible affectCompatible(Type t1, Type t2) {
	ResultatAffectCompatible result = new ResultatAffectCompatible();
	result.setOk(false);
	result.setConv2(false);
		   
	if ( (t1.getNature() == NatureType.Interval && t2.getNature() == NatureType.Interval) ||
		(t1.getNature() == NatureType.Real && t2.getNature() == NatureType.Real) ||
		(t1.getNature() == NatureType.Boolean && t2.getNature() == NatureType.Boolean) ||
		(t1.getNature() == NatureType.Real && t2.getNature() == NatureType.Interval) )
		result.setOk(true);
	
	if (t1.getNature() == NatureType.Array && t2.getNature() == NatureType.Array &&
		t1.getIndice().getNature() == NatureType.Interval && t2.getIndice().getNature() == NatureType.Interval &&
		t1.getIndice().getBorneInf() == t2.getIndice().getBorneInf() &&  t1.getIndice().getBorneSup() == t2.getIndice().getBorneSup())
			result = affectCompatible(t1.getElement().getNature(), t2.getElement().getNature());
	
	if (t1.getNature() == NatureType.Real && t2.getNature() == NatureType.Interval)
		result.setConv2(true);
	
	return result;
   }

   /**
    * Teste si le type t1 et le type t2 sont compatible pour l'opération 
    * binaire représentée dans noeud.
    */

   static ResultatBinaireCompatible binaireCompatible (Noeud noeud, Type t1, Type t2) {
	ResultatBinaireCompatible result = new ResultatBinaireCompatible();
	result.setOk(false);
	result.setConv1(true);
	result.setConv2(false);
	
	switch (noeud) {
		case Et : if(t1.getNature() == NatureType.Boolean && t2.getNature() == NatureType.Boolean) {
			 result.setTypeRes(NatureType.Boolean);
			} break;
		case  
					
      return null;
   }

   /**
    * Teste si le type t est compatible pour l'opération binaire représentée 
    * dans noeud.
    */
   static ResultatUnaireCompatible unaireCompatible
         (Noeud noeud, Type t) {
      return null;
   }
         
}

