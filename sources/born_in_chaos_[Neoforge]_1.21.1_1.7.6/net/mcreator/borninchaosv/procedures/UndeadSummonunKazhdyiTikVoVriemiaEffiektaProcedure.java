package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class UndeadSummonunKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (world instanceof ServerLevel _level) {
            _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DIMLARG.get(), x, y + 1.0, z, 8, 0.4, 0.6, 0.4, 0.1);
         }

         if (world instanceof Level _level) {
            if (!_level.isClientSide()) {
               _level.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:missionary_spawn_mobs")),
                  SoundSource.NEUTRAL,
                  1.1F,
                  1.0F
               );
            } else {
               _level.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:missionary_spawn_mobs")),
                  SoundSource.NEUTRAL,
                  1.1F,
                  1.0F,
                  false
               );
            }
         }

         if (Math.random() < 0.3) {
            if (!world.getBlockState(BlockPos.containing(x + 2.5, y, z + 0.5)).canOcclude()
               && (
                  !world.getBlockState(BlockPos.containing(x + 2.5, y + 1.0, z + 0.5)).canOcclude()
                     || world.getBlockState(BlockPos.containing(x + 2.5, y, z)).getBlock() == Blocks.SNOW
               )) {
               if (world instanceof ServerLevel _levelx) {
                  _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DIMLARG.get(), x + 2.5, y, z + 0.5, 8, 0.3, 0.6, 0.3, 0.1);
               }

               if (Math.random() < 0.25) {
                  if (world instanceof ServerLevel _levelx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.DOOR_KNIGHT.get())
                        .spawn(_levelx, BlockPos.containing(x + 2.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setYRot(entity.getYRot());
                        entityToSpawn.setYBodyRot(entity.getYRot());
                        entityToSpawn.setYHeadRot(entity.getYRot());
                        entityToSpawn.setXRot(entity.getXRot());
                     }
                  }
               } else if (world instanceof ServerLevel _levelxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.ZOMBIE_BRUISER.get())
                     .spawn(_levelxx, BlockPos.containing(x + 2.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(entity.getYRot());
                     entityToSpawn.setYBodyRot(entity.getYRot());
                     entityToSpawn.setYHeadRot(entity.getYRot());
                     entityToSpawn.setXRot(entity.getXRot());
                  }
               }
            }
         } else {
            if (!world.getBlockState(BlockPos.containing(x + 2.5, y, z + 0.5)).canOcclude()
               && (
                  !world.getBlockState(BlockPos.containing(x + 2.5, y + 1.0, z + 0.5)).canOcclude()
                     || world.getBlockState(BlockPos.containing(x + 2.5, y, z)).getBlock() == Blocks.SNOW
               )) {
               if (world instanceof ServerLevel _levelxxx) {
                  _levelxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DIMLARG.get(), x + 2.5, y, z + 0.5, 8, 0.2, 0.4, 0.2, 0.1);
               }

               if (world instanceof ServerLevel _levelxxx) {
                  _levelxxx.sendParticles(ParticleTypes.POOF, x + 2.5, y, z + 0.5, 8, 0.2, 0.4, 0.2, 0.1);
               }

               if (Math.random() < 0.3) {
                  if (world instanceof ServerLevel _levelxxx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.DECREPIT_SKELETON.get())
                        .spawn(_levelxxx, BlockPos.containing(x + 2.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setYRot(entity.getYRot());
                        entityToSpawn.setYBodyRot(entity.getYRot());
                        entityToSpawn.setYHeadRot(entity.getYRot());
                        entityToSpawn.setXRot(entity.getXRot());
                     }
                  }
               } else if (Math.random() < 0.25) {
                  if (world instanceof ServerLevel _levelxxxx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.BABY_SKELETON.get())
                        .spawn(_levelxxxx, BlockPos.containing(x + 2.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setYRot(entity.getYRot());
                        entityToSpawn.setYBodyRot(entity.getYRot());
                        entityToSpawn.setYHeadRot(entity.getYRot());
                        entityToSpawn.setXRot(entity.getXRot());
                     }
                  }
               } else if (Math.random() < 0.2) {
                  if (world instanceof ServerLevel _levelxxxxx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.SIAMESE_SKELETONS.get())
                        .spawn(_levelxxxxx, BlockPos.containing(x + 2.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setYRot(entity.getYRot());
                        entityToSpawn.setYBodyRot(entity.getYRot());
                        entityToSpawn.setYHeadRot(entity.getYRot());
                        entityToSpawn.setXRot(entity.getXRot());
                     }
                  }
               } else if (world instanceof ServerLevel _levelxxxxxx) {
                  Entity entityToSpawn = EntityType.ZOMBIE_VILLAGER.spawn(_levelxxxxxx, BlockPos.containing(x + 2.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(entity.getYRot());
                     entityToSpawn.setYBodyRot(entity.getYRot());
                     entityToSpawn.setYHeadRot(entity.getYRot());
                     entityToSpawn.setXRot(entity.getXRot());
                  }
               }
            }

            if (!world.getBlockState(BlockPos.containing(x - 2.5, y, z + 0.5)).canOcclude()
               && (
                  !world.getBlockState(BlockPos.containing(x - 2.5, y + 1.0, z + 0.5)).canOcclude()
                     || world.getBlockState(BlockPos.containing(x - 2.5, y, z)).getBlock() == Blocks.SNOW
               )) {
               if (world instanceof ServerLevel _levelxxxxxxx) {
                  _levelxxxxxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DIMLARG.get(), x - 2.5, y, z + 0.5, 8, 0.2, 0.4, 0.2, 0.1);
               }

               if (world instanceof ServerLevel _levelxxxxxxx) {
                  _levelxxxxxxx.sendParticles(ParticleTypes.POOF, x - 2.5, y, z + 0.5, 8, 0.2, 0.4, 0.2, 0.1);
               }

               if (Math.random() < 0.25) {
                  if (world instanceof ServerLevel _levelxxxxxxx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.DECAYING_ZOMBIE.get())
                        .spawn(_levelxxxxxxx, BlockPos.containing(x - 2.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setYRot(entity.getYRot());
                        entityToSpawn.setYBodyRot(entity.getYRot());
                        entityToSpawn.setYHeadRot(entity.getYRot());
                        entityToSpawn.setXRot(entity.getXRot());
                     }
                  }
               } else if (Math.random() < 0.3) {
                  if (world instanceof ServerLevel _levelxxxxxxxx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.BABY_SKELETON.get())
                        .spawn(_levelxxxxxxxx, BlockPos.containing(x - 2.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setYRot(entity.getYRot());
                        entityToSpawn.setYBodyRot(entity.getYRot());
                        entityToSpawn.setYHeadRot(entity.getYRot());
                        entityToSpawn.setXRot(entity.getXRot());
                     }
                  }
               } else if (Math.random() < 0.2) {
                  if (world instanceof ServerLevel _levelxxxxxxxxx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.ZOMBIE_LUMBERJACK.get())
                        .spawn(_levelxxxxxxxxx, BlockPos.containing(x - 2.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setYRot(entity.getYRot());
                        entityToSpawn.setYBodyRot(entity.getYRot());
                        entityToSpawn.setYHeadRot(entity.getYRot());
                        entityToSpawn.setXRot(entity.getXRot());
                     }
                  }
               } else if (world instanceof ServerLevel _levelxxxxxxxxxx) {
                  Entity entityToSpawn = EntityType.ZOMBIE_VILLAGER
                     .spawn(_levelxxxxxxxxxx, BlockPos.containing(x - 2.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(entity.getYRot());
                     entityToSpawn.setYBodyRot(entity.getYRot());
                     entityToSpawn.setYHeadRot(entity.getYRot());
                     entityToSpawn.setXRot(entity.getXRot());
                  }
               }
            }

            if (!world.getBlockState(BlockPos.containing(x + 0.5, y, z + 2.5)).canOcclude()
               && (
                  !world.getBlockState(BlockPos.containing(x + 0.5, y + 1.0, z + 2.5)).canOcclude()
                     || world.getBlockState(BlockPos.containing(x, y, z + 2.5)).getBlock() == Blocks.SNOW
               )) {
               if (world instanceof ServerLevel _levelxxxxxxxxxxx) {
                  _levelxxxxxxxxxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DIMLARG.get(), x + 0.5, y, z + 2.5, 8, 0.2, 0.4, 0.2, 0.1);
               }

               if (world instanceof ServerLevel _levelxxxxxxxxxxx) {
                  _levelxxxxxxxxxxx.sendParticles(ParticleTypes.POOF, x + 0.5, y, z + 2.5, 8, 0.2, 0.4, 0.2, 0.1);
               }

               if (Math.random() < 0.2) {
                  if (world instanceof ServerLevel _levelxxxxxxxxxxx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.DECAYING_ZOMBIE.get())
                        .spawn(_levelxxxxxxxxxxx, BlockPos.containing(x + 0.5, y, z + 2.5), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setYRot(entity.getYRot());
                        entityToSpawn.setYBodyRot(entity.getYRot());
                        entityToSpawn.setYHeadRot(entity.getYRot());
                        entityToSpawn.setXRot(entity.getXRot());
                     }
                  }
               } else if (Math.random() < 0.3) {
                  if (world instanceof ServerLevel _levelxxxxxxxxxxxx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.BABY_SKELETON.get())
                        .spawn(_levelxxxxxxxxxxxx, BlockPos.containing(x + 0.5, y, z + 2.5), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setYRot(entity.getYRot());
                        entityToSpawn.setYBodyRot(entity.getYRot());
                        entityToSpawn.setYHeadRot(entity.getYRot());
                        entityToSpawn.setXRot(entity.getXRot());
                     }
                  }
               } else if (Math.random() < 0.2) {
                  if (world instanceof ServerLevel _levelxxxxxxxxxxxxx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.ZOMBIE_FISHERMAN.get())
                        .spawn(_levelxxxxxxxxxxxxx, BlockPos.containing(x + 0.5, y, z + 2.5), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setYRot(entity.getYRot());
                        entityToSpawn.setYBodyRot(entity.getYRot());
                        entityToSpawn.setYHeadRot(entity.getYRot());
                        entityToSpawn.setXRot(entity.getXRot());
                     }
                  }
               } else if (world instanceof ServerLevel _levelxxxxxxxxxxxxxx) {
                  Entity entityToSpawn = EntityType.ZOMBIE_VILLAGER
                     .spawn(_levelxxxxxxxxxxxxxx, BlockPos.containing(x + 0.5, y, z + 2.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(entity.getYRot());
                     entityToSpawn.setYBodyRot(entity.getYRot());
                     entityToSpawn.setYHeadRot(entity.getYRot());
                     entityToSpawn.setXRot(entity.getXRot());
                  }
               }
            }
         }
      }
   }
}
