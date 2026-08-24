package net.mcreator.borninchaosv.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class PermafrostShardItem extends Item {
   public PermafrostShardItem() {
      super(new Properties().stacksTo(64).rarity(Rarity.COMMON));
   }
}
