package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public class ExperienceContainerKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         if (world instanceof ServerLevel _level) {
            _level.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.XP_PARTICLE.get(), entity.getX(), entity.getY() + 1.3, entity.getZ(), 1, 0.3, 0.2, 0.3, 0.1
            );
         }
      }
   }
}
