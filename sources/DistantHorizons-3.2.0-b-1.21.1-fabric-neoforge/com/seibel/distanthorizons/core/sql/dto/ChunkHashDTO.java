package com.seibel.distanthorizons.core.sql.dto;

import com.seibel.distanthorizons.core.pos.DhChunkPos;

public class ChunkHashDTO implements IBaseDTO<DhChunkPos> {
   public DhChunkPos pos;
   public int chunkHash;

   public ChunkHashDTO(DhChunkPos pos, int chunkHash) {
      this.pos = pos;
      this.chunkHash = chunkHash;
   }

   public DhChunkPos getKey() {
      return this.pos;
   }

   @Override
   public void close() {
   }
}
