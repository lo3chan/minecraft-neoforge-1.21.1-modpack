package net.cibernet.alchemancy.properties;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class ReinforcedProperty extends Property {
   private static final AttributeModifier ARMOR_MOD = new AttributeModifier(
      ResourceLocation.fromNamespaceAndPath("alchemancy", "reinforced_property_modifier"), 3.0, Operation.ADD_VALUE
   );
   private static final AttributeModifier TOUGHNESS_MOD = new AttributeModifier(
      ResourceLocation.fromNamespaceAndPath("alchemancy", "reinforced_property_modifier"), 1.0, Operation.ADD_VALUE
   );

   @Override
   public void applyAttributes(ItemAttributeModifierEvent event) {
      if (getEquipmentSlotForItem(event.getItemStack()).isArmor()) {
         event.addModifier(Attributes.ARMOR, ARMOR_MOD, EquipmentSlotGroup.ARMOR);
         event.addModifier(Attributes.ARMOR_TOUGHNESS, TOUGHNESS_MOD, EquipmentSlotGroup.ARMOR);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 5920602;
   }
}
