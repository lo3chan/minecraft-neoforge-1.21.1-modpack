package net.mehvahdjukaar.moonlight.api.item;

import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.minecraft.world.item.Item.Properties;

public class WoodBasedItem extends BlockTypeBasedItem<WoodType> {
   public WoodBasedItem(Properties builder, WoodType woodType) {
      super(builder, woodType);
   }
}
