package com.github.alexthe666.alexsmobs.citadel.server.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public class WorldChunkUtil {
   public static boolean isBlockLoaded(LevelAccessor world, BlockPos pos) {
      return isChunkLoaded(world, pos.getX() >> 4, pos.getZ() >> 4);
   }

   public static boolean isChunkLoaded(LevelAccessor world, int x, int z) {
      if (world.getChunkSource() instanceof ServerChunkCache) {
         ChunkHolder holder = ((ServerChunkCache)world.getChunkSource()).chunkMap.getVisibleChunkIfPresent(ChunkPos.asLong(x, z));
         return holder != null ? holder.getFullChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).isSuccess() : false;
      } else {
         return world.getChunk(x, z, ChunkStatus.FULL, false) != null;
      }
   }

   public static boolean isChunkLoaded(LevelAccessor world, ChunkPos pos) {
      return isChunkLoaded(world, pos.x, pos.z);
   }

   public static boolean isEntityBlockLoaded(LevelAccessor world, BlockPos pos) {
      return isEntityChunkLoaded(world, pos.getX() >> 4, pos.getZ() >> 4);
   }

   public static boolean isEntityChunkLoaded(LevelAccessor world, int x, int z) {
      return isEntityChunkLoaded(world, new ChunkPos(x, z));
   }

   public static boolean isEntityChunkLoaded(LevelAccessor world, ChunkPos pos) {
      return !(world instanceof ServerLevel)
         ? isChunkLoaded(world, pos)
         : isChunkLoaded(world, pos) && ((ServerLevel)world).isPositionEntityTicking(pos.getWorldPosition());
   }
}
