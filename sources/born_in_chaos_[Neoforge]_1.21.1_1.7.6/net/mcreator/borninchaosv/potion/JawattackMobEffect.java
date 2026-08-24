package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.JawattackPriNalozhieniiEffiektaProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class JawattackMobEffect extends MobEffect {
   public JawattackMobEffect() {
      super(MobEffectCategory.HARMFUL, -11446963);
   }

   public void onEffectStarted(LivingEntity entity, int amplifier) {
      JawattackPriNalozhieniiEffiektaProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ());
   }
}
