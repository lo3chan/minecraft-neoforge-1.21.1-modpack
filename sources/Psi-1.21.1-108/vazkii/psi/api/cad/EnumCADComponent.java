package vazkii.psi.api.cad;

import java.util.Locale;

public enum EnumCADComponent {
   ASSEMBLY,
   CORE,
   SOCKET,
   BATTERY,
   DYE;

   public String getName() {
      return "psi.component." + this.name().toLowerCase(Locale.ROOT);
   }
}
