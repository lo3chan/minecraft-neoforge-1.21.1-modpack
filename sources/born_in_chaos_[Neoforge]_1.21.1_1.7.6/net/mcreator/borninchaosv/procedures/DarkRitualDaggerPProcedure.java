package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.entity.ControlledBabySkeletonEntity;
import net.mcreator.borninchaosv.entity.ControlledSpiritualAssistantEntity;
import net.mcreator.borninchaosv.entity.MrPumpkinControlledEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract;

@EventBusSubscriber
public class DarkRitualDaggerPProcedure {
   @SubscribeEvent
   public static void onRightClickEntity(EntityInteract event) {
      if (event.getHand() == event.getEntity().getUsedItemHand()) {
         execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getTarget(), event.getEntity());
      }
   }

   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      execute(null, world, x, y, z, entity, sourceentity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (sourceentity instanceof Player && !(sourceentity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(BornInChaosV1ModMobEffects.SACRIFICE))) {
            if ((sourceentity instanceof LivingEntity _entGetArmorxxx ? _entGetArmorxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
                  != BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_HELMET.get()
               || (sourceentity instanceof LivingEntity _entGetArmorxx ? _entGetArmorxx.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).getItem()
                  != BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_LEGGINGS.get()
               || (sourceentity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem()
                  != BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_CHESTPLATE.get()
               || (sourceentity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem()
                  != BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_BOOTS.get()
               || (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
                  != BornInChaosV1ModItems.DARK_RITUAL_DAGGER.get()
               || !(entity instanceof ControlledBabySkeletonEntity)
                  && !(entity instanceof MrPumpkinControlledEntity)
                  && !(entity instanceof ControlledSpiritualAssistantEntity)) {
               if ((sourceentity instanceof LivingEntity _entGetArmorxxxxxxx ? _entGetArmorxxxxxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY)
                        .getItem()
                     == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_HELMET.get()
                  && (sourceentity instanceof LivingEntity _entGetArmorxxxxxx ? _entGetArmorxxxxxx.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY)
                        .getItem()
                     == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_LEGGINGS.get()
                  && (sourceentity instanceof LivingEntity _entGetArmorxxxxx ? _entGetArmorxxxxx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY)
                        .getItem()
                     == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_CHESTPLATE.get()
                  && (sourceentity instanceof LivingEntity _entGetArmorxxxx ? _entGetArmorxxxx.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem()
                     == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_BOOTS.get()
                  && (sourceentity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).getItem()
                     == BornInChaosV1ModItems.DARK_RITUAL_DAGGER.get()
                  && entity instanceof Animal
                  && (entity instanceof LivingEntity _livEntx ? _livEntx.getMaxHealth() : -1.0F) <= 15.0F) {
                  entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC), sourceentity), 100.0F);
                  if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1000, 1));
                  }

                  if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1000, 1));
                  }

                  if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.SACRIFICE, 30, 0, false, false));
                  }

                  if (world instanceof ServerLevel _level) {
                     _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.RITUAL.get(), x + 0.5, y + 0.5, z + 0.5, 15, 0.3, 0.3, 0.3, 0.3);
                  }

                  if (world instanceof ServerLevel _level) {
                     _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SWAP.get(), x + 0.5, y + 1.0, z + 0.5, 1, 0.1, 0.1, 0.1, 0.1);
                  }

                  if (world instanceof Level _level) {
                     if (!_level.isClientSide()) {
                        _level.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                           SoundSource.NEUTRAL,
                           1.0F,
                           1.0F
                        );
                     } else {
                        _level.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                           SoundSource.NEUTRAL,
                           1.0F,
                           1.0F,
                           false
                        );
                     }
                  }

                  if (sourceentity instanceof LivingEntity _entity) {
                     _entity.swing(InteractionHand.MAIN_HAND, true);
                  }

                  if (world instanceof ServerLevel _levelx) {
                     (sourceentity instanceof LivingEntity _livEntxxx ? _livEntxxx.getMainHandItem() : ItemStack.EMPTY)
                        .hurtAndBreak(1, _levelx, null, _stkprov -> {});
                  }

                  if (sourceentity instanceof Player _player) {
                     _player.getCooldowns().addCooldown((Item)BornInChaosV1ModItems.DARK_RITUAL_DAGGER.get(), 30);
                  }
               } else if ((sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).getItem()
                     == BornInChaosV1ModItems.DARK_RITUAL_DAGGER.get()
                  && (
                     entity instanceof ControlledBabySkeletonEntity
                        || entity instanceof MrPumpkinControlledEntity
                        || entity instanceof ControlledSpiritualAssistantEntity
                  )) {
                  entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC), sourceentity), 100.0F);
                  if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 500, 1));
                  }

                  if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 500, 1));
                  }

                  if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.SACRIFICE, 30, 0, false, false));
                  }

                  if (world instanceof ServerLevel _levelx) {
                     _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.RITUAL.get(), x + 0.5, y + 0.5, z + 0.5, 15, 0.3, 0.3, 0.3, 0.3);
                  }

                  if (world instanceof ServerLevel _levelx) {
                     _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SWAP.get(), x + 0.5, y + 1.0, z + 0.5, 1, 0.1, 0.1, 0.1, 0.1);
                  }

                  if (world instanceof Level _levelx) {
                     if (!_levelx.isClientSide()) {
                        _levelx.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                           SoundSource.NEUTRAL,
                           1.0F,
                           1.0F
                        );
                     } else {
                        _levelx.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                           SoundSource.NEUTRAL,
                           1.0F,
                           1.0F,
                           false
                        );
                     }
                  }

                  if (sourceentity instanceof LivingEntity _entity) {
                     _entity.swing(InteractionHand.MAIN_HAND, true);
                  }

                  if (world instanceof ServerLevel _levelxx) {
                     (sourceentity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY)
                        .hurtAndBreak(1, _levelxx, null, _stkprov -> {});
                  }

                  if (sourceentity instanceof Player _player) {
                     _player.getCooldowns().addCooldown((Item)BornInChaosV1ModItems.DARK_RITUAL_DAGGER.get(), 30);
                  }
               } else if ((sourceentity instanceof LivingEntity _livEntxxx ? _livEntxxx.getMainHandItem() : ItemStack.EMPTY).getItem()
                     == BornInChaosV1ModItems.DARK_RITUAL_DAGGER.get()
                  && entity instanceof Animal
                  && (entity instanceof LivingEntity _livEntxx ? _livEntxx.getMaxHealth() : -1.0F) <= 15.0F
                  && (entity instanceof LivingEntity _livEntx ? _livEntx.getMaxHealth() : -1.0F) > 10.0F) {
                  entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC), sourceentity), 100.0F);
                  if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 500, 1));
                  }

                  if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 500, 0));
                  }

                  if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.SACRIFICE, 30, 0, false, false));
                  }

                  if (world instanceof ServerLevel _levelxx) {
                     _levelxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.RITUAL.get(), x + 0.5, y + 0.5, z + 0.5, 15, 0.3, 0.3, 0.3, 0.3);
                  }

                  if (world instanceof ServerLevel _levelxx) {
                     _levelxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SWAP.get(), x + 0.5, y + 1.0, z + 0.5, 1, 0.1, 0.1, 0.1, 0.1);
                  }

                  if (world instanceof Level _levelxx) {
                     if (!_levelxx.isClientSide()) {
                        _levelxx.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                           SoundSource.NEUTRAL,
                           1.0F,
                           1.0F
                        );
                     } else {
                        _levelxx.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                           SoundSource.NEUTRAL,
                           1.0F,
                           1.0F,
                           false
                        );
                     }
                  }

                  if (sourceentity instanceof LivingEntity _entity) {
                     _entity.swing(InteractionHand.MAIN_HAND, true);
                  }

                  if (world instanceof ServerLevel _levelxxx) {
                     (sourceentity instanceof LivingEntity _livEntxxxx ? _livEntxxxx.getMainHandItem() : ItemStack.EMPTY)
                        .hurtAndBreak(1, _levelxxx, null, _stkprov -> {});
                  }

                  if (sourceentity instanceof Player _player) {
                     _player.getCooldowns().addCooldown((Item)BornInChaosV1ModItems.DARK_RITUAL_DAGGER.get(), 30);
                  }
               } else if ((sourceentity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).getItem()
                     == BornInChaosV1ModItems.DARK_RITUAL_DAGGER.get()
                  && entity instanceof Animal
                  && (entity instanceof LivingEntity _livEntx ? _livEntx.getMaxHealth() : -1.0F) <= 10.0F) {
                  entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC), sourceentity), 100.0F);
                  if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 0));
                  }

                  if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 300, 0));
                  }

                  if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.SACRIFICE, 30, 0, false, false));
                  }

                  if (world instanceof ServerLevel _levelxxx) {
                     _levelxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.RITUAL.get(), x + 0.5, y + 0.5, z + 0.5, 15, 0.3, 0.3, 0.3, 0.3);
                  }

                  if (world instanceof ServerLevel _levelxxx) {
                     _levelxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SWAP.get(), x + 0.5, y + 1.0, z + 0.5, 1, 0.1, 0.1, 0.1, 0.1);
                  }

                  if (world instanceof Level _levelxxx) {
                     if (!_levelxxx.isClientSide()) {
                        _levelxxx.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                           SoundSource.NEUTRAL,
                           1.0F,
                           1.0F
                        );
                     } else {
                        _levelxxx.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                           SoundSource.NEUTRAL,
                           1.0F,
                           1.0F,
                           false
                        );
                     }
                  }

                  if (sourceentity instanceof LivingEntity _entity) {
                     _entity.swing(InteractionHand.MAIN_HAND, true);
                  }

                  if (world instanceof ServerLevel _levelxxxx) {
                     (sourceentity instanceof LivingEntity _livEntxxx ? _livEntxxx.getMainHandItem() : ItemStack.EMPTY)
                        .hurtAndBreak(1, _levelxxxx, null, _stkprov -> {});
                  }

                  if (sourceentity instanceof Player _player) {
                     _player.getCooldowns().addCooldown((Item)BornInChaosV1ModItems.DARK_RITUAL_DAGGER.get(), 30);
                  }
               }
            } else {
               entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC), sourceentity), 100.0F);
               if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1500, 1));
               }

               if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1500, 1));
               }

               if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.SACRIFICE, 30, 0, false, false));
               }

               if (world instanceof ServerLevel _levelxxxx) {
                  _levelxxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.RITUAL.get(), x + 0.5, y + 0.5, z + 0.5, 15, 0.3, 0.3, 0.3, 0.3);
               }

               if (world instanceof ServerLevel _levelxxxx) {
                  _levelxxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SWAP.get(), x + 0.5, y + 1.0, z + 0.5, 1, 0.1, 0.1, 0.1, 0.1);
               }

               if (world instanceof Level _levelxxxx) {
                  if (!_levelxxxx.isClientSide()) {
                     _levelxxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _levelxxxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }

               if (sourceentity instanceof LivingEntity _entity) {
                  _entity.swing(InteractionHand.MAIN_HAND, true);
               }

               if (world instanceof ServerLevel _levelxxxxx) {
                  (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY)
                     .hurtAndBreak(1, _levelxxxxx, null, _stkprov -> {});
               }

               if (sourceentity instanceof Player _player) {
                  _player.getCooldowns().addCooldown((Item)BornInChaosV1ModItems.DARK_RITUAL_DAGGER.get(), 30);
               }
            }
         }
      }
   }
}
