package io.github.razordevs.deep_aether.block.natural;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.block.natural.AetherDoubleDropBlock;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

public class AetherCoarseDirtBlock extends AetherDoubleDropBlock {
   public AetherCoarseDirtBlock(Properties properties) {
      super(properties);
   }

   @Nullable
   public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
      return context.getItemInHand().getItem() instanceof HoeItem
         ? ((Block)AetherBlocks.AETHER_DIRT.get()).defaultBlockState()
         : super.getToolModifiedState(state, context, itemAbility, simulate);
   }
}
