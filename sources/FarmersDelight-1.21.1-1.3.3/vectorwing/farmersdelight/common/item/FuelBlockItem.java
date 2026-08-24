package vectorwing.farmersdelight.common.item;

import javax.annotation.Nullable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

@Deprecated(
   forRemoval = true
)
public class FuelBlockItem extends BlockItem {
   public final int burnTime;

   public FuelBlockItem(Block block, Properties properties) {
      super(block, properties);
      this.burnTime = 100;
   }

   public FuelBlockItem(Block block, Properties properties, int burnTime) {
      super(block, properties);
      this.burnTime = burnTime;
   }

   public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
      return this.burnTime;
   }
}
