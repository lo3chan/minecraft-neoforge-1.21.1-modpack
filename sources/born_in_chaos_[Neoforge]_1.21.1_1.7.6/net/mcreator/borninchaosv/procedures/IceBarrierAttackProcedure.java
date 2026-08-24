package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
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
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class IceBarrierAttackProcedure {
   @SubscribeEvent
   public static void onEntityAttacked(LivingIncomingDamageEvent event) {
      if (event.getEntity() != null) {
         execute(event, event.getEntity().level(), event.getEntity(), event.getSource().getEntity());
      }
   }

   public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
      execute(null, world, entity, sourceentity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(BornInChaosV1ModMobEffects.ICE_BARRIER)) {
            if (world instanceof ServerLevel _level) {
               _level.sendParticles(
                  (SimpleParticleType)BornInChaosV1ModParticleTypes.LITTLESNOWFLAKE.get(),
                  sourceentity.getX(),
                  sourceentity.getY() + 1.4,
                  sourceentity.getZ(),
                  7,
                  0.25,
                  0.2,
                  0.25,
                  0.1
               );
            }

            if (world instanceof ServerLevel _level) {
               _level.sendParticles(
                  (SimpleParticleType)BornInChaosV1ModParticleTypes.WANINGSNOWFLAKE.get(),
                  sourceentity.getX(),
                  sourceentity.getY() + 1.4,
                  sourceentity.getZ(),
                  4,
                  0.25,
                  0.2,
                  0.25,
                  0.1
               );
            }

            if (!world.isClientSide() && world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(sourceentity.getX(), sourceentity.getY() + 1.0, sourceentity.getZ()),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.hurt_freeze")),
                     SoundSource.NEUTRAL,
                     0.9F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     sourceentity.getX(),
                     sourceentity.getY() + 1.0,
                     sourceentity.getZ(),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.hurt_freeze")),
                     SoundSource.NEUTRAL,
                     0.9F,
                     1.0F,
                     false
                  );
               }
            }

            if (!(sourceentity instanceof KrampusEntity) && !(sourceentity instanceof KrampusHenchmanEntity)) {
               if (sourceentity instanceof LivingEntity _livEnt16 && _livEnt16.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)) {
                  if (sourceentity instanceof LivingEntity _livEnt18
                     && _livEnt18.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                     && (
                           sourceentity instanceof LivingEntity _livEnt && _livEnt.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                              ? _livEnt.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                              : 0
                        )
                        >= 6) {
                     if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_CHILLING, 80, 6));
                     }
                  } else if (sourceentity instanceof LivingEntity _livEnt21
                     && _livEnt21.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                     && (
                           sourceentity instanceof LivingEntity _livEntx && _livEntx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                              ? _livEntx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                              : 0
                        )
                        < 6
                     && sourceentity instanceof LivingEntity _entity
                     && !_entity.level().isClientSide()) {
                     _entity.addEffect(
                        new MobEffectInstance(
                           BornInChaosV1ModMobEffects.BONE_CHILLING,
                           (
                                 sourceentity instanceof LivingEntity _livEntxxx && _livEntxxx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                                    ? _livEntxxx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getDuration()
                                    : 0
                              )
                              - 5,
                           (
                                 sourceentity instanceof LivingEntity _livEntxx && _livEntxx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                                    ? _livEntxx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                                    : 0
                              )
                              + 1
                        )
                     );
                  }
               } else if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_CHILLING, 240, 0));
               }
            }
         }
      }
   }
}
