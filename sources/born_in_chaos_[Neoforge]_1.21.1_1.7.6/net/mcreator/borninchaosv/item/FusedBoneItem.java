package net.mcreator.borninchaosv.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class FusedBoneItem extends Item {
   public FusedBoneItem() {
      super(new Properties().stacksTo(64).rarity(Rarity.COMMON));
   }
}
