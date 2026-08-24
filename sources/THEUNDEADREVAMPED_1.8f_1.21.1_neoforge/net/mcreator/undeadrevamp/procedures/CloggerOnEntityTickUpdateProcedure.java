package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.CloggerEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModAttributes;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CloggerOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity) {
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entityiterator) {
                  entity.getPersistentData().putDouble("inrange", 1.0);
               } else {
                  entity.getPersistentData().putDouble("inrange", 0.0);
               }
            }
         }

         if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity) {
            entity.getPersistentData().putDouble("honeyman_c", 1.0);
         }

         if ((
               entity instanceof LivingEntity _livingEntity10 && _livingEntity10.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD)
                  ? _livingEntity10.getAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD).getValue()
                  : 0.0
            )
            != 0.0) {
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(16.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) == entityiteratorx) {
                  entity.lookAt(Anchor.EYES, new Vec3(entityiteratorx.getX(), entityiteratorx.getY(), entityiteratorx.getZ()));
               }
            }
         }

         if ((
                  entity instanceof LivingEntity _livingEntity18 && _livingEntity18.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD)
                     ? _livingEntity18.getAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD).getValue()
                     : 0.0
               )
               != 0.0
            && entity.getPersistentData().getDouble("eating") == 0.0
            && entity.getPersistentData().getDouble("smashmode") < 1.0
            && entity.getPersistentData().getDouble("rushmode") == 1.0) {
            if (entity.getPersistentData().getDouble("honeyman_c") == 1.0
               && entity.getPersistentData().getDouble("honeyman_a") == 0.0
               && entity.getPersistentData().getDouble("honeyman_b") == 0.0
               && entity.getPersistentData().getDouble("tt") == 0.0
               && (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity
               && entity.isAlive()) {
               entity.getPersistentData().putDouble("honeyman_a", 0.0);
               entity.getPersistentData().putDouble("honeyman_b", 1.0);
               if (world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.pig.ambient")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.pig.ambient")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 30, false, false));
               }

               UndeadRevamp2Mod.queueServerWork(20, () -> entity.getPersistentData().putDouble("tt", 1.0));
               if (entity instanceof CloggerEntity) {
                  ((CloggerEntity)entity).setAnimation("rush");
               }
            }

            if (entity.getPersistentData().getDouble("activatehitbox") == 1.0) {
               Vec3 _center = new Vec3(x, y, z);

               for (Entity entityiteratorxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.5), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) == entityiteratorxx) {
                     if (entityiteratorxx instanceof LivingEntity _livEnt39 && _livEnt39.isBlocking()) {
                        if (world instanceof Level _levelx) {
                           if (!_levelx.isClientSide()) {
                              _levelx.playSound(
                                 null,
                                 BlockPos.containing(x, y, z),
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.block")),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F
                              );
                           } else {
                              _levelx.playLocalSound(
                                 x,
                                 y,
                                 z,
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.block")),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F,
                                 false
                              );
                           }
                        }

                        entityiteratorxx.hurt(
                           new DamageSource(world.holderOrThrow(DamageTypes.FALLING_ANVIL), entity),
                           (float)(
                              entity instanceof LivingEntity _livingEntity41 && _livingEntity41.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                                 ? _livingEntity41.getAttribute(Attributes.ATTACK_DAMAGE).getValue()
                                 : 0.0
                           )
                        );
                        entity.setDeltaMovement(
                           new Vec3(
                              Math.sin(Math.toRadians(entityiteratorxx.getYRot() + 180.0F)) * 1.25 * 1.1,
                              (Math.sin(Math.toRadians(0.0F - entityiteratorxx.getXRot())) + 0.5) * 1.0,
                              Math.cos(Math.toRadians(entityiteratorxx.getYRot())) * 1.25 * 1.3
                           )
                        );
                     } else {
                        if (world instanceof Level _levelxx) {
                           if (!_levelxx.isClientSide()) {
                              _levelxx.playSound(
                                 null,
                                 BlockPos.containing(x, y, z),
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.crit")),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F
                              );
                           } else {
                              _levelxx.playLocalSound(
                                 x,
                                 y,
                                 z,
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.crit")),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F,
                                 false
                              );
                           }
                        }

                        entityiteratorxx.hurt(
                           new DamageSource(world.holderOrThrow(DamageTypes.FALLING_ANVIL), entity),
                           (float)(
                              (
                                    entity instanceof LivingEntity _livingEntity49 && _livingEntity49.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                                       ? _livingEntity49.getAttribute(Attributes.ATTACK_DAMAGE).getValue()
                                       : 0.0
                                 )
                                 + 5.0
                           )
                        );
                        entityiteratorxx.setDeltaMovement(
                           new Vec3(
                              Math.sin(Math.toRadians(entityiteratorxx.getYRot() + 180.0F)) * 1.25 * -1.1,
                              (Math.sin(Math.toRadians(0.0F - entityiteratorxx.getXRot())) + 0.5) * 1.12,
                              Math.cos(Math.toRadians(entityiteratorxx.getYRot())) * 1.25 * -1.3
                           )
                        );
                     }
                  }
               }
            }

            if (entity.getPersistentData().getDouble("explo") == 1.0) {
               if ((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F)
                  <= (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F) / 100.0F * 30.0F) {
                  if (world instanceof Level _levelxxx && !_levelxxx.isClientSide()) {
                     _levelxxx.explode(null, x, y, z, 5.0F, ExplosionInteraction.MOB);
                  }
               } else if (world instanceof Level _levelxxx && !_levelxxx.isClientSide()) {
                  _levelxxx.explode(null, x, y, z, 3.0F, ExplosionInteraction.MOB);
               }

               entity.getPersistentData().putDouble("explo", 0.0);
            }

            if (entity.getPersistentData().getDouble("tt") == 1.0
               && entity.getPersistentData().getDouble("honeyman_b") == 1.0
               && entity.getPersistentData().getDouble("honeyman_a") == 0.0) {
               entity.getPersistentData().putDouble("tt", 0.0);
               entity.getPersistentData().putDouble("honeyman_a", 1.0);
            }

            if (entity.getPersistentData().getDouble("honeyman_a") == 1.0 && entity.getPersistentData().getDouble("honeyman_b") == 1.0) {
               entity.getPersistentData().putDouble("honeyman_a", 0.0);
               if (entity.isAlive()) {
                  if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) instanceof LivingEntity
                     && !(entity instanceof LivingEntity _livEnt74 && _livEnt74.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))
                     && ((CloggerEntity)entity).animationprocedure.equals("rush")) {
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

                     if (world instanceof Level _levelxxxx) {
                        if (!_levelxxxx.isClientSide()) {
                           _levelxxxx.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.pig.death")),
                              SoundSource.NEUTRAL,
                              2.0F,
                              -3.0F
                           );
                        } else {
                           _levelxxxx.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.pig.death")),
                              SoundSource.NEUTRAL,
                              2.0F,
                              -3.0F,
                              false
                           );
                        }
                     }

                     entity.setDeltaMovement(
                        new Vec3(
                           Math.sin(Math.toRadians(entity.getYRot() + 180.0F)) * 1.35 * 1.2,
                           (Math.sin(Math.toRadians(0.0F - entity.getXRot())) + 0.7) * 1.1,
                           Math.cos(Math.toRadians(entity.getYRot())) * 1.35 * 1.39
                        )
                     );
                     entity.getPersistentData().putDouble("activatehitbox", 1.0);
                  }

                  UndeadRevamp2Mod.queueServerWork(15, () -> entity.getPersistentData().putDouble("explo", 0.0));
                  UndeadRevamp2Mod.queueServerWork(
                     35,
                     () -> {
                        entity.getPersistentData().putDouble("honeyman_b", 0.0);
                        if ((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F)
                           <= (entity instanceof LivingEntity _livEntxx ? _livEntxx.getMaxHealth() : -1.0F) / 100.0F * 50.0F) {
                           if ((
                                 entity instanceof LivingEntity _livingEntity88
                                       && _livingEntity88.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD)
                                    ? _livingEntity88.getAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD).getValue()
                                    : 0.0
                              )
                              != 3.0) {
                              if (entity instanceof LivingEntity _livingEntity89
                                 && _livingEntity89.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD)) {
                                 _livingEntity89.getAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD).setBaseValue(0.0);
                              }
                           } else if ((entity instanceof LivingEntity _livEntxxx ? _livEntxxx.getHealth() : -1.0F)
                              <= (entity instanceof LivingEntity _livEntxx ? _livEntxx.getMaxHealth() : -1.0F) / 100.0F * 30.0F) {
                              entity.getPersistentData().putDouble("smashmode", 0.0);
                              entity.getPersistentData().putDouble("rushmode", 1.0);
                           } else {
                              if (entity.getPersistentData().getDouble("inrange") == 1.0) {
                                 if (entity instanceof LivingEntity _livEnt95 && _livEnt95.hasEffect(MobEffects.HUNGER)) {
                                    entity.getPersistentData().putDouble("smashmode", 1.0);
                                    entity.getPersistentData().putDouble("rushmode", 0.0);
                                 } else {
                                    entity.getPersistentData().putDouble("smashmode", 1.0);
                                    entity.getPersistentData().putDouble("rushmode", 0.0);
                                 }
                              }

                              if (entity.getPersistentData().getDouble("inrange") == 0.0) {
                                 if (Math.random() < 0.88) {
                                    if (!(entity instanceof LivingEntity _livEnt101 && _livEnt101.hasEffect(MobEffects.HUNGER))) {
                                       entity.getPersistentData().putDouble("smashmode", 0.0);
                                       entity.getPersistentData().putDouble("rushmode", 1.0);
                                    } else if (Math.random() < 0.5) {
                                       entity.getPersistentData().putDouble("smashmode", 0.0);
                                       entity.getPersistentData().putDouble("rushmode", 0.0);
                                       entity.getPersistentData().putDouble("eating", 1.0);
                                       UndeadRevamp2Mod.queueServerWork(100, () -> {
                                          entity.getPersistentData().putDouble("eating", 0.0);
                                          entity.getPersistentData().putDouble("rushmode", 1.0);
                                       });
                                    } else {
                                       entity.getPersistentData().putDouble("smashmode", 0.0);
                                       entity.getPersistentData().putDouble("rushmode", 1.0);
                                    }
                                 } else {
                                    entity.getPersistentData().putDouble("smashmode", 0.0);
                                    entity.getPersistentData().putDouble("rushmode", 0.0);
                                    entity.getPersistentData().putDouble("eating", 1.0);
                                    UndeadRevamp2Mod.queueServerWork(100, () -> {
                                       entity.getPersistentData().putDouble("eating", 0.0);
                                       entity.getPersistentData().putDouble("rushmode", 1.0);
                                    });
                                 }
                              }
                           }
                        } else {
                           if (entity.getPersistentData().getDouble("inrange") == 1.0) {
                              if (entity instanceof LivingEntity _livEnt119 && _livEnt119.hasEffect(MobEffects.HUNGER)) {
                                 entity.getPersistentData().putDouble("smashmode", 1.0);
                                 entity.getPersistentData().putDouble("rushmode", 0.0);
                              } else {
                                 entity.getPersistentData().putDouble("smashmode", 1.0);
                                 entity.getPersistentData().putDouble("rushmode", 0.0);
                              }
                           }

                           if (entity.getPersistentData().getDouble("inrange") == 0.0) {
                              if (Math.random() < 0.88) {
                                 if (!(entity instanceof LivingEntity _livEnt125 && _livEnt125.hasEffect(MobEffects.HUNGER))) {
                                    entity.getPersistentData().putDouble("smashmode", 0.0);
                                    entity.getPersistentData().putDouble("rushmode", 1.0);
                                 } else if (Math.random() < 0.5) {
                                    entity.getPersistentData().putDouble("smashmode", 0.0);
                                    entity.getPersistentData().putDouble("rushmode", 0.0);
                                    entity.getPersistentData().putDouble("eating", 1.0);
                                    UndeadRevamp2Mod.queueServerWork(100, () -> {
                                       entity.getPersistentData().putDouble("eating", 0.0);
                                       entity.getPersistentData().putDouble("rushmode", 1.0);
                                    });
                                 } else {
                                    entity.getPersistentData().putDouble("smashmode", 0.0);
                                    entity.getPersistentData().putDouble("rushmode", 1.0);
                                 }
                              } else {
                                 entity.getPersistentData().putDouble("smashmode", 0.0);
                                 entity.getPersistentData().putDouble("rushmode", 0.0);
                                 entity.getPersistentData().putDouble("eating", 1.0);
                                 UndeadRevamp2Mod.queueServerWork(100, () -> {
                                    entity.getPersistentData().putDouble("eating", 0.0);
                                    entity.getPersistentData().putDouble("rushmode", 1.0);
                                 });
                              }
                           }
                        }
                     }
                  );
                  UndeadRevamp2Mod.queueServerWork(7, () -> {
                     entity.getPersistentData().putDouble("activatehitbox", 0.0);
                     if (Math.random() < 0.25) {
                        entity.getPersistentData().putDouble("explo", 1.0);
                     }
                  });
               }
            }
         }

         if ((
                  entity instanceof LivingEntity _livingEntity146
                        && _livingEntity146.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD)
                     ? _livingEntity146.getAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD).getValue()
                     : 0.0
               )
               != 0.0
            && entity.getPersistentData().getDouble("eating") == 0.0
            && entity.getPersistentData().getDouble("smashmode") == 1.0
            && entity.getPersistentData().getDouble("rushmode") != 1.0
            && entity.getPersistentData().getDouble("honeyman_c") == 1.0
            && (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) instanceof LivingEntity
            && entity.getPersistentData().getDouble("passorsmash") == 0.0
            && entity.getPersistentData().getDouble("pastat") == 1.0) {
            if (!(entity instanceof LivingEntity _livEnt155 && _livEnt155.hasEffect(UndeadRevamp2ModMobEffects.ANIMATIONTEST))) {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.ANIMATIONTEST, 100, 0, false, false));
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 75, 30, false, false));
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 75, 3, false, false));
               }

               entity.getPersistentData().putDouble("passorsmash", 1.0);
               if (entity.isAlive() && entity instanceof CloggerEntity) {
                  ((CloggerEntity)entity).setAnimation("smash");
               }

               if (entity instanceof LivingEntity _livEnt162 && _livEnt162.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS)) {
                  entity.getPersistentData().putDouble("passorsmash", 1.0);
               }
            }

            if (entity.getPersistentData().getDouble("passorsmash") == 1.0) {
               UndeadRevamp2Mod.queueServerWork(2, () -> {
                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.BROKENTANK, 3, 0, false, false));
                  }
               });
            }

            if (entity.getPersistentData().getDouble("passorsmash") == 1.0
               && !(entity instanceof LivingEntity _livEnt168 && _livEnt168.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))) {
               UndeadRevamp2Mod.queueServerWork(
                  10,
                  () -> {
                     if (world instanceof Level _levelxxxxx) {
                        if (!_levelxxxxx.isClientSide()) {
                           _levelxxxxx.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.pig.death")),
                              SoundSource.NEUTRAL,
                              2.0F,
                              -3.0F
                           );
                        } else {
                           _levelxxxxx.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.pig.death")),
                              SoundSource.NEUTRAL,
                              2.0F,
                              -3.0F,
                              false
                           );
                        }
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
                  }
               );
               UndeadRevamp2Mod.queueServerWork(
                  41,
                  () -> {
                     if (entity.isAlive()
                        && entity instanceof LivingEntity _livEnt173
                        && _livEnt173.hasEffect(UndeadRevamp2ModMobEffects.ANIMATIONTEST)
                        && entity.getPersistentData().getDouble("passorsmash") == 1.0
                        && entity.getPersistentData().getDouble("pastat") == 1.0
                        && ((CloggerEntity)entity).animationprocedure.equals("smash")) {
                        if ((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F)
                           <= (entity instanceof LivingEntity _livEntxx ? _livEntxx.getMaxHealth() : -1.0F) / 100.0F * 50.0F) {
                           if (world instanceof ServerLevel _levelxxxxx) {
                              _levelxxxxx.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 25, 12.0, 1.0, 10.0, 1.0);
                           }

                           if (!(entity instanceof LivingEntity _livEnt180 && _livEnt180.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))) {
                              if (world instanceof Level _levelxxxxx) {
                                 if (!_levelxxxxx.isClientSide()) {
                                    _levelxxxxx.playSound(
                                       null,
                                       BlockPos.containing(x, y, z),
                                       (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.explode")),
                                       SoundSource.NEUTRAL,
                                       7.0F,
                                       8.0F
                                    );
                                 } else {
                                    _levelxxxxx.playLocalSound(
                                       x,
                                       y,
                                       z,
                                       (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.explode")),
                                       SoundSource.NEUTRAL,
                                       7.0F,
                                       8.0F,
                                       false
                                    );
                                 }
                              }

                              Vec3 _center = new Vec3(x, y, z);

                              for (Entity entityiteratorxxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6.5), e -> true)
                                 .stream()
                                 .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                                 .toList()) {
                                 if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entityiteratorxxx
                                    || (entityiteratorxxx instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null) == entity) {
                                    if (entityiteratorxxx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                                       _entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 8, 25, false, false));
                                    }

                                    entityiteratorxxx.hurt(
                                       new DamageSource(world.holderOrThrow(DamageTypes.FALLING_ANVIL), entity),
                                       (float)(
                                          (
                                                entity instanceof LivingEntity _livingEntity187
                                                      && _livingEntity187.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                                                   ? _livingEntity187.getAttribute(Attributes.ATTACK_DAMAGE).getValue()
                                                   : 0.0
                                             )
                                             + 4.0
                                       )
                                    );
                                 }
                              }

                              if (world instanceof Level _levelx) {
                                 if (!_levelx.isClientSide()) {
                                    _levelx.playSound(
                                       null,
                                       BlockPos.containing(x, y, z),
                                       (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                                       SoundSource.NEUTRAL,
                                       1.0F,
                                       -3.0F
                                    );
                                 } else {
                                    _levelx.playLocalSound(
                                       x,
                                       y,
                                       z,
                                       (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                                       SoundSource.NEUTRAL,
                                       1.0F,
                                       -3.0F,
                                       false
                                    );
                                 }
                              }

                              entity.getPersistentData().putDouble("pastat", 0.0);
                           }
                        } else {
                           if (world instanceof ServerLevel _levelxx) {
                              _levelxx.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 20, 10.0, 1.0, 10.0, 1.0);
                           }

                           if (!(entity instanceof LivingEntity _livEnt194 && _livEnt194.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))) {
                              if (world instanceof Level _levelxx) {
                                 if (!_levelxx.isClientSide()) {
                                    _levelxx.playSound(
                                       null,
                                       BlockPos.containing(x, y, z),
                                       (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.explode")),
                                       SoundSource.NEUTRAL,
                                       2.0F,
                                       1.0F
                                    );
                                 } else {
                                    _levelxx.playLocalSound(
                                       x,
                                       y,
                                       z,
                                       (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.explode")),
                                       SoundSource.NEUTRAL,
                                       2.0F,
                                       1.0F,
                                       false
                                    );
                                 }
                              }

                              Vec3 _center = new Vec3(x, y, z);

                              for (Entity entityiteratorxxxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5.0), e -> true)
                                 .stream()
                                 .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                                 .toList()) {
                                 if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entityiteratorxxxx
                                    || (entityiteratorxxxx instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null) == entity) {
                                    if (entityiteratorxxxx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                                       _entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 5, 25, false, false));
                                    }

                                    entityiteratorxxxx.hurt(
                                       new DamageSource(world.holderOrThrow(DamageTypes.FALLING_ANVIL), entity),
                                       (float)(
                                          (
                                                entity instanceof LivingEntity _livingEntity201
                                                      && _livingEntity201.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                                                   ? _livingEntity201.getAttribute(Attributes.ATTACK_DAMAGE).getValue()
                                                   : 0.0
                                             )
                                             - 4.0
                                       )
                                    );
                                 }
                              }

                              if (world instanceof Level _levelxxx) {
                                 if (!_levelxxx.isClientSide()) {
                                    _levelxxx.playSound(
                                       null,
                                       BlockPos.containing(x, y, z),
                                       (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                                       SoundSource.NEUTRAL,
                                       1.0F,
                                       -3.0F
                                    );
                                 } else {
                                    _levelxxx.playLocalSound(
                                       x,
                                       y,
                                       z,
                                       (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                                       SoundSource.NEUTRAL,
                                       1.0F,
                                       -3.0F,
                                       false
                                    );
                                 }
                              }

                              entity.getPersistentData().putDouble("pastat", 0.0);
                           }
                        }
                     }
                  }
               );
               UndeadRevamp2Mod.queueServerWork(
                  60,
                  () -> {
                     if (!(entity instanceof LivingEntity _livEnt208 && _livEnt208.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))
                        && entity.getPersistentData().getDouble("pastat") == 0.0
                        && entity.getPersistentData().getDouble("passorsmash") == 1.0) {
                        UndeadRevamp2Mod.queueServerWork(
                           15,
                           () -> {
                              if ((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F)
                                 <= (entity instanceof LivingEntity _livEntxx ? _livEntxx.getMaxHealth() : -1.0F) / 100.0F * 50.0F) {
                                 if ((
                                       entity instanceof LivingEntity _livingEntity213
                                             && _livingEntity213.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD)
                                          ? _livingEntity213.getAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD).getValue()
                                          : 0.0
                                    )
                                    != 3.0) {
                                    if ((
                                             entity instanceof LivingEntity _livingEntity214
                                                   && _livingEntity214.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD)
                                                ? _livingEntity214.getAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD).getValue()
                                                : 0.0
                                          )
                                          != 0.0
                                       && entity instanceof LivingEntity _livingEntity215
                                       && _livingEntity215.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD)) {
                                       _livingEntity215.getAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD).setBaseValue(0.0);
                                    }
                                 } else if (Math.random() < 0.75) {
                                    if (entity.getPersistentData().getDouble("inrange") == 0.0) {
                                       if (Math.random() < 0.5) {
                                          if (world instanceof Level _levelxxxxx) {
                                             if (!_levelxxxxx.isClientSide()) {
                                                _levelxxxxx.playSound(
                                                   null,
                                                   BlockPos.containing(x, y, z),
                                                   (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.horse.breathe")),
                                                   SoundSource.NEUTRAL,
                                                   1.0F,
                                                   -5.0F
                                                );
                                             } else {
                                                _levelxxxxx.playLocalSound(
                                                   x,
                                                   y,
                                                   z,
                                                   (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.horse.breathe")),
                                                   SoundSource.NEUTRAL,
                                                   1.0F,
                                                   -5.0F,
                                                   false
                                                );
                                             }
                                          }

                                          entity.getPersistentData().putDouble("smashmode", 1.0);
                                          entity.getPersistentData().putDouble("rushmode", 0.0);
                                       } else {
                                          entity.getPersistentData().putDouble("smashmode", 0.0);
                                          entity.getPersistentData().putDouble("rushmode", 1.0);
                                       }
                                    } else {
                                       entity.getPersistentData().putDouble("smashmode", 1.0);
                                       entity.getPersistentData().putDouble("rushmode", 0.0);
                                    }

                                    entity.getPersistentData().putDouble("pastat", 1.0);
                                    entity.getPersistentData().putDouble("passorsmash", 0.0);
                                 } else {
                                    entity.getPersistentData().putDouble("pastat", 1.0);
                                    entity.getPersistentData().putDouble("passorsmash", 0.0);
                                    if (entity.getPersistentData().getDouble("inrange") == 0.0) {
                                       entity.getPersistentData().putDouble("smashmode", 0.0);
                                       entity.getPersistentData().putDouble("rushmode", 0.0);
                                       entity.getPersistentData().putDouble("eating", 1.0);
                                       UndeadRevamp2Mod.queueServerWork(100, () -> {
                                          entity.getPersistentData().putDouble("eating", 0.0);
                                          entity.getPersistentData().putDouble("rushmode", 1.0);
                                       });
                                    } else {
                                       entity.getPersistentData().putDouble("smashmode", 0.0);
                                       entity.getPersistentData().putDouble("rushmode", 1.0);
                                    }
                                 }
                              } else if (Math.random() < 0.75) {
                                 if (entity.getPersistentData().getDouble("inrange") == 0.0) {
                                    entity.getPersistentData().putDouble("smashmode", 0.0);
                                    entity.getPersistentData().putDouble("rushmode", 1.0);
                                 } else {
                                    entity.getPersistentData().putDouble("smashmode", 1.0);
                                    entity.getPersistentData().putDouble("rushmode", 0.0);
                                 }

                                 entity.getPersistentData().putDouble("pastat", 1.0);
                                 entity.getPersistentData().putDouble("passorsmash", 0.0);
                              } else {
                                 entity.getPersistentData().putDouble("pastat", 1.0);
                                 entity.getPersistentData().putDouble("passorsmash", 0.0);
                                 if (entity.getPersistentData().getDouble("inrange") == 0.0) {
                                    entity.getPersistentData().putDouble("smashmode", 0.0);
                                    entity.getPersistentData().putDouble("rushmode", 0.0);
                                    entity.getPersistentData().putDouble("eating", 1.0);
                                    UndeadRevamp2Mod.queueServerWork(100, () -> {
                                       entity.getPersistentData().putDouble("eating", 0.0);
                                       entity.getPersistentData().putDouble("rushmode", 1.0);
                                    });
                                 } else {
                                    entity.getPersistentData().putDouble("smashmode", 0.0);
                                    entity.getPersistentData().putDouble("rushmode", 1.0);
                                 }
                              }
                           }
                        );
                     }
                  }
               );
            }
         }

         if ((entity instanceof LivingEntity _livEntxxx ? _livEntxxx.getHealth() : -1.0F)
               <= (entity instanceof LivingEntity _livEntxx ? _livEntxx.getMaxHealth() : -1.0F) / 100.0F * 30.0F
            && entity instanceof CloggerEntity animatable) {
            animatable.setTexture("enraged_clogger");
         }

         if (entity.getPersistentData().getDouble("wait") == 0.0
            && !entity.getPersistentData().getBoolean("noatk")
            && entity.getPersistentData().getDouble("honeyman_c") == 0.0) {
            entity.getPersistentData().putDouble("wait", 1.0);
            UndeadRevamp2Mod.queueServerWork(40, () -> {
               if (!entity.level().isClientSide()) {
                  entity.discard();
               }
            });
            if (entity instanceof CloggerEntity) {
               ((CloggerEntity)entity).setAnimation("digging");
            }
         }

         if ((
                  entity instanceof LivingEntity _livingEntity267
                        && _livingEntity267.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD)
                     ? _livingEntity267.getAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD).getValue()
                     : 0.0
               )
               == 0.0
            && !entity.getPersistentData().getBoolean("noatk")) {
            entity.getPersistentData().putBoolean("noatk", true);
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5000, 160, false, false));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 5000, 0, false, false));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 5000, 0, false, false));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 5000, 0, false, false));
            }

            UndeadRevamp2Mod.queueServerWork(3, () -> {
               if (entity instanceof CloggerEntity) {
                  ((CloggerEntity)entity).setAnimation("roar");
               }
            });
            UndeadRevamp2Mod.queueServerWork(
               68,
               () -> {
                  if (world instanceof ServerLevel _levelxxxxx) {
                     _levelxxxxx.sendParticles(ParticleTypes.ANGRY_VILLAGER, x, y, z, 10, 5.0, 5.0, 5.0, 1.0);
                  }

                  if (world instanceof Level _levelxxxxx) {
                     if (!_levelxxxxx.isClientSide()) {
                        _levelxxxxx.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:clogger_roaring")),
                           SoundSource.NEUTRAL,
                           50.0F,
                           1.0F
                        );
                     } else {
                        _levelxxxxx.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:clogger_roaring")),
                           SoundSource.NEUTRAL,
                           50.0F,
                           1.0F,
                           false
                        );
                     }
                  }
               }
            );
            UndeadRevamp2Mod.queueServerWork(150, () -> {
               entity.getPersistentData().putDouble("smashmode", 0.0);
               entity.getPersistentData().putDouble("rushmode", 1.0);
               entity.getPersistentData().putDouble("honeyman_a", 0.0);
               entity.getPersistentData().putDouble("eating", 0.0);
               entity.getPersistentData().putDouble("honeyman_b", 0.0);
               entity.getPersistentData().putDouble("honeyman_c", 0.0);
               entity.getPersistentData().putDouble("activatehitbox", 0.0);
               entity.getPersistentData().putDouble("explo", 0.0);
               entity.getPersistentData().putDouble("tt", 0.0);
               entity.getPersistentData().putDouble("passorsmash", 0.0);
               entity.getPersistentData().putDouble("pastat", 1.0);
               entity.getPersistentData().putDouble("inrange", 3.0);
            });
            UndeadRevamp2Mod.queueServerWork(
               155,
               () -> {
                  entity.getPersistentData().putDouble("wait", 0.0);
                  entity.getPersistentData().putBoolean("noatk", false);
                  if (entity instanceof LivingEntity _livingEntity294
                     && _livingEntity294.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD)) {
                     _livingEntity294.getAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD).setBaseValue(3.0);
                  }
               }
            );
         }

         if (!world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 100.0, 100.0, 100.0), e -> true).isEmpty()) {
            if ((
                  entity instanceof LivingEntity _livingEntity297 && _livingEntity297.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.BOSSLOADER)
                     ? _livingEntity297.getAttribute(UndeadRevamp2ModAttributes.BOSSLOADER).getValue()
                     : 0.0
               )
               == 1.0) {
               if (entity instanceof LivingEntity _livingEntity298 && _livingEntity298.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.BOSSLOADER)) {
                  _livingEntity298.getAttribute(UndeadRevamp2ModAttributes.BOSSLOADER).setBaseValue(0.0);
               }

               if ((
                     entity instanceof LivingEntity _livingEntity299
                           && _livingEntity299.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD)
                        ? _livingEntity299.getAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD).getValue()
                        : 0.0
                  )
                  == 1.0) {
                  entity.getPersistentData().putDouble("smashmode", 0.0);
                  entity.getPersistentData().putDouble("rushmode", 1.0);
                  entity.getPersistentData().putDouble("honeyman_a", 0.0);
                  entity.getPersistentData().putDouble("eating", 0.0);
                  entity.getPersistentData().putDouble("honeyman_b", 0.0);
                  entity.getPersistentData().putDouble("honeyman_c", 0.0);
                  entity.getPersistentData().putDouble("activatehitbox", 0.0);
                  entity.getPersistentData().putDouble("explo", 0.0);
                  entity.getPersistentData().putDouble("tt", 0.0);
                  entity.getPersistentData().putDouble("passorsmash", 0.0);
                  entity.getPersistentData().putDouble("pastat", 1.0);
                  entity.getPersistentData().putDouble("inrange", 3.0);
                  entity.getPersistentData().putDouble("wait", 0.0);
                  entity.getPersistentData().putBoolean("noatk", false);
               } else if ((
                     entity instanceof LivingEntity _livingEntity314
                           && _livingEntity314.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD)
                        ? _livingEntity314.getAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD).getValue()
                        : 0.0
                  )
                  == 3.0) {
                  entity.getPersistentData().putDouble("smashmode", 0.0);
                  entity.getPersistentData().putDouble("rushmode", 0.0);
                  entity.getPersistentData().putDouble("honeyman_a", 0.0);
                  entity.getPersistentData().putDouble("eating", 0.0);
                  entity.getPersistentData().putDouble("honeyman_b", 0.0);
                  entity.getPersistentData().putDouble("honeyman_c", 0.0);
                  entity.getPersistentData().putDouble("activatehitbox", 0.0);
                  entity.getPersistentData().putDouble("explo", 0.0);
                  entity.getPersistentData().putDouble("tt", 0.0);
                  entity.getPersistentData().putDouble("passorsmash", 0.0);
                  entity.getPersistentData().putDouble("pastat", 1.0);
                  entity.getPersistentData().putDouble("inrange", 3.0);
                  entity.getPersistentData().putDouble("wait", 0.0);
                  entity.getPersistentData().putBoolean("noatk", false);
               } else {
                  entity.getPersistentData().putDouble("smashmode", 0.0);
                  entity.getPersistentData().putDouble("rushmode", 0.0);
                  entity.getPersistentData().putDouble("honeyman_a", 0.0);
                  entity.getPersistentData().putDouble("eating", 0.0);
                  entity.getPersistentData().putDouble("honeyman_b", 0.0);
                  entity.getPersistentData().putDouble("honeyman_c", 0.0);
                  entity.getPersistentData().putDouble("activatehitbox", 0.0);
                  entity.getPersistentData().putDouble("explo", 0.0);
                  entity.getPersistentData().putDouble("tt", 0.0);
                  entity.getPersistentData().putDouble("passorsmash", 0.0);
                  entity.getPersistentData().putDouble("pastat", 1.0);
                  entity.getPersistentData().putDouble("inrange", 3.0);
                  entity.getPersistentData().putDouble("wait", 0.0);
                  entity.getPersistentData().putBoolean("noatk", false);
               }
            }
         } else if (entity instanceof LivingEntity _livingEntity343 && _livingEntity343.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.BOSSLOADER)) {
            _livingEntity343.getAttribute(UndeadRevamp2ModAttributes.BOSSLOADER).setBaseValue(1.0);
         }
      }
   }
}
