package net.joefoxe.hexerei.block.connected;

import javax.annotation.Nullable;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.common.ItemAbility;

public class WaxedLayeredBlock extends LayeredBlock implements Waxed {
   public WaxedLayeredBlock(Properties p_55926_) {
      super(p_55926_);
   }

   @Nullable
   public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
      return this.getUnWaxed(state, context, itemAbility);
   }
}
