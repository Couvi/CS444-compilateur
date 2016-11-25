package ProjetCompil.Gencode.Src;

import ProjetCompil.Global.Src.*;
import ProjetCompil.Global.Src3.*;
import java.util.HashMap;
import java.util.Map;

/** Classe gérant l'occupation et l'utilisation des registres du processeur lors de la compilation **/
public class Reg {
	
	  private static Map<Registre, Boolean> pool = new HashMap<Registre, Boolean>();

	  /** Certain registres sont réservé durant l'initialisation pour le bon fonctionnement du programme. 
		Par exemple les registre R0, R1, R2 sont d'office réservé pour un usage bien précis dans le programme
 	  **/
	  public static void init () {
	    for (Registre r : Registre.values()) {
	      pool.put(r, true);
	    }
	    pool.put(Registre.R0, false);
	    pool.put(Registre.R1, false);
	    pool.put(Registre.R2, false);
	    pool.put(Registre.GB, false);
	    pool.put(Registre.LB, false);
	  }

	  /** Permet d'obtenir un registre qui est libre. Elle return null si plus aucun registre n'est libre et qu'il faut utiliser la pile. **/
	  public static Registre allouer() {
	    for (Registre r : Registre.values()) {
	      if (pool.get(r)) {
	        pool.put(r, false);
	        return r;
	      }
	    }
	    return null;
	  }

	  /** Permet de rendre un registre qui a été demandé précédemment. Il va donc être libéré et pourra être utilisé ultérieurement **/
	  public static void liberer(Registre reg) {
		  if (reg != null && reg!=Registre.R0 && reg!=Registre.R1 && reg!=Registre.R2 && reg!=Registre.GB && reg!=Registre.LB)
			  pool.put(reg, true);
	  }
}