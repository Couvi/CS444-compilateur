package ProjetCompil.Gencode.Src;

import ProjetCompil.Global.Src.*;
import ProjetCompil.Global.Src3.*;
import java.util.HashMap;
import java.util.Map;

public class Reg {
	
	  private static Map<Registre, Boolean> pool = new HashMap<Registre, Boolean>();

	  public static void init () {
	    for (Registre r : Registre.values()) {
	      pool.put(r, true);
	    }
	    pool.put(Registre.R0, false);
	    pool.put(Registre.GB, false);
	    pool.put(Registre.LB, false);
	    pool.put(Registre.R1, false);
	  }

	  public static Registre allouer() {
	    for (Registre r : Registre.values()) {
	      if (pool.get(r)) {
	        pool.put(r, false);
	        return r;
	      }
	    }
	    return null;
	  }

	  public static void liberer(Registre reg) {
		  if (reg != null && reg!=Registre.R0 && reg!=Registre.GB && reg!=Registre.LB && reg!=Registre.R1)
			  pool.put(reg, true);
	  }
}