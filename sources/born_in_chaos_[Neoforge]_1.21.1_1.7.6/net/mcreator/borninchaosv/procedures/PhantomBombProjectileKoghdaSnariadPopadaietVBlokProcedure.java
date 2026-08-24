package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level.ExplosionInteraction;

public class PhantomBombProjectileKoghdaSnariadPopadaietVBlokProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world instanceof Level _level && !_level.isClientSide()) {
         _level.explode(null, x, y, z, 4.0F, ExplosionInteraction.TNT);
      }

      if (world instanceof ServerLevel _level) {
         Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.PHANTOM_BOMB_ENTITY.get())
            .spawn(_level, BlockPos.containing(x + 1.0, y, z), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
         }
      }

      if (world instanceof ServerLevel _levelx) {
         Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.PHANTOM_BOMB_ENTITY.get())
            .spawn(_levelx, BlockPos.containing(x - 1.0, y, z), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
         }
      }

      if (world instanceof ServerLevel _levelxx) {
         _levelxx.sendParticles(ParticleTypes.PORTAL, x, y, z, 18, 0.3, 0.3, 0.3, 0.1);
      }
   }
}
