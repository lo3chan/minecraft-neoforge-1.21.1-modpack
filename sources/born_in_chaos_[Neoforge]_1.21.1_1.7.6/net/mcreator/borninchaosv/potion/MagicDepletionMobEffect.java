package net.mcreator.borninchaosv.potion;

import java.util.Set;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.EffectCures;

public class MagicDepletionMobEffect extends MobEffect {
   public MagicDepletionMobEffect() {
      super(MobEffectCategory.HARMFUL, -11918299);
   }

   public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
      cures.add(EffectCures.PROTECTED_BY_TOTEM);
   }
}
