package at.petrak.hexcasting.common.blocks.circles;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.casting.circles.ICircleComponent;
import at.petrak.hexcasting.api.casting.eval.ExecutionClientView;
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.lib.HexItems;
import java.util.EnumSet;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BlockSlate extends BlockCircleComponent implements EntityBlock, SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final EnumProperty<AttachFace> ATTACH_FACE = BlockStateProperties.ATTACH_FACE;
   public static final double THICKNESS = 1.0;
   public static final VoxelShape AABB_FLOOR = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
   public static final VoxelShape AABB_CEILING = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
   public static final VoxelShape AABB_EAST_WALL = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
   public static final VoxelShape AABB_WEST_WALL = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
   public static final VoxelShape AABB_SOUTH_WALL = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
   public static final VoxelShape AABB_NORTH_WALL = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

   public BlockSlate(Properties p_53182_) {
      super(p_53182_);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(ENERGIZED, false)).setValue(FACING, Direction.NORTH))
            .setValue(WATERLOGGED, false)
      );
   }

   public boolean propagatesSkylightDown(BlockState state, @Nonnull BlockGetter reader, @Nonnull BlockPos pos) {
      return !(Boolean)state.getValue(WATERLOGGED);
   }

   @Nonnull
   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   @Override
   public ICircleComponent.ControlFlow acceptControlFlow(
      CastingImage imageIn, CircleCastEnv env, Direction enterDir, BlockPos pos, BlockState bs, ServerLevel world
   ) {
      if (world.getBlockEntity(pos) instanceof BlockEntitySlate tile) {
         HexPattern pattern = tile.pattern;
         EnumSet<Direction> exitDirsSet = this.possibleExitDirections(pos, bs, world);
         exitDirsSet.remove(enterDir.getOpposite());
         Stream exitDirs = exitDirsSet.stream().map(dir -> this.exitPositionFromDirection(pos, dir));
         if (pattern == null) {
            return new ICircleComponent.ControlFlow.Continue(imageIn, exitDirs.toList());
         } else {
            CastingVM vm = new CastingVM(imageIn, env);
            ExecutionClientView result = vm.queueExecuteAndWrapIota(new PatternIota(pattern), world);
            return (ICircleComponent.ControlFlow)(result.getResolutionType().getSuccess()
               ? new ICircleComponent.ControlFlow.Continue(vm.getImage(), exitDirs.toList())
               : new ICircleComponent.ControlFlow.Stop());
         }
      } else {
         return new ICircleComponent.ControlFlow.Stop();
      }
   }

   @Override
   public boolean canEnterFromDirection(Direction enterDir, BlockPos pos, BlockState bs, ServerLevel world) {
      Direction thisNormal = this.normalDir(pos, bs, world);
      return enterDir != thisNormal.getOpposite();
   }

   @Override
   public EnumSet<Direction> possibleExitDirections(BlockPos pos, BlockState bs, Level world) {
      EnumSet<Direction> allDirs = EnumSet.allOf(Direction.class);
      Direction normal = this.normalDir(pos, bs, world);
      allDirs.remove(normal);
      return allDirs;
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
      if (level.getBlockEntity(pos) instanceof BlockEntitySlate slate) {
         ItemStack stack = new ItemStack(HexItems.SLATE);
         if (slate.pattern != null) {
            HexItems.SLATE.writeDatum(stack, new PatternIota(slate.pattern));
         }

         return stack;
      } else {
         return new ItemStack(this);
      }
   }

   @Override
   public Direction normalDir(BlockPos pos, BlockState bs, Level world, int recursionLeft) {
      return switch ((AttachFace)bs.getValue(ATTACH_FACE)) {
         case FLOOR -> Direction.UP;
         case CEILING -> Direction.DOWN;
         case WALL -> (Direction)bs.getValue(FACING);
         default -> throw new IncompatibleClassChangeError();
      };
   }

   @Override
   public float particleHeight(BlockPos pos, BlockState bs, Level world) {
      return -0.4375F;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new BlockEntitySlate(pPos, pState);
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return switch ((AttachFace)pState.getValue(ATTACH_FACE)) {
         case FLOOR -> AABB_FLOOR;
         case CEILING -> AABB_CEILING;
         case WALL -> {
            switch ((Direction)pState.getValue(FACING)) {
               case NORTH:
                  yield AABB_NORTH_WALL;
               case EAST:
                  yield AABB_EAST_WALL;
               case SOUTH:
                  yield AABB_SOUTH_WALL;
               default:
                  yield AABB_WEST_WALL;
            }
         }
         default -> throw new IncompatibleClassChangeError();
      };
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{FACING, ATTACH_FACE, WATERLOGGED});
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      FluidState fluidState = pContext.getLevel().getFluidState(pContext.getClickedPos());

      for (Direction direction : pContext.getNearestLookingDirections()) {
         BlockState blockstate;
         if (direction.getAxis() == Axis.Y) {
            blockstate = (BlockState)((BlockState)this.defaultBlockState()
                  .setValue(ATTACH_FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR))
               .setValue(FACING, pContext.getHorizontalDirection().getOpposite());
         } else {
            blockstate = (BlockState)((BlockState)this.defaultBlockState().setValue(ATTACH_FACE, AttachFace.WALL)).setValue(FACING, direction.getOpposite());
         }

         blockstate = (BlockState)blockstate.setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8);
         if (blockstate.canSurvive(pContext.getLevel(), pContext.getClickedPos())) {
            return blockstate;
         }
      }

      return null;
   }

   public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
      return canAttach(pLevel, pPos, getConnectedDirection(pState).getOpposite());
   }

   public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
      if ((Boolean)pState.getValue(WATERLOGGED)) {
         pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
      }

      return getConnectedDirection(pState).getOpposite() == pFacing && !pState.canSurvive(pLevel, pCurrentPos)
         ? pState.getFluidState().createLegacyBlock()
         : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
   }

   public static boolean canAttach(LevelReader pReader, BlockPos pPos, Direction pDirection) {
      BlockPos blockpos = pPos.relative(pDirection);
      return pReader.getBlockState(blockpos).isFaceSturdy(pReader, blockpos, pDirection.getOpposite());
   }

   protected static Direction getConnectedDirection(BlockState pState) {
      return switch ((AttachFace)pState.getValue(ATTACH_FACE)) {
         case FLOOR -> Direction.UP;
         case CEILING -> Direction.DOWN;
         default -> (Direction)pState.getValue(FACING);
      };
   }
}
