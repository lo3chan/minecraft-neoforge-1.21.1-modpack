package net.joefoxe.hexerei.block.custom;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HerbDryingRackFull extends Block implements SimpleWaterloggedBlock {
   public static final IntegerProperty ANGLE = IntegerProperty.create("angle", 0, 180);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final VoxelShape SHAPE = Optional.of(Block.box(0.5, 5.5, 7.5, 15.5, 16.0, 8.5)).get();
   public static final VoxelShape SHAPE_TURNED = Optional.of(Block.box(7.5, 5.5, 0.5, 8.5, 16.0, 15.5)).get();

   public RenderShape getRenderShape(BlockState iBlockState) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
      return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()))
            .setValue(ANGLE, 0))
         .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
   }

   public BlockState rotate(BlockState pState, Rotation pRot) {
      return (BlockState)pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate((Direction)pState.getValue(HorizontalDirectionalBlock.FACING)));
   }

   public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
      return p_220053_1_.getValue(HorizontalDirectionalBlock.FACING) != Direction.NORTH
            && p_220053_1_.getValue(HorizontalDirectionalBlock.FACING) != Direction.SOUTH
         ? SHAPE_TURNED
         : SHAPE;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.herb_drying_rack_full_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
      } else {
         tooltipComponents.add(
            Component.translatable(
                  "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
      }

      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }

   public HerbDryingRackFull(Properties properties) {
      super(properties.noCollission());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HorizontalDirectionalBlock.FACING, ANGLE, WATERLOGGED});
   }

   public void setAngle(Level worldIn, BlockPos pos, BlockState state, int angle) {
      worldIn.setBlock(pos, (BlockState)state.setValue(ANGLE, Mth.clamp(angle, 0, 180)), 2);
   }

   public int getAngle(Level worldIn, BlockPos pos) {
      return (Integer)worldIn.getBlockState(pos).getValue(ANGLE);
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
      }

      return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, pos, facingPos);
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
      return canSupportCenter(worldIn, pos.above(), Direction.DOWN);
   }
}
