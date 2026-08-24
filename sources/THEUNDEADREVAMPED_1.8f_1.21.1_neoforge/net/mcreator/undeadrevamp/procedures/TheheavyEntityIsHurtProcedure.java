package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.TheheavyEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TheheavyEntityIsHurtProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (entity.isAlive() && entity.getPersistentData().getDouble("roar") == 1.0 && entity.getPersistentData().getDouble("throw") == 0.0) {
            if (!(entity instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))
               && !(entity instanceof LivingEntity _livEnt4 && _livEnt4.hasEffect(UndeadRevamp2ModMobEffects.BROKENTANK))
               && (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity
               && (
                  entity.getPersistentData().getDouble("passorsmash") == 0.0 && entity.getPersistentData().getDouble("pastat") == 1.0
                     || entity.getPersistentData().getDouble("pastat") == 0.0 && entity.getPersistentData().getDouble("passorsmash") == 1.0
               )
               && entity.getPersistentData().getDouble("BLOCKIN") == 0.0) {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.BROKENTANK, 50, 0, false, false));
               }

               if (entity.isAlive() && sourceentity instanceof LivingEntity) {
                  entity.getPersistentData().putDouble("BLOCKIN", 1.0);
                  if (entity instanceof TheheavyEntity) {
                     ((TheheavyEntity)entity).setAnimation("block");
                  }
               }

               if (Math.random() < 0.5) {
                  if (entity.getPersistentData().getDouble("capped") != 1.0) {
                     UndeadRevamp2Mod.queueServerWork(21, () -> {
                        if (entity.isAlive() && entity instanceof TheheavyEntity) {
                           ((TheheavyEntity)entity).setAnimation("grab");
                        }
                     });
                     UndeadRevamp2Mod.queueServerWork(
                        27,
                        () -> {
                           if (Math.random() < 0.7 && world instanceof Level _level) {
                              if (!_level.isClientSide()) {
                                 _level.playSound(
                                    null,
                                    BlockPos.containing(x, y, z),
                                    (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:heavyattack")),
                                    SoundSource.NEUTRAL,
                                    1.0F,
                                    1.0F
                                 );
                              } else {
                                 _level.playLocalSound(
                                    x,
                                    y,
                                    z,
                                    (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:heavyattack")),
                                    SoundSource.NEUTRAL,
                                    1.0F,
                                    1.0F,
                                    false
                                 );
                              }
                           }

                           if (entity.isAlive()) {
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

                              Vec3 _center = new Vec3(entity.getX(), entity.getY(), entity.getZ());

                              for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.75), e -> true)
                                 .stream()
                                 .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                                 .toList()) {
                                 if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entityiterator) {
                                    if (entityiterator instanceof LivingEntity _livEnt29 && _livEnt29.isBlocking()) {
                                       if (world instanceof Level _levelxx) {
                                          if (!_levelxx.isClientSide()) {
                                             _levelxx.playSound(
                                                null,
                                                BlockPos.containing(x, y, z),
                                                (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.block")),
                                                SoundSource.NEUTRAL,
                                                1.0F,
                                                1.0F
                                             );
                                          } else {
                                             _levelxx.playLocalSound(
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
                                    } else if (entity.isAlive() && !(entityiterator instanceof ItemEntity)) {
                                       entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.FALLING_ANVIL), sourceentity), 5.0F);
                                       entityiterator.setDeltaMovement(
                                          new Vec3(
                                             Math.sin(Math.toRadians(entityiterator.getYRot() + 180.0F)) * 2.0 * -1.0,
                                             0.0,
                                             Math.cos(Math.toRadians(entityiterator.getYRot())) * 2.0 * -1.0
                                          )
                                       );
                                    }
                                 }
                              }
                           }
                        }
                     );
                     UndeadRevamp2Mod.queueServerWork(28, () -> {
                        if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                           _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.ANIMATIONTEST, 7, 0, false, false));
                        }
                     });
                     UndeadRevamp2Mod.queueServerWork(35, () -> entity.getPersistentData().putDouble("BLOCKIN", 0.0));
                  } else {
                     UndeadRevamp2Mod.queueServerWork(10, () -> entity.getPersistentData().putDouble("BLOCKIN", 0.0));
                  }
               } else {
                  UndeadRevamp2Mod.queueServerWork(
                     25,
                     () -> {
                        entity.getPersistentData().putDouble("BLOCKIN", 0.0);
                        if ((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) / 100.0F / 60.0F
                              < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F)
                           && Math.random() < 0.1
                           && entity.getPersistentData().getDouble("roar") == 1.0) {
                           entity.getPersistentData().putDouble("roar", 0.0);
                        }
                     }
                  );
               }
            }

            if (entity.getPersistentData().getDouble("BLOCKIN") == 1.0) {
               Vec3 _center = new Vec3(x, y, z);

               for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.0), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) == entityiterator) {
                     if (world instanceof Level _level) {
                        if (!_level.isClientSide()) {
                           _level.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.attack_iron_door")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              1.0F
                           );
                        } else {
                           _level.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.attack_iron_door")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              1.0F,
                              false
                           );
                        }
                     }

                     if (!(entityiterator instanceof ItemEntity)) {
                        entityiterator.setDeltaMovement(
                           new Vec3(
                              Math.sin(Math.toRadians(entityiterator.getYRot() + 180.0F)) * 2.0 * -1.0,
                              0.0,
                              Math.cos(Math.toRadians(entityiterator.getYRot())) * 2.0 * -1.0
                           )
                        );
                     }
                  }
               }
            }
         }

         if (entity.isAlive()
            && sourceentity instanceof LivingEntity
            && entity instanceof LivingEntity _livEnt63
            && _livEnt63.hasEffect(UndeadRevamp2ModMobEffects.ANIMATIONTEST)) {
            if (world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:cloggerbleed")),
                     SoundSource.NEUTRAL,
                     2.0F,
                     1.0F
                  );
               } else {
                  _levelx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:cloggerbleed")),
                     SoundSource.NEUTRAL,
                     2.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof Level _levelxx) {
               if (!_levelxx.isClientSide()) {
                  _levelxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:heavyhurt")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     -2.0F
                  );
               } else {
                  _levelxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:heavyhurt")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     -2.0F,
                     false
                  );
               }
            }

            if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.UNDEADSTUNS, 30, 0));
            }

            if (sourceentity instanceof LivingEntity && sourceentity instanceof ServerPlayer _player) {
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

            entity.getPersistentData().putDouble("capped", 1.0);
            entity.getPersistentData().putDouble("BLOCKIN", 0.0);
            if (entity.getPersistentData().getDouble("roar") == 1.0) {
               entity.getPersistentData().putDouble("roar", 0.0);
            }
         }
      }
   }
}
