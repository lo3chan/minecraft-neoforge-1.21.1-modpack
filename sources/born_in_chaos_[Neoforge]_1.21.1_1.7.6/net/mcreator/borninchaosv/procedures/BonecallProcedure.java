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

public class BonecallProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!entity.isOnFire()
            && !(entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(BornInChaosV1ModMobEffects.MAGIC_DEPLETION))
            && (entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F)
               < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F) - 5.0F) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 160, 3, false, false));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 160, 1, false, false));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.MAGIC_DEPLETION, 260, 0, false, false));
            }

            if (!world.isClientSide()) {
               if (world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton.ambient")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton.ambient")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }

               if (world instanceof Level _levelx) {
                  if (!_levelx.isClientSide()) {
                     _levelx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.evoker.prepare_summon")),
                        SoundSource.NEUTRAL,
                        0.6F,
                        1.0F
                     );
                  } else {
                     _levelx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.evoker.prepare_summon")),
                        SoundSource.NEUTRAL,
                        0.6F,
                        1.0F,
                        false
                     );
                  }
               }
            }

            if (!world.getBlockState(BlockPos.containing(x - 2.0, y, z + 0.5)).canOcclude()
               || world.getBlockState(BlockPos.containing(x - 2.0, y, z + 0.5)).getBlock() == Blocks.SNOW) {
               if (world instanceof ServerLevel _levelxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.BONE_IMP_MINION.get())
                     .spawn(_levelxx, BlockPos.containing(x - 2.0, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(entity.getYRot());
                     entityToSpawn.setYBodyRot(entity.getYRot());
                     entityToSpawn.setYHeadRot(entity.getYRot());
                     entityToSpawn.setXRot(entity.getXRot());
                  }
               }

               if (world instanceof ServerLevel _levelxxx) {
                  _levelxxx.sendParticles(ParticleTypes.POOF, x - 2.0, y, z + 0.5, 5, 0.3, 0.3, 0.3, 0.1);
               }

               if (world instanceof ServerLevel _levelxxx) {
                  _levelxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.RITUAL.get(), x - 2.0, y, z + 0.5, 5, 0.3, 0.3, 0.3, 0.1);
               }
            }

            if (!world.getBlockState(BlockPos.containing(x + 2.0, y, z + 0.5)).canOcclude()
               || world.getBlockState(BlockPos.containing(x + 2.0, y, z + 0.5)).getBlock() == Blocks.SNOW) {
               if (world instanceof ServerLevel _levelxxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.BONE_IMP_MINION.get())
                     .spawn(_levelxxx, BlockPos.containing(x + 2.0, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(entity.getYRot());
                     entityToSpawn.setYBodyRot(entity.getYRot());
                     entityToSpawn.setYHeadRot(entity.getYRot());
                     entityToSpawn.setXRot(entity.getXRot());
                  }
               }

               if (world instanceof ServerLevel _levelxxxx) {
                  _levelxxxx.sendParticles(ParticleTypes.POOF, x + 2.0, y, z + 0.5, 5, 0.3, 0.3, 0.3, 0.1);
               }

               if (world instanceof ServerLevel _levelxxxx) {
                  _levelxxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.RITUAL.get(), x + 2.0, y, z + 0.5, 5, 0.3, 0.3, 0.3, 0.1);
               }
            }

            if (!world.getBlockState(BlockPos.containing(x + 0.5, y, z + 1.5)).canOcclude()
               || world.getBlockState(BlockPos.containing(x + 0.5, y, z + 1.5)).getBlock() == Blocks.SNOW) {
               if (world instanceof ServerLevel _levelxxxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.BONE_IMP_MINION.get())
                     .spawn(_levelxxxx, BlockPos.containing(x + 0.5, y, z + 1.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(entity.getYRot());
                     entityToSpawn.setYBodyRot(entity.getYRot());
                     entityToSpawn.setYHeadRot(entity.getYRot());
                     entityToSpawn.setXRot(entity.getXRot());
                  }
               }

               if (world instanceof ServerLevel _levelxxxxx) {
                  _levelxxxxx.sendParticles(ParticleTypes.POOF, x + 0.5, y, z + 1.5, 5, 0.3, 0.3, 0.3, 0.1);
               }

               if (world instanceof ServerLevel _levelxxxxx) {
                  _levelxxxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.RITUAL.get(), x + 0.5, y, z + 1.5, 5, 0.3, 0.3, 0.3, 0.1);
               }
            }
         }
      }
   }
}
