package net.Pandarix.block.custom;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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

public class ChickenFossilHeadBlock extends FossilBaseHeadBlock {
   private static final VoxelShape CHICKEN_HEAD_SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 4.0, 12.0);

   public ChickenFossilHeadBlock(Properties settings) {
      super(settings);
   }

   @NotNull
   public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
      return CHICKEN_HEAD_SHAPE;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
      components.add(
         Component.translatable("block.betterarcheology.chicken_fossil_head_tooltip")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.translatable("block.betterarcheology.fossil_head_set").withStyle(ChatFormatting.BLUE))
      );
      super.appendHoverText(stack, context, components, tooltipFlag);
   }
}
