package com.yungnickyoung.minecraft.betterdeserttemples.world.state;

import com.yungnickyoung.minecraft.betterdeserttemples.BetterDesertTemplesCommon;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

public class TempleStateRegion {
   private final String regionKey;
   private final File regionFile;
   private final ConcurrentHashMap<Long, Boolean> templeStateMap = new ConcurrentHashMap<>();

   public TempleStateRegion(Path basePath, String regionKey) {
      this.regionKey = regionKey;
      this.regionFile = basePath.resolve(regionKey).toFile();
      this.createRegionFileIfDoesNotExist();
   }

   public synchronized void setTempleCleared(BlockPos templePos, boolean isCleared) {
      this.templeStateMap.put(templePos.asLong(), isCleared);
      this.createRegionFileIfDoesNotExist();
      CompoundTag compoundTag = this.readRegionFile();
      compoundTag.putBoolean(templePos.toString(), isCleared);
      this.writeRegionFile(compoundTag);
   }

   public synchronized boolean isTempleCleared(BlockPos templePos) {
      long templePosAsLong = templePos.asLong();
      if (this.templeStateMap.containsKey(templePosAsLong)) {
         return this.templeStateMap.get(templePosAsLong);
      } else {
         this.createRegionFileIfDoesNotExist();
         boolean isCleared = false;
         CompoundTag compoundTag = this.readRegionFile();
         if (compoundTag == null) {
            compoundTag = new CompoundTag();
            compoundTag.putBoolean(templePos.toString(), isCleared);
            this.writeRegionFile(compoundTag);
         } else if (compoundTag.contains(templePos.toString())) {
            isCleared = compoundTag.getBoolean(templePos.toString());
         } else {
            compoundTag.putBoolean(templePos.toString(), isCleared);
            this.writeRegionFile(compoundTag);
         }

         this.templeStateMap.put(templePosAsLong, isCleared);
         return isCleared;
      }
   }

   public synchronized void reset() {
      this.templeStateMap.clear();
   }

   private void writeRegionFile(CompoundTag compoundTag) {
      try {
         NbtIo.write(compoundTag, this.regionFile.toPath());
      } catch (IOException var3) {
         BetterDesertTemplesCommon.LOGGER.error("Encountered error writing data to temple region file {}", this.regionKey);
         BetterDesertTemplesCommon.LOGGER.error(var3);
      }
   }

   private CompoundTag readRegionFile() {
      try {
         return NbtIo.read(this.regionFile.toPath());
      } catch (IOException var2) {
         BetterDesertTemplesCommon.LOGGER.error("Encountered error reading data from temple region file {}", this.regionKey);
         BetterDesertTemplesCommon.LOGGER.error(var2);
         return new CompoundTag();
      }
   }

   private synchronized void createRegionFileIfDoesNotExist() {
      if (!this.regionFile.exists()) {
         try {
            this.regionFile.createNewFile();
            NbtIo.write(new CompoundTag(), this.regionFile.toPath());
         } catch (IOException var2) {
            BetterDesertTemplesCommon.LOGGER.error("Unable to create temple region file for region {}", this.regionKey);
            BetterDesertTemplesCommon.LOGGER.error(var2);
         }
      }
   }
}
