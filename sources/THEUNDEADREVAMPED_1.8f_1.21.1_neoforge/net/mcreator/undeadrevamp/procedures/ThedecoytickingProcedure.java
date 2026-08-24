package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ThedecoytickingProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(UndeadRevamp2ModMobEffects.ANIMATIONTEST)) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 25, false, false));
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(MobEffects.INVISIBILITY);
            }
         } else {
            if (entity.isAlive() && (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity) {
               Vec3 _center = new Vec3(x, y, z);

               for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.0), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entityiterator && entity instanceof LivingEntity _entity) {
                     _entity.removeEffect(MobEffects.INVISIBILITY);
                  }
               }
            }

            if (entity.isAlive()
               && !(entity instanceof LivingEntity _livEnt9 && _livEnt9.hasEffect(MobEffects.MOVEMENT_SLOWDOWN))
               && entity instanceof LivingEntity _entity
               && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 5, 20, false, false));
            }

            if (Math.random() < 0.03) {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 25, false, false));
               }

               if (world instanceof ServerLevel _level) {
                  _level.sendParticles(ParticleTypes.DRAGON_BREATH, x, y, z, 30, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 0.001);
               }
            }

            if (Math.random() < 0.3) {
               Vec3 _center = new Vec3(x, y, z);

               for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.0), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) == entityiteratorx) {
                     if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 50, false, false));
                     }

                     if (entity instanceof LivingEntity _entity) {
                        _entity.removeEffect(MobEffects.INVISIBILITY);
                     }
                  }
               }
            }

            entity.getPersistentData().putDouble("decoyticks", entity.getPersistentData().getDouble("decoyticks") - 1.0);
            if (entity.getPersistentData().getDouble("decoyticks") <= 0.0) {
               if (!entity.level().isClientSide()) {
                  entity.discard();
               }

               if (world instanceof ServerLevel _level) {
                  _level.sendParticles(ParticleTypes.SOUL, x, y, z, 30, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 0.001);
               }
            }
         }

         if (entity instanceof LivingEntity _livEnt31 && _livEnt31.hasEffect(MobEffects.INVISIBILITY) && world instanceof ServerLevel _level) {
            _level.sendParticles(ParticleTypes.DRAGON_BREATH, x, y, z, 3, 0.2, 0.3, 0.2, 1.0E-6);
         }

         if (entity.isInWaterRainOrBubble()) {
            entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.DROWN)), 1.0F);
         }
      }
   }
}
