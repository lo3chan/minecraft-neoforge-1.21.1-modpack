package com.github.alexthe666.alexsmobs.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ItemPigshoes extends Item {
   public ItemPigshoes(Properties props) {
      super(props);
   }

   public int getEnchantmentValue() {
      return 1;
   }

   public boolean isEnchantable(ItemStack stack) {
      return true;
   }
}
