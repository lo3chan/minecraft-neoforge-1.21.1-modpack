package net.bettercombat.logic;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;

public class EntityAttributeHelper {
   public static boolean itemHasRangeAttribute(ItemStack stack) {
      ItemAttributeModifiers attributeModifiers = (ItemAttributeModifiers)stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
      if (attributeModifiers != null) {
         for (Entry modifier : attributeModifiers.modifiers()) {
            if (((Attribute)modifier.attribute().value()).equals(Attributes.ENTITY_INTERACTION_RANGE.value())) {
               return true;
            }
         }
      }

      return false;
   }

   public static int rangeModifierCount(ItemStack stack) {
      ItemAttributeModifiers attributeModifiers = (ItemAttributeModifiers)stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
      if (attributeModifiers != null) {
         int count = 0;

         for (Entry modifier : attributeModifiers.modifiers()) {
            if (((Attribute)modifier.attribute().value()).equals(Attributes.ENTITY_INTERACTION_RANGE.value())) {
               count++;
            }
         }

         return count;
      } else {
         return 0;
      }
   }
}
