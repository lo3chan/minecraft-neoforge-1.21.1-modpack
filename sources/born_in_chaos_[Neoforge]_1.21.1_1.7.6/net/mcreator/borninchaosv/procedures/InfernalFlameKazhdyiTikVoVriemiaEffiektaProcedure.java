package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.FelsteedEntity;
import net.mcreator.borninchaosv.entity.InfernalSpiritEntity;
import net.mcreator.borninchaosv.entity.LordPumpkinheadEntity;
import net.mcreator.borninchaosv.entity.LordPumpkinheadHeadEntity;
import net.mcreator.borninchaosv.entity.LordPumpkinheadWithoutaHorseEntity;
import net.mcreator.borninchaosv.entity.LordTheHeadlessEntity;
import net.mcreator.borninchaosv.entity.LordsFelsteedEntity;
import net.mcreator.borninchaosv.entity.PumpkinheadEntity;
import net.mcreator.borninchaosv.entity.SirPumpkinheadEntity;
import net.mcreator.borninchaosv.entity.SirPumpkinheadWithoutHorseEntity;
import net.mcreator.borninchaosv.entity.SirTheHeadlessEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class InfernalFlameKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (world instanceof ServerLevel _level) {
            _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.ANIM_FIRE.get(), x, y + 1.0, z, 1, 0.3, 0.3, 0.3, 0.1);
         }

         if ((
               entity instanceof LordPumpkinheadEntity
                  || entity instanceof SirTheHeadlessEntity
                  || entity instanceof PumpkinheadEntity
                  || entity instanceof FelsteedEntity
                  || entity instanceof SirPumpkinheadEntity
                  || entity instanceof SirPumpkinheadWithoutHorseEntity
                  || entity instanceof LordPumpkinheadHeadEntity
                  || entity instanceof LordPumpkinheadWithoutaHorseEntity
                  || entity instanceof LordsFelsteedEntity
                  || entity instanceof InfernalSpiritEntity
                  || entity instanceof LordTheHeadlessEntity
            )
            && entity instanceof LivingEntity _entity) {
            _entity.removeEffect(BornInChaosV1ModMobEffects.INFERNAL_FLAME);
         }

         if (entity.getPersistentData().getDouble("infernal") == 0.0) {
            entity.getPersistentData().putDouble("infernal", 40.0);
         } else {
            entity.getPersistentData().putDouble("infernal", entity.getPersistentData().getDouble("infernal") - 1.0);
         }

         if (entity.getPersistentData().getDouble("infernal") == 0.0) {
            if (!(entity instanceof LivingEntity _livEnt18 && _livEnt18.hasEffect(MobEffects.FIRE_RESISTANCE))
               && (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) >= 30.0F) {
               entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.IN_FIRE)), 5.0F);
               if (world instanceof ServerLevel _level) {
                  _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.INFERNAL_SURGE.get(), x, y + 1.0, z, 3, 0.3, 0.3, 0.3, 0.1);
               }

               if (world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.hurt_on_fire")),
                        SoundSource.NEUTRAL,
                        0.9F,
                        0.8F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.hurt_on_fire")),
                        SoundSource.NEUTRAL,
                        0.9F,
                        0.8F,
                        false
                     );
                  }
               }
            } else if (!(entity instanceof LivingEntity _livEnt24 && _livEnt24.hasEffect(MobEffects.FIRE_RESISTANCE))
               && (entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) < 30.0F
               && (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) > 6.0F) {
               entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.IN_FIRE)), 3.0F);
               if (world instanceof ServerLevel _levelx) {
                  _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.INFERNAL_SURGE.get(), x, y + 1.0, z, 3, 0.3, 0.3, 0.3, 0.1);
               }

               if (world instanceof Level _levelx) {
                  if (!_levelx.isClientSide()) {
                     _levelx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.hurt_on_fire")),
                        SoundSource.NEUTRAL,
                        0.9F,
                        0.8F
                     );
                  } else {
                     _levelx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.hurt_on_fire")),
                        SoundSource.NEUTRAL,
                        0.9F,
                        0.8F,
                        false
                     );
                  }
               }
            } else if (!(entity instanceof LivingEntity _livEnt31 && _livEnt31.hasEffect(MobEffects.FIRE_RESISTANCE))
               && (entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) <= 6.0F
               && (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) > 3.0F) {
               entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.IN_FIRE)), 2.0F);
               if (world instanceof ServerLevel _levelxx) {
                  _levelxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.INFERNAL_SURGE.get(), x, y + 1.0, z, 3, 0.3, 0.3, 0.3, 0.1);
               }

               if (world instanceof Level _levelxx) {
                  if (!_levelxx.isClientSide()) {
                     _levelxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.hurt_on_fire")),
                        SoundSource.NEUTRAL,
                        0.9F,
                        0.8F
                     );
                  } else {
                     _levelxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.hurt_on_fire")),
                        SoundSource.NEUTRAL,
                        0.9F,
                        0.8F,
                        false
                     );
                  }
               }
            } else if (!(entity instanceof LivingEntity _livEnt38 && _livEnt38.hasEffect(MobEffects.FIRE_RESISTANCE))
               && (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) <= 3.0F) {
               entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.IN_FIRE)), 1.0F);
               if (world instanceof ServerLevel _levelxxx) {
                  _levelxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.INFERNAL_SURGE.get(), x, y + 1.0, z, 3, 0.3, 0.3, 0.3, 0.1);
               }

               if (world instanceof Level _levelxxx) {
                  if (!_levelxxx.isClientSide()) {
                     _levelxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.hurt_on_fire")),
                        SoundSource.NEUTRAL,
                        0.9F,
                        0.8F
                     );
                  } else {
                     _levelxxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.hurt_on_fire")),
                        SoundSource.NEUTRAL,
                        0.9F,
                        0.8F,
                        false
                     );
                  }
               }
            }
         }
      }
   }
}
