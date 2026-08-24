package com.mcwlights.kikoz.objects;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CeilingLight extends LightBaseTall {
   private static final VoxelShape TOP_MID = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
   private static final VoxelShape ONE = Block.box(2.0, 6.0, 2.0, 14.0, 11.0, 14.0);
   private static final VoxelShape TWO = Block.box(4.0, 2.0, 4.0, 12.0, 7.0, 12.0);
   private static final VoxelShape THREE = Block.box(6.0, 11.0, 6.0, 10.0, 16.0, 10.0);
   private static final VoxelShape BASE_BOTTOM = Shapes.or(ONE, new VoxelShape[]{TWO, THREE});

   public CeilingLight(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, true)).setValue(PART, LightBaseTall.LightPart.BOTTOM))
            .setValue(POWERED, false)
      );
   }

   @Override
   protected BlockState LightState(BlockState state, LevelAccessor level, BlockPos pos) {
      boolean above = level.getBlockState(pos.above()).getBlock() == this;
      boolean below = level.getBlockState(pos.below()).getBlock() == this;
      Boolean lit = (Boolean)state.getValue(LIT);
      if (lit) {
         if (above && below) {
            return (BlockState)((BlockState)state.setValue(PART, LightBaseTall.LightPart.MIDDLE)).setValue(LIT, false);
         } else if (!above && below) {
            return (BlockState)((BlockState)state.setValue(PART, LightBaseTall.LightPart.TOP)).setValue(LIT, false);
         } else {
            return above && !below
               ? (BlockState)((BlockState)state.setValue(PART, LightBaseTall.LightPart.BOTTOM)).setValue(LIT, true)
               : (BlockState)((BlockState)state.setValue(PART, LightBaseTall.LightPart.BASE)).setValue(LIT, true);
         }
      } else if (above && below) {
         return (BlockState)((BlockState)state.setValue(PART, LightBaseTall.LightPart.MIDDLE)).setValue(LIT, false);
      } else if (!above && below) {
         return (BlockState)((BlockState)state.setValue(PART, LightBaseTall.LightPart.TOP)).setValue(LIT, false);
      } else {
         return above && !below
            ? (BlockState)((BlockState)state.setValue(PART, LightBaseTall.LightPart.BOTTOM)).setValue(LIT, false)
            : (BlockState)((BlockState)state.setValue(PART, LightBaseTall.LightPart.BASE)).setValue(LIT, false);
      }
   }

   @Override
   protected ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      LightBaseTall.LightPart part = (LightBaseTall.LightPart)state.getValue(PART);
      Item item = itemstack.getItem();
      if (item == this.asItem()) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else if ((worldIn.isClientSide() || part != LightBaseTall.LightPart.TOP) && part != LightBaseTall.LightPart.MIDDLE) {
         if (part != LightBaseTall.LightPart.BOTTOM && part != LightBaseTall.LightPart.BASE) {
            return ItemInteractionResult.sidedSuccess(worldIn.isClientSide);
         } else {
            state = (BlockState)state.cycle(LIT);
            worldIn.setBlock(pos, state, 10);
            worldIn.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.8F);
            return ItemInteractionResult.sidedSuccess(worldIn.isClientSide);
         }
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      switch ((LightBaseTall.LightPart)state.getValue(PART)) {
         case BASE:
            return BASE_BOTTOM;
         case TOP:
            return TOP_MID;
         case MIDDLE:
            return TOP_MID;
         case BOTTOM:
            return BASE_BOTTOM;
         default:
            return BASE_BOTTOM;
      }
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{PART, LIT, POWERED});
   }
}
