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

public class InfernalEvilPumpkinSKoghdaBlokRazrushienIghrokomProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world instanceof Level _level) {
         if (!_level.isClientSide()) {
            _level.playSound(
               null,
               BlockPos.containing(x, y, z),
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.wither.spawn")),
               SoundSource.NEUTRAL,
               0.8F,
               0.8F
            );
         } else {
            _level.playLocalSound(
               x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.wither.spawn")), SoundSource.NEUTRAL, 0.8F, 0.8F, false
            );
         }
      }

      if (world instanceof ServerLevel _levelx) {
         _levelx.sendParticles(ParticleTypes.POOF, x, y, z, 14, 1.0, 1.0, 1.0, 0.1);
      }

      if (world instanceof ServerLevel _levelx) {
         _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SOUL_FIRE.get(), x, y, z, 14, 1.0, 1.0, 1.0, 0.1);
      }

      if (world instanceof ServerLevel _levelx) {
         Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.SIR_PUMPKINHEAD.get())
            .spawn(_levelx, BlockPos.containing(x + 0.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
         }
      }
   }
}
