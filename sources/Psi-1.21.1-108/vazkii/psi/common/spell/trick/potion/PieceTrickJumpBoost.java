package vazkii.psi.common.spell.trick.potion;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import vazkii.psi.api.spell.Spell;

public class PieceTrickJumpBoost extends PieceTrickPotionBase {
   public PieceTrickJumpBoost(Spell spell) {
      super(spell);
   }

   @Override
   public Holder<MobEffect> getPotion() {
      return MobEffects.JUMP;
   }
}
