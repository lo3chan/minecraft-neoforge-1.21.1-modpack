package net.mcreator.undeadrevamp.procedures;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class BriskfanWhileProjectileFlyingTickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity immediatesourceentity) {
      if (immediatesourceentity != null) {
         if (!immediatesourceentity.getPersistentData().getBoolean("used")) {
            immediatesourceentity.getPersistentData().putBoolean("used", true);
            immediatesourceentity.getPersistentData().putDouble("xvel", immediatesourceentity.getDeltaMovement().x());
            immediatesourceentity.getPersistentData().putDouble("yvel", immediatesourceentity.getDeltaMovement().y());
            immediatesourceentity.getPersistentData().putDouble("zvel", immediatesourceentity.getDeltaMovement().z());
         }

         immediatesourceentity.setDeltaMovement(
            new Vec3(
               immediatesourceentity.getPersistentData().getDouble("xvel"),
               immediatesourceentity.getPersistentData().getDouble("yvel"),
               immediatesourceentity.getPersistentData().getDouble("zvel")
            )
         );
         immediatesourceentity.getPersistentData().putDouble("despawntimer", immediatesourceentity.getPersistentData().getDouble("despawntimer") + 1.0);
         if (immediatesourceentity.getPersistentData().getDouble("despawntimer") >= 40.0 && !immediatesourceentity.level().isClientSide()) {
            immediatesourceentity.discard();
         }

         if (world instanceof ServerLevel _level) {
            _level.sendParticles(
               ParticleTypes.SWEEP_ATTACK,
               x,
               y,
               z,
               5,
               immediatesourceentity.getBbWidth(),
               immediatesourceentity.getBbHeight(),
               immediatesourceentity.getBbWidth(),
               1.0
            );
         }

         if (world instanceof ServerLevel _level) {
            _level.sendParticles(
               ParticleTypes.FLASH,
               x,
               y,
               z,
               1,
               immediatesourceentity.getBbWidth(),
               immediatesourceentity.getBbHeight(),
               immediatesourceentity.getBbWidth(),
               1.0
            );
         }
      }
   }
}
