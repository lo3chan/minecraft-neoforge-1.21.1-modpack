package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.entity.KrampusEntity;
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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

@EventBusSubscriber
public class KrampusAttackProcedure {
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
         if (sourceentity instanceof KrampusEntity && (entity instanceof Mob || entity instanceof Monster || entity instanceof Player)) {
            if (Math.random() < 0.35 && !(entity instanceof LivingEntity _livEnt4 && _livEnt4.isBlocking())) {
               if (!world.isClientSide() && world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:krampus_blow")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:krampus_blow")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }

               if (entity instanceof LivingEntity _livEnt7
                  && _livEnt7.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                  && sourceentity instanceof LivingEntity _entity) {
                  _entity.setHealth((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) + 8.0F);
               }

               if (entity instanceof Player) {
                  if (world instanceof ServerLevel _levelx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelx,
                        entity.getX(),
                        entity.getY() + 1.0,
                        entity.getZ(),
                        (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).copy()
                     );
                     entityToSpawn.setPickUpDelay(45);
                     _levelx.addFreshEntity(entityToSpawn);
                  }

                  if (world instanceof ServerLevel _levelx) {
                     _levelx.sendParticles(
                        (SimpleParticleType)BornInChaosV1ModParticleTypes.STUNSTARS.get(),
                        entity.getX(),
                        entity.getY() + 1.5,
                        entity.getZ(),
                        5,
                        0.25,
                        0.3,
                        0.25,
                        0.1
                     );
                  }

                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.LOST_ITEMS, 35, 0, false, false));
                  }

                  if (entity.getCapability(ItemHandler.ENTITY, null) instanceof IItemHandlerModifiable _modHandlerIter) {
                     for (int _idx = 0; _idx < _modHandlerIter.getSlots(); _idx++) {
                        ItemStack itemstackiterator = _modHandlerIter.getStackInSlot(_idx).copy();
                        (entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY)
                           .shrink((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getCount());
                     }
                  }
               }
            } else if (entity instanceof LivingEntity _livEnt27
               && _livEnt27.isBlocking()
               && (entity instanceof LivingEntity _entUseItem28 ? _entUseItem28.getUseItem() : ItemStack.EMPTY).getItem() instanceof ShieldItem) {
               if (world instanceof ServerLevel _levelx) {
                  (entity instanceof LivingEntity _entUseItem30 ? _entUseItem30.getUseItem() : ItemStack.EMPTY).hurtAndBreak(25, _levelx, null, _stkprov -> {});
               }

               if (entity instanceof Player _player) {
                  _player.getCooldowns()
                     .addCooldown((entity instanceof LivingEntity _entUseItem32 ? _entUseItem32.getUseItem() : ItemStack.EMPTY).getItem(), 100);
               }
            }
         }
      }
   }
}
