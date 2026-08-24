package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EtherealSpiritPlayerFinishesUsingItemProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(BornInChaosV1ModMobEffects.MAGIC_DEPLETION);
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.DIG_SLOWDOWN);
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.WEAKNESS);
         }
      }
   }
}
