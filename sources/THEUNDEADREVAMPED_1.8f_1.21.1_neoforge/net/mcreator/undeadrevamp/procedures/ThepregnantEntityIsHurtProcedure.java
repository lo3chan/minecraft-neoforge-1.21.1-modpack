package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class ThepregnantEntityIsHurtProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (sourceentity instanceof LivingEntity && (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) < 50.0F) {
            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.slime.hurt")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.slime.hurt")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles(ParticleTypes.SPLASH, x, y, z, 30, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 0.5);
            }
         }

         if (entity.isOnFire()) {
            entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.PLAYER_ATTACK)), 5.0F);
            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles(ParticleTypes.CRIT, x, y, z, 20, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 1.0);
            }
         }

         if (sourceentity instanceof LivingEntity
            && entity instanceof LivingEntity _livEnt15
            && _livEnt15.hasEffect(UndeadRevamp2ModMobEffects.BROKENTANK)
            && world instanceof Level _levelx) {
            if (!_levelx.isClientSide()) {
               _levelx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:pregnanthurt")),
                  SoundSource.NEUTRAL,
                  3.0F,
                  2.0F
               );
            } else {
               _levelx.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:pregnanthurt")),
                  SoundSource.NEUTRAL,
                  3.0F,
                  2.0F,
                  false
               );
            }
         }
      }
   }
}
