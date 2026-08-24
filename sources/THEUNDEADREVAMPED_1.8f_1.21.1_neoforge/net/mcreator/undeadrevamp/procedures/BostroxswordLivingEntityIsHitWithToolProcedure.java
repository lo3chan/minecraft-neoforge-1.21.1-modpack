package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModAttributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BostroxswordLivingEntityIsHitWithToolProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (Math.random() < 0.3) {
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(1.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               entityiterator.setDeltaMovement(
                  new Vec3(
                     Math.sin(Math.toRadians(sourceentity.getYRot() + 180.0F)) * 1.25 * 1.3,
                     (Math.sin(Math.toRadians(0.0F - sourceentity.getXRot())) + 0.5) * 1.5,
                     Math.cos(Math.toRadians(sourceentity.getYRot())) * 1.25 * 1.3
                  )
               );
            }
         } else {
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(1.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               entityiterator.setDeltaMovement(
                  new Vec3(
                     Math.sin(Math.toRadians(sourceentity.getYRot() + 180.0F)) * 1.25 * 1.1,
                     (Math.sin(Math.toRadians(0.0F - sourceentity.getXRot())) + 0.5) * 1.0,
                     Math.cos(Math.toRadians(sourceentity.getYRot())) * 1.25 * 1.1
                  )
               );
            }
         }

         if (entity instanceof LivingEntity _livingEntity10 && _livingEntity10.getAttributes().hasAttribute(Attributes.KNOCKBACK_RESISTANCE)) {
            _livingEntity10.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0);
         }

         if (!(entity instanceof LivingEntity _livEnt11 && _livEnt11.isBaby())
            && entity instanceof LivingEntity _livingEntity13
            && _livingEntity13.getAttributes().hasAttribute(Attributes.ATTACK_SPEED)) {
            _livingEntity13.getAttribute(Attributes.ATTACK_SPEED)
               .setBaseValue(
                  (
                        entity instanceof LivingEntity _livingEntity12 && _livingEntity12.getAttributes().hasAttribute(Attributes.ATTACK_SPEED)
                           ? _livingEntity12.getAttribute(Attributes.ATTACK_SPEED).getValue()
                           : 0.0
                     )
                     - 0.2
               );
         }

         if (entity instanceof LivingEntity _livingEntity15 && _livingEntity15.getAttributes().hasAttribute(Attributes.ATTACK_KNOCKBACK)) {
            _livingEntity15.getAttribute(Attributes.ATTACK_KNOCKBACK)
               .setBaseValue(
                  (
                        entity instanceof LivingEntity _livingEntity14 && _livingEntity14.getAttributes().hasAttribute(Attributes.ATTACK_KNOCKBACK)
                           ? _livingEntity14.getAttribute(Attributes.ATTACK_KNOCKBACK).getValue()
                           : 0.0
                     )
                     - 0.05
               );
         }

         if (entity instanceof LivingEntity _livingEntity17 && _livingEntity17.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.CHEROATTACKSPEED)) {
            _livingEntity17.getAttribute(UndeadRevamp2ModAttributes.CHEROATTACKSPEED)
               .setBaseValue(
                  (
                        entity instanceof LivingEntity _livingEntity16
                              && _livingEntity16.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.CHEROATTACKSPEED)
                           ? _livingEntity16.getAttribute(UndeadRevamp2ModAttributes.CHEROATTACKSPEED).getBaseValue()
                           : 0.0
                     )
                     + 0.5
               );
         }

         if (Math.random() < 0.38) {
            UndeadRevamp2Mod.queueServerWork(
               5,
               () -> entity.hurt(
                  new DamageSource(world.holderOrThrow(DamageTypes.CRAMMING), sourceentity),
                  (float)(
                     entity instanceof LivingEntity _livingEntity18 && _livingEntity18.getAttributes().hasAttribute(Attributes.ARMOR)
                        ? _livingEntity18.getAttribute(Attributes.ARMOR).getValue()
                        : 0.0
                  )
               )
            );
         }
      }
   }
}
