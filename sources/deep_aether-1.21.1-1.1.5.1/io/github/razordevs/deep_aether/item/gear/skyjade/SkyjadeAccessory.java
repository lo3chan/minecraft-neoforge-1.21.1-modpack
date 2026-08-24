package io.github.razordevs.deep_aether.item.gear.skyjade;

import net.minecraft.world.item.ItemStack;

public interface SkyjadeAccessory {
   static float handleMiningSpeed(float speed, ItemStack stack) {
      return speed * ((float)stack.getMaxDamage() / stack.getDamageValue());
   }
}
