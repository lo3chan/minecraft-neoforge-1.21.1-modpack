package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class PotionofRagePriZaviershieniiIspolzovaniiaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.DIG_SLOWDOWN);
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.WEAKNESS);
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
         }

         if (entity instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(BornInChaosV1ModMobEffects.LIGHT_RAMPAGE)
            || entity instanceof LivingEntity _livEnt4 && _livEnt4.hasEffect(BornInChaosV1ModMobEffects.MEDIUM_RAMPAGE)
            || entity instanceof LivingEntity _livEnt5 && _livEnt5.hasEffect(BornInChaosV1ModMobEffects.STRONG_RAMPAGE)
            || entity instanceof LivingEntity _livEnt6 && _livEnt6.hasEffect(BornInChaosV1ModMobEffects.FURIOUS_RAMPAGE)
            || entity instanceof LivingEntity _livEnt7 && _livEnt7.hasEffect(BornInChaosV1ModMobEffects.RAMPANT_RAMPAGE)) {
            if (!(entity instanceof LivingEntity _livEnt9 && _livEnt9.hasEffect(BornInChaosV1ModMobEffects.LIGHT_RAMPAGE))
               || entity instanceof LivingEntity _livEnt10 && _livEnt10.hasEffect(BornInChaosV1ModMobEffects.MEDIUM_RAMPAGE)
               || entity instanceof LivingEntity _livEnt11 && _livEnt11.hasEffect(BornInChaosV1ModMobEffects.STRONG_RAMPAGE)
               || entity instanceof LivingEntity _livEnt12 && _livEnt12.hasEffect(BornInChaosV1ModMobEffects.FURIOUS_RAMPAGE)
               || entity instanceof LivingEntity _livEnt13 && _livEnt13.hasEffect(BornInChaosV1ModMobEffects.RAMPANT_RAMPAGE)) {
               if (!(
                     !(entity instanceof LivingEntity _livEnt16 && _livEnt16.hasEffect(BornInChaosV1ModMobEffects.LIGHT_RAMPAGE))
                        && entity instanceof LivingEntity _livEnt17
                  )
                  || !_livEnt17.hasEffect(BornInChaosV1ModMobEffects.MEDIUM_RAMPAGE)
                  || entity instanceof LivingEntity _livEnt18 && _livEnt18.hasEffect(BornInChaosV1ModMobEffects.STRONG_RAMPAGE)
                  || entity instanceof LivingEntity _livEnt19 && _livEnt19.hasEffect(BornInChaosV1ModMobEffects.FURIOUS_RAMPAGE)
                  || entity instanceof LivingEntity _livEnt20 && _livEnt20.hasEffect(BornInChaosV1ModMobEffects.RAMPANT_RAMPAGE)) {
                  if (entity instanceof LivingEntity _livEnt23 && _livEnt23.hasEffect(BornInChaosV1ModMobEffects.LIGHT_RAMPAGE)
                     || !(
                        !(entity instanceof LivingEntity _livEnt24 && _livEnt24.hasEffect(BornInChaosV1ModMobEffects.MEDIUM_RAMPAGE))
                           && entity instanceof LivingEntity _livEnt25
                     )
                     || !_livEnt25.hasEffect(BornInChaosV1ModMobEffects.STRONG_RAMPAGE)
                     || entity instanceof LivingEntity _livEnt26 && _livEnt26.hasEffect(BornInChaosV1ModMobEffects.FURIOUS_RAMPAGE)
                     || entity instanceof LivingEntity _livEnt27 && _livEnt27.hasEffect(BornInChaosV1ModMobEffects.RAMPANT_RAMPAGE)) {
                     if (entity instanceof LivingEntity _livEnt30 && _livEnt30.hasEffect(BornInChaosV1ModMobEffects.LIGHT_RAMPAGE)
                        || entity instanceof LivingEntity _livEnt31 && _livEnt31.hasEffect(BornInChaosV1ModMobEffects.MEDIUM_RAMPAGE)
                        || !(
                           !(entity instanceof LivingEntity _livEnt32 && _livEnt32.hasEffect(BornInChaosV1ModMobEffects.STRONG_RAMPAGE))
                              && entity instanceof LivingEntity _livEnt33
                        )
                        || !_livEnt33.hasEffect(BornInChaosV1ModMobEffects.FURIOUS_RAMPAGE)
                        || entity instanceof LivingEntity _livEnt34 && _livEnt34.hasEffect(BornInChaosV1ModMobEffects.RAMPANT_RAMPAGE)) {
                        if (!(entity instanceof LivingEntity _livEnt38 && _livEnt38.hasEffect(BornInChaosV1ModMobEffects.LIGHT_RAMPAGE))
                           && !(entity instanceof LivingEntity _livEnt39 && _livEnt39.hasEffect(BornInChaosV1ModMobEffects.MEDIUM_RAMPAGE))
                           && !(entity instanceof LivingEntity _livEnt40 && _livEnt40.hasEffect(BornInChaosV1ModMobEffects.STRONG_RAMPAGE))
                           && !(entity instanceof LivingEntity _livEnt41 && _livEnt41.hasEffect(BornInChaosV1ModMobEffects.FURIOUS_RAMPAGE))
                           && entity instanceof LivingEntity _livEnt42
                           && _livEnt42.hasEffect(BornInChaosV1ModMobEffects.RAMPANT_RAMPAGE)
                           && entity instanceof LivingEntity _entity
                           && !_entity.level().isClientSide()) {
                           _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.RAMPANT_RAMPAGE, 2400, 0));
                        }
                     } else {
                        if (entity instanceof LivingEntity _entity) {
                           _entity.removeEffect(BornInChaosV1ModMobEffects.FURIOUS_RAMPAGE);
                        }

                        if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                           _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.RAMPANT_RAMPAGE, 2200, 0));
                        }

                        if (world instanceof Level _level) {
                           if (!_level.isClientSide()) {
                              _level.playSound(
                                 null,
                                 BlockPos.containing(x, y, z),
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.ravager.roar")),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 0.8F
                              );
                           } else {
                              _level.playLocalSound(
                                 x,
                                 y,
                                 z,
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.ravager.roar")),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 0.8F,
                                 false
                              );
                           }
                        }
                     }
                  } else {
                     if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.FURIOUS_RAMPAGE, 2200, 0));
                     }

                     if (entity instanceof LivingEntity _entity) {
                        _entity.removeEffect(BornInChaosV1ModMobEffects.STRONG_RAMPAGE);
                     }
                  }
               } else {
                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.STRONG_RAMPAGE, 2200, 0));
                  }

                  if (entity instanceof LivingEntity _entity) {
                     _entity.removeEffect(BornInChaosV1ModMobEffects.MEDIUM_RAMPAGE);
                  }
               }
            } else {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.MEDIUM_RAMPAGE, 2200, 0));
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.removeEffect(BornInChaosV1ModMobEffects.LIGHT_RAMPAGE);
               }
            }
         } else if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.LIGHT_RAMPAGE, 2200, 0));
         }
      }
   }
}
