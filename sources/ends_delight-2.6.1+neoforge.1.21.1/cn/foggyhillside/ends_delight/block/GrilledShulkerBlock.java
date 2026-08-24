package cn.foggyhillside.ends_delight.block;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.block.FeastBlock;

public class GrilledShulkerBlock extends FeastBlock {
   protected static final VoxelShape ONE_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
   protected static final VoxelShape TWO_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0);

   public GrilledShulkerBlock(Properties properties, Supplier<Item> servingItem, boolean hasLeftovers) {
      super(properties, servingItem, hasLeftovers);
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      switch (state.getValue(SERVINGS)) {
         case 0:
            return TWO_SHAPE;
         case 1:
         case 2:
         case 3:
         case 4:
         default:
            return ONE_SHAPE;
      }
   }
}
