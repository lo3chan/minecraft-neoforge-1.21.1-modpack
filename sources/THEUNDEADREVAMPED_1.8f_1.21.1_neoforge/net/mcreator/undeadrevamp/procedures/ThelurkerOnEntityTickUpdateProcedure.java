package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.AxestromEntity;
import net.mcreator.undeadrevamp.entity.ThelurkerEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ThelurkerOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (Math.random() < 0.01 && !((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity)) {
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7.5), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entityiterator) {
                  if (world instanceof ServerLevel _level) {
                     _level.sendParticles(ParticleTypes.ANGRY_VILLAGER, x, y, z, 5, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 0.0);
                  }

                  if (Math.random() < 0.5) {
                     if (entity instanceof Mob _entity) {
                        _entity.getNavigation().moveTo(Math.random() * 12.0, y - 3.0, Math.random() * 10.0, 1.0);
                     }
                  } else if (entity instanceof Mob _entity) {
                     _entity.getNavigation().moveTo(Math.random() * 10.0, y + 3.0, Math.random() * 10.0, 1.0);
                  }
               }
            }
         }

         if (Math.random() < 0.07
            && !(entity instanceof LivingEntity _livEnt11 && _livEnt11.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))
            && (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity
            && entity.getPersistentData().getDouble("passorsmash") == 0.0
            && entity.getPersistentData().getDouble("pastat") == 1.0
            && !(entity instanceof LivingEntity _livEnt16 && _livEnt16.hasEffect(UndeadRevamp2ModMobEffects.ANIMATIONTEST))) {
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7.5), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entityiteratorx) {
                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.ANIMATIONTEST, 15, 0, false, false));
                  }

                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 15, 30, false, false));
                  }

                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 15, 3, false, false));
                  }

                  entity.getPersistentData().putDouble("passorsmash", 1.0);
                  if (entity instanceof ThelurkerEntity) {
                     ((ThelurkerEntity)entity).setAnimation("bite");
                  }
               }

               if (entity instanceof LivingEntity _livEnt24 && _livEnt24.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS)) {
                  entity.getPersistentData().putDouble("passorsmash", 1.0);
               }
            }

            if (entity.getPersistentData().getDouble("passorsmash") == 1.0
               && !(entity instanceof LivingEntity _livEnt28 && _livEnt28.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))) {
               if (Math.random() < 0.2 && world instanceof Level _level) {
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
                  7,
                  () -> {
                     if (entity.isAlive()
                        && entity.getPersistentData().getDouble("passorsmash") == 1.0
                        && entity.getPersistentData().getDouble("pastat") == 1.0
                        && !(entity instanceof LivingEntity _livEnt33 && _livEnt33.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))) {
                        Vec3 _centerx = new Vec3(x, y, z);

                        for (Entity entityiteratorxx : world.getEntitiesOfClass(Entity.class, new AABB(_centerx, _centerx).inflate(2.5), e -> true)
                           .stream()
                           .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                           .toList()) {
                           if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entityiteratorxx
                              || (entityiteratorxx instanceof Mob _mobEntxxx ? _mobEntxxx.getTarget() : null) == entity) {
                              if (!(entityiteratorxx instanceof AxestromEntity)) {
                                 entityiteratorxx.setDeltaMovement(
                                    new Vec3(
                                       Math.sin(Math.toRadians(entityiteratorxx.getYRot() + 180.0F)) * 1.25 * -1.0,
                                       (Math.sin(Math.toRadians(0.0F - entityiteratorxx.getXRot())) + 0.5) * 1.25,
                                       Math.cos(Math.toRadians(entityiteratorxx.getYRot())) * 1.25 * -1.0
                                    )
                                 );
                              }

                              entityiteratorxx.hurt(
                                 new DamageSource(world.holderOrThrow(DamageTypes.MOB_ATTACK)),
                                 (float)(
                                    (
                                          entity instanceof LivingEntity _livingEntity43
                                                && _livingEntity43.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                                             ? _livingEntity43.getAttribute(Attributes.ATTACK_DAMAGE).getValue()
                                             : 0.0
                                       )
                                       + 8.0
                                 )
                              );
                           }
                        }

                        _centerx = new Vec3(x, y, z);

                        for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_centerx, _centerx).inflate(7.5), e -> true)
                           .stream()
                           .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                           .toList()) {
                           if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entityiteratorx
                              || (entityiteratorx instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entity) {
                              if (entityiteratorx instanceof LivingEntity _livEnt51 && _livEnt51.isBlocking()) {
                                 if (world instanceof Level _levelx) {
                                    if (!_levelx.isClientSide()) {
                                       _levelx.playSound(
                                          null,
                                          BlockPos.containing(x, y, z),
                                          (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.block")),
                                          SoundSource.NEUTRAL,
                                          5.0F,
                                          1.0F
                                       );
                                    } else {
                                       _levelx.playLocalSound(
                                          x,
                                          y,
                                          z,
                                          (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.block")),
                                          SoundSource.NEUTRAL,
                                          5.0F,
                                          1.0F,
                                          false
                                       );
                                    }
                                 }

                                 if (entity instanceof LivingEntity _entityx) {
                                    _entityx.swing(InteractionHand.OFF_HAND, true);
                                 }
                              } else {
                                 entityiteratorx.hurt(
                                    new DamageSource(world.holderOrThrow(DamageTypes.MOB_ATTACK)),
                                    (float)(
                                       entity instanceof LivingEntity _livingEntity52 && _livingEntity52.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                                          ? _livingEntity52.getAttribute(Attributes.ATTACK_DAMAGE).getValue()
                                          : 0.0
                                    )
                                 );
                                 entityiteratorx.setDeltaMovement(
                                    new Vec3(
                                       Math.sin(Math.toRadians(entityiteratorx.getYRot() + 180.0F)) * 1.25 * 1.0,
                                       (Math.sin(Math.toRadians(0.0F - entityiteratorx.getXRot())) + 0.5) * 1.25,
                                       Math.cos(Math.toRadians(entityiteratorx.getYRot())) * 1.25 * 1.0
                                    )
                                 );
                              }
                           }

                           if (world instanceof Level _levelx) {
                              if (!_levelx.isClientSide()) {
                                 _levelx.playSound(
                                    null,
                                    BlockPos.containing(x, y, z),
                                    (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.wither.break_block")),
                                    SoundSource.NEUTRAL,
                                    8.0F,
                                    -3.0F
                                 );
                              } else {
                                 _levelx.playLocalSound(
                                    x,
                                    y,
                                    z,
                                    (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.wither.break_block")),
                                    SoundSource.NEUTRAL,
                                    8.0F,
                                    -3.0F,
                                    false
                                 );
                              }
                           }
                        }

                        if (world instanceof ServerLevel _levelxx) {
                           _levelxx.sendParticles(ParticleTypes.CRIT, x, y, z, 10, 3.0, 1.0, 3.0, 1.0);
                        }

                        if (world instanceof Level _levelxx) {
                           if (!_levelxx.isClientSide()) {
                              _levelxx.playSound(
                                 null,
                                 BlockPos.containing(x, y, z),
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                                 SoundSource.NEUTRAL,
                                 2.0F,
                                 -3.0F
                              );
                           } else {
                              _levelxx.playLocalSound(
                                 x,
                                 y,
                                 z,
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                                 SoundSource.NEUTRAL,
                                 2.0F,
                                 -3.0F,
                                 false
                              );
                           }
                        }

                        entity.getPersistentData().putDouble("pastat", 0.0);
                     }
                  }
               );
               UndeadRevamp2Mod.queueServerWork(18, () -> {
                  if (entity instanceof LivingEntity _entityxx && !_entityxx.level().isClientSide()) {
                     _entityxx.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.BROKENTANK, 60, 0, false, false));
                  }

                  if (entity instanceof LivingEntity _entityx && !_entityx.level().isClientSide()) {
                     _entityx.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 90, 10, false, true));
                  }
               });
               UndeadRevamp2Mod.queueServerWork(90, () -> {
                  if (world instanceof ServerLevel _levelx) {
                     _levelx.sendParticles(ParticleTypes.ANGRY_VILLAGER, x, y, z, 5, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 0.0);
                  }
               });
               UndeadRevamp2Mod.queueServerWork(
                  108,
                  () -> {
                     if (!(entity instanceof LivingEntity _livEnt75 && _livEnt75.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))
                        && entity.getPersistentData().getDouble("pastat") == 0.0
                        && entity.getPersistentData().getDouble("passorsmash") == 1.0) {
                        entity.getPersistentData().putDouble("pastat", 1.0);
                        entity.getPersistentData().putDouble("passorsmash", 0.0);
                     }

                     if (entity instanceof LivingEntity _entityx && !_entityx.level().isClientSide()) {
                        _entityx.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 90, 1, false, false));
                     }
                  }
               );
            }
         }

         if (entity.getPersistentData().getDouble("pastat") != 1.0 && entity.getPersistentData().getDouble("pastat") != 0.0) {
            entity.getPersistentData().putDouble("passorsmash", 0.0);
            entity.getPersistentData().putDouble("pokemode", 0.0);
            entity.getPersistentData().putDouble("pastat", 1.0);
            entity.getPersistentData().putDouble("rage", 0.0);
         }
      }
   }
}
