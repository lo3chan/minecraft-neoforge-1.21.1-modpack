package net.mehvahdjukaar.amendments.common.block;

import net.mehvahdjukaar.amendments.common.CakeRegistry;
import net.mehvahdjukaar.amendments.configs.CommonConfigs;
import net.mehvahdjukaar.amendments.integration.CompatHandler;
import net.mehvahdjukaar.amendments.integration.SuppCompat;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DirectionalCakeBlock extends CakeBlock implements SimpleWaterloggedBlock {
   protected static final VoxelShape[] SHAPES_NORTH = new VoxelShape[]{
      Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0),
      Block.box(1.0, 0.0, 3.0, 15.0, 8.0, 15.0),
      Block.box(1.0, 0.0, 5.0, 15.0, 8.0, 15.0),
      Block.box(1.0, 0.0, 7.0, 15.0, 8.0, 15.0),
      Block.box(1.0, 0.0, 9.0, 15.0, 8.0, 15.0),
      Block.box(1.0, 0.0, 11.0, 15.0, 8.0, 15.0),
      Block.box(1.0, 0.0, 13.0, 15.0, 8.0, 15.0)
   };
   protected static final VoxelShape[] SHAPES_SOUTH = new VoxelShape[]{
      Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0),
      Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 13.0),
      Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 11.0),
      Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 9.0),
      Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 7.0),
      Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 5.0),
      Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 3.0)
   };
   protected static final VoxelShape[] SHAPES_EAST = new VoxelShape[]{
      Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0),
      Block.box(1.0, 0.0, 1.0, 13.0, 8.0, 15.0),
      Block.box(1.0, 0.0, 1.0, 11.0, 8.0, 15.0),
      Block.box(1.0, 0.0, 1.0, 9.0, 8.0, 15.0),
      Block.box(1.0, 0.0, 1.0, 7.0, 8.0, 15.0),
      Block.box(1.0, 0.0, 1.0, 5.0, 8.0, 15.0),
      Block.box(1.0, 0.0, 1.0, 3.0, 8.0, 15.0)
   };
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public final CakeRegistry.CakeType type;

   public DirectionalCakeBlock(CakeRegistry.CakeType type) {
      this(Utils.copyPropertySafe(type.cake).dropsLike(type.cake), type);
   }

   public DirectionalCakeBlock(Properties properties, CakeRegistry.CakeType type) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(BITES, 0)).setValue(FACING, Direction.WEST)).setValue(WATERLOGGED, false)
      );
      this.type = type;
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
      if ((Boolean)stateIn.getValue(WATERLOGGED)) {
         worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
      }

      return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      Direction d = getHitDir(player, hit);
      if (level.isClientSide) {
         if (this.eatSliceD(level, pos, state, player, d).consumesAction()) {
            return InteractionResult.SUCCESS;
         }

         if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            return InteractionResult.CONSUME;
         }
      }

      return this.eatSliceD(level, pos, state, player, d);
   }

   public static Direction getHitDir(Player player, BlockHitResult hit) {
      return hit.getDirection().getAxis() != Axis.Y ? hit.getDirection() : player.getDirection().getOpposite();
   }

   public InteractionResult eatSliceD(LevelAccessor level, BlockPos pos, BlockState state, Player player, Direction dir) {
      if (!player.canEat(false)) {
         return InteractionResult.PASS;
      } else {
         player.awardStat(Stats.EAT_CAKE_SLICE);
         level.gameEvent(player, GameEvent.EAT, pos);
         player.getFoodData().eat(2, 0.1F);
         if (!level.isClientSide()) {
            this.removeSlice(state, pos, level, player, dir);
         }

         return InteractionResult.sidedSuccess(level.isClientSide());
      }
   }

   public void removeSlice(BlockState state, BlockPos pos, LevelAccessor level, Player player, Direction dir) {
      int i = (Integer)state.getValue(BITES);
      if (i < 6) {
         if (i == 0 && CommonConfigs.DIRECTIONAL_CAKE.get()) {
            state = (BlockState)state.setValue(FACING, dir);
         }

         level.setBlock(pos, (BlockState)state.setValue(BITES, i + 1), 3);
      } else {
         level.removeBlock(pos, false);
         level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
      }
   }

   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      return new ItemStack(Items.CAKE);
   }

   public MutableComponent getName() {
      return Blocks.CAKE.getName();
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, WATERLOGGED});
      super.createBlockStateDefinition(builder);
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return switch ((Direction)state.getValue(FACING)) {
         case EAST -> SHAPES_EAST[state.getValue(BITES)];
         case SOUTH -> SHAPES_SOUTH[state.getValue(BITES)];
         case NORTH -> SHAPES_NORTH[state.getValue(BITES)];
         default -> SHAPE_BY_BITE[state.getValue(BITES)];
      };
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()))
         .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.getRotation((Direction)state.getValue(FACING)));
   }

   public void animateTick(BlockState stateIn, Level level, BlockPos pos, RandomSource rand) {
      if (CompatHandler.SUPPLEMENTARIES) {
         SuppCompat.spawnCakeParticles(level, pos, rand);
      }
   }
}
