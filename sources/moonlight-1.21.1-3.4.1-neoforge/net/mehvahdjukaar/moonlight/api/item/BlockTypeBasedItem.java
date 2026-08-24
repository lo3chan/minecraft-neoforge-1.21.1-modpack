package net.mehvahdjukaar.moonlight.api.item;

import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public class BlockTypeBasedItem<T extends BlockType> extends Item {
   private final T blockType;

   public BlockTypeBasedItem(Properties pProperties, T blockType) {
      super(pProperties);
      this.blockType = blockType;
   }

   public T getBlockType() {
      return this.blockType;
   }
}
