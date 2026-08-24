package net.Pandarix.block.custom;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import net.Pandarix.block.entity.ChickenFossilBlockEntity;
import net.Pandarix.block.entity.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChickenFossilBlock extends FossilBaseWithEntityBlock {
   private static final Map<Direction, VoxelShape> CHICKEN_SHAPES_FOR_DIRECTION = ImmutableMap.of(
      Direction.NORTH,
      Shapes.or(
         Block.box(5.0, 0.0, 1.5, 11.0, 11.25, 12.25),
         new VoxelShape[]{Block.box(6.5, 11.25, -4.0, 9.5, 16.0, 3.0), Block.box(7.0, 8.25, 12.25, 9.0, 10.0, 22.25)}
      ),
      Direction.SOUTH,
      Shapes.or(
         Block.box(5.0, 0.0, 3.75, 11.0, 11.25, 14.5),
         new VoxelShape[]{Block.box(6.5, 11.25, 13.0, 9.5, 16.0, 20.0), Block.box(7.0, 8.25, -6.25, 9.0, 10.0, 3.75)}
      ),
      Direction.WEST,
      Shapes.or(
         Block.box(1.5, 0.0, 5.0, 12.25, 11.25, 11.0),
         new VoxelShape[]{Block.box(-4.0, 11.25, 6.5, 3.0, 16.0, 9.5), Block.box(12.25, 8.25, 7.0, 22.25, 10.0, 9.0)}
      ),
      Direction.EAST,
      Shapes.or(
         Block.box(3.75, 0.0, 5.0, 14.5, 11.25, 11.0),
         new VoxelShape[]{Block.box(13.0, 11.25, 6.5, 20.0, 16.0, 9.5), Block.box(-6.25, 8.25, 7.0, 3.75, 10.0, 9.0)}
      )
   );

   public ChickenFossilBlock(Properties settings) {
      super(settings);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return createTickerHelper(type, (BlockEntityType)ModBlockEntities.CHICKEN_FOSSIL.get(), ChickenFossilBlockEntity::tick);
   }

   @NotNull
   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return CHICKEN_SHAPES_FOR_DIRECTION.get(blockState.getValue(FACING));
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new ChickenFossilBlockEntity(pos, state);
   }

   @NotNull
   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> component, TooltipFlag flag) {
      component.add(Component.translatable("block.betterarcheology.chicken_fossil_tooltip").withStyle(ChatFormatting.GRAY));
      super.appendHoverText(stack, context, component, flag);
   }
}
