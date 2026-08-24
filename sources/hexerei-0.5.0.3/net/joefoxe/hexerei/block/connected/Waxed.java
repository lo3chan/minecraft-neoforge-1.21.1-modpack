package net.joefoxe.hexerei.block.connected;

import net.joefoxe.hexerei.item.custom.CleaningClothItem;
import net.joefoxe.hexerei.item.custom.WaxBlendItem;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;

public interface Waxed {
   default BlockState getUnWaxed(BlockState state, UseOnContext context, ItemAbility itemAbility) {
      boolean cloth = CleaningClothItem.CLOTH_WAX_OFF.equals(itemAbility);
      WaxBlendItem.WAX_OFF_BY_BLOCK.get().get(state.getBlock());
      BlockState toReturn = state;
      if (cloth) {
         toReturn = ((Block)WaxBlendItem.WAX_OFF_BY_BLOCK.get().get(state.getBlock())).defaultBlockState();
         if (state.hasProperty(RotatedPillarBlock.AXIS) && toReturn.hasProperty(RotatedPillarBlock.AXIS)) {
            toReturn = (BlockState)toReturn.setValue(RotatedPillarBlock.AXIS, (Axis)state.getValue(RotatedPillarBlock.AXIS));
         }

         context.getLevel().scheduleTick(context.getClickedPos(), (Block)this, 1);
      }

      return toReturn;
   }
}
