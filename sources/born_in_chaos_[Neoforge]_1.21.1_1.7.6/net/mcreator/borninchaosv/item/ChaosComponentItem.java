package net.mcreator.borninchaosv.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class ChaosComponentItem extends Item {
   public ChaosComponentItem() {
      super(new Properties().stacksTo(64).rarity(Rarity.COMMON));
   }
}
