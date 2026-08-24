package vazkii.psi.common.spell.trick.potion;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.StatLabel;

public class PieceTrickWither extends PieceTrickPotionBase {
   public PieceTrickWither(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.time", true).mul("psi.spellparam.power", true).square().mul(20.0));
   }

   @Override
   public Holder<MobEffect> getPotion() {
      return MobEffects.WITHER;
   }

   @Override
   public int getPotency(int power, int time) throws SpellCompilationException {
      return super.getPotency(power, time) * 4;
   }
}
