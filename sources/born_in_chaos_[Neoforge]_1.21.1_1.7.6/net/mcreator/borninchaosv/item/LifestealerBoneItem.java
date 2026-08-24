package net.mcreator.borninchaosv.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class LifestealerBoneItem extends Item {
   public LifestealerBoneItem() {
      super(new Properties().stacksTo(64).fireResistant().rarity(Rarity.COMMON));
   }
}
