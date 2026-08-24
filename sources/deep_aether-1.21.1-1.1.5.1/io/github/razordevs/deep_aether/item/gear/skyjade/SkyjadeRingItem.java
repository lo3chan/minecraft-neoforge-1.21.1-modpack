package io.github.razordevs.deep_aether.item.gear.skyjade;

import com.aetherteam.aether.item.accessories.ring.RingItem;
import io.github.razordevs.deep_aether.DeepAetherConfig;
import io.github.razordevs.deep_aether.init.DAItems;
import io.github.razordevs.deep_aether.init.DASounds;
import io.github.razordevs.deep_aether.item.gear.DAEquipmentUtil;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class SkyjadeRingItem extends RingItem implements SkyjadeAccessory {
   public SkyjadeRingItem(Properties properties) {
      super(DASounds.ITEM_ACCESSORY_EQUIP_SKYJADE_RING, properties);
   }

   public boolean isValidRepairItem(ItemStack repairItem, ItemStack repairMaterial) {
      return repairMaterial.is((Item)DAItems.SKYJADE.get());
   }

   public void onEquip(ItemStack stack, SlotReference reference) {
      if ((Boolean)DeepAetherConfig.SERVER.enable_skyjade_rework.get()) {
         LivingEntity livingEntity = reference.entity();
         AttributeInstance stepHeight = livingEntity.getAttribute(Attributes.STEP_HEIGHT);
         if (stepHeight != null) {
            int count = DAEquipmentUtil.getSkyjadeRingCount(livingEntity);

            for (int i = 1; i <= count; i++) {
               if (!stepHeight.hasModifier(this.getStepHeightModifier(i).id())) {
                  stepHeight.addTransientModifier(this.getStepHeightModifier(i));
               }
            }
         }
      }
   }

   public void onUnequip(ItemStack stack, SlotReference reference) {
      if ((Boolean)DeepAetherConfig.SERVER.enable_skyjade_rework.get()) {
         LivingEntity livingEntity = reference.entity();
         AttributeInstance stepHeight = livingEntity.getAttribute(Attributes.STEP_HEIGHT);
         if (stepHeight != null && stepHeight.hasModifier(this.getStepHeightModifier(DAEquipmentUtil.getSkyjadeRingCount(livingEntity) + 1).id())) {
            stepHeight.removeModifier(this.getStepHeightModifier(DAEquipmentUtil.getSkyjadeRingCount(livingEntity) + 1).id());
         }
      }
   }

   public AttributeModifier getStepHeightModifier(int count) {
      return count == 1
         ? new AttributeModifier(ResourceLocation.fromNamespaceAndPath("deep_aether", "step_height_increase"), 0.5, Operation.ADD_VALUE)
         : new AttributeModifier(ResourceLocation.fromNamespaceAndPath("deep_aether", "step_height_increase_1"), 0.5, Operation.ADD_VALUE);
   }
}
