package vectorwing.farmersdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModItems;

public class RopeBlock extends IronBarsBlock {
   public static final BooleanProperty TIED_TO_BELL = BooleanProperty.create("tied_to_bell");
   protected static final VoxelShape LOWER_SUPPORT_AABB = Block.box(7.0, 0.0, 7.0, 9.0, 1.0, 9.0);

   public RopeBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, false))
                        .setValue(SOUTH, false))
                     .setValue(EAST, false))
                  .setValue(WEST, false))
               .setValue(TIED_TO_BELL, false))
            .setValue(WATERLOGGED, false)
      );
   }

   public boolean isPathfindable(BlockState state, PathComputationType type) {
      return true;
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      return stack.is(ModItems.ROPE.get()) ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if (Configuration.ENABLE_ROPE_REELING.get() && player.isSecondaryUseActive()) {
         if (player.getAbilities().mayBuild && (player.getAbilities().instabuild || player.getInventory().add(new ItemStack(this.asItem())))) {
            MutableBlockPos reelingPos = pos.mutable().move(Direction.DOWN);
            int minBuildHeight = level.getMinBuildHeight();

            while (reelingPos.getY() >= minBuildHeight - 1) {
               BlockState blockStateBelow = level.getBlockState(reelingPos);
               if (!blockStateBelow.is(this)) {
                  reelingPos.move(Direction.UP);
                  level.destroyBlock(reelingPos, false, player);
                  return InteractionResult.sidedSuccess(level.isClientSide);
               }

               reelingPos.move(Direction.DOWN);
            }
         }
      } else {
         MutableBlockPos bellRingingPos = pos.mutable().move(Direction.UP);

         for (int i = 0; i < 24; i++) {
            BlockState blockStateAbove = level.getBlockState(bellRingingPos);
            Block blockAbove = blockStateAbove.getBlock();
            if (blockAbove == Blocks.BELL) {
               ((BellBlock)blockAbove).attemptToRing(level, bellRingingPos, ((Direction)blockStateAbove.getValue(BellBlock.FACING)).getClockWise());
               return InteractionResult.SUCCESS;
            }

            if (blockAbove != ModBlocks.ROPE.get()) {
               return InteractionResult.PASS;
            }

            bellRingingPos.move(Direction.UP);
         }
      }

      return InteractionResult.PASS;
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return getStateWithConnections(this.defaultBlockState(), context.getLevel(), context.getClickedPos(), context.getClickedFace());
   }

   public static BlockState getStateWithConnections(BlockState state, Level level, BlockPos pos, Direction clickedFace) {
      FluidState fluidState = level.getFluidState(pos);
      BlockPos northPos = pos.north();
      BlockPos southPos = pos.south();
      BlockPos westPos = pos.west();
      BlockPos eastPos = pos.east();
      BlockState northState = level.getBlockState(northPos);
      BlockState southState = level.getBlockState(southPos);
      BlockState westState = level.getBlockState(westPos);
      BlockState eastState = level.getBlockState(eastPos);
      boolean isHorizontalPlacement = clickedFace.getAxis().isHorizontal();
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)state.setValue(
                        TIED_TO_BELL, level.getBlockState(pos.above()).getBlock() == Blocks.BELL
                     ))
                     .setValue(
                        NORTH,
                        isHorizontalPlacement
                           ? tieToAnythingValid(northState, northState.isFaceSturdy(level, northPos, Direction.SOUTH))
                           : tieToRopeAndWalls(northState)
                     ))
                  .setValue(
                     SOUTH,
                     isHorizontalPlacement
                        ? tieToAnythingValid(southState, southState.isFaceSturdy(level, southPos, Direction.NORTH))
                        : tieToRopeAndWalls(southState)
                  ))
               .setValue(
                  WEST,
                  isHorizontalPlacement ? tieToAnythingValid(westState, westState.isFaceSturdy(level, westPos, Direction.EAST)) : tieToRopeAndWalls(westState)
               ))
            .setValue(
               EAST,
               isHorizontalPlacement ? tieToAnythingValid(eastState, eastState.isFaceSturdy(level, eastPos, Direction.WEST)) : tieToRopeAndWalls(eastState)
            ))
         .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
   }

   public static boolean tieToAnythingValid(BlockState state, boolean solidSide) {
      return !isExceptionForConnection(state) && solidSide || tieToRopeAndWalls(state);
   }

   public static boolean tieToRopeAndWalls(BlockState state) {
      return state.getBlock() instanceof IronBarsBlock || state.is(BlockTags.WALLS);
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return Shapes.empty();
   }

   public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
      return LOWER_SUPPORT_AABB;
   }

   public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
      return useContext.getItemInHand().getItem() == this.asItem();
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      boolean tiedToBell = (Boolean)state.getValue(TIED_TO_BELL);
      if (facing == Direction.UP) {
         tiedToBell = level.getBlockState(facingPos).getBlock() == Blocks.BELL;
      }

      return facing.getAxis().isHorizontal()
         ? (BlockState)((BlockState)state.setValue(TIED_TO_BELL, tiedToBell))
            .setValue((Property)PROPERTY_BY_DIRECTION.get(facing), tieToRopeAndWalls(facingState))
         : super.updateShape((BlockState)state.setValue(TIED_TO_BELL, tiedToBell), facing, facingState, level, currentPos, facingPos);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{NORTH, EAST, WEST, SOUTH, WATERLOGGED, TIED_TO_BELL});
   }
}
