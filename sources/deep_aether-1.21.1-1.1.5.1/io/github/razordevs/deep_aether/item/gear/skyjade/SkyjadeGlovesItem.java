package io.github.razordevs.deep_aether.item.gear.skyjade;

import com.aetherteam.aether.inventory.AetherAccessorySlots;
import com.aetherteam.aether.item.accessories.gloves.GlovesItem;
import io.github.razordevs.deep_aether.DeepAetherConfig;
import io.github.razordevs.deep_aether.init.DASounds;
import io.github.razordevs.deep_aether.item.gear.DAArmorMaterials;
import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class SkyjadeGlovesItem extends GlovesItem {
   public SkyjadeGlovesItem(double punchDamage, Properties properties) {
      super(
         DAArmorMaterials.SKYJADE,
         punchDamage,
         ResourceLocation.fromNamespaceAndPath("deep_aether", "skyjade_gloves"),
         DASounds.ITEM_ARMOR_EQUIP_SKYJADE,
         properties
      );
   }

   public void getDynamicModifiers(ItemStack stack, SlotReference reference, AccessoryAttributeBuilder builder) {
      if (!(Boolean)DeepAetherConfig.SERVER.enable_skyjade_rework.get() && reference.slotName().equals(AetherAccessorySlots.GLOVES_SLOT_LOCATION.toString())) {
         builder.addStackable(
            Attributes.ATTACK_DAMAGE,
            new AttributeModifier(
               ResourceLocation.fromNamespaceAndPath("deep_aether", "gloves_damage_bonus"), this.calculateIncrease(stack), Operation.ADD_VALUE
            )
         );
      }
   }

   private float calculateIncrease(ItemStack stack) {
      int maxDurability = stack.getMaxDamage();
      int currentDurability = maxDurability - stack.getDamageValue();
      if (currentDurability >= maxDurability - (int)(maxDurability / 4.0)) {
         return 1.0F;
      } else if (currentDurability >= maxDurability - (int)(maxDurability / 3.0)) {
         return 0.75F;
      } else {
         return currentDurability >= maxDurability - (int)(maxDurability / 1.5) ? 0.5F : 0.25F;
      }
   }

   public boolean isEnchantable(ItemStack itemStack) {
      return (Boolean)DeepAetherConfig.SERVER.skyjade_enchant.get() && !(Boolean)DeepAetherConfig.SERVER.enable_skyjade_rework.get();
   }

   public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      return (Boolean)DeepAetherConfig.SERVER.skyjade_enchant.get() && !(Boolean)DeepAetherConfig.SERVER.enable_skyjade_rework.get();
   }
}
