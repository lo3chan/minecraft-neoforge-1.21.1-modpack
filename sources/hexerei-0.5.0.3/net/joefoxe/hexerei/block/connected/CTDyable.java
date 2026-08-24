package net.joefoxe.hexerei.block.connected;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;

public interface CTDyable {
   default DyeColor getDyeColor(BlockState blockState) {
      return DyeColor.WHITE;
   }
}
