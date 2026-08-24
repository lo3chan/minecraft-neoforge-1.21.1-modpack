package com.aetherteam.aether.item.food;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class HealingStoneItem extends Item {
   public HealingStoneItem() {
      super(new Properties().rarity(Rarity.RARE).food(AetherFoods.HEALING_STONE));
   }

   public boolean isFoil(ItemStack stack) {
      return true;
   }
}
