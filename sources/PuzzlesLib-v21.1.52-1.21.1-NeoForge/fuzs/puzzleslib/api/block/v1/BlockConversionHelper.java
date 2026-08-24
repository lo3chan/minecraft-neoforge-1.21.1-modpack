package fuzs.puzzleslib.api.block.v1;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class BlockConversionHelper {
   private BlockConversionHelper() {
   }

   public static void setBlockItemBlock(BlockItem item, Block block) {
      setItemForBlock(block, item);
      setBlockForItem(item, block);
   }

   public static void setItemForBlock(Block block, Item item) {
      Objects.requireNonNull(block, () -> "block " + (item != null ? "for item '" + BuiltInRegistries.ITEM.getKey(item) + "' " : "") + "is null");
      Objects.requireNonNull(item, () -> "item for block '" + BuiltInRegistries.BLOCK.getKey(block) + "' is null");
      Item.BY_BLOCK.put(block, item);
      block.item = item;
   }

   public static void setBlockForItem(BlockItem item, Block block) {
      Objects.requireNonNull(item, () -> "item " + (block != null ? "for block '" + BuiltInRegistries.BLOCK.getKey(block) + "' " : "") + "is null");
      Objects.requireNonNull(block, () -> "block for item '" + BuiltInRegistries.ITEM.getKey(item) + "' is null");
      Block oldBlock = item.getBlock();
      if (oldBlock != null) {
         oldBlock.item = item;
      }

      item.block = block;
   }

   public static void copyBoundTags(Block from, Block to) {
      Objects.requireNonNull(from, () -> "source " + (to != null ? "for target '" + BuiltInRegistries.BLOCK.getKey(to) + "' " : "") + "is null");
      Objects.requireNonNull(to, () -> "target for source '" + BuiltInRegistries.BLOCK.getKey(from) + "' is null");
      Set<TagKey<Block>> fromTagKeys = from.builtInRegistryHolder().tags().collect(Collectors.toSet());
      Set<TagKey<Block>> toTagKeys = to.builtInRegistryHolder().tags().collect(Collectors.toSet());
      if (toTagKeys.isEmpty()) {
         to.builtInRegistryHolder().bindTags(fromTagKeys);
      } else if (!Objects.equals(fromTagKeys, toTagKeys)) {
         throw new IllegalStateException("Target block tags for " + to.builtInRegistryHolder().key() + " not empty: " + toTagKeys);
      }
   }
}
