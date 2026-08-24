package com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject;

import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.world.level.ChunkPos;

public class DhGenerationChunkHolder_neoforge extends GenerationChunkHolder {
   public DhGenerationChunkHolder_neoforge(ChunkPos pos) {
      super(pos);
   }

   public int getTicketLevel() {
      return 0;
   }

   public int getQueueLevel() {
      return 0;
   }
}
