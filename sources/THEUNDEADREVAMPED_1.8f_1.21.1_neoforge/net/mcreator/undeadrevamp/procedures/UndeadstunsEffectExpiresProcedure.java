package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.ThebeartamerEntity;
import net.mcreator.undeadrevamp.entity.ThepregnantEntity;
import net.mcreator.undeadrevamp.entity.ThewolfEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public class UndeadstunsEffectExpiresProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof ThebeartamerEntity && world instanceof ServerLevel _level) {
            _level.sendParticles(ParticleTypes.ANGRY_VILLAGER, x, y, z, 5, 1.0, 2.0, 1.0, 0.02);
         }

         if (entity instanceof ThewolfEntity && world instanceof ServerLevel _level) {
            _level.sendParticles(ParticleTypes.ANGRY_VILLAGER, x, y, z, 5, 1.0, 2.0, 1.0, 0.02);
         }

         if (entity instanceof ThepregnantEntity && world instanceof ServerLevel _level) {
            _level.sendParticles(ParticleTypes.ANGRY_VILLAGER, x, y, z, 5, 1.0, 2.0, 1.0, 0.02);
         }
      }
   }
}
