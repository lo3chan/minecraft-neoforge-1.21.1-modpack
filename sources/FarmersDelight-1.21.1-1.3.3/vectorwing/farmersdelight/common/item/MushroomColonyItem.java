package vectorwing.farmersdelight.common.item;

import javax.annotation.Nullable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import vectorwing.farmersdelight.common.block.MushroomColonyBlock;

public class MushroomColonyItem extends BlockItem {
   public MushroomColonyItem(Block block, Properties properties) {
      super(block, properties);
   }

   @Nullable
   protected BlockState getPlacementState(BlockPlaceContext context) {
      BlockState originalState = this.getBlock().getStateForPlacement(context);
      if (originalState != null) {
         BlockState matureState = (BlockState)originalState.setValue(MushroomColonyBlock.COLONY_AGE, 3);
         return this.canPlace(context, matureState) ? matureState : null;
      } else {
         return null;
      }
   }
}
