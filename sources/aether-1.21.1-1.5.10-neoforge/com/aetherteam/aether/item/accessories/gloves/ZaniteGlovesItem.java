package com.aetherteam.aether.item.accessories.gloves;

import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.item.combat.AetherArmorMaterials;
import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ZaniteGlovesItem extends GlovesItem {
   public ZaniteGlovesItem(double punchDamage, Properties properties) {
      super(AetherArmorMaterials.ZANITE, punchDamage, "zanite_gloves", AetherSoundEvents.ITEM_ARMOR_EQUIP_ZANITE, properties);
   }

   @Override
   public void getDynamicModifiers(ItemStack stack, SlotReference reference, AccessoryAttributeBuilder builder) {
      builder.addStackable(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_PUNCH_DAMAGE_ID, this.calculateIncrease(stack), Operation.ADD_VALUE));
   }

   private float calculateIncrease(ItemStack stack) {
      int maxDurability = stack.getMaxDamage();
      int currentDurability = maxDurability - stack.getDamageValue();
      if (currentDurability >= maxDurability - (int)(maxDurability / 4.0)) {
         return 0.25F;
      } else {
         return currentDurability >= maxDurability - (int)(maxDurability / 1.5) ? 0.5F : 0.75F;
      }
   }
}
