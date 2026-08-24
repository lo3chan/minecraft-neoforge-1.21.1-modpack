package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class ChillingHorrorEventMobsAttacksProcedure {
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
         if ((sourceentity instanceof Zombie || sourceentity instanceof Skeleton) && (entity instanceof Player || entity instanceof Mob)) {
            if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.ICY_SWEETNESS.get()) {
               if (entity instanceof LivingEntity _livEnt6 && _livEnt6.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)) {
                  if (entity instanceof LivingEntity _livEnt8
                     && _livEnt8.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                     && (
                           entity instanceof LivingEntity _livEntx && _livEntx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                              ? _livEntx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                              : 0
                        )
                        >= 6) {
                     if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_CHILLING, 80, 6));
                     }
                  } else if (entity instanceof LivingEntity _livEnt11
                     && _livEnt11.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                     && (
                           entity instanceof LivingEntity _livEntxx && _livEntxx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                              ? _livEntxx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                              : 0
                        )
                        < 6
                     && entity instanceof LivingEntity _entity
                     && !_entity.level().isClientSide()) {
                     _entity.addEffect(
                        new MobEffectInstance(
                           BornInChaosV1ModMobEffects.BONE_CHILLING,
                           140,
                           (
                                 entity instanceof LivingEntity _livEntxxx && _livEntxxx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                                    ? _livEntxxx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                                    : 0
                              )
                              + 1
                        )
                     );
                  }
               } else if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_CHILLING, 180, 0));
               }
            } else if ((sourceentity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).getItem()
                  == BornInChaosV1ModItems.NUT_HAMMER.get()
               && Math.random() < 0.25
               && !world.isClientSide()
               && !(entity instanceof LivingEntity _livEnt18 && _livEnt18.hasEffect(BornInChaosV1ModMobEffects.STUN))) {
               if (world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:bonk_hit")),
                        SoundSource.NEUTRAL,
                        1.6F,
                        1.0F
                     );
                  } else {
                     _level.playLocalSound(
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:bonk_hit")),
                        SoundSource.NEUTRAL,
                        1.6F,
                        1.0F,
                        false
                     );
                  }
               }

               if (world instanceof ServerLevel _levelx) {
                  _levelx.sendParticles(
                     (SimpleParticleType)BornInChaosV1ModParticleTypes.STUNSTARS.get(),
                     entity.getX(),
                     entity.getY() + 1.5,
                     entity.getZ(),
                     6,
                     0.3,
                     0.2,
                     0.3,
                     0.1
                  );
               }

               entity.teleportTo(entity.getX(), entity.getY() - 0.3, entity.getZ());
               if (entity instanceof ServerPlayer _serverPlayer) {
                  _serverPlayer.connection.teleport(entity.getX(), entity.getY() - 0.3, entity.getZ(), entity.getYRot(), entity.getXRot());
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.STUN, 20, 0));
               }
            }
         }
      }
   }
}
