package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModBlocks;
import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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

public class InfectedDiamondOrePriRazrushieniiBlokaOtVzryvaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world instanceof ServerLevel _level) {
         Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.DIAMOND_TERMITE.get())
            .spawn(_level, BlockPos.containing(x + 0.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
         }
      }

      if (world instanceof ServerLevel _levelx) {
         _levelx.sendParticles(ParticleTypes.POOF, x, y, z, 5, 0.2, 0.2, 0.2, 0.1);
      }

      if (world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock() == BornInChaosV1ModBlocks.INFECTED_DIAMOND_ORE.get()) {
         world.destroyBlock(BlockPos.containing(x + 1.0, y, z), false);
         if (world instanceof ServerLevel _levelx) {
            Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.DIAMOND_TERMITE.get())
               .spawn(_levelx, BlockPos.containing(x + 1.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
            }
         }

         if (world instanceof ServerLevel _levelxx) {
            _levelxx.sendParticles(ParticleTypes.POOF, x + 1.5, y, z + 0.5, 5, 0.2, 0.2, 0.2, 0.1);
         }

         if (world instanceof Level _levelxx) {
            if (!_levelxx.isClientSide()) {
               _levelxx.playSound(
                  null,
                  BlockPos.containing(x + 1.5, y, z + 0.5),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.stone.break")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxx.playLocalSound(
                  x + 1.5,
                  y,
                  z + 0.5,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.stone.break")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }
      }

      if (world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock() == BornInChaosV1ModBlocks.INFECTED_DIAMOND_ORE.get()) {
         world.destroyBlock(BlockPos.containing(x - 1.0, y, z), false);
         if (world instanceof ServerLevel _levelxxx) {
            Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.DIAMOND_TERMITE.get())
               .spawn(_levelxxx, BlockPos.containing(x - 0.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
            }
         }

         if (world instanceof ServerLevel _levelxxxx) {
            _levelxxxx.sendParticles(ParticleTypes.POOF, x - 0.5, y, z + 0.5, 5, 0.2, 0.2, 0.2, 0.1);
         }

         if (world instanceof Level _levelxxxx) {
            if (!_levelxxxx.isClientSide()) {
               _levelxxxx.playSound(
                  null,
                  BlockPos.containing(x - 0.5, y, z + 0.5),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.stone.break")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxxxx.playLocalSound(
                  x - 0.5,
                  y,
                  z + 0.5,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.stone.break")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }
      }

      if (world.getBlockState(BlockPos.containing(x, y + 1.0, z)).getBlock() == BornInChaosV1ModBlocks.INFECTED_DIAMOND_ORE.get()) {
         world.destroyBlock(BlockPos.containing(x, y + 1.0, z), false);
         if (world instanceof ServerLevel _levelxxxxx) {
            Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.DIAMOND_TERMITE.get())
               .spawn(_levelxxxxx, BlockPos.containing(x + 0.5, y + 0.5, z + 0.5), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
            }
         }

         if (world instanceof ServerLevel _levelxxxxxx) {
            _levelxxxxxx.sendParticles(ParticleTypes.POOF, x + 0.5, y + 0.5, z + 0.5, 5, 0.2, 0.2, 0.2, 0.1);
         }

         if (world instanceof Level _levelxxxxxx) {
            if (!_levelxxxxxx.isClientSide()) {
               _levelxxxxxx.playSound(
                  null,
                  BlockPos.containing(x + 0.5, y + 0.5, z + 0.5),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.stone.break")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxxxxxx.playLocalSound(
                  x + 0.5,
                  y + 0.5,
                  z + 0.5,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.stone.break")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }
      }

      if (world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == BornInChaosV1ModBlocks.INFECTED_DIAMOND_ORE.get()) {
         world.destroyBlock(BlockPos.containing(x, y - 1.0, z), false);
         if (world instanceof ServerLevel _levelxxxxxxx) {
            Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.DIAMOND_TERMITE.get())
               .spawn(_levelxxxxxxx, BlockPos.containing(x + 0.5, y - 0.5, z + 0.5), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
            }
         }

         if (world instanceof ServerLevel _levelxxxxxxxx) {
            _levelxxxxxxxx.sendParticles(ParticleTypes.POOF, x - 0.5, y - 0.5, z + 0.5, 5, 0.2, 0.2, 0.2, 0.1);
         }

         if (world instanceof Level _levelxxxxxxxx) {
            if (!_levelxxxxxxxx.isClientSide()) {
               _levelxxxxxxxx.playSound(
                  null,
                  BlockPos.containing(x + 0.5, y - 0.5, z + 0.5),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.stone.break")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxxxxxxxx.playLocalSound(
                  x + 0.5,
                  y - 0.5,
                  z + 0.5,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.stone.break")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }
      }

      if (world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock() == BornInChaosV1ModBlocks.INFECTED_DIAMOND_ORE.get()) {
         world.destroyBlock(BlockPos.containing(x, y, z + 1.0), false);
         if (world instanceof ServerLevel _levelxxxxxxxxx) {
            Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.DIAMOND_TERMITE.get())
               .spawn(_levelxxxxxxxxx, BlockPos.containing(x + 0.5, y, z + 1.5), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
            }
         }

         if (world instanceof ServerLevel _levelxxxxxxxxxx) {
            _levelxxxxxxxxxx.sendParticles(ParticleTypes.POOF, x + 0.5, y, z + 1.5, 5, 0.2, 0.2, 0.2, 0.1);
         }

         if (world instanceof Level _levelxxxxxxxxxx) {
            if (!_levelxxxxxxxxxx.isClientSide()) {
               _levelxxxxxxxxxx.playSound(
                  null,
                  BlockPos.containing(x + 0.5, y, z + 1.5),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.stone.break")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxxxxxxxxxx.playLocalSound(
                  x + 0.5,
                  y,
                  z + 1.5,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.stone.break")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }
      }

      if (world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock() == BornInChaosV1ModBlocks.INFECTED_DIAMOND_ORE.get()) {
         world.destroyBlock(BlockPos.containing(x, y, z - 1.0), false);
         if (world instanceof ServerLevel _levelxxxxxxxxxxx) {
            Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.DIAMOND_TERMITE.get())
               .spawn(_levelxxxxxxxxxxx, BlockPos.containing(x + 0.5, y, z - 1.5), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
            }
         }

         if (world instanceof ServerLevel _levelxxxxxxxxxxxx) {
            _levelxxxxxxxxxxxx.sendParticles(ParticleTypes.POOF, x + 0.5, y, z - 1.5, 5, 0.2, 0.2, 0.2, 0.1);
         }

         if (world instanceof Level _levelxxxxxxxxxxxx) {
            if (!_levelxxxxxxxxxxxx.isClientSide()) {
               _levelxxxxxxxxxxxx.playSound(
                  null,
                  BlockPos.containing(x + 0.5, y, z - 1.5),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.stone.break")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxxxxxxxxxxxx.playLocalSound(
                  x + 0.5,
                  y,
                  z - 1.5,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.stone.break")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }
      }
   }
}
