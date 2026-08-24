package io.github.razordevs.deep_aether.block.natural;

import com.aetherteam.aether.block.AetherBlockStateProperties;
import com.aetherteam.aether.block.natural.AetherLogBlock;
import io.github.razordevs.deep_aether.init.DABlocks;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

public class DALogBlock extends AetherLogBlock {
   public DALogBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, false));
   }

   @Nullable
   public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility toolAction, boolean simulate) {
      if (context.getItemInHand().getItem() instanceof AxeItem) {
         if (state.is((Block)DABlocks.ROSEROOT_LOG.get())) {
            return (BlockState)((Block)DABlocks.STRIPPED_ROSEROOT_LOG.get()).defaultBlockState().setValue(AXIS, (Axis)state.getValue(AXIS));
         }

         if (state.is((Block)DABlocks.ROSEROOT_WOOD.get())) {
            return (BlockState)((Block)DABlocks.STRIPPED_ROSEROOT_LOG.get()).defaultBlockState().setValue(AXIS, (Axis)state.getValue(AXIS));
         }

         if (state.is((Block)DABlocks.YAGROOT_LOG.get())) {
            return (BlockState)((Block)DABlocks.STRIPPED_YAGROOT_LOG.get()).defaultBlockState().setValue(AXIS, (Axis)state.getValue(AXIS));
         }

         if (state.is((Block)DABlocks.YAGROOT_WOOD.get())) {
            return (BlockState)((Block)DABlocks.STRIPPED_YAGROOT_WOOD.get()).defaultBlockState().setValue(AXIS, (Axis)state.getValue(AXIS));
         }

         if (state.is((Block)DABlocks.CRUDEROOT_LOG.get())) {
            return (BlockState)((Block)DABlocks.STRIPPED_CRUDEROOT_LOG.get()).defaultBlockState().setValue(AXIS, (Axis)state.getValue(AXIS));
         }

         if (state.is((Block)DABlocks.CRUDEROOT_WOOD.get())) {
            return (BlockState)((Block)DABlocks.CRUDEROOT_WOOD.get()).defaultBlockState().setValue(AXIS, (Axis)state.getValue(AXIS));
         }

         if (state.is((Block)DABlocks.CONBERRY_LOG.get())) {
            return (BlockState)((Block)DABlocks.STRIPPED_CONBERRY_LOG.get()).defaultBlockState().setValue(AXIS, (Axis)state.getValue(AXIS));
         }

         if (state.is((Block)DABlocks.CONBERRY_WOOD.get())) {
            return (BlockState)((Block)DABlocks.STRIPPED_CONBERRY_WOOD.get()).defaultBlockState().setValue(AXIS, (Axis)state.getValue(AXIS));
         }

         if (state.is((Block)DABlocks.SUNROOT_LOG.get())) {
            return (BlockState)((Block)DABlocks.STRIPPED_SUNROOT_LOG.get()).defaultBlockState().setValue(AXIS, (Axis)state.getValue(AXIS));
         }

         if (state.is((Block)DABlocks.SUNROOT_WOOD.get())) {
            return (BlockState)((Block)DABlocks.STRIPPED_SUNROOT_WOOD.get()).defaultBlockState().setValue(AXIS, (Axis)state.getValue(AXIS));
         }
      }

      return super.getToolModifiedState(state, context, toolAction, simulate);
   }
}
