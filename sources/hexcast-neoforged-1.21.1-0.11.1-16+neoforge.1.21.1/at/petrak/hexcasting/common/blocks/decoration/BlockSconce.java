package at.petrak.hexcasting.common.blocks.decoration;

import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AmethystBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockSconce extends AmethystBlock implements SimpleWaterloggedBlock {
   public static final DirectionProperty FACING = BlockStateProperties.FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static VoxelShape AABB_UP = Block.box(4.0, 0.0, 4.0, 12.0, 1.0, 12.0);
   protected static VoxelShape AABB_DOWN = Block.box(4.0, 15.0, 4.0, 12.0, 16.0, 12.0);
   protected static VoxelShape AABB_NORTH = Block.box(4.0, 4.0, 15.0, 12.0, 12.0, 16.0);
   protected static VoxelShape AABB_SOUTH = Block.box(4.0, 4.0, 0.0, 12.0, 12.0, 1.0);
   protected static VoxelShape AABB_WEST = Block.box(15.0, 4.0, 4.0, 16.0, 12.0, 12.0);
   protected static VoxelShape AABB_EAST = Block.box(0.0, 4.0, 4.0, 1.0, 12.0, 12.0);

   public BlockSconce(Properties p_49795_) {
      super(p_49795_);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, false)).setValue(FACING, Direction.UP));
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return switch ((Direction)pState.getValue(FACING)) {
         case UP -> AABB_UP;
         case DOWN -> AABB_DOWN;
         case NORTH -> AABB_NORTH;
         case EAST -> AABB_EAST;
         case SOUTH -> AABB_SOUTH;
         case WEST -> AABB_WEST;
         default -> throw new IncompatibleClassChangeError();
      };
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{FACING, WATERLOGGED});
   }

   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      FluidState fluidState = pContext.getLevel().getFluidState(pContext.getClickedPos());
      BlockState blockstate = (BlockState)this.defaultBlockState().setValue(FACING, pContext.getClickedFace());
      return (BlockState)blockstate.setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8);
   }

   public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
      if ((Boolean)pState.getValue(WATERLOGGED)) {
         pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
      }

      return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
   }

   public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource rand) {
      if (rand.nextFloat() < 0.8F) {
         double cx = pPos.getX() + 0.5;
         double cy = pPos.getY() + 0.5;
         double cz = pPos.getZ() + 0.5;

         double dX = switch ((Direction)pState.getValue(FACING)) {
            case EAST -> rand.triangle(0.009999999776482582, 0.05000000074505806);
            case WEST -> rand.triangle(-0.009999999776482582, -0.05000000074505806);
            default -> rand.triangle(-0.009999999776482582, 0.009999999776482582);
         };

         double dY = switch ((Direction)pState.getValue(FACING)) {
            case UP -> rand.triangle(0.009999999776482582, 0.05000000074505806);
            case DOWN -> rand.triangle(-0.009999999776482582, -0.05000000074505806);
            default -> rand.triangle(-0.009999999776482582, 0.009999999776482582);
         };

         double dZ = switch ((Direction)pState.getValue(FACING)) {
            case NORTH -> rand.triangle(-0.009999999776482582, -0.05000000074505806);
            case SOUTH -> rand.triangle(0.009999999776482582, 0.05000000074505806);
            default -> rand.triangle(-0.009999999776482582, 0.009999999776482582);
         };
         int[] colors = new int[]{-9482325, -5009677, -3170061, -3170061, -555};
         pLevel.addParticle(new ConjureParticleOptions(colors[rand.nextInt(colors.length)]), cx, cy, cz, dX, dY, dZ);
         if (rand.nextFloat() < 0.08F) {
            pLevel.playLocalSound(cx, cy, cz, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 0.5F + rand.nextFloat() * 1.2F, false);
         }
      }
   }
}
