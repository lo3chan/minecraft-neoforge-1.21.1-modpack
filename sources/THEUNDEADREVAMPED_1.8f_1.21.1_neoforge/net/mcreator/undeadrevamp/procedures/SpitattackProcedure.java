package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import javax.annotation.Nullable;
import net.mcreator.undeadrevamp.configuration.MobsabilityConfiguration;
import net.mcreator.undeadrevamp.entity.AxestromEntity;
import net.mcreator.undeadrevamp.entity.BigsuckerEntity;
import net.mcreator.undeadrevamp.entity.CloggerEntity;
import net.mcreator.undeadrevamp.entity.LecheryEntity;
import net.mcreator.undeadrevamp.entity.SlavemanEntity;
import net.mcreator.undeadrevamp.entity.SuckerEntity;
import net.mcreator.undeadrevamp.entity.ThebidyEntity;
import net.mcreator.undeadrevamp.entity.ThebidyupsideEntity;
import net.mcreator.undeadrevamp.entity.ThegliterEntity;
import net.mcreator.undeadrevamp.entity.ThehorrorsEntity;
import net.mcreator.undeadrevamp.entity.ThehunterEntity;
import net.mcreator.undeadrevamp.entity.TheimmortalEntity;
import net.mcreator.undeadrevamp.entity.ThepregnantEntity;
import net.mcreator.undeadrevamp.entity.TheskeeperEntity;
import net.mcreator.undeadrevamp.entity.ThesomnolenceEntity;
import net.mcreator.undeadrevamp.entity.ThespitterEntity;
import net.mcreator.undeadrevamp.entity.TheswarmerEntity;
import net.mcreator.undeadrevamp.entity.ThewolfEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModItems;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModParticleTypes;
import net.mcreator.undeadrevamp.network.UndeadRevamp2ModVariables;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class SpitattackProcedure {
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
         if (entity instanceof ThespitterEntity) {
            entity.setShiftKeyDown(false);
         }

         if (sourceentity instanceof ThehorrorsEntity && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 250, 0, false, true));
         }

         if (sourceentity instanceof LivingEntity _livEnt4
            && _livEnt4.hasEffect(UndeadRevamp2ModMobEffects.EXPLOSIVEHAND)
            && !(entity instanceof LivingEntity _livEnt5 && _livEnt5.isBlocking())) {
            if (sourceentity instanceof LivingEntity _entity) {
               _entity.removeEffect(UndeadRevamp2ModMobEffects.EXPLOSIVEHAND);
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.FULMINATION, 500, 2, false, true));
            }
         }

         if (sourceentity instanceof LecheryEntity
            && !(entity instanceof LivingEntity _livEnt9 && _livEnt9.hasEffect(UndeadRevamp2ModMobEffects.FULMINATION))
            && !(entity instanceof LivingEntity _livEnt10 && _livEnt10.isBlocking())
            && entity instanceof LivingEntity _entity
            && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.FULMINATION, 420, 0, false, true));
         }

         if (sourceentity instanceof ThehunterEntity) {
            if (Math.random() < 0.5) {
               if (entity instanceof ThehunterEntity) {
                  ((ThehunterEntity)entity).setAnimation("claw");
               }

               if (world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _level.playLocalSound(
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
            } else {
               if (entity instanceof ThehunterEntity) {
                  ((ThehunterEntity)entity).setAnimation("claw2");
               }

               if (world instanceof Level _levelx) {
                  if (!_levelx.isClientSide()) {
                     _levelx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _levelx.playLocalSound(
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

               if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 20, false, true));
               }
            }

            if ((Boolean)MobsabilityConfiguration.HUNT_HEAL.get() && !(entity instanceof LivingEntity _livEnt19 && _livEnt19.isBlocking())) {
               if (sourceentity instanceof LivingEntity _entity) {
                  _entity.setHealth(sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F);
               }

               if (world instanceof ServerLevel _levelxx) {
                  _levelxx.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 8, 1.0, 1.0, 1.0, 1.0);
               }
            }

            if (entity instanceof LivingEntity _livEnt23 && _livEnt23.isBlocking() && entity instanceof ServerPlayer _player) {
               AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:earthyblood"));
               if (_adv != null) {
                  AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                  if (!_ap.isDone()) {
                     for (String criteria : _ap.getRemainingCriteria()) {
                        _player.getAdvancements().award(_adv, criteria);
                     }
                  }
               }
            }

            if (sourceentity.getPersistentData().getDouble("horned") == 1.0) {
               if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.ANIMATIONTEST, 30, 0, false, true));
               }
            } else if (sourceentity.getPersistentData().getDouble("horned") == 2.0) {
               if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.ANIMATIONTEST, 25, 0, false, true));
               }
            } else if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.ANIMATIONTEST, 35, 0, false, true));
            }
         }

         if (sourceentity instanceof ThewolfEntity) {
            if (world instanceof Level _levelxx) {
               if (!_levelxx.isClientSide()) {
                  _levelxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelxx.playLocalSound(
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

            if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.BROKENTANK, 3, 0, false, true));
            }
         }

         if (sourceentity instanceof AxestromEntity) {
            if (world instanceof Level _levelxxx) {
               if (!_levelxxx.isClientSide()) {
                  _levelxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelxxx.playLocalSound(
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

            if (!sourceentity.level().isClientSide()) {
               sourceentity.discard();
            }
         }

         if (sourceentity instanceof TheswarmerEntity && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.HONEYSPLAT, 400, 0, false, true));
         }

         if (sourceentity instanceof TheskeeperEntity && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.TRYPANOSOMIASIS, 1000, 0, false, true));
         }

         if ((sourceentity instanceof ThebidyEntity || sourceentity instanceof ThebidyupsideEntity) && !sourceentity.isInWater()) {
            if (world instanceof ServerLevel _levelxxxx) {
               _levelxxxx.sendParticles((SimpleParticleType)UndeadRevamp2ModParticleTypes.BOMBERGOO.get(), x, y, z, 200, 1.5, 1.2, 1.5, 0.001);
            }

            if (world instanceof ServerLevel _levelxxxx) {
               _levelxxxx.sendParticles((SimpleParticleType)UndeadRevamp2ModParticleTypes.BOMBERGOO.get(), x, y, z, 30, 3.0, 1.0, 3.0, 0.2);
            }

            if (world instanceof Level _levelxxxx) {
               if (!_levelxxxx.isClientSide()) {
                  _levelxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:bidyboom")),
                     SoundSource.NEUTRAL,
                     3.0F,
                     1.0F
                  );
               } else {
                  _levelxxxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:bidyboom")),
                     SoundSource.NEUTRAL,
                     3.0F,
                     1.0F,
                     false
                  );
               }
            }

            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(1.5), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiterator instanceof LivingEntity) {
                  if (!(entityiterator instanceof LivingEntity _livEnt47 && _livEnt47.isBlocking())) {
                     entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 6.0F);
                     if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.GOOED, 60, 1));
                     }
                  }

                  entityiterator.setDeltaMovement(
                     new Vec3(
                        Math.sin(Math.toRadians(entityiterator.getYRot() + 180.0F)) * 1.25 * -0.5,
                        (Math.sin(Math.toRadians(0.0F - entityiterator.getXRot())) + 0.5) * 0.12,
                        Math.cos(Math.toRadians(entityiterator.getYRot())) * 1.25 * 0.5
                     )
                  );
               }
            }

            if (!sourceentity.level().isClientSide()) {
               sourceentity.discard();
            }
         }

         if (sourceentity instanceof ThepregnantEntity && world instanceof Level _levelxxxxx) {
            if (!_levelxxxxx.isClientSide()) {
               _levelxxxxx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                  SoundSource.NEUTRAL,
                  0.5F,
                  -2.0F
               );
            } else {
               _levelxxxxx.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                  SoundSource.NEUTRAL,
                  0.5F,
                  -2.0F,
                  false
               );
            }
         }

         if (sourceentity instanceof SuckerEntity) {
            if (world instanceof Level _levelxxxxxx) {
               if (!_levelxxxxxx.isClientSide()) {
                  _levelxxxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:hunter_fly")),
                     SoundSource.NEUTRAL,
                     0.2F,
                     1.0F
                  );
               } else {
                  _levelxxxxxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:hunter_fly")),
                     SoundSource.NEUTRAL,
                     0.2F,
                     1.0F,
                     false
                  );
               }
            }

            if (sourceentity instanceof SuckerEntity) {
               ((SuckerEntity)sourceentity).setAnimation("bite");
            }
         }

         if (sourceentity instanceof BigsuckerEntity) {
            if (world instanceof Level _levelxxxxxxx) {
               if (!_levelxxxxxxx.isClientSide()) {
                  _levelxxxxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:hunter_fly")),
                     SoundSource.NEUTRAL,
                     0.2F,
                     1.0F
                  );
               } else {
                  _levelxxxxxxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:hunter_fly")),
                     SoundSource.NEUTRAL,
                     0.2F,
                     1.0F,
                     false
                  );
               }
            }

            if (sourceentity instanceof BigsuckerEntity) {
               ((BigsuckerEntity)sourceentity).setAnimation("bite");
            }
         }

         if (sourceentity instanceof SlavemanEntity) {
            if (world instanceof Level _levelxxxxxxxx) {
               if (!_levelxxxxxxxx.isClientSide()) {
                  _levelxxxxxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelxxxxxxxx.playLocalSound(
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

         if (sourceentity instanceof ThegliterEntity) {
            entity.igniteForSeconds(5.0F);
         }

         if ((sourceentity instanceof LivingEntity _entGetArmorxx ? _entGetArmorxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
               == UndeadRevamp2ModItems.PRIMODIALARMOUR_HELMET.get()
            && (sourceentity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem()
               == UndeadRevamp2ModItems.PRIMODIALARMOUR_CHESTPLATE.get()
            && (sourceentity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem()
               == UndeadRevamp2ModItems.PRIMODIALARMOUR_BOOTS.get()
            && Math.random() < 0.3) {
            entity.igniteForSeconds(5.0F);
         }

         if (sourceentity instanceof TheimmortalEntity && sourceentity.getPersistentData().getDouble("decored") == 1.0) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.CURSEOFPHAMORE, 300, 0, false, true));
            }

            if (world instanceof Level _levelxxxxxxxxx) {
               if (!_levelxxxxxxxxx.isClientSide()) {
                  _levelxxxxxxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("particle.soul_escape")),
                     SoundSource.NEUTRAL,
                     5.0F,
                     1.0F
                  );
               } else {
                  _levelxxxxxxxxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("particle.soul_escape")),
                     SoundSource.NEUTRAL,
                     5.0F,
                     1.0F,
                     false
                  );
               }
            }
         }

         if (sourceentity instanceof TheimmortalEntity) {
            entity.makeStuckInBlock(Blocks.AIR.defaultBlockState(), new Vec3(0.25, 0.05, 0.25));
         }

         if (sourceentity instanceof CloggerEntity && sourceentity.getPersistentData().getDouble("eating") == 1.0) {
            if (world instanceof Level _levelxxxxxxxxxx) {
               if (!_levelxxxxxxxxxx.isClientSide()) {
                  _levelxxxxxxxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:cloogereatsu")),
                     SoundSource.NEUTRAL,
                     5.0F,
                     1.0F
                  );
               } else {
                  _levelxxxxxxxxxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:cloogereatsu")),
                     SoundSource.NEUTRAL,
                     5.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (!(entity instanceof LivingEntity _livEnt86 && _livEnt86.isBlocking())) {
               if (world instanceof Level _levelxxxxxxxxxxx) {
                  if (!_levelxxxxxxxxxxx.isClientSide()) {
                     _levelxxxxxxxxxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:cloogereatsu")),
                        SoundSource.NEUTRAL,
                        5.0F,
                        1.0F
                     );
                  } else {
                     _levelxxxxxxxxxxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:cloogereatsu")),
                        SoundSource.NEUTRAL,
                        5.0F,
                        1.0F,
                        false
                     );
                  }
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.GOOED, 120, 0, false, true));
               }

               sourceentity.getPersistentData().putDouble("eating", 0.0);
               entity.startRiding(sourceentity);
            }
         }

         if (sourceentity instanceof ThesomnolenceEntity) {
            if (world instanceof Level _levelxxxxxxxxxxxx) {
               if (!_levelxxxxxxxxxxxx.isClientSide()) {
                  _levelxxxxxxxxxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.bee.sting")),
                     SoundSource.NEUTRAL,
                     0.2F,
                     1.0F
                  );
               } else {
                  _levelxxxxxxxxxxxx.playLocalSound(
                     x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.bee.sting")), SoundSource.NEUTRAL, 0.2F, 1.0F, false
                  );
               }
            }

            if (sourceentity instanceof ThesomnolenceEntity) {
               ((ThesomnolenceEntity)sourceentity).setAnimation("bite");
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.TRYPANOSOMIASIS, 1000, 0, false, true));
            }

            if (Math.random() < 0.15 && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.SLEEPWALKING, 200, 0, false, true));
            }
         }

         if ((sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).getItem() == UndeadRevamp2ModItems.TOOTHMACE.get()
            && 3.0 < ((UndeadRevamp2ModVariables.PlayerVariables)sourceentity.getData(UndeadRevamp2ModVariables.PLAYER_VARIABLES)).fallinmylove
            && !(
               sourceentity instanceof Player _plrCldCheck99
                  && _plrCldCheck99.getCooldowns()
                     .isOnCooldown((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem())
            )) {
            if (sourceentity instanceof Player _playerx) {
               _playerx.getCooldowns()
                  .addCooldown((sourceentity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).getItem(), 200);
            }

            if (world instanceof Level _levelxxxxxxxxxxxxx) {
               if (!_levelxxxxxxxxxxxxx.isClientSide()) {
                  _levelxxxxxxxxxxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.firework_rocket.blast")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelxxxxxxxxxxxxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.firework_rocket.blast")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof Level _levelxxxxxxxxxxxxxx) {
               if (!_levelxxxxxxxxxxxxxx.isClientSide()) {
                  _levelxxxxxxxxxxxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:stonecrashes")),
                     SoundSource.NEUTRAL,
                     0.5F,
                     1.0F
                  );
               } else {
                  _levelxxxxxxxxxxxxxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:stonecrashes")),
                     SoundSource.NEUTRAL,
                     0.5F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelxxxxxxxxxxxxxxx) {
               _levelxxxxxxxxxxxxxxx.sendParticles(ParticleTypes.LAVA, x, y, z, 40, 5.0, 5.0, 5.0, 1.0);
            }

            Vec3 _center = new Vec3(sourceentity.getX(), sourceentity.getY(), sourceentity.getZ());

            for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.5), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (sourceentity != entityiteratorx && !(entityiteratorx instanceof LivingEntity _livEnt109 && _livEnt109.isBlocking())) {
                  if (entityiteratorx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20, 10, false, false));
                  }

                  entityiteratorx.igniteForSeconds(8.0F);
                  entityiteratorx.setDeltaMovement(
                     new Vec3(
                        Math.sin(Math.toRadians(entity.getX() + 180.0)) * 1.25 * -0.5,
                        (Math.sin(Math.toRadians(0.0F - entityiteratorx.getXRot())) + 0.5) * 0.12,
                        Math.cos(Math.toRadians(entity.getY())) * 1.25 * 0.5
                     )
                  );
               }
            }
         }
      }
   }
}
