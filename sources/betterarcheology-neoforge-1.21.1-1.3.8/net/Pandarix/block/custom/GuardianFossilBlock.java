package net.Pandarix.block.custom;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import net.Pandarix.block.entity.GuardianFossilBlockEntity;
import net.Pandarix.block.entity.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuardianFossilBlock extends FossilBaseWithEntityBlock implements SimpleWaterloggedBlock {
   private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   private static final Map<Direction, VoxelShape> SHAPES_FOR_DIRECTION = ImmutableMap.of(
      Direction.NORTH,
      Shapes.or(
         Block.box(1.0, 0.0, -2.0, 15.0, 15.0, 12.0), new VoxelShape[]{Block.box(4.0, 4.0, 12.0, 12.0, 12.0, 23.0), Block.box(7.0, 2.0, 23.0, 9.0, 14.0, 32.0)}
      ),
      Direction.SOUTH,
      Shapes.or(
         Block.box(1.0, 0.0, 4.0, 15.0, 15.0, 18.0), new VoxelShape[]{Block.box(4.0, 4.0, -7.0, 12.0, 12.0, 4.0), Block.box(7.0, 2.0, -16.0, 9.0, 14.0, -7.0)}
      ),
      Direction.WEST,
      Shapes.or(
         Block.box(-2.0, 0.0, 1.0, 12.0, 15.0, 15.0), new VoxelShape[]{Block.box(12.0, 4.0, 4.0, 23.0, 12.0, 12.0), Block.box(23.0, 2.0, 7.0, 32.0, 14.0, 9.0)}
      ),
      Direction.EAST,
      Shapes.or(
         Block.box(4.0, 0.0, 1.0, 18.0, 15.0, 15.0), new VoxelShape[]{Block.box(-7.0, 4.0, 4.0, 4.0, 12.0, 12.0), Block.box(-16.0, 2.0, 7.0, -7.0, 14.0, 9.0)}
      )
   );

   public GuardianFossilBlock(Properties settings) {
      super(settings);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, Boolean.FALSE)).setValue(FACING, Direction.NORTH)
      );
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
      return (BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, fluidstate.isSourceOfType(Fluids.WATER)))
         .setValue(FACING, pContext.getHorizontalDirection().getOpposite());
   }

   @NotNull
   public FluidState getFluidState(BlockState pState) {
      return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return createTickerHelper(type, (BlockEntityType)ModBlockEntities.GUARDIAN_FOSSIL.get(), GuardianFossilBlockEntity::tick);
   }

   @NotNull
   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPES_FOR_DIRECTION.get(blockState.getValue(FACING));
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new GuardianFossilBlockEntity(pos, state);
   }

   @NotNull
   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> component, TooltipFlag flag) {
      component.add(Component.translatable("block.betterarcheology.guardian_fossil_tooltip").withStyle(ChatFormatting.GRAY));
      super.appendHoverText(stack, context, component, flag);
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{WATERLOGGED, FACING});
   }
}
