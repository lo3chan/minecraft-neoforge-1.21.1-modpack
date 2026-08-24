package net.mcreator.borninchaosv.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class DarkMetalNuggetItem extends Item {
   public DarkMetalNuggetItem() {
      super(new Properties().stacksTo(64).fireResistant().rarity(Rarity.COMMON));
   }
}
