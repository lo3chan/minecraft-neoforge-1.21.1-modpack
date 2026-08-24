package io.github.razordevs.deep_aether.block.natural;

import com.aetherteam.aether.block.AetherBlockStateProperties;
import com.aetherteam.aether.block.natural.AetherDoubleDropBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DoubleDropMudBlock extends AetherDoubleDropBlock {
   protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);

   public DoubleDropMudBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, false));
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   public VoxelShape getBlockSupportShape(BlockState state, BlockGetter getter, BlockPos pos) {
      return Shapes.block();
   }

   public VoxelShape getVisualShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
      return Shapes.block();
   }

   protected boolean isPathfindable(BlockState state, PathComputationType path) {
      return false;
   }

   public float getShadeBrightness(BlockState state, BlockGetter getter, BlockPos pos) {
      return 0.2F;
   }
}
