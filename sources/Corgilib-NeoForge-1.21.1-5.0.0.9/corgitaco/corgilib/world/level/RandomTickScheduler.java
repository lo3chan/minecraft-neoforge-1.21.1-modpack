package corgitaco.corgilib.world.level;

import java.util.List;
import net.minecraft.core.BlockPos;

public interface RandomTickScheduler {
   void scheduleRandomTick(BlockPos var1);

   List<BlockPos> getScheduledRandomTicks();
}
