package vectorwing.farmersdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModItems;

public class RicePaniclesBlock extends CropBlock {
   public static final IntegerProperty RICE_AGE = BlockStateProperties.AGE_3;
   private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
      Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0),
      Block.box(3.0, 0.0, 3.0, 13.0, 10.0, 13.0),
      Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0),
      Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0)
   };

   public RicePaniclesBlock(Properties properties) {
      super(properties);
   }

   public IntegerProperty getAgeProperty() {
      return RICE_AGE;
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE_BY_AGE[state.getValue(this.getAgeProperty())];
   }

   public int getMaxAge() {
      return 3;
   }

   protected ItemLike getBaseSeedId() {
      return (ItemLike)ModItems.RICE.get();
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{RICE_AGE});
   }

   protected int getBonemealAgeIncrease(Level level) {
      return super.getBonemealAgeIncrease(level) / 3;
   }

   protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
      return state.is(ModBlocks.RICE_CROP.get());
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      return (level.getRawBrightness(pos, 0) >= 8 || level.canSeeSky(pos)) && this.mayPlaceOn(level.getBlockState(pos.below()), level, pos);
   }
}
