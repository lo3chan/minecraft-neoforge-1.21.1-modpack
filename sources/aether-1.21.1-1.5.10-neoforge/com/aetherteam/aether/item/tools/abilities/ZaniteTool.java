package com.aetherteam.aether.item.tools.abilities;

import com.aetherteam.aether.item.EquipmentUtil;
import net.minecraft.world.item.ItemStack;

public interface ZaniteTool {
   default float increaseSpeed(ItemStack stack, float speed) {
      return (float)EquipmentUtil.calculateZaniteBuff(stack, speed);
   }
}
