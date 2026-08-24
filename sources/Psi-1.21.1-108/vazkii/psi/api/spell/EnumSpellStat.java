package vazkii.psi.api.spell;

import java.util.Locale;
import vazkii.psi.api.cad.EnumCADStat;

public enum EnumSpellStat {
   COMPLEXITY(EnumCADStat.COMPLEXITY),
   POTENCY(EnumCADStat.POTENCY),
   COST(null),
   PROJECTION(EnumCADStat.PROJECTION),
   BANDWIDTH(EnumCADStat.BANDWIDTH);

   private final EnumCADStat target;

   private EnumSpellStat(EnumCADStat target) {
      this.target = target;
   }

   public EnumCADStat getTarget() {
      return this.target;
   }

   public String getName() {
      return "psi.spellstat." + this.name().toLowerCase(Locale.ROOT);
   }

   public String getDesc() {
      return this.getName() + ".desc";
   }
}
