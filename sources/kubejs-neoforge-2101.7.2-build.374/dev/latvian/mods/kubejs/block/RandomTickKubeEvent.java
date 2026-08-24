package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.level.KubeLevelEvent;
import dev.latvian.mods.kubejs.level.LevelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class RandomTickKubeEvent implements KubeLevelEvent {
   private final ServerLevel level;
   private final BlockPos pos;
   private final BlockState state;
   public final RandomSource random;
   private LevelBlock block;

   public RandomTickKubeEvent(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
      this.level = level;
      this.pos = pos;
      this.state = state;
      this.random = random;
   }

   public ServerLevel getLevel() {
      return this.level;
   }

   public LevelBlock getBlock() {
      if (this.block == null) {
         this.block = this.level.kjs$getBlock(this.pos).cache(this.state);
      }

      return this.block;
   }
}
