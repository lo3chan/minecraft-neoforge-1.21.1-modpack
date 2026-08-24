package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class FulminationOnEffectActiveTickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity.isAlive()) {
            if (world instanceof ServerLevel _level) {
               _level.sendParticles(
                  (SimpleParticleType)UndeadRevamp2ModParticleTypes.PINKDUST.get(),
                  x,
                  y,
                  z,
                  5,
                  entity.getBbWidth(),
                  entity.getBbHeight(),
                  entity.getBbWidth(),
                  0.01
               );
            }

            if (world instanceof ServerLevel _level) {
               _level.sendParticles(
                  (SimpleParticleType)UndeadRevamp2ModParticleTypes.BRIGHTPINKDUST.get(),
                  x,
                  y,
                  z,
                  5,
                  entity.getBbWidth(),
                  entity.getBbHeight(),
                  entity.getBbWidth(),
                  0.01
               );
            }
         }

         if (Math.random() < 0.07 && world instanceof Level _level) {
            if (!_level.isClientSide()) {
               _level.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.campfire.crackle")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _level.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.campfire.crackle")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }

         if (entity.isInWaterRainOrBubble()) {
            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(UndeadRevamp2ModMobEffects.FULMINATION);
            }

            if (world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.fire.extinguish")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.fire.extinguish")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelxx) {
               _levelxx.sendParticles(ParticleTypes.LARGE_SMOKE, x, y, z, 10, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 0.01);
            }
         }
      }
   }
}
