package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModGameRules;
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

public class DoorKnightDeathTimeIsReachedProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.MAGGOTS_APPEARANCE) && Math.random() < 0.2) {
         if (world instanceof ServerLevel _level) {
            _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.FLESHSPLASH.get(), x, y + 1.0, z, 5, 0.3, 0.3, 0.3, 0.1);
         }

         if (world instanceof ServerLevel _level) {
            Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.MAGGOT.get())
               .spawn(_level, BlockPos.containing(x + 0.5, y + 1.0, z + 0.1), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
            }
         }

         if (world instanceof ServerLevel _levelx) {
            _levelx.sendParticles(ParticleTypes.POOF, x + 0.5, y + 1.0, z + 0.1, 3, 0.1, 0.1, 0.1, 0.1);
         }

         if (world instanceof ServerLevel _levelx) {
            Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.MAGGOT.get())
               .spawn(_levelx, BlockPos.containing(x + 0.5, y + 1.0, z + 0.9), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
            }
         }

         if (world instanceof ServerLevel _levelxx) {
            _levelxx.sendParticles(ParticleTypes.POOF, x + 0.5, y + 1.0, z + 0.9, 3, 0.1, 0.1, 0.1, 0.1);
         }

         if (!world.isClientSide()) {
            if (world instanceof Level _levelxx) {
               if (!_levelxx.isClientSide()) {
                  _levelxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.slime_block.break")),
                     SoundSource.NEUTRAL,
                     0.9F,
                     0.9F
                  );
               } else {
                  _levelxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.slime_block.break")),
                     SoundSource.NEUTRAL,
                     0.9F,
                     0.9F,
                     false
                  );
               }
            }

            if (world instanceof Level _levelxxx) {
               if (!_levelxxx.isClientSide()) {
                  _levelxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:stomach_open")),
                     SoundSource.NEUTRAL,
                     0.8F,
                     1.0F
                  );
               } else {
                  _levelxxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:stomach_open")),
                     SoundSource.NEUTRAL,
                     0.8F,
                     1.0F,
                     false
                  );
               }
            }
         }
      }
   }
}
