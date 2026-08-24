package net.mcreator.borninchaosv.procedures;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class GadflyAttackConditionProcedure {
   public static boolean execute(Entity entity) {
      return entity == null ? false : !(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(MobEffects.REGENERATION));
   }
}
