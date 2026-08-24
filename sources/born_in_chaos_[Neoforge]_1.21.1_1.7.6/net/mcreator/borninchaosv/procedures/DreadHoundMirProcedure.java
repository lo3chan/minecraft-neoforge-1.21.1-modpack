package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class DreadHoundMirProcedure {
   public static boolean execute(Entity entity) {
      return entity == null ? false : !(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(BornInChaosV1ModMobEffects.DOGTRUCE));
   }
}
