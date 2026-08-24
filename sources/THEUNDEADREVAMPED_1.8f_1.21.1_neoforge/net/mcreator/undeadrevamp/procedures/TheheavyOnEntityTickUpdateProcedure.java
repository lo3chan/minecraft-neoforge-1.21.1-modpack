package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.BoulderthrowProjectileEntity;
import net.mcreator.undeadrevamp.entity.CrackleballEntity;
import net.mcreator.undeadrevamp.entity.SlavemanEntity;
import net.mcreator.undeadrevamp.entity.TheheavyEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModAttributes;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModGameRules;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TheheavyOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity.isAlive()) {
            if (entity.getPersistentData().getDouble("roar") == 1.0) {
               if (!(entity instanceof LivingEntity _livEnt2 && _livEnt2.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))
                  && (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity
                  && entity.getPersistentData().getDouble("fleepick") == 0.0
                  && entity.getPersistentData().getDouble("throw") == 0.0
                  && entity.getPersistentData().getDouble("BLOCKIN") == 0.0
                  && entity.getPersistentData().getDouble("passorsmash") == 0.0
                  && entity.getPersistentData().getDouble("pastat") == 1.0
                  && !(entity instanceof LivingEntity _livEnt10 && _livEnt10.hasEffect(UndeadRevamp2ModMobEffects.ANIMATIONTEST))) {
                  Vec3 _center = new Vec3(entity.getX(), entity.getY(), entity.getZ());

                  for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(1.5), e -> true)
                     .stream()
                     .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                     .toList()) {
                     if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entityiterator) {
                        if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                           _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 18, 30, false, false));
                        }

                        entity.getPersistentData().putDouble("passorsmash", 1.0);
                        if (Math.random() < 0.2) {
                           if (entity instanceof TheheavyEntity) {
                              ((TheheavyEntity)entity).setAnimation("smask2");
                           }
                        } else if (Math.random() < 0.2) {
                           if (entity instanceof TheheavyEntity) {
                              ((TheheavyEntity)entity).setAnimation("smask3");
                           }
                        } else if (entity instanceof TheheavyEntity) {
                           ((TheheavyEntity)entity).setAnimation("smask");
                        }
                     }

                     if (entity instanceof LivingEntity _livEnt21 && _livEnt21.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS)) {
                        entity.getPersistentData().putDouble("passorsmash", 1.0);
                     }
                  }

                  if (entity.getPersistentData().getDouble("passorsmash") == 1.0) {
                     UndeadRevamp2Mod.queueServerWork(2, () -> {
                        if (entity instanceof LivingEntity _entityx && !_entityx.level().isClientSide()) {
                           _entityx.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.BROKENTANK, 3, 0, false, false));
                        }
                     });
                  }

                  if (entity.getPersistentData().getDouble("passorsmash") == 1.0
                     && !(entity instanceof LivingEntity _livEnt28 && _livEnt28.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))) {
                     if (Math.random() < 0.1 && world instanceof Level _level) {
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

                     UndeadRevamp2Mod.queueServerWork(
                        4,
                        () -> {
                           if (entity.isAlive()
                              && entity.getPersistentData().getDouble("passorsmash") == 1.0
                              && entity.getPersistentData().getDouble("pastat") == 1.0
                              && !(entity instanceof LivingEntity _livEnt33 && _livEnt33.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))) {
                              Vec3 _centerx = new Vec3(entity.getX(), entity.getY(), entity.getZ());

                              for (Entity entityiteratorxx : world.getEntitiesOfClass(Entity.class, new AABB(_centerx, _centerx).inflate(1.5), e -> true)
                                 .stream()
                                 .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                                 .toList()) {
                                 if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entityiteratorxx
                                    || (entityiteratorxx instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entity) {
                                    if (entity.getPersistentData().getDouble("stone") == 0.0) {
                                       if (Math.random() < 0.15 && world instanceof Level _levelx) {
                                          if (!_levelx.isClientSide()) {
                                             _levelx.playSound(
                                                null,
                                                BlockPos.containing(x, y, z),
                                                (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:heavyattack")),
                                                SoundSource.NEUTRAL,
                                                5.0F,
                                                1.0F
                                             );
                                          } else {
                                             _levelx.playLocalSound(
                                                x,
                                                y,
                                                z,
                                                (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:heavyattack")),
                                                SoundSource.NEUTRAL,
                                                5.0F,
                                                1.0F,
                                                false
                                             );
                                          }
                                       }

                                       if (entityiteratorxx instanceof LivingEntity _livEnt43 && _livEnt43.isBlocking()) {
                                          if (entityiteratorxx instanceof LivingEntity _entityxx && !_entityxx.level().isClientSide()) {
                                             _entityxx.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 3, 15, false, false));
                                          }

                                          if (world instanceof Level _levelx) {
                                             if (!_levelx.isClientSide()) {
                                                _levelx.playSound(
                                                   null,
                                                   BlockPos.containing(x, y, z),
                                                   (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.block")),
                                                   SoundSource.NEUTRAL,
                                                   1.0F,
                                                   -3.0F
                                                );
                                             } else {
                                                _levelx.playLocalSound(
                                                   x,
                                                   y,
                                                   z,
                                                   (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.block")),
                                                   SoundSource.NEUTRAL,
                                                   1.0F,
                                                   -3.0F,
                                                   false
                                                );
                                             }
                                          }
                                       } else {
                                          if (world instanceof Level _levelxx) {
                                             if (!_levelxx.isClientSide()) {
                                                _levelxx.playSound(
                                                   null,
                                                   BlockPos.containing(x, y, z),
                                                   (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.crit")),
                                                   SoundSource.NEUTRAL,
                                                   1.0F,
                                                   -3.0F
                                                );
                                             } else {
                                                _levelxx.playLocalSound(
                                                   x,
                                                   y,
                                                   z,
                                                   (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.crit")),
                                                   SoundSource.NEUTRAL,
                                                   1.0F,
                                                   -3.0F,
                                                   false
                                                );
                                             }
                                          }

                                          if (entityiteratorxx instanceof LivingEntity _entityxxx && !_entityxxx.level().isClientSide()) {
                                             _entityxxx.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 5, 15, false, false));
                                          }

                                          entityiteratorxx.hurt(
                                             new DamageSource(world.holderOrThrow(DamageTypes.FALLING_ANVIL), entity),
                                             (float)(
                                                entity instanceof LivingEntity _livingEntity46
                                                      && _livingEntity46.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                                                   ? _livingEntity46.getAttribute(Attributes.ATTACK_DAMAGE).getValue()
                                                   : 0.0
                                             )
                                          );
                                       }
                                    } else {
                                       if (Math.random() < 0.15 && world instanceof Level _levelxxx) {
                                          if (!_levelxxx.isClientSide()) {
                                             _levelxxx.playSound(
                                                null,
                                                BlockPos.containing(x, y, z),
                                                (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:heavyattack")),
                                                SoundSource.NEUTRAL,
                                                5.0F,
                                                1.0F
                                             );
                                          } else {
                                             _levelxxx.playLocalSound(
                                                x,
                                                y,
                                                z,
                                                (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:heavyattack")),
                                                SoundSource.NEUTRAL,
                                                5.0F,
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
                                                (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.crit")),
                                                SoundSource.NEUTRAL,
                                                1.0F,
                                                -3.0F
                                             );
                                          } else {
                                             _levelxxxx.playLocalSound(
                                                x,
                                                y,
                                                z,
                                                (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.crit")),
                                                SoundSource.NEUTRAL,
                                                1.0F,
                                                -3.0F,
                                                false
                                             );
                                          }
                                       }

                                       if (entityiteratorxx instanceof LivingEntity _entityx && !_entityx.level().isClientSide()) {
                                          _entityx.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 5, 15, false, false));
                                       }

                                       entityiteratorxx.hurt(
                                          new DamageSource(world.holderOrThrow(DamageTypes.FALLING_ANVIL), entity),
                                          (float)(
                                             (
                                                   entity instanceof LivingEntity _livingEntity54
                                                         && _livingEntity54.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                                                      ? _livingEntity54.getAttribute(Attributes.ATTACK_DAMAGE).getValue()
                                                      : 0.0
                                                )
                                                + 3.0
                                          )
                                       );
                                    }
                                 }
                              }

                              if (entity.getPersistentData().getDouble("stone") > 1.0) {
                                 entity.getPersistentData().putDouble("stone", entity.getPersistentData().getDouble("stone") - 1.0);
                              } else if (entity.getPersistentData().getDouble("stone") == 1.0) {
                                 entity.getPersistentData().putDouble("stone", entity.getPersistentData().getDouble("stone") - 1.0);
                                 if (world instanceof Level _levelxxxxx) {
                                    if (!_levelxxxxx.isClientSide()) {
                                       _levelxxxxx.playSound(
                                          null,
                                          BlockPos.containing(x, y, z),
                                          (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.firework_rocket.blast")),
                                          SoundSource.NEUTRAL,
                                          1.0F,
                                          1.0F
                                       );
                                    } else {
                                       _levelxxxxx.playLocalSound(
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

                                 if (world instanceof Level _levelxxxxxx) {
                                    if (!_levelxxxxxx.isClientSide()) {
                                       _levelxxxxxx.playSound(
                                          null,
                                          BlockPos.containing(x, y, z),
                                          (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:stonecrashes")),
                                          SoundSource.NEUTRAL,
                                          0.5F,
                                          1.0F
                                       );
                                    } else {
                                       _levelxxxxxx.playLocalSound(
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

                                 _centerx = new Vec3(entity.getX(), entity.getY(), entity.getZ());

                                 for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_centerx, _centerx).inflate(2.5), e -> true)
                                    .stream()
                                    .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                                    .toList()) {
                                    if ((
                                          (entity instanceof Mob _mobEntxxx ? _mobEntxxx.getTarget() : null) == entityiteratorx
                                             || (entityiteratorx instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null) == entity
                                       )
                                       && !(entityiteratorx instanceof LivingEntity _livEnt73 && _livEnt73.isBlocking())) {
                                       entityiteratorx.igniteForSeconds(8.0F);
                                    }
                                 }

                                 if (entity.getPersistentData().getDouble("capped") == 1.0) {
                                    if (entity instanceof TheheavyEntity animatablexx) {
                                       animatablexx.setTexture("heavydecap");
                                    }
                                 } else if (entity instanceof TheheavyEntity animatablex) {
                                    animatablex.setTexture("heavy");
                                 }

                                 if (world instanceof ServerLevel _levelxxxxxxx) {
                                    _levelxxxxxxx.sendParticles(ParticleTypes.LAVA, x, y, z, 25, 1.0, 1.0, 1.0, 1.0);
                                 }
                              }

                              if (world instanceof Level _levelxxxxxxx) {
                                 if (!_levelxxxxxxx.isClientSide()) {
                                    _levelxxxxxxx.playSound(
                                       null,
                                       BlockPos.containing(x, y, z),
                                       (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                                       SoundSource.NEUTRAL,
                                       1.0F,
                                       -3.0F
                                    );
                                 } else {
                                    _levelxxxxxxx.playLocalSound(
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
                     );
                     UndeadRevamp2Mod.queueServerWork(
                        15,
                        () -> {
                           if (!(entity instanceof LivingEntity _livEnt83 && _livEnt83.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))
                              && entity.getPersistentData().getDouble("pastat") == 0.0
                              && entity.getPersistentData().getDouble("passorsmash") == 1.0) {
                              UndeadRevamp2Mod.queueServerWork(
                                 (int)(
                                    (
                                          entity instanceof LivingEntity _livingEntity86
                                                && _livingEntity86.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.CHEROATTACKSPEED)
                                             ? _livingEntity86.getAttribute(UndeadRevamp2ModAttributes.CHEROATTACKSPEED).getValue()
                                             : 0.0
                                       )
                                       + 3.0
                                 ),
                                 () -> {
                                    entity.getPersistentData().putDouble("pastat", 1.0);
                                    entity.getPersistentData().putDouble("passorsmash", 0.0);
                                    if (Math.random() < 0.25
                                       && entity.getPersistentData().getDouble("stone") == 0.0
                                       && entity.getPersistentData().getDouble("roar") == 1.0
                                       && entity.getPersistentData().getDouble("BLOCKIN") == 0.0) {
                                       entity.getPersistentData().putDouble("fleepick", 1.0);
                                       if (entity instanceof LivingEntity _entityxx && !_entityxx.level().isClientSide()) {
                                          _entityxx.addEffect(new MobEffectInstance(MobEffects.LUCK, 60, 0, false, false));
                                       }
                                    }

                                    if (Math.random() < 0.2
                                       && entity.getPersistentData().getDouble("stone") > 0.0
                                       && entity.getPersistentData().getDouble("throw") == 0.0) {
                                       entity.getPersistentData().putDouble("throw", 1.0);
                                       if (entity instanceof LivingEntity _entityx && !_entityx.level().isClientSide()) {
                                          _entityx.addEffect(new MobEffectInstance(MobEffects.LUCK, 60, 0, false, false));
                                       }
                                    }
                                 }
                              );
                           }
                        }
                     );
                  }
               }

               if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity) {
                  Vec3 _center = new Vec3(x, y, z);

                  for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(14.0), e -> true)
                     .stream()
                     .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                     .toList()) {
                     if (entityiterator instanceof SlavemanEntity
                        && entityiterator instanceof Mob _entity
                        && (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) instanceof LivingEntity _ent) {
                        _entity.setTarget(_ent);
                     }
                  }
               }
            }

            if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity
               && entity.getPersistentData().getDouble("r_range") == 1.0
               && entity.getPersistentData().getDouble("roar") == 0.0
               && entity.getPersistentData().getDouble("BLOCKIN") == 0.0) {
               entity.lookAt(
                  Anchor.EYES,
                  new Vec3(
                     (entity instanceof Mob _mobEntxxx ? _mobEntxxx.getTarget() : null).getX(),
                     (entity instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null).getY(),
                     (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null).getZ()
                  )
               );
               if (entity.getPersistentData().getDouble("capped") == 1.0) {
                  if (entity instanceof TheheavyEntity) {
                     ((TheheavyEntity)entity).setAnimation("roar2");
                  }
               } else if (entity instanceof TheheavyEntity) {
                  ((TheheavyEntity)entity).setAnimation("roar");
               }

               entity.getPersistentData().putDouble("BLOCKIN", 1.0);
               UndeadRevamp2Mod.queueServerWork(
                  60,
                  () -> {
                     if (entity.getPersistentData().getDouble("capped") == 1.0) {
                        if (world instanceof Level _levelx) {
                           if (!_levelx.isClientSide()) {
                              _levelx.playSound(
                                 null,
                                 BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()),
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:heavyroar")),
                                 SoundSource.NEUTRAL,
                                 8.0F,
                                 1.25F
                              );
                           } else {
                              _levelx.playLocalSound(
                                 entity.getX(),
                                 entity.getY(),
                                 entity.getZ(),
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:heavyroar")),
                                 SoundSource.NEUTRAL,
                                 8.0F,
                                 1.25F,
                                 false
                              );
                           }
                        }

                        if (world instanceof Level _levelx) {
                           if (!_levelx.isClientSide()) {
                              _levelx.playSound(
                                 null,
                                 BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()),
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:heavyroar")),
                                 SoundSource.NEUTRAL,
                                 8.0F,
                                 1.25F
                              );
                           } else {
                              _levelx.playLocalSound(
                                 entity.getX(),
                                 entity.getY(),
                                 entity.getZ(),
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:heavyroar")),
                                 SoundSource.NEUTRAL,
                                 8.0F,
                                 1.25F,
                                 false
                              );
                           }
                        }

                        if (world instanceof ServerLevel _levelxx) {
                           _levelxx.sendParticles(
                              ParticleTypes.ANGRY_VILLAGER,
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              30,
                              entity.getBbWidth(),
                              entity.getBbHeight(),
                              entity.getBbWidth(),
                              1.0
                           );
                        }

                        if (entity instanceof LivingEntity _entityx && !_entityx.level().isClientSide()) {
                           _entityx.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0, false, false));
                        }
                     } else if (world instanceof Level _levelxx) {
                        if (!_levelxx.isClientSide()) {
                           _levelxx.playSound(
                              null,
                              BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:heavyroar")),
                              SoundSource.NEUTRAL,
                              5.0F,
                              1.0F
                           );
                        } else {
                           _levelxx.playLocalSound(
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:heavyroar")),
                              SoundSource.NEUTRAL,
                              5.0F,
                              1.0F,
                              false
                           );
                        }
                     }

                     if (world instanceof ServerLevel _levelxxx) {
                        Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.CRACKLEBALL.get())
                           .spawn(_levelxxx, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                           entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                        }
                     }

                     if (world instanceof ServerLevel _levelxxxx) {
                        Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.CRACKLEBALL.get())
                           .spawn(_levelxxxx, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                           entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                        }
                     }

                     if (world instanceof ServerLevel _levelxxxxx) {
                        Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.CRACKLEBALL.get())
                           .spawn(_levelxxxxx, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                           entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                        }
                     }

                     if (world instanceof ServerLevel _levelxxxxxx) {
                        Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.CRACKLEBALL.get())
                           .spawn(_levelxxxxxx, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                           entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                        }
                     }

                     if (world instanceof ServerLevel _levelxxxxxxx) {
                        Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.CRACKLEBALL.get())
                           .spawn(_levelxxxxxxx, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                           entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                        }
                     }

                     Vec3 _center = new Vec3(entity.getX(), entity.getY(), entity.getZ());

                     for (Entity entityiteratorxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4.0), e -> true)
                        .stream()
                        .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                        .toList()) {
                        if (!(entityiteratorxx instanceof CrackleballEntity)) {
                           entityiteratorxx.setDeltaMovement(
                              new Vec3(
                                 Math.sin(Math.toRadians(entityiteratorxx.getYRot() + 180.0F)) * 2.0 * -1.0,
                                 (Math.sin(Math.toRadians(0.0F - entityiteratorxx.getXRot())) + 0.5) * 1.5,
                                 Math.cos(Math.toRadians(entityiteratorxx.getYRot())) * 2.0 * -1.0
                              )
                           );
                        }
                     }

                     _center = new Vec3(x, y, z);

                     for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(10.0), e -> true)
                        .stream()
                        .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                        .toList()) {
                        entityiteratorx.lookAt(Anchor.EYES, new Vec3(entity.getX(), entity.getY(), entity.getZ()));
                     }
                  }
               );
               UndeadRevamp2Mod.queueServerWork(115, () -> {
                  entity.getPersistentData().putDouble("BLOCKIN", 0.0);
                  entity.getPersistentData().putDouble("roar", 1.0);
               });
            }

            if (entity.getPersistentData().getDouble("BLOCKIN") == 1.0) {
               if (entity.getPersistentData().getDouble("capped") == 1.0) {
                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2, 0, false, false));
                  }

                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2, 30, false, false));
                  }

                  entity.setDeltaMovement(new Vec3(0.0, -1.0, 0.0));
               } else {
                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2, 20, false, false));
                  }

                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2, 30, false, false));
                  }

                  entity.setDeltaMovement(new Vec3(0.0, -1.0, 0.0));
               }
            }

            if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity) {
               if (entity.getPersistentData().getDouble("fleepick") == 1.0
                  && !(entity instanceof LivingEntity _livEnt192 && _livEnt192.hasEffect(MobEffects.LUCK))) {
                  entity.getPersistentData().putDouble("fleepick", 2.0);
                  entity.getPersistentData().putDouble("BLOCKIN", 1.0);
                  if (entity instanceof TheheavyEntity) {
                     ((TheheavyEntity)entity).setAnimation("pickup");
                  }

                  UndeadRevamp2Mod.queueServerWork(
                     20,
                     () -> {
                        if (world instanceof Level _levelx) {
                           if (!_levelx.isClientSide()) {
                              _levelx.playSound(
                                 null,
                                 BlockPos.containing(x, y, z),
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.wither.break_block")),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F
                              );
                           } else {
                              _levelx.playLocalSound(
                                 x,
                                 y,
                                 z,
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.wither.break_block")),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F,
                                 false
                              );
                           }
                        }

                        world.levelEvent(2001, BlockPos.containing(x, y - 1.0, z), Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z))));
                        world.levelEvent(
                           2001, BlockPos.containing(x + 1.0, y - 1.0, z), Block.getId(world.getBlockState(BlockPos.containing(x + 1.0, y - 1.0, z)))
                        );
                        if (entity.getPersistentData().getDouble("capped") == 1.0) {
                           if (entity instanceof TheheavyEntity animatablexx) {
                              animatablexx.setTexture("heavystonedecapstone");
                           }
                        } else if (entity instanceof TheheavyEntity animatablex) {
                           animatablex.setTexture("heavycap");
                        }

                        entity.getPersistentData().putDouble("stone", 3.0);
                     }
                  );
                  UndeadRevamp2Mod.queueServerWork(50, () -> {
                     entity.getPersistentData().putDouble("BLOCKIN", 0.0);
                     entity.getPersistentData().putDouble("fleepick", 0.0);
                     if (Math.random() < 0.6) {
                        entity.getPersistentData().putDouble("throw", 1.0);
                        if (entity instanceof LivingEntity _entityx && !_entityx.level().isClientSide()) {
                           _entityx.addEffect(new MobEffectInstance(MobEffects.LUCK, 10, 0, false, false));
                        }
                     }
                  });
               }

               if (!(entity instanceof LivingEntity _livEnt211 && _livEnt211.hasEffect(MobEffects.LUCK))
                  && entity.getPersistentData().getDouble("throw") == 1.0) {
                  if (entity.getPersistentData().getDouble("honeyman_b") == 0.0) {
                     entity.lookAt(
                        Anchor.EYES,
                        new Vec3(
                           (entity instanceof Mob _mobEntxxx ? _mobEntxxx.getTarget() : null).getX(),
                           (entity instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null).getY(),
                           (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null).getZ()
                        )
                     );
                     entity.getPersistentData().putDouble("BLOCKIN", 1.0);
                     entity.getPersistentData().putDouble("honeyman_b", 1.0);
                     if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 22, 30, false, false));
                     }

                     if (entity instanceof TheheavyEntity) {
                        ((TheheavyEntity)entity).setAnimation("throw");
                     }

                     UndeadRevamp2Mod.queueServerWork(16, () -> entity.getPersistentData().putDouble("honeyman_a", 1.0));
                  }

                  if (entity.getPersistentData().getDouble("honeyman_a") == 1.0 && entity.isAlive()) {
                     if (entity.getPersistentData().getDouble("capped") == 1.0) {
                        if (entity instanceof TheheavyEntity animatable) {
                           animatable.setTexture("heavydecap");
                        }
                     } else if (entity instanceof TheheavyEntity animatable) {
                        animatable.setTexture("heavy");
                     }

                     entity.getPersistentData().putDouble("honeyman_a", 0.0);
                     if (world instanceof Level _levelx) {
                        if (!_levelx.isClientSide()) {
                           _levelx.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:windblast")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              1.0F
                           );
                        } else {
                           _levelx.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:windblast")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              1.0F,
                              false
                           );
                        }
                     }

                     if (world instanceof ServerLevel projectileLevel) {
                        Projectile _entityToSpawn = (new Object() {
                              public Projectile getArrow(Level level, Entity shooter, float damage, final int knockback, final byte piercing) {
                                 AbstractArrow entityToSpawn = new BoulderthrowProjectileEntity(
                                    (EntityType)UndeadRevamp2ModEntities.BOULDERTHROW_PROJECTILE.get(), level
                                 ) {
                                    public byte getPierceLevel() {
                                       return piercing;
                                    }

                                    @Override
                                    protected void doKnockback(LivingEntity livingEntity, DamageSource damageSource) {
                                       if (knockback > 0) {
                                          double d1 = Math.max(0.0, 1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                                          Vec3 vec3 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(knockback * 0.6 * d1);
                                          if (vec3.lengthSqr() > 0.0) {
                                             livingEntity.push(vec3.x, 0.1, vec3.z);
                                          }
                                       }
                                    }
                                 };
                                 entityToSpawn.setOwner(shooter);
                                 entityToSpawn.setBaseDamage(damage);
                                 entityToSpawn.setSilent(true);
                                 return entityToSpawn;
                              }
                           })
                           .getArrow(projectileLevel, entity, 18.0F, 1, (byte)0);
                        _entityToSpawn.setPos(
                           entity.getX() + entity.getLookAngle().x - 0.5,
                           entity.getY() + entity.getLookAngle().y + 1.5,
                           entity.getZ() + entity.getLookAngle().z
                        );
                        _entityToSpawn.shoot(entity.getLookAngle().x, entity.getLookAngle().y, entity.getLookAngle().z, 1.5F, 0.0F);
                        projectileLevel.addFreshEntity(_entityToSpawn);
                     }

                     entity.getPersistentData().putDouble("honeyman_b", 0.0);
                     entity.getPersistentData().putDouble("throw", 0.0);
                     entity.getPersistentData().putDouble("stone", 0.0);
                     UndeadRevamp2Mod.queueServerWork(5, () -> entity.getPersistentData().putDouble("BLOCKIN", 0.0));
                  }
               }

               if (entity.getPersistentData().getDouble("stone") > 0.0
                  && entity.getPersistentData().getDouble("throw") == 0.0
                  && entity.getPersistentData().getDouble("roar") == 1.0) {
                  Vec3 _center = new Vec3(x, y, z);

                  for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.5), e -> true)
                     .stream()
                     .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                     .toList()) {
                     if ((entity instanceof Mob _mobEntxxxx ? _mobEntxxxx.getTarget() : null) != entityiteratorx && Math.random() < 7.5E-4) {
                        entity.getPersistentData().putDouble("throw", 1.0);
                        if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                           _entity.addEffect(new MobEffectInstance(MobEffects.LUCK, 20, 0, false, false));
                        }
                     }
                  }
               }

               if (entity.getPersistentData().getDouble("stone") == 0.0
                  && entity.getPersistentData().getDouble("fleepick") == 0.0
                  && entity.getPersistentData().getDouble("roar") == 1.0) {
                  Vec3 _center = new Vec3(x, y, z);

                  for (Entity entityiteratorxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5.0), e -> true)
                     .stream()
                     .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                     .toList()) {
                     if ((entity instanceof Mob _mobEntxxxxx ? _mobEntxxxxx.getTarget() : null) != entityiteratorxx && Math.random() < 1.5E-4) {
                        entity.getPersistentData().putDouble("fleepick", 1.0);
                        if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                           _entity.addEffect(new MobEffectInstance(MobEffects.LUCK, 35, 0, false, false));
                        }
                     }
                  }
               }
            }
         }

         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiteratorxxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entityiteratorxxx == (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null)) {
               entity.getPersistentData().putDouble("r_range", 1.0);
            } else {
               entity.getPersistentData().putDouble("r_range", 0.0);
            }
         }

         if (world.getLevelData().getGameRules().getBoolean(UndeadRevamp2ModGameRules.SUNRAY) && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z))) {
            if (world instanceof Level _lvl273
               && _lvl273.isDay()
               && !world.getLevelData().isRaining()
               && !world.getLevelData().isThundering()
               && !entity.isInWaterRainOrBubble()
               && !entity.isOnFire()
               && !world.isClientSide()) {
               entity.igniteForSeconds(5.0F);
            }

            if ((world.getLevelData().isRaining() || world.getLevelData().isThundering()) && !world.isClientSide()) {
               entity.clearFire();
            }
         }

         if (entity.getPersistentData().getDouble("capped") == 1.0
            && entity instanceof LivingEntity _livingEntity285
            && _livingEntity285.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.CHEROATTACKSPEED)) {
            _livingEntity285.getAttribute(UndeadRevamp2ModAttributes.CHEROATTACKSPEED).setBaseValue(5.0);
         }

         if (entity.getPersistentData().getDouble("roar") != 1.0 && entity.getPersistentData().getDouble("roar") != 0.0) {
            entity.getPersistentData().putDouble("passorsmash", 0.0);
            entity.getPersistentData().putDouble("pokemode", 0.0);
            entity.getPersistentData().putDouble("pastat", 1.0);
            entity.getPersistentData().putDouble("rage", 0.0);
            entity.getPersistentData().putDouble("roar", 0.0);
            entity.getPersistentData().putDouble("BLOCKIN", 0.0);
            entity.getPersistentData().putDouble("stone", 0.0);
            entity.getPersistentData().putDouble("fleepick", 0.0);
            entity.getPersistentData().putDouble("throw", 0.0);
            entity.getPersistentData().putDouble("honeyman_a", 0.0);
            entity.getPersistentData().putDouble("honeyman_b", 0.0);
            entity.getPersistentData().putDouble("r_range", 0.0);
         }

         if (entity.getPersistentData().getDouble("capped") == 1.0) {
            if (entity.getPersistentData().getDouble("stone") > 0.0) {
               if (entity instanceof TheheavyEntity animatable) {
                  animatable.setTexture("heavystonedecapstone");
               }
            } else if (entity instanceof TheheavyEntity animatable) {
               animatable.setTexture("heavydecap");
            }
         }
      }
   }
}
