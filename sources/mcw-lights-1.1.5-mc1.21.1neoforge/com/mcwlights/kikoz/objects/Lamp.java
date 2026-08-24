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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Lamp extends LightBaseTall {
   private static final VoxelShape ONE = Block.box(4.0, 0.0, 4.0, 12.0, 2.0, 12.0);
   private static final VoxelShape TWO = Block.box(6.0, 2.0, 6.0, 10.0, 6.0, 10.0);
   private static final VoxelShape THREE = Block.box(2.0, 6.0, 2.0, 14.0, 16.0, 14.0);
   private static final VoxelShape BASE = Shapes.or(ONE, new VoxelShape[]{TWO, THREE});
   private static final VoxelShape TOP = Block.box(2.0, 0.0, 2.0, 14.0, 15.0, 14.0);
   private static final VoxelShape MIDDLE_BOTTOM = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

   public Lamp(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, true)).setValue(PART, LightBaseTall.LightPart.BOTTOM))
            .setValue(POWERED, false)
      );
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      switch ((LightBaseTall.LightPart)state.getValue(PART)) {
         case BASE:
            return BASE;
         case TOP:
            return TOP;
         case MIDDLE:
            return MIDDLE_BOTTOM;
         case BOTTOM:
            return MIDDLE_BOTTOM;
         default:
            return BASE;
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
      } else if ((!worldIn.isClientSide() || part != LightBaseTall.LightPart.BOTTOM) && part != LightBaseTall.LightPart.MIDDLE) {
         if (part != LightBaseTall.LightPart.TOP && part != LightBaseTall.LightPart.BASE) {
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
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{PART, LIT, POWERED});
   }
}
