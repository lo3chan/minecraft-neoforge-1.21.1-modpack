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

public class CreeperFossilHeadBlock extends FossilBaseHeadBlock {
   private static final VoxelShape SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);

   public CreeperFossilHeadBlock(Properties settings) {
      super(settings);
   }

   @NotNull
   public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
      return SHAPE;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
      components.add(
         Component.translatable("block.betterarcheology.creeper_fossil_head_tooltip")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.translatable("block.betterarcheology.fossil_head_set").withStyle(ChatFormatting.BLUE))
      );
      super.appendHoverText(stack, context, components, tooltipFlag);
   }
}
