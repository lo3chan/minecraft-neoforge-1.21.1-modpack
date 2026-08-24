package com.mcwlights.kikoz.objects;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class LightBaseTall extends LightBaseShort {
   public static final EnumProperty<LightBaseTall.LightPart> PART = EnumProperty.create("part", LightBaseTall.LightPart.class);
   public static final BooleanProperty LIT = BlockStateProperties.LIT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   public LightBaseTall(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, true)).setValue(PART, LightBaseTall.LightPart.BASE))
            .setValue(POWERED, false)
      );
   }

   protected BlockState LightState(BlockState state, LevelAccessor level, BlockPos pos) {
      boolean above = level.getBlockState(pos.above()).getBlock() == this;
      boolean below = level.getBlockState(pos.below()).getBlock() == this;
      Boolean lit = (Boolean)state.getValue(LIT);
      if (lit) {
         if (above && below) {
            return (BlockState)((BlockState)state.setValue(PART, LightBaseTall.LightPart.MIDDLE)).setValue(LIT, false);
         } else if (!above && below) {
            return (BlockState)((BlockState)state.setValue(PART, LightBaseTall.LightPart.TOP)).setValue(LIT, true);
         } else {
            return above && !below
               ? (BlockState)((BlockState)state.setValue(PART, LightBaseTall.LightPart.BOTTOM)).setValue(LIT, false)
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

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState statetwo, boolean bool) {
      if (!statetwo.is(state.getBlock())) {
         this.LightState(state, level, pos);
      }
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return this.LightState(super.getStateForPlacement(context), context.getLevel(), context.getClickedPos());
   }

   public void placeAt(Level level, BlockPos pos, int num) {
      level.setBlock(pos, this.defaultBlockState(), num);
   }

   @Override
   public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor level, BlockPos pos, BlockPos newPos) {
      return this.LightState(state, level, pos);
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

   public static enum LightPart implements StringRepresentable {
      BASE("base"),
      TOP("top"),
      MIDDLE("middle"),
      BOTTOM("bottom");

      private final String name;

      private LightPart(final String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
