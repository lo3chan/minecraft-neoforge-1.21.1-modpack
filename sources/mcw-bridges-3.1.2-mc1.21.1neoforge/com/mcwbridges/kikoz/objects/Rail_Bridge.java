package com.mcwbridges.kikoz.objects;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Rail_Bridge extends Block {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   protected static final VoxelShape OCCLUSION = Block.box(0.0, 0.1, 0.0, 16.0, 16.0, 16.0);

   public Rail_Bridge(Properties prop) {
      super(prop);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false));
   }

   public VoxelShape getOcclusionShape(BlockState state, BlockGetter reader, BlockPos pos) {
      return Shapes.empty();
   }

   public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext contx) {
      return OCCLUSION;
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{POWERED, FACING});
   }

   public int getSignal(BlockState state, BlockGetter reader, BlockPos pos, Direction dir) {
      return state.getValue(POWERED) ? 10 : 0;
   }

   public boolean isSignalSource(BlockState state) {
      return (Boolean)state.getValue(POWERED);
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      Item item = itemstack.getItem();
      Boolean power = (Boolean)state.getValue(POWERED);
      if (power && item == Items.AIR) {
         state = (BlockState)state.cycle(POWERED);
         level.setBlock(pos, state, 2);
         dropTorch(level, pos);
         return ItemInteractionResult.CONSUME;
      } else if (item == Items.REDSTONE_TORCH && !power) {
         state = (BlockState)state.cycle(POWERED);
         level.setBlock(pos, state, 2);
         if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
         }

         return ItemInteractionResult.CONSUME;
      } else {
         return !power && item != Items.REDSTONE_TORCH
            ? ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
            : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public static void dropTorch(Level level, BlockPos pos) {
      popResource(level, pos, new ItemStack(Items.REDSTONE_TORCH, 1));
   }

   public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
      Boolean i = (Boolean)state.getValue(POWERED);
      if (!level.isClientSide && i) {
         dropTorch(level, pos);
      }

      return super.playerWillDestroy(level, pos, state, player);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext contx) {
      return (BlockState)this.defaultBlockState().setValue(FACING, contx.getHorizontalDirection().getClockWise());
   }

   public void placeAt(Level level, BlockPos pos, int num) {
      level.setBlock(pos, this.defaultBlockState(), num);
   }
}
