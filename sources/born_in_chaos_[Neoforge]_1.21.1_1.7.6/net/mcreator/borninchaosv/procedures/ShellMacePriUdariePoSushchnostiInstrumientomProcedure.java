package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class ShellMacePriUdariePoSushchnostiInstrumientomProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof WaterAnimal && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 160, 0, false, false));
         }

         if ((entity.isInWaterRainOrBubble() || entity.getType().is(EntityTypeTags.AQUATIC) || entity instanceof Drowned)
            && !(entity instanceof LivingEntity _livEnt5 && _livEnt5.hasEffect(BornInChaosV1ModMobEffects.BARBEDATTACK))) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BARBEDATTACK, 10, 0, false, false));
            }

            if (entity.isAlive() && !(entity instanceof LivingEntity _livEnt8 && _livEnt8.isBlocking()) && entity.isAlive()) {
               if (world instanceof ServerLevel _level) {
                  _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SPIKERELEASE.get(), x, y + 1.2, z, 3, 0.3, 0.3, 0.3, 0.1);
               }

               if (world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                        SoundSource.NEUTRAL,
                        0.8F,
                        0.7F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                        SoundSource.NEUTRAL,
                        0.8F,
                        0.7F,
                        false
                     );
                  }
               }

               if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) > 16.0F) {
                  if (entity instanceof LivingEntity _entity) {
                     _entity.setHealth((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) - 8.0F);
                  }
               } else if ((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) > 12.0F) {
                  if (entity instanceof LivingEntity _entity) {
                     _entity.setHealth((entity instanceof LivingEntity _livEntxx ? _livEntxx.getHealth() : -1.0F) - 4.0F);
                  }
               } else if ((entity instanceof LivingEntity _livEntxx ? _livEntxx.getHealth() : -1.0F) == 12.0F) {
                  if (entity instanceof LivingEntity _entity) {
                     _entity.setHealth((entity instanceof LivingEntity _livEntxxx ? _livEntxxx.getHealth() : -1.0F) - 3.0F);
                  }
               } else if ((entity instanceof LivingEntity _livEntxxx ? _livEntxxx.getHealth() : -1.0F) == 11.0F) {
                  if (entity instanceof LivingEntity _entity) {
                     _entity.setHealth((entity instanceof LivingEntity _livEntxxxx ? _livEntxxxx.getHealth() : -1.0F) - 2.0F);
                  }
               } else if ((entity instanceof LivingEntity _livEntxxxx ? _livEntxxxx.getHealth() : -1.0F) == 10.0F && entity instanceof LivingEntity _entity) {
                  _entity.setHealth((entity instanceof LivingEntity _livEntxxxxx ? _livEntxxxxx.getHealth() : -1.0F) - 1.0F);
               }
            }
         }
      }
   }
}
