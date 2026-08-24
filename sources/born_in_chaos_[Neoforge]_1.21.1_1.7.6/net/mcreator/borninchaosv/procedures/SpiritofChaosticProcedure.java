package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.SpiritofChaosEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level.ExplosionInteraction;

public class SpiritofChaosticProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _livEnt0
            && _livEnt0.hasEffect(MobEffects.DAMAGE_RESISTANCE)
            && entity instanceof LivingEntity _livEnt1
            && _livEnt1.hasEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK)) {
            if (entity.getPersistentData().getDouble("appearancem") == 0.0) {
               entity.getPersistentData().putDouble("appearancem", 1.0);
            } else {
               entity.getPersistentData().putDouble("appearancem", entity.getPersistentData().getDouble("appearancem") - 1.0);
            }

            if (entity.getPersistentData().getDouble("appearancem") == 0.0) {
               if (entity instanceof SpiritofChaosEntity) {
                  ((SpiritofChaosEntity)entity).setAnimation("appearance");
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.removeEffect(MobEffects.DAMAGE_RESISTANCE);
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.removeEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK);
               }
            }
         }

         if (entity instanceof LivingEntity _livEnt10 && _livEnt10.hasEffect(BornInChaosV1ModMobEffects.OBSESSION)) {
            if (!entity.level().isClientSide()) {
               entity.discard();
            }

            if (world instanceof Level _level && !_level.isClientSide()) {
               _level.explode(null, x, y, z, 3.0F, ExplosionInteraction.NONE);
            }
         }
      }
   }
}
