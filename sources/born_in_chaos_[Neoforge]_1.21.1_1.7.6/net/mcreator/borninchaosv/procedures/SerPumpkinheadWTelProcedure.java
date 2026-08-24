package net.mcreator.borninchaosv.procedures;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class SerPumpkinheadWTelProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(BornInChaosV1ModMobEffects.MAGIC_DEPLETION))
            && !entity.isOnFire()
            && (entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F)
               < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F) - 5.0F) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 160, 1, false, false));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.MAGIC_DEPLETION, 200, 0, false, false));
            }

            if (!world.getBlockState(BlockPos.containing(x, y, z)).canOcclude() || world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.SNOW) {
               if (world instanceof ServerLevel _level) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.MR_PUMPKIN.get())
                     .spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(entity.getYRot());
                     entityToSpawn.setYBodyRot(entity.getYRot());
                     entityToSpawn.setYHeadRot(entity.getYRot());
                     entityToSpawn.setXRot(entity.getXRot());
                  }
               }

               if (world instanceof ServerLevel _levelx) {
                  _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SOUL_FIRE.get(), x, y, z, 9, 0.3, 0.3, 0.3, 0.1);
               }

               if (world instanceof ServerLevel _levelx) {
                  _levelx.sendParticles(ParticleTypes.POOF, x, y, z, 6, 0.3, 0.3, 0.3, 0.1);
               }

               if (!world.isClientSide() && world instanceof Level _levelx) {
                  if (!_levelx.isClientSide()) {
                     _levelx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.glow_squid.squirt")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _levelx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.glow_squid.squirt")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }

               if ((
                     world.getBlockState(BlockPos.containing(x + 8.0, y + 1.0, z + 0.5)).canOcclude()
                        || world.getBlockState(BlockPos.containing(x + 8.0, y - 1.0, z + 0.5)).getBlock() == Blocks.LAVA
                           && world.getBlockState(BlockPos.containing(x + 8.0, y - 1.0, z + 0.5)).getBlock() == Blocks.WATER
                  )
                  && world.getBlockState(BlockPos.containing(x + 8.0, y, z + 0.5)).getBlock() != Blocks.SNOW) {
                  if ((
                        world.getBlockState(BlockPos.containing(x - 8.0, y + 1.0, z + 0.5)).canOcclude()
                           || world.getBlockState(BlockPos.containing(x - 8.0, y - 1.0, z + 0.5)).getBlock() == Blocks.LAVA
                              && world.getBlockState(BlockPos.containing(x - 8.0, y - 1.0, z + 0.5)).getBlock() == Blocks.WATER
                     )
                     && world.getBlockState(BlockPos.containing(x - 8.0, y, z + 0.5)).getBlock() != Blocks.SNOW) {
                     if ((
                           world.getBlockState(BlockPos.containing(x + 0.5, y + 1.0, z + 8.0)).canOcclude()
                              || world.getBlockState(BlockPos.containing(x + 0.5, y - 1.0, z + 8.0)).getBlock() == Blocks.LAVA
                                 && world.getBlockState(BlockPos.containing(x + 0.5, y - 1.0, z + 8.0)).getBlock() == Blocks.WATER
                        )
                        && world.getBlockState(BlockPos.containing(x + 0.5, y, z + 8.0)).getBlock() != Blocks.SNOW) {
                        entity.teleportTo(x + 0.5, y + 1.0, z - 8.0);
                        if (entity instanceof ServerPlayer _serverPlayer) {
                           _serverPlayer.connection.teleport(x + 0.5, y + 1.0, z - 8.0, entity.getYRot(), entity.getXRot());
                        }
                     } else {
                        entity.teleportTo(x + 0.5, y + 1.0, z + 8.0);
                        if (entity instanceof ServerPlayer _serverPlayer) {
                           _serverPlayer.connection.teleport(x + 0.5, y + 1.0, z + 8.0, entity.getYRot(), entity.getXRot());
                        }
                     }
                  } else {
                     entity.teleportTo(x - 8.0, y + 1.0, z + 0.5);
                     if (entity instanceof ServerPlayer _serverPlayer) {
                        _serverPlayer.connection.teleport(x - 8.0, y + 1.0, z + 0.5, entity.getYRot(), entity.getXRot());
                     }
                  }
               } else {
                  entity.teleportTo(x + 8.0, y + 1.0, z + 0.5);
                  if (entity instanceof ServerPlayer _serverPlayer) {
                     _serverPlayer.connection.teleport(x + 8.0, y + 1.0, z + 0.5, entity.getYRot(), entity.getXRot());
                  }
               }

               if (world instanceof ServerLevel _levelxx) {
                  _levelxx.sendParticles(ParticleTypes.POOF, x, y, z, 9, 1.0, 1.0, 1.0, 1.0);
               }

               if (!world.isClientSide() && world instanceof Level _levelxx) {
                  if (!_levelxx.isClientSide()) {
                     _levelxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.enderman.teleport")),
                        SoundSource.NEUTRAL,
                        0.8F,
                        1.0F
                     );
                  } else {
                     _levelxx.playLocalSound(
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
         }
      }
   }
}
