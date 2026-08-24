package net.mcreator.borninchaosv.procedures;

import java.util.Comparator;
import net.mcreator.borninchaosv.entity.NightmareStalkerEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TerrifyingPresenceKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!entity.isAlive() && entity instanceof LivingEntity _entity) {
            _entity.removeEffect(BornInChaosV1ModMobEffects.TERRIFYING_PRESENCE);
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.DAMAGE_BOOST);
         }

         if (entity instanceof LivingEntity _entity) {
            AttributeInstance _attrInst = _entity.getAttribute(Attributes.STEP_HEIGHT);
            if (_attrInst != null) {
               _attrInst.setBaseValue(3.0);
            }
         }

         if (entity instanceof LivingEntity _entityx && !_entityx.level().isClientSide()) {
            _entityx.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 10, 2, false, false));
         }

         if (world instanceof ServerLevel _level) {
            _level.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.DARKMATTER.get(), entity.getX(), entity.getY() + 2.0, entity.getZ(), 1, 0.3, 0.3, 0.3, 0.3
            );
         }

         if (world instanceof ServerLevel _level) {
            _level.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.DARKSPOTS.get(), entity.getX(), entity.getY() + 0.3, entity.getZ(), 3, 2.5, 0.1, 2.5, 0.2
            );
         }

         if (entity.getPersistentData().getDouble("terrifyingpresence") == 0.0) {
            entity.getPersistentData().putDouble("terrifyingpresence", 30.0);
         } else {
            entity.getPersistentData().putDouble("terrifyingpresence", entity.getPersistentData().getDouble("terrifyingpresence") - 1.0);
         }

         if (entity.getPersistentData().getDouble("terrifyingpresence") == 0.0) {
            if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) > 10.0F && entity instanceof LivingEntity _entityx) {
               _entityx.setHealth((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) + 1.0F);
            }

            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (!(entityiterator instanceof NightmareStalkerEntity)
                  && entityiterator instanceof Player
                  && !(entityiterator instanceof LivingEntity _livEnt23 && _livEnt23.hasEffect(BornInChaosV1ModMobEffects.WITHER_RESISTANCE))) {
                  if (world.dayTime() >= 2400000L) {
                     entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.WITHER)), 4.0F);
                     if (entityiterator instanceof LivingEntity _entityx && !_entityx.level().isClientSide()) {
                        _entityx.addEffect(new MobEffectInstance(MobEffects.WITHER, 15, 0));
                     }
                  } else if (world.dayTime() < 2400000L && world.dayTime() >= 1200000L) {
                     entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.WITHER)), 3.0F);
                     if (entityiterator instanceof LivingEntity _entityx && !_entityx.level().isClientSide()) {
                        _entityx.addEffect(new MobEffectInstance(MobEffects.WITHER, 15, 0));
                     }
                  } else {
                     entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.WITHER)), 2.0F);
                     if (entityiterator instanceof LivingEntity _entityx && !_entityx.level().isClientSide()) {
                        _entityx.addEffect(new MobEffectInstance(MobEffects.WITHER, 15, 0));
                     }
                  }

                  if (world instanceof ServerLevel _level) {
                     _level.sendParticles(
                        (SimpleParticleType)BornInChaosV1ModParticleTypes.DARK_SMOKE.get(),
                        entityiterator.getX(),
                        entityiterator.getY() + 1.5,
                        entityiterator.getZ(),
                        5,
                        0.3,
                        0.2,
                        0.3,
                        0.1
                     );
                  }
               }
            }
         }
      }
   }
}
