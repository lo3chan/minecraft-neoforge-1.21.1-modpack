package com.yungnickyoung.minecraft.betterdeserttemples.world.state;

import com.yungnickyoung.minecraft.betterdeserttemples.BetterDesertTemplesCommon;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class TempleStateCache {
   public ConcurrentHashMap<String, TempleStateRegion> templeStateRegionMap = new ConcurrentHashMap<>();
   private final Path savePath;

   public TempleStateCache(Path dimensionPath) {
      this.savePath = dimensionPath.resolve("betterdeserttemples");
      this.createDirectoryIfDoesNotExist();
   }

   public boolean isTempleCleared(BlockPos templePos) {
      String templeRegionKey = this.getRegionKey(templePos);
      TempleStateRegion templeStateRegion = this.templeStateRegionMap.computeIfAbsent(templeRegionKey, key -> new TempleStateRegion(this.savePath, key));
      return templeStateRegion.isTempleCleared(templePos);
   }

   public void setTempleCleared(BlockPos templePos, boolean isCleared) {
      String templeRegionKey = this.getRegionKey(templePos);
      TempleStateRegion templeStateRegion = this.templeStateRegionMap.computeIfAbsent(templeRegionKey, key -> new TempleStateRegion(this.savePath, key));
      templeStateRegion.setTempleCleared(templePos, isCleared);
   }

   private String getRegionKey(BlockPos templePos) {
      ChunkPos chunkPos = new ChunkPos(templePos);
      return "r." + chunkPos.getRegionX() + "." + chunkPos.getRegionZ() + ".temples";
   }

   private void createDirectoryIfDoesNotExist() {
      try {
         Files.createDirectories(this.savePath);
      } catch (IOException var2) {
         BetterDesertTemplesCommon.LOGGER.error("Unable to create temples save path {}", this.savePath);
         BetterDesertTemplesCommon.LOGGER.error(var2);
      }
   }
}
