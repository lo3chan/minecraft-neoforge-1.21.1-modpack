package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.entity.LordPumpkinheadEntity;
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
public class LordPumpkinheadatackProcedure {
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
         if (sourceentity instanceof LordPumpkinheadEntity && (entity instanceof Mob || entity instanceof Monster || entity instanceof Player)) {
            if (!(
                  (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F)
                     > (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F) / 2.0F - 50.0F
               )
               || entity instanceof LivingEntity _livEnt6 && _livEnt6.hasEffect(BornInChaosV1ModMobEffects.SOUL_STRATIFICATION)) {
               if ((sourceentity instanceof LivingEntity _livEntxxx ? _livEntxxx.getHealth() : -1.0F)
                     <= (entity instanceof LivingEntity _livEntxx ? _livEntxx.getMaxHealth() : -1.0F) / 2.0F - 50.0F
                  && !(entity instanceof LivingEntity _livEnt13 && _livEnt13.hasEffect(BornInChaosV1ModMobEffects.LIVING_BOMB))) {
                  if (world instanceof Level _level) {
                     if (!_level.isClientSide()) {
                        _level.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:pumpkinhead_bomb_curse")),
                           SoundSource.NEUTRAL,
                           1.0F,
                           1.0F
                        );
                     } else {
                        _level.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:pumpkinhead_bomb_curse")),
                           SoundSource.NEUTRAL,
                           1.0F,
                           1.0F,
                           false
                        );
                     }
                  }

                  if (world instanceof ServerLevel _levelx) {
                     _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.INFERNAL_SURGE.get(), x, y + 1.0, z, 6, 0.2, 0.3, 0.2, 0.1);
                  }

                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.LIVING_BOMB, 320, 0));
                  }

                  if (!(entity instanceof LivingEntity _livEnt17 && _livEnt17.isBlocking())
                     && entity instanceof LivingEntity _entity
                     && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.INFERNAL_FLAME, 300, 0));
                  }
               }
            } else {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.SOUL_STRATIFICATION, 160, 0));
               }

               if (world instanceof ServerLevel _levelx) {
                  _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.INFERNAL_SURGE.get(), x, y + 1.0, z, 6, 0.2, 0.3, 0.2, 0.1);
               }

               if (!(entity instanceof LivingEntity _livEnt9 && _livEnt9.isBlocking())
                  && entity instanceof LivingEntity _entity
                  && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.INFERNAL_FLAME, 160, 0));
               }
            }

            if (!(entity instanceof Player) && sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 2, false, false));
            }
         }
      }
   }
}
