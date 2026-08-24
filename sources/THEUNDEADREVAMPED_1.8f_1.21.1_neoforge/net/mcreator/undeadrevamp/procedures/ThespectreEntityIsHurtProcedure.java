package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.ThespectreEntity;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class ThespectreEntityIsHurtProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity.isAlive() && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.BROKENTANK, 250, 1, false, false));
         }

         if (!entity.getPersistentData().getBoolean("brokedone")
            && entity.isAlive()
            && !(entity instanceof LivingEntity _livEnt4 && _livEnt4.hasEffect(UndeadRevamp2ModMobEffects.TANKLEAK))
            && Math.random() < 0.1) {
            entity.getPersistentData().putBoolean("brokedone", true);
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.TANKLEAK, 10000, 1, false, false));
            }

            if ((entity instanceof ThespectreEntity animatable ? animatable.getTexture() : "null").equals("spectrestrongervariant")) {
               if (world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.glass.break")),
                        SoundSource.NEUTRAL,
                        2.5F,
                        1.0F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.glass.break")),
                        SoundSource.NEUTRAL,
                        2.5F,
                        1.0F,
                        false
                     );
                  }
               }

               if (entity instanceof ThespectreEntity animatablex) {
                  animatablex.setTexture("stronngerspectrebrokentank");
               }
            } else if ((entity instanceof ThespectreEntity animatable ? animatable.getTexture() : "null").equals("spectre")) {
               if (entity instanceof ThespectreEntity animatablex) {
                  animatablex.setTexture("spectrebrokentank");
               }

               if (world instanceof Level _levelx) {
                  if (!_levelx.isClientSide()) {
                     _levelx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.glass.break")),
                        SoundSource.NEUTRAL,
                        2.5F,
                        1.0F
                     );
                  } else {
                     _levelx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.glass.break")),
                        SoundSource.NEUTRAL,
                        2.5F,
                        1.0F,
                        false
                     );
                  }
               }
            }
         }

         if (!(entity instanceof LivingEntity _livEnt13 && _livEnt13.hasEffect(UndeadRevamp2ModMobEffects.ANIMATIONTEST)) && Math.random() < 0.1) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.ANIMATIONTEST, 35, 0, false, false));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 90, 1, false, false));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 35, 6, false, false));
            }
         }
      }
   }
}
