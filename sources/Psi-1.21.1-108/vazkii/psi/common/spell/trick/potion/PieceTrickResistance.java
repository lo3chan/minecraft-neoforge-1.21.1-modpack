package vazkii.psi.common.spell.trick.potion;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.StatLabel;

public class PieceTrickResistance extends PieceTrickPotionBase {
   public PieceTrickResistance(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.power", true).cube().mul("psi.spellparam.time", true).mul(5.0));
   }

   @Override
   public Holder<MobEffect> getPotion() {
      return MobEffects.DAMAGE_RESISTANCE;
   }

   @Override
   public int getPotency(int power, int time) throws SpellCompilationException {
      return (int)this.multiplySafe(power, new double[]{power, power, time, 5.0});
   }
}
