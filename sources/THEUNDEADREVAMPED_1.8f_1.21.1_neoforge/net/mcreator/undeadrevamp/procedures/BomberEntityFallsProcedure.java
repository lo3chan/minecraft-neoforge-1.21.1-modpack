package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.configuration.MobsabilityConfiguration;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BomberEntityFallsProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity.getPersistentData().getDouble("gaszz_sped") == 1.0) {
            entity.getPersistentData().putDouble("gaszz_sped", 0.0);
            if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity) {
               Vec3 _center = new Vec3(x, y, z);

               for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4.0), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entityiterator) {
                     if (world instanceof Level _level) {
                        if (!_level.isClientSide()) {
                           _level.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:bomber_ready")),
                              SoundSource.NEUTRAL,
                              0.5F,
                              1.0F
                           );
                        } else {
                           _level.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:bomber_ready")),
                              SoundSource.NEUTRAL,
                              0.5F,
                              1.0F,
                              false
                           );
                        }
                     }

                     if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 30, false, false));
                     }

                     if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 3, false, false));
                     }

                     if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.BOMBEREXPLODING, 58, 0, false, false));
                     }

                     entity.setShiftKeyDown(true);
                  }
               }

               UndeadRevamp2Mod.queueServerWork(
                  45,
                  () -> {
                     if (entity.isAlive()) {
                        if (entity instanceof LivingEntity _livEnt13 && _livEnt13.hasEffect(UndeadRevamp2ModMobEffects.BOMBEREXPLODING)) {
                           if (!entity.level().isClientSide()) {
                              entity.discard();
                           }

                           entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 50.0F);
                           if (world instanceof Level _levelx) {
                              if (!_levelx.isClientSide()) {
                                 _levelx.playSound(
                                    null,
                                    BlockPos.containing(x, y, z),
                                    (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.explode")),
                                    SoundSource.NEUTRAL,
                                    3.0F,
                                    1.0F
                                 );
                              } else {
                                 _levelx.playLocalSound(
                                    x,
                                    y,
                                    z,
                                    (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.explode")),
                                    SoundSource.NEUTRAL,
                                    3.0F,
                                    1.0F,
                                    false
                                 );
                              }
                           }

                           if (world instanceof ServerLevel _levelx) {
                              _levelx.sendParticles((SimpleParticleType)UndeadRevamp2ModParticleTypes.BOMBERGOO.get(), x, y, z, 700, 1.0, 1.0, 1.0, 1.0);
                           }

                           if (world instanceof ServerLevel _levelx) {
                              _levelx.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 25, 1.0, 1.0, 1.0, 1.0);
                           }

                           Vec3 _centerx = new Vec3(entity.getX(), entity.getY(), entity.getZ());

                           for (Entity entityiteratorx : world.getEntitiesOfClass(
                                 Entity.class, new AABB(_centerx, _centerx).inflate((Double)MobsabilityConfiguration.BOMBRAD.get() / 2.0), e -> true
                              )
                              .stream()
                              .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_centerx)))
                              .toList()) {
                              if (entityiteratorx != entity && entityiteratorx instanceof LivingEntity) {
                                 entityiteratorx.getPersistentData().putDouble("aoe_x", entity.getX() - entityiteratorx.getX());
                                 entityiteratorx.getPersistentData()
                                    .putDouble("aoe_y", entity.getY() + entity.getBbHeight() - (entityiteratorx.getY() + entityiteratorx.getBbHeight()));
                                 entityiteratorx.getPersistentData().putDouble("aoe_z", entity.getZ() - entityiteratorx.getZ());
                                 entityiteratorx.getPersistentData().putDouble("distance", 0.0);
                                 UndeadRevamp2Mod.queueServerWork(
                                    1,
                                    () -> {
                                       for (int index0 = 0; index0 < 20; index0++) {
                                          if (world.isEmptyBlock(
                                             BlockPos.containing(
                                                entity.getX()
                                                   + entityiteratorx.getPersistentData().getDouble("aoe_x")
                                                      * entityiteratorx.getPersistentData().getDouble("distance"),
                                                entity.getY()
                                                   + entity.getBbHeight()
                                                   + entityiteratorx.getPersistentData().getDouble("aoe_y")
                                                      * entityiteratorx.getPersistentData().getDouble("distance"),
                                                entity.getZ()
                                                   + entityiteratorx.getPersistentData().getDouble("aoe_z")
                                                      * entityiteratorx.getPersistentData().getDouble("distance")
                                             )
                                          )) {
                                             entityiteratorx.getPersistentData().putBoolean("behind_wall", false);
                                             entityiteratorx.getPersistentData()
                                                .putDouble("distance", entityiteratorx.getPersistentData().getDouble("distance") - 0.05);
                                          } else {
                                             entityiteratorx.getPersistentData().putBoolean("behind_wall", true);
                                          }

                                          UndeadRevamp2Mod.queueServerWork(
                                             1,
                                             () -> {
                                                if (!entityiteratorx.getPersistentData().getBoolean("behind_wall")) {
                                                   entityiteratorx.setDeltaMovement(
                                                      new Vec3(
                                                         Math.sin(Math.toRadians(entityiteratorx.getYRot() + 180.0F)) * 1.25 * -1.2,
                                                         (Math.sin(Math.toRadians(0.0F - entityiteratorx.getXRot())) + 0.55) * 1.1,
                                                         Math.cos(Math.toRadians(entityiteratorx.getYRot())) * 1.25 * -1.3
                                                      )
                                                   );
                                                   if (entityiteratorx instanceof LivingEntity _entityx && !_entityx.level().isClientSide()) {
                                                      _entityx.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.GOOED, 400, 0, false, true));
                                                   }

                                                   entityiteratorx.hurt(new DamageSource(world.holderOrThrow(DamageTypes.EXPLOSION), entity), 1.0F);
                                                }
                                             }
                                          );
                                       }
                                    }
                                 );
                              }
                           }
                        } else {
                           entity.setShiftKeyDown(false);
                        }
                     }
                  }
               );
            }
         }
      }
   }
}
