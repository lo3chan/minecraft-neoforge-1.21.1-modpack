package net.mehvahdjukaar.amendments.common.block;

import com.mojang.serialization.MapCodec;
import net.mehvahdjukaar.amendments.common.tile.ToolHookBlockTile;
import net.mehvahdjukaar.moonlight.api.block.ItemDisplayTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ToolHookBlock extends Block implements EntityBlock {
   public static final MapCodec<ToolHookBlock> CODEC = simpleCodec(ToolHookBlock::new);
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final VoxelShape NORTH_AABB = Block.box(5.0, 5.0, 10.0, 11.0, 15.0, 16.0);
   public static final VoxelShape SOUTH_AABB = Block.box(5.0, 5.0, 0.0, 11.0, 15.0, 6.0);
   public static final VoxelShape WEST_AABB = Block.box(10.0, 5.0, 5.0, 16.0, 15.0, 11.0);
   public static final VoxelShape EAST_AABB = Block.box(0.0, 5.0, 5.0, 6.0, 15.0, 11.0);

   public ToolHookBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   protected MapCodec<? extends ToolHookBlock> codec() {
      return CODEC;
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return switch ((Direction)state.getValue(FACING)) {
         case WEST -> WEST_AABB;
         case SOUTH -> SOUTH_AABB;
         case NORTH -> NORTH_AABB;
         default -> EAST_AABB;
      };
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      Direction direction = (Direction)state.getValue(FACING);
      BlockPos blockPos = pos.relative(direction.getOpposite());
      BlockState blockState = level.getBlockState(blockPos);
      return direction.getAxis().isHorizontal() && blockState.isFaceSturdy(level, blockPos, direction);
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      return direction.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, pos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState blockState = this.defaultBlockState();
      LevelReader levelReader = context.getLevel();
      BlockPos blockPos = context.getClickedPos();

      for (Direction direction : context.getNearestLookingDirections()) {
         if (direction.getAxis().isHorizontal()) {
            Direction opposite = direction.getOpposite();
            blockState = (BlockState)blockState.setValue(FACING, opposite);
            if (blockState.canSurvive(levelReader, blockPos)) {
               return blockState;
            }
         }
      }

      return null;
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      return level.getBlockEntity(pos) instanceof ToolHookBlockTile tile
         ? tile.interactWithPlayerItem(player, hand, stack)
         : super.useItemOn(stack, state, level, pos, player, hand, hit);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new ToolHookBlockTile(pos, state);
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader world, BlockPos pos, Player player) {
      if (world.getBlockEntity(pos) instanceof ItemDisplayTile tile) {
         ItemStack i = tile.getDisplayedItem();
         if (!i.isEmpty()) {
            return i;
         }
      }

      return super.getCloneItemStack(world, pos, state);
   }

   public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         if (world.getBlockEntity(pos) instanceof ItemDisplayTile tile) {
            Containers.dropContents(world, pos, tile);
            world.updateNeighbourForOutputSignal(pos, this);
         }

         super.onRemove(state, world, pos, newState, isMoving);
      }
   }
}
