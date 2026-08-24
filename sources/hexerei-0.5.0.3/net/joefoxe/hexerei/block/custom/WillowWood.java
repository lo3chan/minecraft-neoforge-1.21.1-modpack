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

public class WillowWood extends RotatedPillarBlock {
   public WillowWood(Properties properties) {
      super(properties);
   }

   @Nullable
   public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
      boolean rightClickedWithAxe = ItemAbilities.AXE_STRIP.equals(itemAbility);
      BlockState toReturn = ((WillowWood)ModBlocks.WILLOW_WOOD.get()).defaultBlockState();
      if (rightClickedWithAxe) {
         toReturn = (BlockState)((RotatedPillarBlock)ModBlocks.STRIPPED_WILLOW_WOOD.get()).defaultBlockState().setValue(AXIS, (Axis)state.getValue(AXIS));
      }

      return toReturn;
   }
}
