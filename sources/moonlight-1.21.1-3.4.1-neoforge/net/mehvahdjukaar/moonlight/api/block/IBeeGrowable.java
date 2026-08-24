package net.mehvahdjukaar.moonlight.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

public interface IBeeGrowable {
   boolean getPollinated(Level var1, BlockPos var2, BlockState var3);

   default boolean isPlantFullyGrown(BlockState state, BlockPos pos, Level level) {
      return state.getBlock() instanceof CropBlock cb ? cb.isMaxAge(state) : false;
   }
}
