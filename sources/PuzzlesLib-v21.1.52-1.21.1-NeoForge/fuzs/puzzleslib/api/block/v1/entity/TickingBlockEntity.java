package fuzs.puzzleslib.api.block.v1.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface TickingBlockEntity {
   default void clientTick(Level level, BlockPos blockPos, BlockState blockState) {
      this.clientTick();
   }

   @Deprecated(
      forRemoval = true
   )
   default void clientTick() {
   }

   default void serverTick(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
      this.serverTick();
   }

   @Deprecated(
      forRemoval = true
   )
   default void serverTick() {
   }
}
