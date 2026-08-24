package net.cibernet.alchemancy.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

public class ShockUtils {
   static ResourceKey<DamageType> SHOCK_DAMAGE_KEY = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("alchemancy", "shock"));

   public static void selfDamagingMeleeShockAttack(Entity source, Vec3 position, float power) {
      customShockAttack(source.level(), null, position, power, target -> meleeShockDamage(source, position));
   }

   public static void meleeShockAttack(Entity source, Vec3 position, float power) {
      customShockAttack(source.level(), source, position, power, target -> meleeShockDamage(source, position));
   }

   public static void meleeShockAttack(Entity source, LivingEntity mainTarget, float power) {
      customShockAttack(source.level(), source, mainTarget.position(), power, target -> meleeShockDamage(source, target.position()));
   }

   public static void rangedShockAttack(Entity owner, Entity directSource, LivingEntity mainTarget, float power) {
      customShockAttack(directSource.level(), directSource, mainTarget.position(), power, target -> rangedShockDamage(owner, directSource, target));
   }

   public static void environmentalShockAttack(Level level, Vec3 position, float power) {
      customShockAttack(level, null, position, power, target -> environmentalShockDamage(level.damageSources(), position));
   }

   public static void customShockAttack(
      Level level, @Nullable Entity source, Vec3 position, float power, Function<LivingEntity, DamageSource> damageSourceSupplier
   ) {
      ArrayList<Entity> immune = new ArrayList<>();
      if (source != null) {
         immune.add(source);
      }

      customShockAttack(level, source, position, power, damageSourceSupplier, immune);
   }

   private static void customShockAttack(
      Level level,
      @Nullable Entity source,
      Vec3 position,
      float power,
      Function<LivingEntity, DamageSource> damageSourceSupplier,
      ArrayList<Entity> alreadyAffected
   ) {
      if (alreadyAffected.size() <= 8) {
         List<LivingEntity> candidates = level.getEntitiesOfClass(
            LivingEntity.class,
            CommonUtils.boundingBoxAroundPoint(position, Math.min(8.0F, power)),
            EntitySelector.NO_SPECTATORS.and(entity -> !(entity instanceof LivingEntity living && living.isDeadOrDying()) && !alreadyAffected.contains(entity))
         );
         candidates.sort(Comparator.comparingDouble(targetx -> targetx.distanceToSqr(position)));
         Iterator var7 = candidates.iterator();
         if (var7.hasNext()) {
            LivingEntity target = (LivingEntity)var7.next();
            float distanceTo = Mth.sqrt((float)target.distanceToSqr(position));
            float damage = power - distanceTo * 0.5F;

            for (EquipmentSlot slot : EquipmentSlot.values()) {
               if (slot.isArmor() && target.getItemBySlot(slot).is(AlchemancyTags.Items.INCREASES_SHOCK_DAMAGE_RECEIVED)) {
                  damage += power * 0.25F;
               }
            }

            if (target.isInWater()) {
               damage *= 1.5F;
            }

            if (damage <= 0.0F) {
               return;
            }

            Vec3 targetPos = target.position().add(0.0, target.getBbHeight() * 0.5F, 0.0);
            if (level instanceof ServerLevel serverLevel) {
               int sparkSegments = (int)(power * 2.0F);
               if (distanceTo < target.getBbWidth() * 0.5F) {
                  for (int i = 0; i < sparkSegments; i++) {
                     serverLevel.sendParticles(
                        ParticleTypes.ELECTRIC_SPARK, target.getRandomX(1.5), target.getRandomY(), target.getRandomZ(1.5), 1, 0.0, 0.0, 0.0, 0.0
                     );
                  }
               } else {
                  for (int i = 0; i < sparkSegments; i++) {
                     Vec3 vec = targetPos.lerp(position, (double)i / sparkSegments).offsetRandom(target.getRandom(), 0.1F);
                     serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, vec.x, vec.y, vec.z, 1, 0.0, 0.0, 0.0, 0.0);
                  }
               }
            }

            damage -= 0.5F;
            if (damage <= 0.0F) {
               return;
            }

            target.hurt(damageSourceSupplier.apply(target), damage);
            alreadyAffected.add(target);
            customShockAttack(level, source, targetPos, damage, damageSourceSupplier, alreadyAffected);
         }
      }
   }

   public static DamageSource shockDamage(DamageSources damageSources, @Nullable Entity source, @Nullable Entity directEntity, Vec3 position) {
      return new DamageSource(damageSources.damageTypes.getHolderOrThrow(SHOCK_DAMAGE_KEY), source, directEntity, position);
   }

   public static DamageSource rangedShockDamage(Entity source, Entity directEntity, @NonNull LivingEntity mainTarget) {
      return shockDamage(directEntity.damageSources(), source, directEntity, mainTarget.position());
   }

   public static DamageSource meleeShockDamage(Entity source, Vec3 position) {
      return shockDamage(source.damageSources(), source, source, position);
   }

   public static DamageSource environmentalShockDamage(DamageSources damageSources, Vec3 position) {
      return shockDamage(damageSources, null, null, position);
   }
}
