package net.cibernet.alchemancy.properties;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class FeralProperty extends Property {
   private static final AttributeModifier SPEED_MOD = new AttributeModifier(
      ResourceLocation.fromNamespaceAndPath("alchemancy", "feral_property_modifier"), 0.45, Operation.ADD_MULTIPLIED_TOTAL
   );
   public static final AttributeModifier OFFHAND_BONUS = new AttributeModifier(
      ResourceLocation.fromNamespaceAndPath("alchemancy", "feral_blade_offhand_bonus"), 0.25, Operation.ADD_MULTIPLIED_TOTAL
   );

   @Override
   public void applyAttributes(ItemAttributeModifierEvent event) {
      event.addModifier(Attributes.ATTACK_SPEED, SPEED_MOD, EquipmentSlotGroup.HAND);
   }

   @Override
   public int getColor(ItemStack stack) {
      return 7465472;
   }
}
