package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.MissionaryRaiderEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class MissionaryRaiderHitProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!world.getLevelData().isRaining()
            && entity.getPersistentData().getBoolean("callforrain")
            && (entity instanceof LivingEntity _livEntxx ? _livEntxx.getHealth() : -1.0F) > 100.0F
            && (entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F)
               < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F)) {
            world.getLevelData().setRaining(true);
            if (world instanceof ServerLevel _level) {
               LightningBolt entityToSpawn = (LightningBolt)EntityType.LIGHTNING_BOLT.create(_level);
               entityToSpawn.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(x, y, z)));
               entityToSpawn.setVisualOnly(true);
               _level.addFreshEntity(entityToSpawn);
            }

            entity.getPersistentData().putBoolean("callforrain", false);
         }

         if ((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F)
            <= (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F) - 10.0F) {
            if (!(entity instanceof LivingEntity _livEnt10 && _livEnt10.hasEffect(BornInChaosV1ModMobEffects.STUNNING_STRIKE))
               && !(entity instanceof LivingEntity _livEnt11 && _livEnt11.hasEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK))
               && entity instanceof LivingEntity _livEnt12
               && _livEnt12.hasEffect(BornInChaosV1ModMobEffects.MAGIC_DEPLETION)) {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.STUNNING_STRIKE, 42, 0, false, false));
               }

               if (entity instanceof MissionaryRaiderEntity) {
                  ((MissionaryRaiderEntity)entity).setAnimation("stun");
               }
            }

            if (!(entity instanceof LivingEntity _livEnt15 && _livEnt15.hasEffect(BornInChaosV1ModMobEffects.UNDEAD_SUMMONUN))
               && !(entity instanceof LivingEntity _livEnt16 && _livEnt16.hasEffect(BornInChaosV1ModMobEffects.MAGIC_DEPLETION))) {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.MAGIC_DEPLETION, 300, 0, false, false));
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.UNDEAD_SUMMONUN, 60, 0, false, false));
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 2, false, false));
               }

               if (!world.getBlockState(BlockPos.containing(x + 8.0, y, z)).canOcclude()
                  && !world.getBlockState(BlockPos.containing(x + 8.0, y + 1.0, z)).canOcclude()) {
                  entity.teleportTo(x + 8.0, y, z);
                  if (entity instanceof ServerPlayer _serverPlayer) {
                     _serverPlayer.connection.teleport(x + 8.0, y, z, entity.getYRot(), entity.getXRot());
                  }

                  if (world instanceof ServerLevel _level) {
                     _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DIMLARG.get(), x, y, z, 8, 0.4, 0.6, 0.4, 0.1);
                  }

                  if (!world.isClientSide() && world instanceof Level _level) {
                     if (!_level.isClientSide()) {
                        _level.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:missionary_teleport")),
                           SoundSource.NEUTRAL,
                           0.7F,
                           1.0F
                        );
                     } else {
                        _level.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:missionary_teleport")),
                           SoundSource.NEUTRAL,
                           0.7F,
                           1.0F,
                           false
                        );
                     }
                  }
               } else if (!world.getBlockState(BlockPos.containing(x - 8.0, y, z)).canOcclude()
                  && !world.getBlockState(BlockPos.containing(x - 8.0, y + 1.0, z)).canOcclude()) {
                  if (world instanceof ServerLevel _levelx) {
                     _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DIM.get(), x, y, z, 8, 0.4, 0.6, 0.4, 0.1);
                  }

                  entity.teleportTo(x - 8.0, y, z);
                  if (entity instanceof ServerPlayer _serverPlayer) {
                     _serverPlayer.connection.teleport(x - 8.0, y, z, entity.getYRot(), entity.getXRot());
                  }

                  if (!world.isClientSide() && world instanceof Level _levelx) {
                     if (!_levelx.isClientSide()) {
                        _levelx.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:missionary_teleport")),
                           SoundSource.NEUTRAL,
                           0.7F,
                           1.0F
                        );
                     } else {
                        _levelx.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:missionary_teleport")),
                           SoundSource.NEUTRAL,
                           0.7F,
                           1.0F,
                           false
                        );
                     }
                  }
               } else if (!world.getBlockState(BlockPos.containing(x, y, z + 8.0)).canOcclude()
                  && !world.getBlockState(BlockPos.containing(x, y + 1.0, z + 8.0)).canOcclude()) {
                  entity.teleportTo(x, y, z + 8.0);
                  if (entity instanceof ServerPlayer _serverPlayer) {
                     _serverPlayer.connection.teleport(x, y, z + 8.0, entity.getYRot(), entity.getXRot());
                  }

                  if (world instanceof ServerLevel _levelxx) {
                     _levelxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DIM.get(), x, y, z, 8, 0.4, 0.6, 0.4, 0.1);
                  }

                  if (!world.isClientSide() && world instanceof Level _levelxx) {
                     if (!_levelxx.isClientSide()) {
                        _levelxx.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:missionary_teleport")),
                           SoundSource.NEUTRAL,
                           0.7F,
                           1.0F
                        );
                     } else {
                        _levelxx.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:missionary_teleport")),
                           SoundSource.NEUTRAL,
                           0.7F,
                           1.0F,
                           false
                        );
                     }
                  }
               } else if (!world.getBlockState(BlockPos.containing(x, y, z - 8.0)).canOcclude()
                  && !world.getBlockState(BlockPos.containing(x, y + 1.0, z - 8.0)).canOcclude()) {
                  entity.teleportTo(x, y, z - 8.0);
                  if (entity instanceof ServerPlayer _serverPlayer) {
                     _serverPlayer.connection.teleport(x, y, z - 8.0, entity.getYRot(), entity.getXRot());
                  }

                  if (world instanceof ServerLevel _levelxxx) {
                     _levelxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DIM.get(), x, y, z, 8, 0.4, 0.6, 0.4, 0.1);
                  }

                  if (!world.isClientSide() && world instanceof Level _levelxxx) {
                     if (!_levelxxx.isClientSide()) {
                        _levelxxx.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:missionary_teleport")),
                           SoundSource.NEUTRAL,
                           0.7F,
                           1.0F
                        );
                     } else {
                        _levelxxx.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:missionary_teleport")),
                           SoundSource.NEUTRAL,
                           0.7F,
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
}
