package net.mcreator.undeadrevamp.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class CustomiconItem extends Item {
   public CustomiconItem() {
      super(new Properties().stacksTo(64).rarity(Rarity.COMMON));
   }
}
