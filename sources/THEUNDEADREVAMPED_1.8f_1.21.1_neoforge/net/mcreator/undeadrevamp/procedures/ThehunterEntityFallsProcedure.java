package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ThehunterEntityFallsProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(MobEffects.HUNGER)) && entity.getPersistentData().getDouble("gaszz_sped") == 1.0) {
            entity.getPersistentData().putDouble("gaszz_sped", 0.0);
            if (!(entity instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(UndeadRevamp2ModMobEffects.FLYINGSPPEDUP))) {
               if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof Player) {
                  Vec3 _center = new Vec3(x, y, z);

                  for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4.0), e -> true)
                     .stream()
                     .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                     .toList()) {
                     if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) != entityiterator) {
                        if (!world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 4.0, 4.0, 4.0), e -> true).isEmpty()) {
                           if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                              _entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 10, 20, false, false));
                           }
                        } else if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                           _entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 10, 30, false, false));
                        }

                        if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                           _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 45, 20, false, false));
                        }

                        if (world instanceof Level _level) {
                           if (!_level.isClientSide()) {
                              _level.playSound(
                                 null,
                                 BlockPos.containing(x, y, z),
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:hunter_fly")),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F
                              );
                           } else {
                              _level.playLocalSound(
                                 x,
                                 y,
                                 z,
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:hunter_fly")),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F,
                                 false
                              );
                           }
                        }

                        UndeadRevamp2Mod.queueServerWork(10, () -> {
                           if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                              _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.FLYINGSPPEDUP, 70, 4, false, false));
                           }
                        });
                     }
                  }
               } else if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity) {
                  Vec3 _center = new Vec3(x, y, z);

                  for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4.0), e -> true)
                     .stream()
                     .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                     .toList()) {
                     if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) != entityiteratorx) {
                        if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                           _entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 10, 30, false, false));
                        }

                        if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                           _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 45, 20, false, false));
                        }

                        if (world instanceof Level _levelx) {
                           if (!_levelx.isClientSide()) {
                              _levelx.playSound(
                                 null,
                                 BlockPos.containing(x, y, z),
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:hunter_fly")),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F
                              );
                           } else {
                              _levelx.playLocalSound(
                                 x,
                                 y,
                                 z,
                                 (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:hunter_fly")),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F,
                                 false
                              );
                           }
                        }

                        UndeadRevamp2Mod.queueServerWork(10, () -> {
                           if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                              _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.FLYINGSPPEDUP, 70, 4, false, false));
                           }
                        });
                     }
                  }
               }
            }
         }
      }
   }
}
