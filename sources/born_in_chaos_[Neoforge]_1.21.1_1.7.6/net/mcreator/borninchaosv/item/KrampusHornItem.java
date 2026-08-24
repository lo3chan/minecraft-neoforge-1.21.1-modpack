package net.mcreator.borninchaosv.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class KrampusHornItem extends Item {
   public KrampusHornItem() {
      super(new Properties().stacksTo(64).rarity(Rarity.COMMON));
   }
}
