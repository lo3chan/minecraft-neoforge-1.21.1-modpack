package net.Pandarix.block.custom;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import net.Pandarix.block.entity.FleeFromBlockEntity;
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

public class OcelotFossilBlock extends FossilBaseWithEntityBlock {
   private static final Map<Direction, VoxelShape> OCELOT_SHAPES_FOR_DIRECTION = ImmutableMap.of(
      Direction.NORTH,
      Shapes.or(Block.box(5.5, 0.0, 0.0, 11.5, 9.5, 17.75), Block.box(6.0, 5.0, -7.0, 11.0, 10.0, 1.0)),
      Direction.SOUTH,
      Shapes.or(Block.box(5.5, 0.0, -1.75, 11.5, 9.5, 16.0), Block.box(6.0, 5.0, 15.0, 11.0, 10.0, 23.0)),
      Direction.EAST,
      Shapes.or(Block.box(-1.25, 0.0, 5.0, 16.5, 9.5, 11.0), Block.box(15.5, 5.0, 5.5, 23.5, 10.0, 10.5)),
      Direction.WEST,
      Shapes.or(Block.box(0.5, 0.0, 5.0, 18.25, 9.5, 11.0), Block.box(-6.5, 5.0, 5.5, 1.5, 10.0, 10.5))
   );

   public OcelotFossilBlock(Properties settings) {
      super(settings);
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new FleeFromBlockEntity(pos, state);
   }

   @NotNull
   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return OCELOT_SHAPES_FOR_DIRECTION.get(pState.getValue(FACING));
   }

   @NotNull
   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> component, TooltipFlag flag) {
      component.add(Component.translatable("block.betterarcheology.ocelot_fossil_tooltip").withStyle(ChatFormatting.GRAY));
      super.appendHoverText(stack, context, component, flag);
   }
}
