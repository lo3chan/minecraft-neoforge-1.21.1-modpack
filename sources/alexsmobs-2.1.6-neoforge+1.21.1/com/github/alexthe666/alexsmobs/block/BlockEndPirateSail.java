package com.github.alexthe666.alexsmobs.block;

import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockEndPirateSail extends Block {
   public static final BooleanProperty EASTORWEST = BooleanProperty.create("eastorwest");
   public static final EnumProperty<BlockEndPirateSail.SailType> SAIL = EnumProperty.create("sail", BlockEndPirateSail.SailType.class);
   protected static final VoxelShape EW_AABB = Block.box(7.0, 0.0, 0.0, 9.0, 16.0, 16.0);
   protected static final VoxelShape NS_AABB = Block.box(0.0, 0.0, 7.0, 16.0, 16.0, 9.0);

   public BlockEndPirateSail(boolean spectre) {
      super(
         Properties.of()
            .mapColor(MapColor.COLOR_BLUE)
            .noOcclusion()
            .emissiveRendering((a, b, c) -> true)
            .sound(SoundType.WOOL)
            .lightLevel(state -> 5)
            .requiresCorrectToolForDrops()
            .strength(0.4F)
      );
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(EASTORWEST, false)).setValue(SAIL, BlockEndPirateSail.SailType.SINGLE)
      );
   }

   public VoxelShape getShape(BlockState p_52807_, BlockGetter p_52808_, BlockPos p_52809_, CollisionContext p_52810_) {
      return p_52807_.getValue(EASTORWEST) ? EW_AABB : NS_AABB;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> p_58032_) {
      p_58032_.add(new Property[]{EASTORWEST, SAIL});
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      LevelReader levelreader = context.getLevel();
      BlockPos blockpos = context.getClickedPos();
      BlockPos actualPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
      BlockPos u = blockpos.above();
      BlockPos d = blockpos.below();
      BlockState clickState = levelreader.getBlockState(actualPos);
      BlockState upState = levelreader.getBlockState(u);
      BlockState downState = levelreader.getBlockState(d);
      boolean axis = context.getClickedFace().getAxis() == Axis.Y
         ? context.getHorizontalDirection().getAxis() == Axis.X
         : context.getClickedFace().getAxis() != Axis.X;
      if (clickState.getBlock() instanceof BlockEndPirateSail) {
         axis = (Boolean)clickState.getValue(EASTORWEST);
      }

      BlockState axisState = (BlockState)this.defaultBlockState().setValue(EASTORWEST, axis);
      return (BlockState)axisState.setValue(SAIL, getSailTypeFor(axisState, downState, upState));
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState state2, LevelAccessor levelreader, BlockPos blockpos, BlockPos pos2) {
      BlockPos u = blockpos.above();
      BlockPos d = blockpos.below();
      BlockState upState = levelreader.getBlockState(u);
      BlockState downState = levelreader.getBlockState(d);
      return (BlockState)state.setValue(SAIL, getSailTypeFor(state, downState, upState));
   }

   private static BlockEndPirateSail.SailType getSailTypeFor(BlockState us, BlockState below, BlockState above) {
      if (below.getBlock() instanceof BlockEndPirateSail && below.getValue(EASTORWEST) == us.getValue(EASTORWEST)) {
         return above.getBlock() instanceof BlockEndPirateSail ? BlockEndPirateSail.SailType.MIDDLE : BlockEndPirateSail.SailType.TOP;
      } else {
         return above.getBlock() instanceof BlockEndPirateSail && above.getValue(EASTORWEST) == us.getValue(EASTORWEST)
            ? BlockEndPirateSail.SailType.BOTTOM
            : BlockEndPirateSail.SailType.SINGLE;
      }
   }

   private static enum SailType implements StringRepresentable {
      SINGLE,
      TOP,
      MIDDLE,
      BOTTOM;

      @Override
      public String toString() {
         return this.getSerializedName();
      }

      public String getSerializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
