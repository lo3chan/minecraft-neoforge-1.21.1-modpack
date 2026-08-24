package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import javax.annotation.Nullable;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.ThebeartamerEntity;
import net.mcreator.undeadrevamp.entity.ThepregnantEntity;
import net.mcreator.undeadrevamp.entity.ThewolfEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModItems;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;

@EventBusSubscriber
public class HitstackProcedure {
   @SubscribeEvent
   public static void onEntityAttacked(Post event) {
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
         double particleRadius = 0.0;
         double particleAmount = 0.0;
         if (entity instanceof LivingEntity _livEnt0
            && _livEnt0.hasEffect(UndeadRevamp2ModMobEffects.BROKENTANK)
            && (entity instanceof ThebeartamerEntity || entity instanceof ThepregnantEntity || entity instanceof ThewolfEntity)
            && sourceentity instanceof LivingEntity) {
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiterator == sourceentity) {
                  if (entity instanceof ThebeartamerEntity) {
                     ((ThebeartamerEntity)entity).setAnimation("stunned");
                  }

                  if (entity instanceof ThewolfEntity) {
                     ((ThewolfEntity)entity).setAnimation("stunned");
                  }

                  if (!((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) < 10.0F) && entity instanceof ThepregnantEntity) {
                     ((ThepregnantEntity)entity).setAnimation("knock");
                  }

                  if (entity instanceof ThepregnantEntity && world instanceof Level _level) {
                     if (!_level.isClientSide()) {
                        _level.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:wheezehurt")),
                           SoundSource.NEUTRAL,
                           5.0F,
                           -2.0F
                        );
                     } else {
                        _level.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:wheezehurt")),
                           SoundSource.NEUTRAL,
                           5.0F,
                           -2.0F,
                           false
                        );
                     }
                  }

                  if (!(entity instanceof ThepregnantEntity) && world instanceof Level _levelx) {
                     if (!_levelx.isClientSide()) {
                        _levelx.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:clank")),
                           SoundSource.NEUTRAL,
                           2.5F,
                           0.5F
                        );
                     } else {
                        _levelx.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:clank")),
                           SoundSource.NEUTRAL,
                           2.5F,
                           0.5F,
                           false
                        );
                     }
                  }

                  if (entity instanceof ThebeartamerEntity && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.UNDEADSTUNS, 90, 0, false, false));
                  }

                  if (entity instanceof ThewolfEntity && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.UNDEADSTUNS, 80, 0, false, false));
                  }

                  if (entity instanceof ThepregnantEntity && !((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) < 10.0F)) {
                     if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.UNDEADSTUNS, 60, 0, false, false));
                     }

                     entity.getPersistentData().putDouble("pukeshut", 1.0);
                  }

                  if (!(entity instanceof ThepregnantEntity) && !((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) < 10.0F)) {
                     entity.setDeltaMovement(
                        new Vec3(
                           Math.sin(Math.toRadians(entity.getYRot() + 180.0F)) * 2.5 * -1.0,
                           (Math.sin(Math.toRadians(0.0F - entity.getXRot())) + 0.5) * 2.0,
                           Math.cos(Math.toRadians(entity.getYRot())) * 1.25 * -1.5
                        )
                     );
                  }

                  if (!(
                     entity instanceof ServerPlayer _plr28
                        && _plr28.level() instanceof ServerLevel
                        && _plr28.getAdvancements()
                           .getOrStartProgress(_plr28.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:sugarcoat")))
                           .isDone()
                  )) {
                     if (!(Math.random() < 0.02)) {
                        if (sourceentity instanceof ServerPlayer _player) {
                           AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:sugarcoat"));
                           if (_adv != null) {
                              AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                              if (!_ap.isDone()) {
                                 for (String criteria : _ap.getRemainingCriteria()) {
                                    _player.getAdvancements().award(_adv, criteria);
                                 }
                              }
                           }
                        }
                     } else {
                        if (sourceentity instanceof ServerPlayer _playerx) {
                           AdvancementHolder _adv = _playerx.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:sugarcoat"));
                           if (_adv != null) {
                              AdvancementProgress _ap = _playerx.getAdvancements().getOrStartProgress(_adv);
                              if (!_ap.isDone()) {
                                 for (String criteria : _ap.getRemainingCriteria()) {
                                    _playerx.getAdvancements().award(_adv, criteria);
                                 }
                              }
                           }
                        }

                        if (Math.random() < 0.02) {
                           if (world instanceof Level _levelxx) {
                              if (!_levelxx.isClientSide()) {
                                 _levelxx.playSound(
                                    null,
                                    BlockPos.containing(x, y, z),
                                    (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:sugareww")),
                                    SoundSource.NEUTRAL,
                                    5.0F,
                                    1.0F
                                 );
                              } else {
                                 _levelxx.playLocalSound(
                                    x,
                                    y,
                                    z,
                                    (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:sugareww")),
                                    SoundSource.NEUTRAL,
                                    5.0F,
                                    1.0F,
                                    false
                                 );
                              }
                           }

                           if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                              _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.UNDEADSTUNS, 50, 0, false, false));
                           }
                        }
                     }
                  }

                  entity.getPersistentData().putDouble("pastat", 1.0);
                  if (world instanceof ServerLevel _levelxxx) {
                     _levelxxx.sendParticles(ParticleTypes.FLAME, x, y, z, 5, 1.0, 1.0, 1.0, 1.0E-8);
                  }

                  entity.getPersistentData().putDouble("passorsmash", 0.0);
               }
            }
         }

         if ((sourceentity instanceof LivingEntity _entGetArmorxx ? _entGetArmorxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
               == UndeadRevamp2ModItems.PRIMODIALARMOUR_HELMET.get()
            && (sourceentity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem()
               == UndeadRevamp2ModItems.PRIMODIALARMOUR_CHESTPLATE.get()
            && (sourceentity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem()
               == UndeadRevamp2ModItems.PRIMODIALARMOUR_BOOTS.get()) {
            entity.igniteForSeconds(15.0F);
         }

         if ((entity instanceof LivingEntity _entGetArmorxx ? _entGetArmorxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
               == UndeadRevamp2ModItems.BOSTROXSET_HELMET.get()
            && (entity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem()
               == UndeadRevamp2ModItems.BOSTROXSET_CHESTPLATE.get()
            && (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem()
               == UndeadRevamp2ModItems.BOSTROXSET_BOOTS.get()) {
            entity.setDeltaMovement(
               new Vec3(
                  Math.sin(Math.toRadians(entity.getYRot() + 180.0F)) * 1.25 * -1.0,
                  (Math.sin(Math.toRadians(0.0F - entity.getXRot())) + 0.5) * 1.25,
                  Math.cos(Math.toRadians(entity.getYRot())) * 1.25 * -1.0
               )
            );
            if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == UndeadRevamp2ModItems.BOSTROXSWORD.get()) {
               if (world instanceof ServerLevel _levelxxx) {
                  _levelxxx.sendParticles(ParticleTypes.CRIT, x, y, z, 60, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 1.0);
               }

               entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.FALLING_ANVIL)), 5.0F);
            }

            if (entity.isSprinting()) {
               entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.FALLING_BLOCK)), 4.0F);
               if (world instanceof Level _levelxxx) {
                  if (!_levelxxx.isClientSide()) {
                     _levelxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.anvil.hit")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _levelxxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.anvil.hit")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }
            }
         }

         if ((entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
            == UndeadRevamp2ModItems.SHIELDMASK_HELMET.get()) {
            if (!(entity instanceof Player)) {
               if (world instanceof Level _levelxxxx) {
                  if (!_levelxxxx.isClientSide()) {
                     _levelxxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:parry")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _levelxxxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:parry")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }

               world.addParticle(ParticleTypes.FLASH, x, y, z, 0.0, 1.0, 0.0);
               Vec3 _center = new Vec3(x, y, z);

               for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.0), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if (entityiteratorx != entity) {
                     entityiteratorx.hurt(new DamageSource(world.holderOrThrow(DamageTypes.WITHER), entity), 1.0F);
                     entityiteratorx.setDeltaMovement(
                        new Vec3(
                           Math.sin(Math.toRadians(entityiteratorx.getYRot() + 180.0F)) * 1.25 * -1.2,
                           (Math.sin(Math.toRadians(0.0F - entityiteratorx.getXRot())) + 0.55) * 1.1,
                           Math.cos(Math.toRadians(entityiteratorx.getYRot())) * 1.25 * -1.38
                        )
                     );
                  }
               }
            } else if (entity.isShiftKeyDown()) {
               if (world instanceof Level _levelxxxxx) {
                  if (!_levelxxxxx.isClientSide()) {
                     _levelxxxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:parry")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _levelxxxxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:parry")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }

               world.addParticle(ParticleTypes.FLASH, x, y, z, 0.0, 1.0, 0.0);
               Vec3 _center = new Vec3(x, y, z);

               for (Entity entityiteratorxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.0), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if (entityiteratorxx != entity) {
                     entityiteratorxx.hurt(new DamageSource(world.holderOrThrow(DamageTypes.WITHER), entity), 1.0F);
                     entityiteratorxx.setDeltaMovement(
                        new Vec3(
                           Math.sin(Math.toRadians(entityiteratorxx.getYRot() + 180.0F)) * 1.25 * -1.2,
                           (Math.sin(Math.toRadians(0.0F - entityiteratorxx.getXRot())) + 0.55) * 1.1,
                           Math.cos(Math.toRadians(entityiteratorxx.getYRot())) * 1.25 * -1.38
                        )
                     );
                  }
               }
            }
         }

         if ((sourceentity instanceof LivingEntity _entGetArmorxx ? _entGetArmorxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
               == UndeadRevamp2ModItems.WITHERCHARGEMASK_HELMET.get()
            && !(
               sourceentity instanceof Player _plrCldCheck93
                  && _plrCldCheck93.getCooldowns()
                     .isOnCooldown(
                        (sourceentity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
                     )
            )
            && sourceentity.isShiftKeyDown()) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.WITHERFLAME, 70, 0, false, true));
            }

            if (sourceentity instanceof Player _playerxx) {
               _playerxx.getCooldowns()
                  .addCooldown(
                     (sourceentity instanceof LivingEntity _entGetArmorxxx ? _entGetArmorxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem(),
                     150
                  );
            }
         }

         if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY)
               .getEnchantmentLevel(
                  world.registryAccess()
                     .lookupOrThrow(Registries.ENCHANTMENT)
                     .getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("undead_revamp2:devillsweep")))
               )
            != 0) {
            UndeadRevamp2Mod.queueServerWork(
               45
                  - 4
                     * (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY)
                        .getEnchantmentLevel(
                           world.registryAccess()
                              .lookupOrThrow(Registries.ENCHANTMENT)
                              .getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("undead_revamp2:devillsweep")))
                        ),
               () -> {
                  Vec3 _centerx = new Vec3(x, y, z);

                  LivingEntity _livEntx;
                  for (Entity entityiteratorxxx : world.getEntitiesOfClass(
                        Entity.class,
                        new AABB(_centerx, _centerx)
                           .inflate(
                              (
                                    4.8
                                       + 1.25
                                          * (sourceentity instanceof _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY)
                                             .getEnchantmentLevel(
                                                world.registryAccess()
                                                   .lookupOrThrow(Registries.ENCHANTMENT)
                                                   .getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("undead_revamp2:devillsweep")))
                                             )
                                 )
                                 / 2.0
                           ),
                        e -> true
                     )
                     .stream()
                     .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_centerx)))
                     .toList()) {
                     if (sourceentity == entityiteratorxxx && entity != sourceentity) {
                        if ((sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY)
                              .getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS))
                           != 0) {
                           entity.hurt(
                              new DamageSource(world.holderOrThrow(DamageTypes.MOB_ATTACK), sourceentity),
                              (float)(
                                 (sourceentity instanceof LivingEntity _livEntxxxx ? _livEntxxxx.getMainHandItem() : ItemStack.EMPTY).getDamageValue() / 4
                                       + (sourceentity instanceof LivingEntity _livEntxxx ? _livEntxxx.getMainHandItem() : ItemStack.EMPTY)
                                          .getEnchantmentLevel(
                                             world.registryAccess()
                                                .lookupOrThrow(Registries.ENCHANTMENT)
                                                .getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("undead_revamp2:devillsweep")))
                                          )
                                    + 0.5
                                       * (sourceentity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY)
                                          .getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS))
                              )
                           );
                        } else {
                           entity.hurt(
                              new DamageSource(world.holderOrThrow(DamageTypes.MOB_ATTACK), sourceentity),
                              (float)(
                                 (sourceentity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).getDamageValue() / 4.2
                                    + (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY)
                                       .getEnchantmentLevel(
                                          world.registryAccess()
                                             .lookupOrThrow(Registries.ENCHANTMENT)
                                             .getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("undead_revamp2:devillsweep")))
                                       )
                              )
                           );
                        }

                        if ((sourceentity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY)
                                 .getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FIRE_ASPECT))
                              != 0
                           || (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY)
                                 .getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SMITE))
                              != 0) {
                           if (entity.getType().is(EntityTypeTags.UNDEAD)) {
                              entity.igniteForSeconds(
                                 2
                                    * (sourceentity instanceof LivingEntity _livEntxxx ? _livEntxxx.getMainHandItem() : ItemStack.EMPTY)
                                       .getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FIRE_ASPECT))
                              );
                           } else {
                              entity.igniteForSeconds(
                                 2
                                       * (sourceentity instanceof LivingEntity _livEntxxx ? _livEntxxx.getMainHandItem() : ItemStack.EMPTY)
                                          .getEnchantmentLevel(
                                             world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FIRE_ASPECT)
                                          )
                                    + 1
                                       * (sourceentity instanceof LivingEntity _livEntxxx ? _livEntxxx.getMainHandItem() : ItemStack.EMPTY)
                                          .getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SMITE))
                              );
                           }
                        }

                        if (world instanceof ServerLevel _levelxxxxxx) {
                           _levelxxxxxx.sendParticles(
                              ParticleTypes.SWEEP_ATTACK, x, y, z, 1, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 1.0
                           );
                        }

                        if (world instanceof Level _levelxxxxxx) {
                           if (!_levelxxxxxx.isClientSide()) {
                              _levelxxxxxx.playSound(
                                 null,
                                 BlockPos.containing(x, y, z),
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F
                              );
                           } else {
                              _levelxxxxxx.playLocalSound(
                                 x,
                                 y,
                                 z,
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
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
                     }
                  }
               }
            );
         }

         if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY)
               .getEnchantmentLevel(
                  world.registryAccess()
                     .lookupOrThrow(Registries.ENCHANTMENT)
                     .getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("undead_revamp2:baneofaerial")))
               )
            != 0) {
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiteratorxxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (sourceentity != entityiteratorxxx && entity != sourceentity && !entity.onGround() && !entity.isUnderWater()) {
                  if (!(entity instanceof LivingEntity _livEnt149 && _livEnt149.hasEffect(UndeadRevamp2ModMobEffects.ANTIFLYINH))) {
                     if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(
                           new MobEffectInstance(
                              UndeadRevamp2ModMobEffects.ANTIFLYINH,
                              80
                                 + 80
                                    * (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY)
                                       .getEnchantmentLevel(
                                          world.registryAccess()
                                             .lookupOrThrow(Registries.ENCHANTMENT)
                                             .getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("undead_revamp2:baneofaerial")))
                                       ),
                              0,
                              false,
                              false
                           )
                        );
                     }

                     if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(
                           new MobEffectInstance(
                              MobEffects.MOVEMENT_SLOWDOWN,
                              80
                                 + 80
                                    * (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY)
                                       .getEnchantmentLevel(
                                          world.registryAccess()
                                             .lookupOrThrow(Registries.ENCHANTMENT)
                                             .getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("undead_revamp2:baneofaerial")))
                                       ),
                              20,
                              false,
                              true
                           )
                        );
                     }

                     if (world instanceof ServerLevel _levelxxxxxx) {
                        _levelxxxxxx.sendParticles(ParticleTypes.FLASH, x, y, z, 1, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 1.0);
                     }

                     if (world instanceof Level _levelxxxxxx) {
                        if (!_levelxxxxxx.isClientSide()) {
                           _levelxxxxxx.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:parry")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              1.0F
                           );
                        } else {
                           _levelxxxxxx.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:parry")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              1.0F,
                              false
                           );
                        }
                     }
                  }

                  if (entity instanceof LivingEntity _livEnt161 && _livEnt161.hasEffect(UndeadRevamp2ModMobEffects.ANTIFLYINH)) {
                     if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(
                           new MobEffectInstance(
                              UndeadRevamp2ModMobEffects.ANTIFLYINH,
                              (
                                    entity instanceof LivingEntity _livEntxx && _livEntxx.hasEffect(UndeadRevamp2ModMobEffects.ANTIFLYINH)
                                       ? _livEntxx.getEffect(UndeadRevamp2ModMobEffects.ANTIFLYINH).getDuration()
                                       : 0
                                 )
                                 + 80
                                 + 80
                                    * (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY)
                                       .getEnchantmentLevel(
                                          world.registryAccess()
                                             .lookupOrThrow(Registries.ENCHANTMENT)
                                             .getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("undead_revamp2:baneofaerial")))
                                       ),
                              0,
                              false,
                              false
                           )
                        );
                     }

                     if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(
                           new MobEffectInstance(
                              MobEffects.MOVEMENT_SLOWDOWN,
                              80
                                 + 80
                                    * (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY)
                                       .getEnchantmentLevel(
                                          world.registryAccess()
                                             .lookupOrThrow(Registries.ENCHANTMENT)
                                             .getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("undead_revamp2:baneofaerial")))
                                       ),
                              20,
                              false,
                              true
                           )
                        );
                     }

                     if (world instanceof Level _levelxxxxxxx) {
                        if (!_levelxxxxxxx.isClientSide()) {
                           _levelxxxxxxx.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:parry")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              1.0F
                           );
                        } else {
                           _levelxxxxxxx.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:parry")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              1.0F,
                              false
                           );
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
