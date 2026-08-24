package com.mcwbridges.kikoz.objects;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Bridge_Support extends Block implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape OCCLUSION = Block.box(0.0, 0.01, 0.0, 16.0, 16.0, 16.0);

   public Bridge_Support(Properties prop) {
      super(prop);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, false));
   }

   public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext contx) {
      return OCCLUSION;
   }

   public void onBroken(Level level, BlockPos pos) {
      level.levelEvent(1029, pos, 0);
   }

   public VoxelShape getOcclusionShape(BlockState state, BlockGetter reader, BlockPos pos) {
      return Shapes.empty();
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.defaultFluidState() : super.getFluidState(state);
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      if (itemstack.getItem() instanceof BlockItem) {
         BlockItem blockItem = (BlockItem)itemstack.getItem();
         if (blockItem.getBlock() == this) {
            BlockPos highestPos = pos;

            while (level.getBlockState(highestPos.above()).getBlock() == this) {
               highestPos = highestPos.above();
            }

            BlockPos placePos = highestPos.above();
            if (level.getBlockState(placePos).isAir()) {
               level.setBlock(placePos, this.defaultBlockState(), 3);
               if (!player.getAbilities().instabuild) {
                  itemstack.shrink(1);
               }

               level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
               return ItemInteractionResult.SUCCESS;
            }
         }
      }

      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   public BlockState getStateForPlacement(BlockPlaceContext contx) {
      FluidState fluidstate = contx.getLevel().getFluidState(contx.getClickedPos());
      boolean flag = fluidstate.getType() == Fluids.WATER;
      return (BlockState)super.getStateForPlacement(contx).setValue(WATERLOGGED, flag);
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public void placeAt(Level level, BlockPos pos) {
      level.setBlock(pos, this.defaultBlockState(), 3);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{WATERLOGGED});
   }
}
