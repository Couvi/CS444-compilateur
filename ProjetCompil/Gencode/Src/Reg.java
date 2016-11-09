package ProjetCompil.Gencode.Src;

import ProjetCompil.Global.Src.*;
import ProjetCompil.Global.Src3.*;

public static class Reg {

  private Map<Registre, Boolean> pool = new HashMap<Registre, Boolean>();

  public void init (void) {
    for (Registre r : Registre.values()) {
      pool.put(r, true);
  }

  public Register request () {
    for (Registre r : Registre.values()) {
      if (pool.get(r)) {
        poll.put(r, false);
        return r;
      }
    }
    return null;
  }

  public void free(Registre reg) {
    poll.put(r, true);
  }

}
