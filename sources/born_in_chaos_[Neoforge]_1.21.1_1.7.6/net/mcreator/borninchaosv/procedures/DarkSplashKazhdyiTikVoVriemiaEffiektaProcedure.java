package net.mcreator.borninchaosv.procedures;

import java.util.Comparator;
import net.mcreator.borninchaosv.entity.LifestealerEntity;
import net.mcreator.borninchaosv.entity.LifestealerTrueFormEntity;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DarkSplashKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity.isAlive() && (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) > 15.0F) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 70, 3, false, false));
            }

            if (entity.getPersistentData().getDouble("chargedsplash") == 0.0) {
               entity.getPersistentData().putDouble("chargedsplash", 50.0);
            } else {
               entity.getPersistentData().putDouble("chargedsplash", entity.getPersistentData().getDouble("chargedsplash") - 1.0);
            }

            if (entity.getPersistentData().getDouble("chargedsplash") == 0.0) {
               if (entity instanceof LivingEntity _entity) {
                  _entity.removeEffect(BornInChaosV1ModMobEffects.DARK_SPLASH);
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0, false, false));
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BLOCK_BREAK, 260, 0, false, false));
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.setHealth((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) + 10.0F);
               }

               if (world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:lifestealer_scream_ap")),
                        SoundSource.NEUTRAL,
                        2.0F,
                        1.0F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:lifestealer_scream_ap")),
                        SoundSource.NEUTRAL,
                        2.0F,
                        1.0F,
                        false
                     );
                  }
               }

               if (world instanceof ServerLevel _levelx) {
                  _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DIM_LONG.get(), x, y + 1.5, z, 25, 0.5, 0.7, 0.5, 0.5);
               }

               Vec3 _center = new Vec3(x, y, z);

               for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5.5), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if (!(entityiterator instanceof LifestealerTrueFormEntity)
                     && !(entityiterator instanceof LifestealerEntity)
                     && !(entityiterator instanceof LivingEntity _livEnt17 && _livEnt17.hasEffect(BornInChaosV1ModMobEffects.WITHER_RESISTANCE))) {
                     entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.WITHER), entity), 10.0F);
                     if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.LIFESTEAL, 260, 0));
                     }

                     if (world instanceof ServerLevel _levelx) {
                        _levelx.sendParticles(
                           (SimpleParticleType)BornInChaosV1ModParticleTypes.DARKMATTER.get(),
                           entityiterator.getX(),
                           entityiterator.getY() + 0.5,
                           entityiterator.getZ(),
                           4,
                           0.4,
                           0.8,
                           0.4,
                           0.1
                        );
                     }

                     entityiterator.setDeltaMovement(
                        new Vec3(2.0 * Math.sin(Math.toRadians(entity.getYRot() + 180.0F)), 0.0, 2.0 * Math.cos(Math.toRadians(entity.getYRot())))
                     );
                  }
               }
            }
         }
      }
   }
}
