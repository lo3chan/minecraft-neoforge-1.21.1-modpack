package net.joefoxe.hexerei.block.custom;

import javax.annotation.Nullable;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class WitchHazelLog extends RotatedPillarBlock {
   public WitchHazelLog(Properties properties) {
      super(properties);
   }

   @Nullable
   public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
      boolean rightClickedWithAxe = ItemAbilities.AXE_STRIP.equals(itemAbility);
      BlockState toReturn = ((WitchHazelLog)ModBlocks.WITCH_HAZEL_LOG.get()).defaultBlockState();
      if (rightClickedWithAxe) {
         toReturn = (BlockState)((RotatedPillarBlock)ModBlocks.STRIPPED_WITCH_HAZEL_LOG.get()).defaultBlockState().setValue(AXIS, (Axis)state.getValue(AXIS));
      }

      return toReturn;
   }
}
