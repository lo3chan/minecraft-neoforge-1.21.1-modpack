package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.entity.DoorKnightEntity;
import net.mcreator.borninchaosv.entity.DoorKnightNotDespawnEntity;
import net.mcreator.borninchaosv.entity.SkeletonThrasherEntity;
import net.mcreator.borninchaosv.entity.SkeletonThrasherNotDespawnEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class AxeAtackProcedure {
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
         if (sourceentity instanceof Player) {
            if (!((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof AxeItem)
               || !(entity instanceof DoorKnightEntity) && !(entity instanceof DoorKnightNotDespawnEntity)
               || entity instanceof LivingEntity _livEnt5 && _livEnt5.hasEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK)) {
               if ((sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof AxeItem
                  && (entity instanceof SkeletonThrasherEntity || entity instanceof SkeletonThrasherNotDespawnEntity)
                  && !(entity instanceof LivingEntity _livEnt14 && _livEnt14.hasEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK))) {
                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BLOCK_BREAK, 120, 0, false, false));
                  }

                  if (world instanceof Level _level) {
                     if (!_level.isClientSide()) {
                        _level.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.break_wooden_door")),
                           SoundSource.NEUTRAL,
                           0.2F,
                           1.0F
                        );
                     } else {
                        _level.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.break_wooden_door")),
                           SoundSource.NEUTRAL,
                           0.2F,
                           1.0F,
                           false
                        );
                     }
                  }

                  if (world instanceof ServerLevel _levelx) {
                     _levelx.sendParticles(ParticleTypes.CRIT, x, y, z, 9, 0.6, 1.0, 0.6, 0.6);
                  }

                  if (entity instanceof LivingEntity _entity) {
                     _entity.removeEffect(MobEffects.DAMAGE_RESISTANCE);
                  }
               }
            } else {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BLOCK_BREAK, 120, 0, false, false));
               }

               if (world instanceof Level _levelx) {
                  if (!_levelx.isClientSide()) {
                     _levelx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.break_wooden_door")),
                        SoundSource.NEUTRAL,
                        0.2F,
                        1.0F
                     );
                  } else {
                     _levelx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.break_wooden_door")),
                        SoundSource.NEUTRAL,
                        0.2F,
                        1.0F,
                        false
                     );
                  }
               }

               if (world instanceof ServerLevel _levelxx) {
                  _levelxx.sendParticles(ParticleTypes.CRIT, x, y, z, 9, 0.6, 1.0, 0.6, 0.6);
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.removeEffect(MobEffects.DAMAGE_RESISTANCE);
               }
            }
         }
      }
   }
}
