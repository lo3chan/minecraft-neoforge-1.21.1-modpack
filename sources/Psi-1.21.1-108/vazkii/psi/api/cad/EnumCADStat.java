package vazkii.psi.api.cad;

import java.util.Locale;

public enum EnumCADStat {
   EFFICIENCY(EnumCADComponent.ASSEMBLY),
   POTENCY(EnumCADComponent.ASSEMBLY),
   COMPLEXITY(EnumCADComponent.CORE),
   PROJECTION(EnumCADComponent.CORE),
   BANDWIDTH(EnumCADComponent.SOCKET),
   SOCKETS(EnumCADComponent.SOCKET),
   SAVED_VECTORS(EnumCADComponent.SOCKET),
   OVERFLOW(EnumCADComponent.BATTERY);

   private final EnumCADComponent source;

   private EnumCADStat(EnumCADComponent source) {
      this.source = source;
   }

   public EnumCADComponent getSourceType() {
      return this.source;
   }

   public String getName() {
      return "psi.cadstat." + this.name().toLowerCase(Locale.ROOT);
   }
}
