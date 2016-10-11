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
			result = affectCompatible(t1.getElement(), t2.getElement());
	
	if ( (t1.getNature() == NatureType.Real && t2.getNature() == NatureType.Interval) )
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
	result.setConv1(false);
	result.setConv2(false);
	
	switch (noeud) {
		case Et :
		case Ou : 
			if(t1.getNature() == NatureType.Boolean && t2.getNature() == NatureType.Boolean) {
			result.setTypeRes(Type.Boolean);
			result.setOk(true);
			} break;

		case Egal :
		case Inf :
		case Sup :
		case NonEgal :
		case InfEgal :
		case SupEgal : 
			if ( (t1.getNature() == NatureType.Interval && t2.getNature() == NatureType.Interval) || (t1.getNature() == NatureType.Real && t2.getNature() == NatureType.Real)) {
				result.setTypeRes(Type.Boolean);
				result.setOk(true);
			}
			else if (t1.getNature() == NatureType.Interval && t2.getNature() == NatureType.Real) {
				result.setTypeRes(Type.Boolean);
				result.setConv1(true);
				result.setOk(true);
			}
			else if (t1.getNature() == NatureType.Real && t2.getNature() == NatureType.Interval) {
				result.setTypeRes(Type.Boolean);
				result.setConv2(true);
				result.setOk(true);
			}
			break;

		case Plus :
		case Moins :
		case Mult :
			if(t1.getNature() == NatureType.Interval && t2.getNature() == NatureType.Interval) {
				result.setTypeRes(Type.Integer);
				result.setOk(true);
			}
			else if (t1.getNature() == NatureType.Interval && t2.getNature() == NatureType.Real) {
				result.setTypeRes(Type.Real);
				result.setConv1(true);
				result.setOk(true);
			}
			else if (t1.getNature() == NatureType.Real && t2.getNature() == NatureType.Interval) {
				result.setTypeRes(Type.Real);
				result.setConv2(true);
				result.setOk(true);
			}
			else if (t1.getNature() == NatureType.Real && t2.getNature() == NatureType.Real) {
				result.setTypeRes(Type.Real);
				result.setOk(true);
			}
			break;

		case Quotient :
		case Reste :
			if (t1.getNature() == NatureType.Interval && t2.getNature() == NatureType.Interval){
			result.setTypeRes(Type.Integer);
			result.setOk(true);
			}	break;

		case DivReel :
			if ( (t1.getNature() == NatureType.Interval && t2.getNature() == NatureType.Interval) || (t1.getNature() == NatureType.Real && t2.getNature() == NatureType.Real)) {
				result.setTypeRes(Type.Real);
				result.setOk(true);
			}
			else if (t1.getNature() == NatureType.Interval && t2.getNature() == NatureType.Real) {
				result.setTypeRes(Type.Real);
				result.setConv1(true);
				result.setOk(true);
			}
			else if (t1.getNature() == NatureType.Real && t2.getNature() == NatureType.Interval) {
				result.setTypeRes(Type.Real);
				result.setConv2(true);
				result.setOk(true);
			}
			break;
	}
					
      return result;
   }

   /**
    * Teste si le type t est compatible pour l'opération binaire représentée 
    * dans noeud.
    */
   static ResultatUnaireCompatible unaireCompatible
         (Noeud noeud, Type t) {
		ResultatUnaireCompatible result = new ResultatUnaireCompatible();
		result.setOk(false);
	
		switch (noeud) {
			case Non : 
				if (t.getNature() == NatureType.Boolean) {
					result.setTypeRes(Type.Boolean);
					result.setOk(true);
				}
				break;
		
			case PlusUnaire :
			case MoinsUnaire :
				if (t.getNature() == NatureType.Interval) {
					result.setTypeRes(Type.Integer);
					result.setOk(true);
				}
				else if (t.getNature() == NatureType.Real) {
					result.setTypeRes(Type.Real);
					result.setOk(true);
				}
		}
	return result;
   }
         
}

