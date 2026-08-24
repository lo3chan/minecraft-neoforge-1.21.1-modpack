package net.Pandarix.block.custom;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import net.Pandarix.block.entity.SkeletonFleeFromBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WolfFossilBlock extends FossilBaseWithEntityBlock {
   private static final Map<Direction, VoxelShape> WOLF_SHAPES_FOR_DIRECTION = ImmutableMap.of(
      Direction.NORTH,
      Shapes.or(Block.box(4.0, 0.0, 2.0, 12.0, 16.0, 17.0), Block.box(4.0, 9.0, -6.0, 12.0, 16.0, 2.0)),
      Direction.SOUTH,
      Shapes.or(Block.box(4.0, 0.0, -1.0, 12.0, 16.0, 14.0), Block.box(4.0, 9.0, 14.0, 12.0, 16.0, 22.0)),
      Direction.EAST,
      Shapes.or(Block.box(-1.0, 0.0, 4.0, 14.0, 16.0, 12.0), Block.box(14.0, 9.0, 4.0, 22.0, 16.0, 12.0)),
      Direction.WEST,
      Shapes.or(Block.box(2.0, 0.0, 4.0, 17.0, 16.0, 12.0), Block.box(-6.0, 9.0, 4.0, 2.0, 16.0, 12.0))
   );

   public WolfFossilBlock(Properties settings) {
      super(settings);
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new SkeletonFleeFromBlockEntity(pos, state);
   }

   @NotNull
   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return WOLF_SHAPES_FOR_DIRECTION.get(pState.getValue(FACING));
   }

   @NotNull
   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> component, TooltipFlag flag) {
      component.add(Component.translatable("block.betterarcheology.wolf_fossil_tooltip").withStyle(ChatFormatting.GRAY));
      super.appendHoverText(stack, context, component, flag);
   }
}
