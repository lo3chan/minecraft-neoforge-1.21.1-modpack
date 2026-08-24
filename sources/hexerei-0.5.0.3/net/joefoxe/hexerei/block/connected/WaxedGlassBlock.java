package net.joefoxe.hexerei.block.connected;

import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.common.ItemAbility;

public class WaxedGlassBlock extends TransparentBlock implements Waxed {
   public WaxedGlassBlock(Properties properties) {
      super(properties);
   }

   public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
      return super.skipRendering(state, adjacentBlockState, side);
   }

   @Nullable
   public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
      return this.getUnWaxed(state, context, itemAbility);
   }
}
