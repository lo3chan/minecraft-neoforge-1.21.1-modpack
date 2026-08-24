package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.entity.NightmareStalkerEntity;
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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class NightmareStalkerDamProcedure {
   @SubscribeEvent
   public static void onEntityAttacked(LivingIncomingDamageEvent event) {
      if (event.getEntity() != null) {
         execute(
            event,
            event.getEntity().level(),
            event.getEntity().getX(),
            event.getEntity().getY(),
            event.getEntity().getZ(),
            event.getEntity(),
            event.getSource().getEntity()
         );
      }
   }

   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      execute(null, world, x, y, z, entity, sourceentity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (sourceentity instanceof NightmareStalkerEntity
            && (entity instanceof Player || entity instanceof Monster || entity instanceof Mob)
            && !(entity instanceof LivingEntity _livEnt4 && _livEnt4.hasEffect(BornInChaosV1ModMobEffects.GAZE_OF_TERROR))) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.GAZE_OF_TERROR, 200, 0));
            }

            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:stalker_roar")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:stalker_roar")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (entity instanceof Player && !(entity instanceof LivingEntity _livEnt8 && _livEnt8.isBlocking())) {
               if (world.dayTime() >= 2400000L) {
                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 1, false, false));
                  }

                  if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 4, false, false));
                  }
               } else if (world.dayTime() >= 1200000L && world.dayTime() < 2400000L) {
                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 0, false, false));
                  }

                  if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 3, false, false));
                  }
               } else {
                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 0, false, false));
                  }

                  if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 2, false, false));
                  }
               }
            }

            if (entity instanceof Monster || entity instanceof Mob) {
               if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(
                     new MobEffectInstance(
                        MobEffects.DAMAGE_BOOST,
                        300,
                        (
                              sourceentity instanceof LivingEntity _livEnt && _livEnt.hasEffect(MobEffects.DAMAGE_BOOST)
                                 ? _livEnt.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier()
                                 : 0
                           )
                           + 1,
                        false,
                        false
                     )
                  );
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 300, 2, false, false));
               }

               if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 4, false, false));
               }
            }

            if (sourceentity instanceof LivingEntity _entity) {
               _entity.removeEffect(BornInChaosV1ModMobEffects.TERRIFYING_PRESENCE);
            }

            sourceentity.getPersistentData().putDouble("ragescale", 0.0);
         }
      }
   }
}
