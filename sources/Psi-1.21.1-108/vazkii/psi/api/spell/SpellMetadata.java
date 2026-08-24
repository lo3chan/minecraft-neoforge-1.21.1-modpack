package vazkii.psi.api.spell;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.cad.EnumCADStat;
import vazkii.psi.api.cad.ICAD;

public final class SpellMetadata {
   private final Map<EnumSpellStat, Integer> stats = new EnumMap<>(EnumSpellStat.class);
   private final Map<EnumSpellStat, Double> statMultipliers = new EnumMap<>(EnumSpellStat.class);
   private final Set<String> flags = new HashSet<>();
   public boolean errorsSuppressed = false;

   public SpellMetadata() {
      for (EnumSpellStat stat : EnumSpellStat.class.getEnumConstants()) {
         this.stats.put(stat, 0);
         this.statMultipliers.put(stat, 1.0);
      }
   }

   public void addStat(EnumSpellStat stat, int val) throws SpellCompilationException {
      int curr = this.stats.get(stat);
      boolean overflow = false;

      try {
         this.setStat(stat, Math.addExact(val, curr));
      } catch (ArithmeticException var6) {
         overflow = true;
      }

      if (overflow) {
         throw new SpellCompilationException("psi.spellerror.statoverflow");
      }
   }

   public void setStat(EnumSpellStat stat, int val) {
      this.stats.put(stat, val);
   }

   public int getStat(EnumSpellStat stat) {
      return (int)(this.stats.get(stat).intValue() * this.statMultipliers.get(stat));
   }

   public void compoundStatMultiplier(EnumSpellStat stat, double val) {
      double curr = this.statMultipliers.get(stat);
      this.setStatMultiplier(stat, val * curr);
   }

   public void addStatMultiplier(EnumSpellStat stat, double val) {
      double curr = this.statMultipliers.get(stat);
      this.setStatMultiplier(stat, val + curr);
   }

   public void setStatMultiplier(EnumSpellStat stat, double val) {
      this.statMultipliers.put(stat, val);
   }

   public double getStatMultiplier(EnumSpellStat stat) {
      return this.statMultipliers.get(stat);
   }

   public Set<EnumSpellStat> getStatSet() {
      return this.stats.keySet();
   }

   public void setFlag(String flag, boolean val) {
      if (val) {
         this.flags.add(flag);
      } else {
         this.flags.remove(flag);
      }
   }

   public boolean getFlag(String flag) {
      return this.flags.contains(flag);
   }

   public boolean evaluateAgainst(ItemStack stack) {
      if (stack != null && stack.getItem() instanceof ICAD cad) {
         for (EnumSpellStat stat : this.stats.keySet()) {
            EnumCADStat cadStat = stat.getTarget();
            if (cadStat != null) {
               int statVal = (int)Math.ceil(this.stats.get(stat).intValue() * this.statMultipliers.get(stat));
               int cadVal = cad.getStatValue(stack, cadStat);
               if (cadVal != -1 && cadVal < statVal) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
