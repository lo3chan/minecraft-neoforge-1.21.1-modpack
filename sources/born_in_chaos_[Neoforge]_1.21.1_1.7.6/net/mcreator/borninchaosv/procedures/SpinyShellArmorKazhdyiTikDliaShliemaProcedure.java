package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class SpinyShellArmorKazhdyiTikDliaShliemaProcedure {
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
         if (entity instanceof Player && (sourceentity instanceof Mob || sourceentity instanceof Monster)) {
            if (!(entity instanceof LivingEntity _livEnt3 && _livEnt3.isBlocking())
               || (entity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
                     != BornInChaosV1ModItems.SPINY_SHELL_ARMOR_HELMET.get()
                  && (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem()
                     != BornInChaosV1ModItems.SPINY_SHELL_ARMOR_CHESTPLATE.get()) {
               if ((entity instanceof LivingEntity _entGetArmorxxx ? _entGetArmorxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
                     == BornInChaosV1ModItems.SPINY_SHELL_ARMOR_HELMET.get()
                  && (entity instanceof LivingEntity _entGetArmorxx ? _entGetArmorxx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem()
                     == BornInChaosV1ModItems.SPINY_SHELL_ARMOR_CHESTPLATE.get()) {
                  if ((entity instanceof LivingEntity _entGetArmorxxxxx ? _entGetArmorxxxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY)
                           .getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.THORNS))
                        != 0
                     && (entity instanceof LivingEntity _entGetArmorxxxx ? _entGetArmorxxxx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY)
                           .getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.THORNS))
                        != 0) {
                     sourceentity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 10.0F);
                     if (world instanceof ServerLevel _level) {
                        _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SPIKERELEASE.get(), x, y + 1.0, z, 3, 0.3, 0.3, 0.3, 0.1);
                     }

                     if (world instanceof Level _level) {
                        if (!_level.isClientSide()) {
                           _level.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                              SoundSource.NEUTRAL,
                              0.6F,
                              0.9F
                           );
                        } else {
                           _level.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                              SoundSource.NEUTRAL,
                              0.6F,
                              0.9F,
                              false
                           );
                        }
                     }
                  } else if ((
                        (entity instanceof LivingEntity _entGetArmorxxxxxxx ? _entGetArmorxxxxxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY)
                                 .getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.THORNS))
                              == 0
                           || (entity instanceof LivingEntity _entGetArmorxxxxxx ? _entGetArmorxxxxxx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY)
                                 .getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.THORNS))
                              != 0
                     )
                     && (
                        (entity instanceof LivingEntity _entGetArmorxxxxx ? _entGetArmorxxxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY)
                                 .getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.THORNS))
                              != 0
                           || (entity instanceof LivingEntity _entGetArmorxxxx ? _entGetArmorxxxx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY)
                                 .getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.THORNS))
                              == 0
                     )) {
                     if ((entity instanceof LivingEntity _entGetArmorxxxxxxxxx ? _entGetArmorxxxxxxxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY)
                              .getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.THORNS))
                           == 0
                        || (entity instanceof LivingEntity _entGetArmorxxxxxxxx ? _entGetArmorxxxxxxxx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY)
                              .getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.THORNS))
                           == 0) {
                        sourceentity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 4.0F);
                        if (world instanceof ServerLevel _levelx) {
                           _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SPIKERELEASE.get(), x, y + 1.0, z, 3, 0.3, 0.3, 0.3, 0.1);
                        }

                        if (world instanceof Level _levelx) {
                           if (!_levelx.isClientSide()) {
                              _levelx.playSound(
                                 null,
                                 BlockPos.containing(x, y, z),
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                                 SoundSource.NEUTRAL,
                                 0.6F,
                                 0.9F
                              );
                           } else {
                              _levelx.playLocalSound(
                                 x,
                                 y,
                                 z,
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                                 SoundSource.NEUTRAL,
                                 0.6F,
                                 0.9F,
                                 false
                              );
                           }
                        }
                     }
                  } else {
                     sourceentity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 8.0F);
                     if (world instanceof ServerLevel _levelxx) {
                        _levelxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SPIKERELEASE.get(), x, y + 1.0, z, 3, 0.3, 0.3, 0.3, 0.1);
                     }

                     if (world instanceof Level _levelxx) {
                        if (!_levelxx.isClientSide()) {
                           _levelxx.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                              SoundSource.NEUTRAL,
                              0.6F,
                              0.9F
                           );
                        } else {
                           _levelxx.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                              SoundSource.NEUTRAL,
                              0.6F,
                              0.9F,
                              false
                           );
                        }
                     }
                  }
               } else if ((entity instanceof LivingEntity _entGetArmorxxxxxxxx ? _entGetArmorxxxxxxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY)
                     .getItem()
                  == BornInChaosV1ModItems.SPINY_SHELL_ARMOR_HELMET.get()) {
                  if ((entity instanceof LivingEntity _entGetArmorxxxxxxxxx ? _entGetArmorxxxxxxxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY)
                        .getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.THORNS))
                     != 0) {
                     sourceentity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 4.0F);
                     if (world instanceof ServerLevel _levelxxx) {
                        _levelxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SPIKERELEASE.get(), x, y + 1.0, z, 3, 0.3, 0.3, 0.3, 0.1);
                     }

                     if (world instanceof Level _levelxxx) {
                        if (!_levelxxx.isClientSide()) {
                           _levelxxx.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                              SoundSource.NEUTRAL,
                              0.6F,
                              0.9F
                           );
                        } else {
                           _levelxxx.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                              SoundSource.NEUTRAL,
                              0.6F,
                              0.9F,
                              false
                           );
                        }
                     }
                  } else if ((entity instanceof LivingEntity _entGetArmorxxxxxxxxx ? _entGetArmorxxxxxxxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY)
                        .getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.THORNS))
                     == 0) {
                     sourceentity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 2.0F);
                     if (world instanceof ServerLevel _levelxxxx) {
                        _levelxxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SPIKERELEASE.get(), x, y + 1.0, z, 3, 0.3, 0.3, 0.3, 0.1);
                     }

                     if (world instanceof Level _levelxxxx) {
                        if (!_levelxxxx.isClientSide()) {
                           _levelxxxx.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                              SoundSource.NEUTRAL,
                              0.6F,
                              0.9F
                           );
                        } else {
                           _levelxxxx.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                              SoundSource.NEUTRAL,
                              0.6F,
                              0.9F,
                              false
                           );
                        }
                     }
                  }
               } else if ((entity instanceof LivingEntity _entGetArmorxxxxxxxxx ? _entGetArmorxxxxxxxxx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY)
                     .getItem()
                  == BornInChaosV1ModItems.SPINY_SHELL_ARMOR_CHESTPLATE.get()) {
                  if ((entity instanceof LivingEntity _entGetArmorxxxxxxxxxx ? _entGetArmorxxxxxxxxxx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY)
                        .getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.THORNS))
                     != 0) {
                     sourceentity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 5.0F);
                     if (world instanceof ServerLevel _levelxxxxx) {
                        _levelxxxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SPIKERELEASE.get(), x, y + 1.0, z, 3, 0.3, 0.3, 0.3, 0.1);
                     }

                     if (world instanceof Level _levelxxxxx) {
                        if (!_levelxxxxx.isClientSide()) {
                           _levelxxxxx.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                              SoundSource.NEUTRAL,
                              0.6F,
                              0.9F
                           );
                        } else {
                           _levelxxxxx.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                              SoundSource.NEUTRAL,
                              0.6F,
                              0.9F,
                              false
                           );
                        }
                     }
                  } else if ((entity instanceof LivingEntity _entGetArmorxxxxxxxxxx
                           ? _entGetArmorxxxxxxxxxx.getItemBySlot(EquipmentSlot.CHEST)
                           : ItemStack.EMPTY)
                        .getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.THORNS))
                     == 0) {
                     sourceentity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 2.0F);
                     if (world instanceof ServerLevel _levelxxxxxx) {
                        _levelxxxxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SPIKERELEASE.get(), x, y + 1.0, z, 3, 0.3, 0.3, 0.3, 0.1);
                     }

                     if (world instanceof Level _levelxxxxxx) {
                        if (!_levelxxxxxx.isClientSide()) {
                           _levelxxxxxx.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                              SoundSource.NEUTRAL,
                              0.6F,
                              0.9F
                           );
                        } else {
                           _levelxxxxxx.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                              SoundSource.NEUTRAL,
                              0.6F,
                              0.9F,
                              false
                           );
                        }
                     }
                  }
               }
            } else {
               sourceentity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 1.0F);
               if (world instanceof ServerLevel _levelxxxxxxx) {
                  _levelxxxxxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SPIKERELEASE.get(), x, y + 1.0, z, 3, 0.3, 0.3, 0.3, 0.1);
               }

               if (world instanceof Level _levelxxxxxxx) {
                  if (!_levelxxxxxxx.isClientSide()) {
                     _levelxxxxxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                        SoundSource.NEUTRAL,
                        0.6F,
                        0.9F
                     );
                  } else {
                     _levelxxxxxxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                        SoundSource.NEUTRAL,
                        0.6F,
                        0.9F,
                        false
                     );
                  }
               }
            }
         }
      }
   }
}
