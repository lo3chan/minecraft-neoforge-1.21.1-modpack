package net.astralya.hexalia.util;

import java.util.ArrayList;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class ModUtil {
   private ModUtil() {
   }

   public static void removeHarmfulEffects(LivingEntity entity) {
      for (MobEffectInstance effect : new ArrayList<>(
         entity.getActiveEffects().stream().filter(effectx -> ((MobEffect)effectx.getEffect().value()).getCategory() == MobEffectCategory.HARMFUL).toList()
      )) {
         entity.removeEffect(effect.getEffect());
      }
   }
}
