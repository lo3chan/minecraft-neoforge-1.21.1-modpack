package dev.corgitaco.dataanchor.util;

import java.util.Collections;
import java.util.List;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface TickableBlockEntityAccessor {
   default List<BlockEntity> dataAnchor$getTickableBlockEntities() {
      return Collections.emptyList();
   }
}
