package vazkii.psi.common.spell.trick.potion;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import vazkii.psi.api.spell.Spell;

public class PieceTrickFireResistance extends PieceTrickPotionBase {
   public PieceTrickFireResistance(Spell spell) {
      super(spell);
   }

   @Override
   public Holder<MobEffect> getPotion() {
      return MobEffects.FIRE_RESISTANCE;
   }

   @Override
   public boolean hasPower() {
      return false;
   }
}
