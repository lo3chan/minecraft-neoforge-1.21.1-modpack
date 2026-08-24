package com.aetherteam.aether.item.accessories.cape;

import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class AgilityCapeItem extends CapeItem {
   private static final ResourceLocation STEP_HEIGHT_ID = ResourceLocation.fromNamespaceAndPath("aether", "agility_cape_step_height");

   public AgilityCapeItem(String capeLocation, Properties properties) {
      super(capeLocation, properties);
   }

   public void tick(ItemStack stack, SlotReference reference) {
      LivingEntity livingEntity = reference.entity();
      AttributeInstance stepHeight = livingEntity.getAttribute(Attributes.STEP_HEIGHT);
      if (stepHeight != null) {
         if (!stepHeight.hasModifier(this.getStepHeightModifier().id()) && !livingEntity.isShiftKeyDown()) {
            stepHeight.addTransientModifier(this.getStepHeightModifier());
         }

         if (livingEntity.isShiftKeyDown()) {
            stepHeight.removeModifier(this.getStepHeightModifier().id());
         }
      }
   }

   public void onUnequip(ItemStack stack, SlotReference reference) {
      LivingEntity livingEntity = reference.entity();
      AttributeInstance stepHeight = livingEntity.getAttribute(Attributes.STEP_HEIGHT);
      if (stepHeight != null && stepHeight.hasModifier(this.getStepHeightModifier().id())) {
         stepHeight.removeModifier(this.getStepHeightModifier().id());
      }
   }

   public AttributeModifier getStepHeightModifier() {
      return new AttributeModifier(STEP_HEIGHT_ID, 0.5, Operation.ADD_VALUE);
   }
}
