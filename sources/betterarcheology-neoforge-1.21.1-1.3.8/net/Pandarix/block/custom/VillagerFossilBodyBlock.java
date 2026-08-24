package net.Pandarix.block.custom;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class VillagerFossilBodyBlock extends FossilBaseBodyBlock {
   private static final Map<Direction, VoxelShape> SHAPES_FOR_DIRECTION = ImmutableMap.of(
      Direction.NORTH,
      Block.box(0.0, 0.0, 8.0, 16.0, 12.0, 15.0),
      Direction.SOUTH,
      Block.box(0.0, 0.0, 1.0, 16.0, 12.0, 8.0),
      Direction.EAST,
      Block.box(1.0, 0.0, 0.0, 8.0, 12.0, 16.0),
      Direction.WEST,
      Block.box(8.0, 0.0, 0.0, 15.0, 12.0, 16.0)
   );

   public VillagerFossilBodyBlock(Properties settings) {
      super(settings);
   }

   @NotNull
   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPES_FOR_DIRECTION.get(pState.getValue(FACING));
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
      components.add(
         Component.translatable("block.betterarcheology.villager_fossil_body_tooltip")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.translatable("block.betterarcheology.fossil_body_set").withStyle(ChatFormatting.BLUE))
      );
      super.appendHoverText(stack, context, components, tooltipFlag);
   }
}
