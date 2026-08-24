package com.aetherteam.aether.item.accessories.miscellaneous;

import com.aetherteam.aether.item.accessories.AccessoryItem;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class RegenerationStoneItem extends AccessoryItem {
   public RegenerationStoneItem(Properties properties) {
      super(properties);
   }

   public void tick(ItemStack stack, SlotReference reference) {
      LivingEntity livingEntity = reference.entity();
      if (livingEntity.tickCount % 50 == 0 && livingEntity.getHealth() < livingEntity.getMaxHealth()) {
         livingEntity.heal(1.0F);
      }
   }
}
