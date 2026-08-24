package net.astralya.hexalia.util;

import net.astralya.hexalia.component.ModComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class MagicResistanceHelper {
   private MagicResistanceHelper() {
   }

   public static float getMagicResistancePct(LivingEntity entity) {
      return getBreakdown(entity).total();
   }

   public static MagicResistanceHelper.ResistanceBreakdown getBreakdown(LivingEntity entity) {
      ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
      ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
      ItemStack legs = entity.getItemBySlot(EquipmentSlot.LEGS);
      ItemStack feet = entity.getItemBySlot(EquipmentSlot.FEET);
      float pieceTotal = getPiecePct(head) + getPiecePct(chest) + getPiecePct(legs) + getPiecePct(feet);
      float bonus = 0.0F;
      ResourceLocation setId = sharedSetId(head, chest, legs, feet);
      if (setId != null) {
         bonus = getFullSetBonus(head, chest, legs, feet, setId);
      }

      return new MagicResistanceHelper.ResistanceBreakdown(pieceTotal, bonus, Math.clamp(pieceTotal + bonus, 0.0F, 1.0F));
   }

   public static boolean hasMagicResistance(ItemStack stack) {
      return getPiecePct(stack) > 0.0F;
   }

   public static float getMagicResistancePct(ItemStack stack) {
      return getPiecePct(stack);
   }

   public static ResourceLocation getSetId(ItemStack stack) {
      return (ResourceLocation)stack.get((DataComponentType)ModComponents.ARMOR_SET_ID.get());
   }

   public static boolean isWearingFullSet(LivingEntity entity, ResourceLocation setId) {
      return matchesSet(entity.getItemBySlot(EquipmentSlot.HEAD), setId)
         && matchesSet(entity.getItemBySlot(EquipmentSlot.CHEST), setId)
         && matchesSet(entity.getItemBySlot(EquipmentSlot.LEGS), setId)
         && matchesSet(entity.getItemBySlot(EquipmentSlot.FEET), setId);
   }

   public static float getFullSetBonusPct(LivingEntity entity, ResourceLocation setId) {
      return !isWearingFullSet(entity, setId)
         ? 0.0F
         : getFullSetBonus(
            entity.getItemBySlot(EquipmentSlot.HEAD),
            entity.getItemBySlot(EquipmentSlot.CHEST),
            entity.getItemBySlot(EquipmentSlot.LEGS),
            entity.getItemBySlot(EquipmentSlot.FEET),
            setId
         );
   }

   public static String formatPercent(float value) {
      return Math.round(value * 100.0F) + "%";
   }

   private static float getPiecePct(ItemStack stack) {
      Float pct = (Float)stack.get((DataComponentType)ModComponents.MAGIC_RESIST_PCT.get());
      return pct == null ? 0.0F : pct;
   }

   private static ResourceLocation sharedSetId(ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet) {
      ResourceLocation setId = getSetId(head);
      if (setId == null) {
         return null;
      } else {
         return matchesSet(chest, setId) && matchesSet(legs, setId) && matchesSet(feet, setId) ? setId : null;
      }
   }

   private static float getFullSetBonus(ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet, ResourceLocation setId) {
      float bonus = 0.0F;

      for (ItemStack stack : new ItemStack[]{head, chest, legs, feet}) {
         if (matchesSet(stack, setId)) {
            Float stackBonus = (Float)stack.get((DataComponentType)ModComponents.FULL_SET_BONUS_PCT.get());
            if (stackBonus != null) {
               bonus = Math.max(bonus, stackBonus);
            }
         }
      }

      return bonus;
   }

   private static boolean matchesSet(ItemStack stack, ResourceLocation setId) {
      return !stack.isEmpty() && setId.equals(getSetId(stack));
   }

   public record ResistanceBreakdown(float pieces, float fullSetBonus, float total) {
   }
}
