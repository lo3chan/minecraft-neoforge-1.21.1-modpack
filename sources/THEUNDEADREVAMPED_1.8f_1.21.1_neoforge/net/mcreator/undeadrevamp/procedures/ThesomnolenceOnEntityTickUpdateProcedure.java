package net.mcreator.undeadrevamp.procedures;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class ThesomnolenceOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!entity.isAlive()) {
            entity.setDeltaMovement(new Vec3(0.0, -2.0, 0.0));
         }

         if (Math.random() < 0.15 && world instanceof ServerLevel _level) {
            _level.sendParticles(ParticleTypes.SNEEZE, x, y, z, 5, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 0.0);
         }

         if (entity.isInWaterRainOrBubble()) {
            entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.DROWN)), 10.0F);
         }
      }
   }
}
