package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;

public class RottenSmellKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!entity.isInWater()
            && !(entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(BornInChaosV1ModMobEffects.INSECT_PROTECTION))
            && world instanceof ServerLevel _level) {
            _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.FLI.get(), x, y + 1.0, z, 1, 0.3, 0.2, 0.3, 0.1);
         }

         if (entity.getPersistentData().getDouble("fly") == 0.0) {
            entity.getPersistentData().putDouble("fly", 400.0);
         } else {
            entity.getPersistentData().putDouble("fly", entity.getPersistentData().getDouble("fly") - 1.0);
         }

         if (entity.getPersistentData().getDouble("fly") == 0.0) {
            if (!entity.isInWaterRainOrBubble()
               && !(entity instanceof LivingEntity _livEnt9 && _livEnt9.hasEffect(BornInChaosV1ModMobEffects.INSECT_PROTECTION))) {
               if (!world.getBlockState(BlockPos.containing(x + 2.5, y, z + 0.5)).canOcclude()
                  && !world.getBlockState(BlockPos.containing(x + 2.5, y + 1.0, z + 0.5)).canOcclude()) {
                  if (world instanceof ServerLevel _level) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CORPSE_FLY.get())
                        .spawn(_level, BlockPos.containing(x + 2.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                     }
                  }

                  if (world instanceof ServerLevel _levelx) {
                     _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.FLI.get(), x + 2.5, y + 1.0, z + 0.5, 4, 0.3, 0.2, 0.3, 0.1);
                  }

                  if (world instanceof ServerLevel _levelx) {
                     _levelx.sendParticles(ParticleTypes.POOF, x + 2.5, y + 1.0, z + 0.5, 4, 0.2, 0.2, 0.2, 0.1);
                  }
               } else if (!world.getBlockState(BlockPos.containing(x - 2.5, y, z + 0.5)).canOcclude()
                  && !world.getBlockState(BlockPos.containing(x - 2.5, y + 1.0, z + 0.5)).canOcclude()) {
                  if (world instanceof ServerLevel _levelx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CORPSE_FLY.get())
                        .spawn(_levelx, BlockPos.containing(x - 2.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                     }
                  }

                  if (world instanceof ServerLevel _levelxx) {
                     _levelxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.FLI.get(), x - 2.5, y + 1.0, z + 0.5, 4, 0.3, 0.2, 0.3, 0.1);
                  }

                  if (world instanceof ServerLevel _levelxx) {
                     _levelxx.sendParticles(ParticleTypes.POOF, x - 2.5, y + 1.0, z + 0.5, 4, 0.2, 0.2, 0.2, 0.1);
                  }
               } else if (!world.getBlockState(BlockPos.containing(x + 0.5, y, z + 2.5)).canOcclude()
                  && !world.getBlockState(BlockPos.containing(x + 0.5, y + 1.0, z + 2.5)).canOcclude()) {
                  if (world instanceof ServerLevel _levelxx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CORPSE_FLY.get())
                        .spawn(_levelxx, BlockPos.containing(x + 0.5, y, z + 2.5), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                     }
                  }

                  if (world instanceof ServerLevel _levelxxx) {
                     _levelxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.FLI.get(), x + 0.5, y + 1.0, z + 2.5, 4, 0.3, 0.2, 0.3, 0.1);
                  }

                  if (world instanceof ServerLevel _levelxxx) {
                     _levelxxx.sendParticles(ParticleTypes.POOF, x + 0.5, y + 1.0, z + 2.5, 4, 0.2, 0.2, 0.2, 0.1);
                  }
               } else if (!world.getBlockState(BlockPos.containing(x + 0.5, y, z - 2.5)).canOcclude()
                  && !world.getBlockState(BlockPos.containing(x + 0.5, y + 1.0, z - 2.5)).canOcclude()) {
                  if (world instanceof ServerLevel _levelxxx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CORPSE_FLY.get())
                        .spawn(_levelxxx, BlockPos.containing(x + 0.5, y, z - 2.5), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                     }
                  }

                  if (world instanceof ServerLevel _levelxxxx) {
                     _levelxxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.FLI.get(), x + 0.5, y + 1.0, z - 2.5, 4, 0.3, 0.2, 0.3, 0.1);
                  }

                  if (world instanceof ServerLevel _levelxxxx) {
                     _levelxxxx.sendParticles(ParticleTypes.POOF, x + 0.5, y + 1.0, z - 2.5, 4, 0.2, 0.2, 0.2, 0.1);
                  }
               }
            } else if (entity.isInWater()) {
               if (!world.getBlockState(BlockPos.containing(x + 4.5, y, z + 0.5)).canOcclude()) {
                  if (world instanceof ServerLevel _levelxxxx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CORPSE_FISH.get())
                        .spawn(_levelxxxx, BlockPos.containing(x + 4.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                     }
                  }

                  if (world instanceof ServerLevel _levelxxxxx) {
                     _levelxxxxx.sendParticles(ParticleTypes.POOF, x + 4.5, y, z + 0.5, 4, 0.2, 0.2, 0.2, 0.1);
                  }

                  if (world instanceof ServerLevel _levelxxxxx) {
                     _levelxxxxx.sendParticles(ParticleTypes.BUBBLE_POP, x + 4.5, y, z + 0.5, 4, 0.3, 0.2, 0.3, 0.1);
                  }
               } else if (!world.getBlockState(BlockPos.containing(x - 4.5, y, z + 0.5)).canOcclude()) {
                  if (world instanceof ServerLevel _levelxxxxx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CORPSE_FISH.get())
                        .spawn(_levelxxxxx, BlockPos.containing(x - 4.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                     }
                  }

                  if (world instanceof ServerLevel _levelxxxxxx) {
                     _levelxxxxxx.sendParticles(ParticleTypes.POOF, x - 4.5, y, z + 0.5, 4, 0.2, 0.2, 0.2, 0.1);
                  }

                  if (world instanceof ServerLevel _levelxxxxxx) {
                     _levelxxxxxx.sendParticles(ParticleTypes.BUBBLE_POP, x - 4.5, y, z + 0.5, 4, 0.3, 0.2, 0.3, 0.1);
                  }
               } else if (!world.getBlockState(BlockPos.containing(x + 0.5, y, z + 4.5)).canOcclude()) {
                  if (world instanceof ServerLevel _levelxxxxxx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CORPSE_FISH.get())
                        .spawn(_levelxxxxxx, BlockPos.containing(x + 0.5, y, z + 4.5), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                     }
                  }

                  if (world instanceof ServerLevel _levelxxxxxxx) {
                     _levelxxxxxxx.sendParticles(ParticleTypes.POOF, x + 0.5, y, z + 4.5, 4, 0.2, 0.2, 0.2, 0.1);
                  }

                  if (world instanceof ServerLevel _levelxxxxxxx) {
                     _levelxxxxxxx.sendParticles(ParticleTypes.BUBBLE_POP, x + 0.5, y, z + 4.5, 4, 0.3, 0.2, 0.3, 0.1);
                  }
               } else if (!world.getBlockState(BlockPos.containing(x + 0.5, y, z - 4.5)).canOcclude()) {
                  if (world instanceof ServerLevel _levelxxxxxxx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CORPSE_FISH.get())
                        .spawn(_levelxxxxxxx, BlockPos.containing(x + 0.5, y, z - 4.5), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                     }
                  }

                  if (world instanceof ServerLevel _levelxxxxxxxx) {
                     _levelxxxxxxxx.sendParticles(ParticleTypes.POOF, x + 0.5, y, z - 4.5, 4, 0.2, 0.2, 0.2, 0.1);
                  }

                  if (world instanceof ServerLevel _levelxxxxxxxx) {
                     _levelxxxxxxxx.sendParticles(ParticleTypes.BUBBLE_POP, x + 0.5, y, z - 4.5, 4, 0.3, 0.2, 0.3, 0.1);
                  }
               }
            }
         }
      }
   }
}
