package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.BomberEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public class BomberexplodingOnEffectActiveTickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (world instanceof ServerLevel _level) {
            _level.sendParticles((SimpleParticleType)UndeadRevamp2ModParticleTypes.BOMBERGOO.get(), x, y, z, 10, 1.0, 1.0, 1.0, 0.7);
         }

         if (entity instanceof BomberEntity animatable) {
            animatable.setTexture("explodingbomber");
         }

         entity.setShiftKeyDown(true);
      }
   }
}
