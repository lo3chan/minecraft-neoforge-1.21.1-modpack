package com.mcwwindows.kikoz.objects;

import com.mcwwindows.kikoz.util.WindowPart;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class MosaicBlock extends StainedGlassBlock implements BeaconBeamBlock {
   public static final EnumProperty<WindowPart> PART = EnumProperty.create("part", WindowPart.class);
   private DyeColor color;

   public DyeColor getColor() {
      return this.color;
   }

   public MosaicBlock(DyeColor color, Properties properties) {
      super(color, properties);
      this.color = color;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(PART, WindowPart.BASE));
   }

   private BlockState WindowState(BlockState state, LevelAccessor level, BlockPos pos) {
      boolean above = level.getBlockState(pos.above()).getBlock() == this;
      boolean below = level.getBlockState(pos.below()).getBlock() == this;
      if (above && below) {
         return (BlockState)state.setValue(PART, WindowPart.MIDDLE);
      } else if (!above && below) {
         return (BlockState)state.setValue(PART, WindowPart.TOP);
      } else {
         return above && !below ? (BlockState)state.setValue(PART, WindowPart.BOTTOM) : (BlockState)state.setValue(PART, WindowPart.BASE);
      }
   }

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState statetwo, boolean bool) {
      if (!statetwo.is(state.getBlock())) {
         this.WindowState(state, level, pos);
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return this.WindowState(super.getStateForPlacement(context), context.getLevel(), context.getClickedPos());
   }

   public void placeAt(Level level, BlockPos pos, int num) {
      level.setBlock(pos, this.defaultBlockState(), num);
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor level, BlockPos pos, BlockPos newPos) {
      return this.WindowState(state, level, pos);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{PART});
   }
}
