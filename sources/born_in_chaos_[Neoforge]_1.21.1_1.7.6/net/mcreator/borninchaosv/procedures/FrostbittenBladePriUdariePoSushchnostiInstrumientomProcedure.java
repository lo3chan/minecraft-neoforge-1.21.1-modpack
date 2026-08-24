package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.KrampusEntity;
import net.mcreator.borninchaosv.entity.KrampusHenchmanEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class FrostbittenBladePriUdariePoSushchnostiInstrumientomProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!(entity instanceof KrampusEntity) && !(entity instanceof KrampusHenchmanEntity)) {
            if (entity instanceof LivingEntity _livEnt2 && _livEnt2.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)) {
               if (entity instanceof LivingEntity _livEnt4
                  && _livEnt4.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                  && (
                        entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                           ? _livEnt.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                           : 0
                     )
                     >= 6) {
                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_CHILLING, 80, 6));
                  }
               } else if (entity instanceof LivingEntity _livEnt7
                  && _livEnt7.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                  && (
                        entity instanceof LivingEntity _livEntx && _livEntx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                           ? _livEntx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                           : 0
                     )
                     < 6
                  && entity instanceof LivingEntity _entity
                  && !_entity.level().isClientSide()) {
                  _entity.addEffect(
                     new MobEffectInstance(
                        BornInChaosV1ModMobEffects.BONE_CHILLING,
                        (
                              entity instanceof LivingEntity _livEntxxx && _livEntxxx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                                 ? _livEntxxx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getDuration()
                                 : 0
                           )
                           - 5,
                        (
                              entity instanceof LivingEntity _livEntxx && _livEntxx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                                 ? _livEntxx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                                 : 0
                           )
                           + 1
                     )
                  );
               }
            } else if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_CHILLING, 240, 0));
            }

            if (!world.isClientSide() && world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.hurt_freeze")),
                     SoundSource.NEUTRAL,
                     0.8F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.hurt_freeze")),
                     SoundSource.NEUTRAL,
                     0.8F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles(
                  (SimpleParticleType)BornInChaosV1ModParticleTypes.LITTLESNOWFLAKE.get(),
                  entity.getX(),
                  entity.getY() + 1.4,
                  entity.getZ(),
                  7,
                  0.25,
                  0.25,
                  0.25,
                  0.1
               );
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles(
                  (SimpleParticleType)BornInChaosV1ModParticleTypes.WANINGSNOWFLAKE.get(),
                  entity.getX(),
                  entity.getY() + 1.4,
                  entity.getZ(),
                  4,
                  0.25,
                  0.25,
                  0.25,
                  0.1
               );
            }
         }
      }
   }
}
