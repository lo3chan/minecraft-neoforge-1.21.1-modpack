package net.mehvahdjukaar.moonlight.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface IFlammable {
   int getFlammability(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4);

   int getFireSpreadSpeed(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4);
}
