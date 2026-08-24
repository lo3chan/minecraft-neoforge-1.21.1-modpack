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
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SerPumpkinheadPiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!entity.isOnFire() && !(entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(BornInChaosV1ModMobEffects.MAGIC_DEPLETION))) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 150, 1, false, false));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.MAGIC_DEPLETION, 200, 0, false, false));
            }

            if (!world.getBlockState(BlockPos.containing(x - 2.0, y, z + 0.5)).canOcclude()
               || world.getBlockState(BlockPos.containing(x - 2.0, y, z + 0.5)).getBlock() == Blocks.SNOW) {
               if (world instanceof ServerLevel _level) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.MR_PUMPKIN.get())
                     .spawn(_level, BlockPos.containing(x - 2.0, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(entity.getYRot());
                     entityToSpawn.setYBodyRot(entity.getYRot());
                     entityToSpawn.setYHeadRot(entity.getYRot());
                     entityToSpawn.setXRot(entity.getXRot());
                  }
               }

               if (world instanceof ServerLevel _levelx) {
                  _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SOUL_FIRE.get(), x - 2.0, y, z + 0.5, 9, 0.3, 0.3, 0.3, 0.1);
               }

               if (world instanceof ServerLevel _levelx) {
                  _levelx.sendParticles(ParticleTypes.POOF, x - 2.0, y, z + 0.5, 6, 0.3, 0.3, 0.3, 0.1);
               }

               if (!world.isClientSide() && world instanceof Level _levelx) {
                  if (!_levelx.isClientSide()) {
                     _levelx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.glow_squid.squirt")),
                        SoundSource.NEUTRAL,
                        0.8F,
                        1.0F
                     );
                  } else {
                     _levelx.playLocalSound(
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

            if (!world.getBlockState(BlockPos.containing(x - 2.0, y, z + 0.5)).canOcclude()
               || world.getBlockState(BlockPos.containing(x - 2.0, y, z + 0.5)).getBlock() == Blocks.SNOW) {
               if (world instanceof ServerLevel _levelxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.MR_PUMPKIN.get())
                     .spawn(_levelxx, BlockPos.containing(x + 2.0, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(entity.getYRot());
                     entityToSpawn.setYBodyRot(entity.getYRot());
                     entityToSpawn.setYHeadRot(entity.getYRot());
                     entityToSpawn.setXRot(entity.getXRot());
                  }
               }

               if (world instanceof ServerLevel _levelxxx) {
                  _levelxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SOUL_FIRE.get(), x + 2.0, y, z + 0.5, 9, 0.3, 0.3, 0.3, 0.1);
               }

               if (world instanceof ServerLevel _levelxxx) {
                  _levelxxx.sendParticles(ParticleTypes.POOF, x - 2.0, y, z + 0.5, 6, 0.3, 0.3, 0.3, 0.1);
               }

               if (!world.isClientSide() && world instanceof Level _levelxxx) {
                  if (!_levelxxx.isClientSide()) {
                     _levelxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.glow_squid.squirt")),
                        SoundSource.NEUTRAL,
                        0.8F,
                        1.0F
                     );
                  } else {
                     _levelxxx.playLocalSound(
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

            if (!world.getBlockState(BlockPos.containing(x + 8.0, y + 1.0, z + 0.5)).canOcclude()) {
               entity.teleportTo(x + 8.0, y + 1.0, z + 0.5);
               if (entity instanceof ServerPlayer _serverPlayer) {
                  _serverPlayer.connection.teleport(x + 8.0, y + 1.0, z + 0.5, entity.getYRot(), entity.getXRot());
               }
            } else if (!world.getBlockState(BlockPos.containing(x - 8.0, y + 1.0, z + 0.5)).canOcclude()) {
               entity.teleportTo(x - 8.0, y + 1.0, z + 0.5);
               if (entity instanceof ServerPlayer _serverPlayer) {
                  _serverPlayer.connection.teleport(x - 8.0, y + 1.0, z + 0.5, entity.getYRot(), entity.getXRot());
               }
            } else if (!world.getBlockState(BlockPos.containing(x + 0.5, y + 1.0, z + 8.0)).canOcclude()) {
               entity.teleportTo(x + 0.5, y + 1.0, z + 8.0);
               if (entity instanceof ServerPlayer _serverPlayer) {
                  _serverPlayer.connection.teleport(x + 0.5, y + 1.0, z + 8.0, entity.getYRot(), entity.getXRot());
               }
            } else {
               entity.teleportTo(x + 0.5, y + 1.0, z - 8.0);
               if (entity instanceof ServerPlayer _serverPlayer) {
                  _serverPlayer.connection.teleport(x + 0.5, y + 1.0, z - 8.0, entity.getYRot(), entity.getXRot());
               }
            }

            if (world instanceof ServerLevel _levelxxxx) {
               _levelxxxx.sendParticles(ParticleTypes.POOF, x, y, z, 9, 1.0, 1.0, 1.0, 1.0);
            }

            if (!world.isClientSide() && world instanceof Level _levelxxxx) {
               if (!_levelxxxx.isClientSide()) {
                  _levelxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.enderman.teleport")),
                     SoundSource.NEUTRAL,
                     0.8F,
                     1.0F
                  );
               } else {
                  _levelxxxx.playLocalSound(
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
         }

         if (entity.isPassenger()) {
            entity.stopRiding();
            if (!world.isClientSide() && world instanceof Level _levelxxxxx) {
               if (!_levelxxxxx.isClientSide()) {
                  _levelxxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.break_wooden_door")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelxxxxx.playLocalSound(
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

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(1.5), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiterator instanceof Boat || entityiterator instanceof ChestBoat) {
                  if (!entityiterator.level().isClientSide()) {
                     entityiterator.discard();
                  }

                  if (world instanceof ServerLevel _levelxxxxxx) {
                     _levelxxxxxx.sendParticles(
                        ParticleTypes.CRIT, entityiterator.getX(), entityiterator.getY() + 1.0, entityiterator.getZ(), 5, 0.3, 0.2, 0.3, 0.1
                     );
                  }
               }
            }
         }
      }
   }
}
