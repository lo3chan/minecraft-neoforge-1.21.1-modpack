package corgitaco.corgilib.mixin.chunk;

import corgitaco.corgilib.world.level.RandomTickScheduler;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({ChunkAccess.class})
public class MixinChunkAccess implements RandomTickScheduler {
   @Unique
   private final List<BlockPos> scheduledRandomTick = new ArrayList<>();

   @Override
   public void scheduleRandomTick(BlockPos pos) {
      this.scheduledRandomTick.add(pos.immutable());
   }

   @Override
   public List<BlockPos> getScheduledRandomTicks() {
      return this.scheduledRandomTick;
   }
}
