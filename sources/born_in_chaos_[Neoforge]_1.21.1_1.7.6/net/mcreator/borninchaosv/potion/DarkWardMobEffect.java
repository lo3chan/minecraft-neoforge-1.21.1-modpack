package net.mcreator.borninchaosv.potion;

import java.util.Set;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;

public class DarkWardMobEffect extends MobEffect {
   public DarkWardMobEffect() {
      super(MobEffectCategory.BENEFICIAL, -15333873);
   }

   public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
   }
}
