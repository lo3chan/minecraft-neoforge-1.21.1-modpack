package net.mehvahdjukaar.moonlight.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IAnalogRotatable {
   void rotateAnalog(BlockState var1, Level var2, BlockPos var3, Direction var4, boolean var5, float var6);

   boolean canRotateAnalog(BlockState var1, Level var2, BlockPos var3, Direction var4);
}
