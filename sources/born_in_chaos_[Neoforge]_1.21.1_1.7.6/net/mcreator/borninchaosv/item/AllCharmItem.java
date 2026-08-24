package net.mcreator.borninchaosv.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class AllCharmItem extends Item {
   public AllCharmItem() {
      super(new Properties().stacksTo(1).rarity(Rarity.COMMON));
   }
}
