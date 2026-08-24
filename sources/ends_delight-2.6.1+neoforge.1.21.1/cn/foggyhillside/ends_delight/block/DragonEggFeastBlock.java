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

public class DragonEggFeastBlock extends FeastBlock {
   protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 11.0, 15.0);

   public DragonEggFeastBlock(Properties properties, Supplier<Item> servingItem, boolean hasLeftovers) {
      super(properties, servingItem, hasLeftovers);
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }
}
