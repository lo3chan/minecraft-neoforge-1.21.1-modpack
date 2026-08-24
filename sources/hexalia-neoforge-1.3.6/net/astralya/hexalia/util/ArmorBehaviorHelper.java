package net.astralya.hexalia.util;

import net.astralya.hexalia.event.BloomwrapEvents;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.item.custom.armor.GhostveilItem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public final class ArmorBehaviorHelper {
   private static final float KNOCKBACK_REDUCTION = 0.8F;
   private static final float BLOOMWRAP_REFLECT_FRACTION = 0.15F;
   private static final double GHOSTVEIL_FORGET_DISTANCE = 16.0;
   private static final double GHOSTVEIL_SNEAK_FORGET_DISTANCE = 24.0;
   private static final double GHOSTVEIL_MIN_DETECT_DISTANCE = 6.0;

   private ArmorBehaviorHelper() {
   }

   public static float adjustedIncomingDamage(LivingEntity entity, DamageSource source, float amount) {
      if (!isMagicDamage(source)) {
         return amount;
      } else {
         float resist = MagicResistanceHelper.getMagicResistancePct(entity);
         return resist > 0.0F ? amount * (1.0F - resist) : amount;
      }
   }

   public static boolean shouldReflectBloomwrapDamage(LivingEntity entity, LivingEntity attacker) {
      return BloomwrapEvents.isWearing(entity, EquipmentSlot.CHEST, (Item)ModItems.BLOOMWRAP_ROBES.get()) && attacker != entity;
   }

   public static float bloomwrapReflectionDamage(float amount) {
      return amount * 0.15F;
   }

   public static float adjustedKnockbackStrength(LivingEntity entity, float strength) {
      return BloomwrapEvents.isWearing(entity, EquipmentSlot.HEAD, (Item)ModItems.BLOOMWRAP_HAT.get()) ? strength * 0.19999999F : strength;
   }

   public static boolean shouldGhostveilClearTarget(Mob mob, Player player) {
      if (!mob.level().isClientSide() && GhostveilItem.isWornBy(player)) {
         double forgetDistance = player.isCrouching() ? 24.0 : 16.0;
         double distanceSqr = mob.distanceToSqr(player);
         return distanceSqr > 36.0 && distanceSqr <= forgetDistance * forgetDistance;
      } else {
         return false;
      }
   }

   private static boolean isMagicDamage(DamageSource source) {
      return source.is(DamageTypes.MAGIC) || source.is(DamageTypes.INDIRECT_MAGIC) || source.is(DamageTypes.WITHER) || source.is(DamageTypes.DRAGON_BREATH);
   }
}
