package net.mcreator.borninchaosv.procedures;

import java.util.Comparator;
import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LordPumpkinheadhitProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEntxx ? _livEntxx.getHealth() : -1.0F) > 20.0F
            && (entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F)
               < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F) - 10.0F
            && !(entity instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK))) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 80, 1, false, false));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BLOCK_BREAK, 120, 0, false, false));
            }

            if (Math.random() < 0.3) {
               if (!world.getBlockState(BlockPos.containing(x + 0.5, y + 1.0, z + 0.5)).canOcclude()
                  || world.getBlockState(BlockPos.containing(x + 0.5, y + 1.0, z + 0.5)).getBlock() == Blocks.SNOW) {
                  if (world instanceof ServerLevel _level) {
                     _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.ANIM_FIRE.get(), x - 0.5, y + 1.0, z + 0.5, 9, 0.2, 0.3, 0.2, 0.1);
                  }

                  if (world instanceof ServerLevel _level) {
                     _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DARK_SMOKE.get(), x - 0.5, y + 1.0, z + 0.5, 6, 0.2, 0.3, 0.2, 0.1);
                  }

                  if (!world.isClientSide() && world instanceof Level _level) {
                     if (!_level.isClientSide()) {
                        _level.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.glow_squid.squirt")),
                           SoundSource.NEUTRAL,
                           0.8F,
                           1.0F
                        );
                     } else {
                        _level.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.glow_squid.squirt")),
                           SoundSource.NEUTRAL,
                           0.8F,
                           1.0F,
                           false
                        );
                     }
                  }

                  if (Math.random() < 0.3) {
                     if (world instanceof ServerLevel _levelx) {
                        Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.MRS_PUMPKIN.get())
                           .spawn(_levelx, BlockPos.containing(x - 0.5, y + 1.0, z + 0.5), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                           entityToSpawn.setYRot(entity.getYRot());
                           entityToSpawn.setYBodyRot(entity.getYRot());
                           entityToSpawn.setYHeadRot(entity.getYRot());
                           entityToSpawn.setXRot(entity.getXRot());
                        }
                     }
                  } else if (Math.random() < 0.13) {
                     if (world instanceof ServerLevel _levelxx) {
                        Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.PUMPKIN_BRUISER.get())
                           .spawn(_levelxx, BlockPos.containing(x - 0.5, y + 1.0, z + 0.5), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                           entityToSpawn.setYRot(entity.getYRot());
                           entityToSpawn.setYBodyRot(entity.getYRot());
                           entityToSpawn.setYHeadRot(entity.getYRot());
                           entityToSpawn.setXRot(entity.getXRot());
                        }
                     }
                  } else if (Math.random() < 0.3) {
                     for (int index0 = 0; index0 < 2; index0++) {
                        if (world instanceof ServerLevel _levelxxx) {
                           Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.PUMPKIN_DUNCE.get())
                              .spawn(_levelxxx, BlockPos.containing(x - 0.5, y + 1.0, z + 0.5), MobSpawnType.MOB_SUMMONED);
                           if (entityToSpawn != null) {
                              entityToSpawn.setYRot(entity.getYRot());
                              entityToSpawn.setYBodyRot(entity.getYRot());
                              entityToSpawn.setYHeadRot(entity.getYRot());
                              entityToSpawn.setXRot(entity.getXRot());
                           }
                        }
                     }
                  } else if (world instanceof ServerLevel _levelxxxx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.SENOR_PUMPKIN.get())
                        .spawn(_levelxxxx, BlockPos.containing(x - 0.5, y + 1.0, z + 0.5), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setYRot(entity.getYRot());
                        entityToSpawn.setYBodyRot(entity.getYRot());
                        entityToSpawn.setYHeadRot(entity.getYRot());
                        entityToSpawn.setXRot(entity.getXRot());
                     }
                  }
               }
            } else {
               if (world instanceof ServerLevel _levelxxxxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.PUMPKIN_BOMB.get())
                     .spawn(_levelxxxxx, BlockPos.containing(x + 0.5, y + 1.0, z + 0.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(entity.getYRot());
                     entityToSpawn.setYBodyRot(entity.getYRot());
                     entityToSpawn.setYHeadRot(entity.getYRot());
                     entityToSpawn.setXRot(entity.getXRot());
                  }
               }

               if (world instanceof ServerLevel _levelxxxxxx) {
                  _levelxxxxxx.sendParticles(
                     (SimpleParticleType)BornInChaosV1ModParticleTypes.ANIM_FIRE.get(), x + 0.5, y + 1.0, z + 0.5, 9, 0.2, 0.3, 0.2, 0.1
                  );
               }

               if (world instanceof ServerLevel _levelxxxxxx) {
                  _levelxxxxxx.sendParticles(
                     (SimpleParticleType)BornInChaosV1ModParticleTypes.DARK_SMOKE.get(), x + 0.5, y + 1.0, z + 0.5, 6, 0.2, 0.3, 0.2, 0.1
                  );
               }

               if (!world.isClientSide() && world instanceof Level _levelxxxxxx) {
                  if (!_levelxxxxxx.isClientSide()) {
                     _levelxxxxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.glow_squid.squirt")),
                        SoundSource.NEUTRAL,
                        0.8F,
                        1.0F
                     );
                  } else {
                     _levelxxxxxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.glow_squid.squirt")),
                        SoundSource.NEUTRAL,
                        0.8F,
                        1.0F,
                        false
                     );
                  }
               }
            }

            if (world instanceof ServerLevel _levelxxxxxxx) {
               _levelxxxxxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DARK_SMOKE.get(), x, y, z, 10, 0.3, 0.8, 0.3, 1.0);
            }

            if (!world.isClientSide() && world instanceof Level _levelxxxxxxx) {
               if (!_levelxxxxxxx.isClientSide()) {
                  _levelxxxxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.enderman.teleport")),
                     SoundSource.NEUTRAL,
                     0.8F,
                     1.0F
                  );
               } else {
                  _levelxxxxxxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.enderman.teleport")),
                     SoundSource.NEUTRAL,
                     0.8F,
                     1.0F,
                     false
                  );
               }
            }

            if (!(Math.random() < 0.6)
               || world.getBlockState(BlockPos.containing(x + 10.0, y + 2.0, z + 0.5)).canOcclude()
               || world.getBlockState(BlockPos.containing(x + 10.0, y + 1.0, z + 0.5)).canOcclude()
                  && world.getBlockState(BlockPos.containing(x + 10.0, y + 1.0, z + 0.5)).getBlock() != Blocks.SNOW) {
               if (!(Math.random() < 0.6)
                  || world.getBlockState(BlockPos.containing(x - 10.0, y + 2.0, z + 0.5)).canOcclude()
                  || world.getBlockState(BlockPos.containing(x - 10.0, y + 1.0, z + 0.5)).canOcclude()
                     && world.getBlockState(BlockPos.containing(x + 10.0, y + 1.0, z + 0.5)).getBlock() != Blocks.SNOW) {
                  if (Math.random() < 0.6
                     && !world.getBlockState(BlockPos.containing(x + 0.5, y + 2.0, z + 10.0)).canOcclude()
                     && (
                        !world.getBlockState(BlockPos.containing(x + 0.5, y + 1.0, z + 10.0)).canOcclude()
                           || world.getBlockState(BlockPos.containing(x + 0.5, y + 1.0, z + 10.0)).getBlock() == Blocks.SNOW
                     )) {
                     entity.teleportTo(x + 0.5, y + 1.0, z + 10.0);
                     if (entity instanceof ServerPlayer _serverPlayer) {
                        _serverPlayer.connection.teleport(x + 0.5, y + 1.0, z + 10.0, entity.getYRot(), entity.getXRot());
                     }
                  } else {
                     entity.teleportTo(x + 0.5, y + 1.0, z - 10.0);
                     if (entity instanceof ServerPlayer _serverPlayer) {
                        _serverPlayer.connection.teleport(x + 0.5, y + 1.0, z - 10.0, entity.getYRot(), entity.getXRot());
                     }
                  }
               } else {
                  entity.teleportTo(x - 10.0, y + 1.0, z + 0.5);
                  if (entity instanceof ServerPlayer _serverPlayer) {
                     _serverPlayer.connection.teleport(x - 10.0, y + 1.0, z + 0.5, entity.getYRot(), entity.getXRot());
                  }
               }
            } else {
               entity.teleportTo(x + 10.0, y + 1.0, z + 0.5);
               if (entity instanceof ServerPlayer _serverPlayer) {
                  _serverPlayer.connection.teleport(x + 10.0, y + 1.0, z + 0.5, entity.getYRot(), entity.getXRot());
               }
            }
         }

         if (entity.isPassenger()) {
            entity.stopRiding();
            if (!world.isClientSide() && world instanceof Level _levelxxxxxxxx) {
               if (!_levelxxxxxxxx.isClientSide()) {
                  _levelxxxxxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.break_wooden_door")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelxxxxxxxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.break_wooden_door")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.5), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiterator instanceof Boat || entityiterator instanceof ChestBoat) {
                  if (!entityiterator.level().isClientSide()) {
                     entityiterator.discard();
                  }

                  if (world instanceof ServerLevel _levelxxxxxxxxx) {
                     _levelxxxxxxxxx.sendParticles(
                        ParticleTypes.CRIT, entityiterator.getX(), entityiterator.getY() + 1.0, entityiterator.getZ(), 5, 0.3, 0.2, 0.3, 0.1
                     );
                  }
               }
            }

            _center = new Vec3(x, y, z);

            for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiteratorx instanceof Player && entityiteratorx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.CURSEOFTHE_BOAT, 200, 0));
               }
            }
         }
      }
   }
}
