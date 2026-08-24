package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.configuration.MobsabilityConfiguration;
import net.mcreator.undeadrevamp.entity.AxestromEntity;
import net.mcreator.undeadrevamp.entity.ThewolfEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ThewolfOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (Math.random() < 0.07
            && !(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))
            && (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity
            && entity.getPersistentData().getDouble("passorsmash") == 0.0
            && entity.getPersistentData().getDouble("pastat") == 1.0
            && !(entity instanceof LivingEntity _livEnt5 && _livEnt5.hasEffect(UndeadRevamp2ModMobEffects.ANIMATIONTEST))) {
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(8.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entityiterator) {
                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.ANIMATIONTEST, 24, 0, false, false));
                  }

                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 24, 30, false, false));
                  }

                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 24, 3, false, false));
                  }

                  entity.getPersistentData().putDouble("passorsmash", 1.0);
                  if (Math.random() < 0.5) {
                     if (entity instanceof ThewolfEntity) {
                        ((ThewolfEntity)entity).setAnimation("spin");
                     }
                  } else if (entity instanceof ThewolfEntity) {
                     ((ThewolfEntity)entity).setAnimation("spin2");
                  }
               }

               if (entity instanceof LivingEntity _livEnt14 && _livEnt14.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS)) {
                  entity.getPersistentData().putDouble("passorsmash", 1.0);
               }
            }

            if (entity.getPersistentData().getDouble("passorsmash") == 1.0
               && !(entity instanceof LivingEntity _livEnt18 && _livEnt18.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))) {
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
                  14,
                  () -> {
                     if (entity.isAlive()
                        && entity.getPersistentData().getDouble("passorsmash") == 1.0
                        && entity.getPersistentData().getDouble("pastat") == 1.0
                        && !(entity instanceof LivingEntity _livEnt23 && _livEnt23.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))) {
                        Vec3 _centerx = new Vec3(x, y, z);

                        for (Entity entityiteratorxxx : world.getEntitiesOfClass(Entity.class, new AABB(_centerx, _centerx).inflate(2.5), e -> true)
                           .stream()
                           .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                           .toList()) {
                           if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entityiteratorxxx
                              || (entityiteratorxxx instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entity) {
                              if (!(entityiteratorxxx instanceof AxestromEntity)) {
                                 entityiteratorxxx.setDeltaMovement(
                                    new Vec3(
                                       Math.sin(Math.toRadians(entityiteratorxxx.getYRot() + 180.0F)) * 1.25 * -1.0,
                                       (Math.sin(Math.toRadians(0.0F - entityiteratorxxx.getXRot())) + 0.5) * 1.25,
                                       Math.cos(Math.toRadians(entityiteratorxxx.getYRot())) * 1.25 * -1.0
                                    )
                                 );
                              }

                              if (!(entityiteratorxxx instanceof LivingEntity _livEnt33 && _livEnt33.isBlocking())) {
                                 entityiteratorxxx.hurt(
                                    new DamageSource(world.holderOrThrow(DamageTypes.FALLING_ANVIL), entity),
                                    (float)((Double)MobsabilityConfiguration.AXE_DMG.get()).doubleValue()
                                 );
                              } else if (world instanceof Level _levelx) {
                                 if (!_levelx.isClientSide()) {
                                    _levelx.playSound(
                                       null,
                                       BlockPos.containing(x, y, z),
                                       (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.attack_wooden_door")),
                                       SoundSource.NEUTRAL,
                                       1.0F,
                                       1.0F
                                    );
                                 } else {
                                    _levelx.playLocalSound(
                                       x,
                                       y,
                                       z,
                                       (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.attack_wooden_door")),
                                       SoundSource.NEUTRAL,
                                       1.0F,
                                       1.0F,
                                       false
                                    );
                                 }
                              }
                           }
                        }

                        if (entity.getPersistentData().getDouble("rage") == 0.0 && (Boolean)MobsabilityConfiguration.STROMY.get()) {
                           _centerx = new Vec3(x, y, z);

                           for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_centerx, _centerx).inflate(2.5), e -> true)
                              .stream()
                              .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                              .toList()) {
                              if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) != entityiteratorx
                                 && (entityiteratorx instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) != entity
                                 && Math.random() < 0.1
                                 && world instanceof ServerLevel _levelx) {
                                 Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.AXESTROM.get())
                                    .spawn(_levelx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                                 if (entityToSpawn != null) {
                                    entityToSpawn.setXRot((float)entity.getLookAngle().y);
                                    entityToSpawn.setDeltaMovement(
                                       (entityiteratorx.getX() - entity.getX()) * 0.06,
                                       (entityiteratorx.getY() + 1.0 - entity.getY()) * 0.09,
                                       (entityiteratorx.getZ() - entity.getZ()) * 0.06
                                    );
                                 }
                              }
                           }
                        }

                        if ((Boolean)MobsabilityConfiguration.STROMY.get()) {
                           if (entity.getPersistentData().getDouble("rage") == 1.0) {
                              _centerx = new Vec3(x, y, z);

                              for (Entity entityiteratorxx : world.getEntitiesOfClass(Entity.class, new AABB(_centerx, _centerx).inflate(8.0), e -> true)
                                 .stream()
                                 .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                                 .toList()) {
                                 if ((entity instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null) == entityiteratorxx
                                    && Math.random() < 0.2
                                    && world instanceof ServerLevel _levelxx) {
                                    Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.AXESTROM.get())
                                       .spawn(_levelxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                                    if (entityToSpawn != null) {
                                       entityToSpawn.setXRot((float)entity.getLookAngle().y);
                                       entityToSpawn.setDeltaMovement(
                                          (entityiteratorxx.getX() - entity.getX()) * 0.06,
                                          (entityiteratorxx.getY() + 1.0 - entity.getY()) * 0.09,
                                          (entityiteratorxx.getZ() - entity.getZ()) * 0.06
                                       );
                                    }
                                 }
                              }
                           }

                           if (world instanceof ServerLevel _levelxxx) {
                              _levelxxx.sendParticles(ParticleTypes.CRIT, x, y, z, 30, 3.0, 1.0, 3.0, 1.0);
                           }

                           if (world instanceof Level _levelxxx) {
                              if (!_levelxxx.isClientSide()) {
                                 _levelxxx.playSound(
                                    null,
                                    BlockPos.containing(x, y, z),
                                    (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                                    SoundSource.NEUTRAL,
                                    2.0F,
                                    1.0F
                                 );
                              } else {
                                 _levelxxx.playLocalSound(
                                    x,
                                    y,
                                    z,
                                    (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                                    SoundSource.NEUTRAL,
                                    2.0F,
                                    1.0F,
                                    false
                                 );
                              }
                           }

                           entity.getPersistentData().putDouble("pastat", 0.0);
                        }
                     }
                  }
               );
               UndeadRevamp2Mod.queueServerWork(18, () -> {
                  if (entity instanceof LivingEntity _entityx && !_entityx.level().isClientSide()) {
                     _entityx.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.BROKENTANK, 3, 0, false, false));
                  }
               });
               UndeadRevamp2Mod.queueServerWork(
                  23,
                  () -> {
                     if (!(entity instanceof LivingEntity _livEnt73 && _livEnt73.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))
                        && entity.getPersistentData().getDouble("pastat") == 0.0
                        && entity.getPersistentData().getDouble("passorsmash") == 1.0) {
                        entity.getPersistentData().putDouble("pastat", 1.0);
                        entity.getPersistentData().putDouble("passorsmash", 0.0);
                     }
                  }
               );
            }
         }

         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(16.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) == entityiterator
               && entityiterator instanceof LivingEntity _entity
               && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.REEKOFMAGIC, 10, 0, false, false));
            }
         }

         if ((entity instanceof LivingEntity _livEntx ? _livEntx.getMaxHealth() : -1.0F) / 100.0F * 30.0F
            > (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F)) {
            if (entity instanceof ThewolfEntity animatable) {
               animatable.setTexture("thewolfenraged");
            }

            entity.getPersistentData().putDouble("rage", 1.0);
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 10, 0, false, false));
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
