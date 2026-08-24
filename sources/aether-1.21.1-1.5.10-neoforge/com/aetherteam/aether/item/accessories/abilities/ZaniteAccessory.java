package com.aetherteam.aether.item.accessories.abilities;

import net.minecraft.world.item.ItemStack;

public interface ZaniteAccessory {
   static float handleMiningSpeed(float speed, ItemStack stack) {
      return speed * (1.4F + stack.getDamageValue() / (stack.getMaxDamage() * 3.0F));
   }
}
