package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
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

public class CursedMarkKoghdaEffiektZakanchivaietsiaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world instanceof Level _level) {
         if (!_level.isClientSide()) {
            _level.playSound(
               null,
               BlockPos.containing(x, y, z),
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.wither.spawn")),
               SoundSource.NEUTRAL,
               0.3F,
               1.0F
            );
         } else {
            _level.playLocalSound(
               x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.wither.spawn")), SoundSource.NEUTRAL, 0.3F, 1.0F, false
            );
         }
      }

      if (world instanceof Level _levelx) {
         if (!_levelx.isClientSide()) {
            _levelx.playSound(
               null,
               BlockPos.containing(x, y, z),
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:persecutor_scream")),
               SoundSource.NEUTRAL,
               0.4F,
               1.0F
            );
         } else {
            _levelx.playLocalSound(
               x,
               y,
               z,
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:persecutor_scream")),
               SoundSource.NEUTRAL,
               0.4F,
               1.0F,
               false
            );
         }
      }

      if (!world.getBlockState(BlockPos.containing(x + 3.0, y + 1.0, z)).canOcclude()) {
         if (world instanceof ServerLevel _levelxx) {
            Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.SCARLET_PERSECUTOR.get())
               .spawn(_levelxx, BlockPos.containing(x + 3.0, y + 0.5, z), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
            }
         }

         if (world instanceof ServerLevel _levelxxx) {
            _levelxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DARK_SMOKE.get(), x + 3.0, y + 0.5, z, 6, 0.5, 0.5, 0.5, 0.1);
         }
      }

      if (!world.getBlockState(BlockPos.containing(x - 3.0, y + 1.0, z)).canOcclude()) {
         if (world instanceof ServerLevel _levelxxx) {
            Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.SCARLET_PERSECUTOR.get())
               .spawn(_levelxxx, BlockPos.containing(x - 3.0, y + 0.5, z), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
            }
         }

         if (world instanceof ServerLevel _levelxxxx) {
            _levelxxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DARK_SMOKE.get(), x - 3.0, y + 0.5, z, 6, 0.5, 0.5, 0.5, 0.1);
         }
      }

      if (!world.getBlockState(BlockPos.containing(x, y + 1.0, z + 3.0)).canOcclude()) {
         if (world instanceof ServerLevel _levelxxxx) {
            Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.SCARLET_PERSECUTOR.get())
               .spawn(_levelxxxx, BlockPos.containing(x, y + 0.5, z + 3.0), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
            }
         }

         if (world instanceof ServerLevel _levelxxxxx) {
            _levelxxxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DARK_SMOKE.get(), x, y + 0.5, z + 3.0, 6, 0.5, 0.5, 0.5, 0.1);
         }
      }

      if (!world.getBlockState(BlockPos.containing(x, y + 1.0, z - 3.0)).canOcclude()) {
         if (world instanceof ServerLevel _levelxxxxx) {
            Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.SCARLET_PERSECUTOR.get())
               .spawn(_levelxxxxx, BlockPos.containing(x, y + 0.5, z - 3.0), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
            }
         }

         if (world instanceof ServerLevel _levelxxxxxx) {
            _levelxxxxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DARK_SMOKE.get(), x, y + 0.5, z - 3.0, 6, 0.5, 0.5, 0.5, 0.1);
         }
      }
   }
}
