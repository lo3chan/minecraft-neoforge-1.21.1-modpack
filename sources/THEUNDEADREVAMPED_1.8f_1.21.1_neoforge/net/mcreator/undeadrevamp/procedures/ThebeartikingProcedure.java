package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.SlavemanEntity;
import net.mcreator.undeadrevamp.entity.ThebeartamerEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModAttributes;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ThebeartikingProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))
            && (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity
            && entity.getPersistentData().getDouble("passorsmash") == 0.0
            && entity.getPersistentData().getDouble("pastat") == 1.0
            && !(entity instanceof LivingEntity _livEnt5 && _livEnt5.hasEffect(UndeadRevamp2ModMobEffects.ANIMATIONTEST))) {
            Vec3 _center = new Vec3(entity.getX(), entity.getY(), entity.getZ());

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.5), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entityiterator) {
                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 35, 30, false, false));
                  }

                  entity.getPersistentData().putDouble("passorsmash", 1.0);
                  if (entity.getPersistentData().getDouble("rage") == 1.0) {
                     if (Math.random() < 0.4) {
                        if (entity instanceof ThebeartamerEntity) {
                           ((ThebeartamerEntity)entity).setAnimation("poke");
                        }

                        entity.getPersistentData().putDouble("pokemode", 1.0);
                     } else if (entity instanceof ThebeartamerEntity) {
                        ((ThebeartamerEntity)entity).setAnimation("smash");
                     }
                  } else if (Math.random() < 0.2) {
                     if (entity instanceof ThebeartamerEntity) {
                        ((ThebeartamerEntity)entity).setAnimation("poke");
                     }

                     entity.getPersistentData().putDouble("pokemode", 1.0);
                  } else if (entity instanceof ThebeartamerEntity) {
                     ((ThebeartamerEntity)entity).setAnimation("smash");
                  }
               }

               if (entity instanceof LivingEntity _livEnt20 && _livEnt20.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS)) {
                  entity.getPersistentData().putDouble("passorsmash", 1.0);
               }
            }

            if (entity.getPersistentData().getDouble("passorsmash") == 1.0) {
               UndeadRevamp2Mod.queueServerWork(10, () -> {
                  if (entity instanceof LivingEntity _entityx && !_entityx.level().isClientSide()) {
                     _entityx.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.BROKENTANK, 3, 0, false, false));
                  }
               });
            }

            if (entity.getPersistentData().getDouble("passorsmash") == 1.0
               && !(entity instanceof LivingEntity _livEnt27 && _livEnt27.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))) {
               if (Math.random() < 0.1 && world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:wolforbruin_attack")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:wolforbruin_attack")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }

               UndeadRevamp2Mod.queueServerWork(
                  13,
                  () -> {
                     if (world instanceof Level _levelx) {
                        if (!_levelx.isClientSide()) {
                           _levelx.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              -2.0F
                           );
                        } else {
                           _levelx.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              -2.0F,
                              false
                           );
                        }
                     }
                  }
               );
               UndeadRevamp2Mod.queueServerWork(
                  19,
                  () -> {
                     if (world instanceof Level _levelx) {
                        if (!_levelx.isClientSide()) {
                           _levelx.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              2.0F
                           );
                        } else {
                           _levelx.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              2.0F,
                              false
                           );
                        }
                     }
                  }
               );
               UndeadRevamp2Mod.queueServerWork(
                  21,
                  () -> {
                     if (entity.getPersistentData().getDouble("pokemode") == 1.0) {
                        if (entity.isAlive()
                           && entity.getPersistentData().getDouble("passorsmash") == 1.0
                           && entity.getPersistentData().getDouble("pastat") == 1.0
                           && !(entity instanceof LivingEntity _livEnt37 && _livEnt37.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))) {
                           Vec3 _centerxx = new Vec3(entity.getX(), entity.getY(), entity.getZ());

                           for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_centerxx, _centerxx).inflate(3.5), e -> true)
                              .stream()
                              .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_centerxx)))
                              .toList()) {
                              if ((entity instanceof Mob _mobEntxxx ? _mobEntxxx.getTarget() : null) == entityiterator) {
                                 entityiterator.setDeltaMovement(
                                    new Vec3(
                                       Math.sin(Math.toRadians(entityiterator.getYRot() + 180.0F)) * 1.25 * -1.5,
                                       (Math.sin(Math.toRadians(0.0F - entityiterator.getXRot())) + 0.5) * 1.25,
                                       Math.cos(Math.toRadians(entityiterator.getYRot())) * 1.25 * -1.5
                                    )
                                 );
                                 if (!(entityiterator instanceof LivingEntity _livEnt47 && _livEnt47.isBlocking())) {
                                    entityiterator.hurt(
                                       new DamageSource(world.holderOrThrow(DamageTypes.FALLING_ANVIL), entity),
                                       (float)(
                                          entity instanceof LivingEntity _livingEntity48
                                                && _livingEntity48.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                                             ? _livingEntity48.getAttribute(Attributes.ATTACK_DAMAGE).getValue()
                                             : 0.0
                                       )
                                    );
                                 }
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

                           entity.getPersistentData().putDouble("pokemode", 0.0);
                           entity.getPersistentData().putDouble("pastat", 0.0);
                        }
                     } else if (entity.isAlive()
                        && entity.getPersistentData().getDouble("passorsmash") == 1.0
                        && entity.getPersistentData().getDouble("pastat") == 1.0
                        && !(entity instanceof LivingEntity _livEnt58 && _livEnt58.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))) {
                        Vec3 _centerx = new Vec3(entity.getX(), entity.getY(), entity.getZ());

                        for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_centerx, _centerx).inflate(2.5), e -> true)
                           .stream()
                           .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_centerx)))
                           .toList()) {
                           if ((entity instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null) == entityiteratorx
                              || (entityiteratorx instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entity) {
                              if (entityiteratorx instanceof LivingEntity _entityx && !_entityx.level().isClientSide()) {
                                 _entityx.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 8, 10, false, false));
                              }

                              entityiteratorx.hurt(
                                 new DamageSource(world.holderOrThrow(DamageTypes.FALLING_ANVIL), entity),
                                 (float)(
                                    (
                                          entity instanceof LivingEntity _livingEntity67
                                                && _livingEntity67.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                                             ? _livingEntity67.getAttribute(Attributes.ATTACK_DAMAGE).getValue()
                                             : 0.0
                                       )
                                       + 2.0
                                 )
                              );
                           }
                        }

                        if (world instanceof ServerLevel _levelx) {
                           _levelx.sendParticles(ParticleTypes.CRIT, x, y, z, 30, 3.0, 1.0, 3.0, 1.0);
                        }

                        if (world instanceof Level _levelx) {
                           if (!_levelx.isClientSide()) {
                              _levelx.playSound(
                                 null,
                                 BlockPos.containing(x, y, z),
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.explode")),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F
                              );
                           } else {
                              _levelx.playLocalSound(
                                 x,
                                 y,
                                 z,
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.explode")),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F,
                                 false
                              );
                           }
                        }

                        entity.getPersistentData().putDouble("pastat", 0.0);
                     }
                  }
               );
               UndeadRevamp2Mod.queueServerWork(
                  27,
                  () -> {
                     if (!(entity instanceof LivingEntity _livEnt75 && _livEnt75.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))
                        && entity.getPersistentData().getDouble("pastat") == 0.0
                        && entity.getPersistentData().getDouble("passorsmash") == 1.0) {
                        UndeadRevamp2Mod.queueServerWork(
                           (int)(
                              entity instanceof LivingEntity _livingEntity78
                                    && _livingEntity78.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.CHEROATTACKSPEED)
                                 ? _livingEntity78.getAttribute(UndeadRevamp2ModAttributes.CHEROATTACKSPEED).getValue()
                                 : 0.0
                           ),
                           () -> {
                              entity.getPersistentData().putDouble("pastat", 1.0);
                              entity.getPersistentData().putDouble("passorsmash", 0.0);
                           }
                        );
                     }
                  }
               );
            }
         }

         if ((entity instanceof LivingEntity _livEntx ? _livEntx.getMaxHealth() : -1.0F) / 100.0F * 40.0F
            > (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F)) {
            if (entity instanceof ThebeartamerEntity animatable) {
               animatable.setTexture("enragedbear");
            }

            entity.getPersistentData().putDouble("rage", 1.0);
            if (entity instanceof LivingEntity _livingEntity88 && _livingEntity88.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.CHEROATTACKSPEED)) {
               _livingEntity88.getAttribute(UndeadRevamp2ModAttributes.CHEROATTACKSPEED)
                  .setBaseValue(
                     (
                           entity instanceof LivingEntity _livingEntity87
                                 && _livingEntity87.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.CHEROATTACKSPEED)
                              ? _livingEntity87.getAttribute(UndeadRevamp2ModAttributes.CHEROATTACKSPEED).getValue()
                              : 0.0
                        )
                        * 0.25
                  );
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10, 0, false, false));
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

         if (entity.getPersistentData().getDouble("pokemode") != 1.0 && entity.getPersistentData().getDouble("pokemode") != 0.0) {
            entity.getPersistentData().putDouble("passorsmash", 0.0);
            entity.getPersistentData().putDouble("pokemode", 0.0);
            entity.getPersistentData().putDouble("pastat", 1.0);
            entity.getPersistentData().putDouble("rage", 0.0);
         }
      }
   }
}
