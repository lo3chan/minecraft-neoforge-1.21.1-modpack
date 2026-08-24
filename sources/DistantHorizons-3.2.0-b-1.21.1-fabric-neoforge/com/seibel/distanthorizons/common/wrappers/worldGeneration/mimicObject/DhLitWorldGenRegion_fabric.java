package com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject;

import com.google.common.collect.ImmutableList;
import com.seibel.distanthorizons.common.wrappers.McObjectConverter_fabric;
import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.BatchGenerationEnvironment$IEmptyChunkRetrievalFunc_fabric;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.util.LodUtil;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.class_1297;
import net.minecraft.class_1923;
import net.minecraft.class_1944;
import net.minecraft.class_1959;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2343;
import net.minecraft.class_2496;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_2791;
import net.minecraft.class_2806;
import net.minecraft.class_2818;
import net.minecraft.class_2821;
import net.minecraft.class_310;
import net.minecraft.class_3218;
import net.minecraft.class_3233;
import net.minecraft.class_3532;
import net.minecraft.class_3568;
import net.minecraft.class_3611;
import net.minecraft.class_3980;
import net.minecraft.class_4076;
import net.minecraft.class_5539;
import net.minecraft.class_6539;
import net.minecraft.class_6754;
import net.minecraft.class_6756;
import net.minecraft.class_9762;
import net.minecraft.class_9767;
import net.minecraft.class_9770;
import net.minecraft.class_2338.class_2339;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DhLitWorldGenRegion_fabric extends class_3233 {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static class_2806 debugTriggeredForStatus = null;
   public final class_3218 serverLevel;
   public final DummyLightEngine_fabric lightEngine;
   public final BatchGenerationEnvironment$IEmptyChunkRetrievalFunc_fabric generator;
   public final int writeRadius;
   public final int size;
   private final DhChunkPos firstPos;
   private final List<class_2791> chunkCacheList;
   private final Long2ObjectOpenHashMap<class_2791> chunkMap = new Long2ObjectOpenHashMap();
   private final ReentrantLock getChunkLock = new ReentrantLock();

   public DhLitWorldGenRegion_fabric(
      int centerChunkX,
      int centerChunkZ,
      class_2791 centerChunk,
      class_3218 serverLevel,
      DummyLightEngine_fabric lightEngine,
      List<class_2791> chunkList,
      class_2806 chunkStatus,
      int writeRadius,
      BatchGenerationEnvironment$IEmptyChunkRetrievalFunc_fabric generator
   ) {
      super(
         serverLevel,
         class_9762.method_60483(centerChunkX, centerChunkZ, writeRadius * 2, (x, z) -> new DhGenerationChunkHolder_fabric(new class_1923(x, z))),
         new class_9770(
            chunkStatus,
            new class_9767(ImmutableList.copyOf(class_2806.method_16558()).reverse()),
            new class_9767(ImmutableList.copyOf(class_2806.method_16558()).reverse()),
            writeRadius * 2,
            (var1, var2, var3, var4) -> null
         ),
         centerChunk
      );
      this.firstPos = McObjectConverter_fabric.convert(chunkList.get(0).method_12004());
      this.serverLevel = serverLevel;
      this.generator = generator;
      this.lightEngine = lightEngine;
      this.writeRadius = writeRadius;
      this.chunkCacheList = chunkList;
      this.size = class_3532.method_15357(Math.sqrt(chunkList.size()));
   }

   public boolean method_37368(class_2338 blockPos) {
      DhChunkPos chunkPos = McObjectConverter_fabric.convert(this.method_33561());
      int sectionCoordX = class_4076.method_18675(blockPos.method_10263());
      int sectionCoordZ = class_4076.method_18675(blockPos.method_10260());
      int absX = Math.abs(chunkPos.getX() - sectionCoordX);
      int absZ = Math.abs(chunkPos.getZ() - sectionCoordZ);
      if (absX <= this.writeRadius && absZ <= this.writeRadius) {
         class_2791 center = this.method_8392(chunkPos.getX(), chunkPos.getZ());
         if (center.method_39461()) {
            class_5539 levelHeightAccessor = center.method_39460();
            int minY = levelHeightAccessor.method_31607();
            int maxY = levelHeightAccessor.method_31600();
            if (blockPos.method_10264() < minY || blockPos.method_10264() >= maxY) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @NotNull
   public class_6756<class_2248> method_8397() {
      return class_6754.method_39362();
   }

   @NotNull
   public class_6756<class_3611> method_8405() {
      return class_6754.method_39362();
   }

   public boolean method_30092(class_2338 blockPos, class_2680 blockState, int i, int j) {
      class_2791 chunkAccess = this.method_22350(blockPos);
      if (chunkAccess instanceof class_2818) {
         return true;
      } else {
         chunkAccess.method_12010(blockPos, blockState, false);
         return true;
      }
   }

   public boolean method_30093(class_2338 blockPos, boolean bl, @Nullable class_1297 entity, int i) {
      class_2680 blockState = this.method_8320(blockPos);
      return blockState.method_26215() ? false : this.method_30092(blockPos, class_2246.field_10124.method_9564(), 3, i);
   }

   public class_2586 method_8321(class_2338 blockPos) {
      class_2680 blockState = this.method_8320(blockPos);
      return blockState.method_26204() instanceof class_2496 ? ((class_2343)blockState.method_26204()).method_10123(blockPos, blockState) : null;
   }

   @NotNull
   public class_2680 method_8320(class_2338 blockPos) {
      int chunkX = class_4076.method_18675(blockPos.method_10263());
      int chunkZ = class_4076.method_18675(blockPos.method_10260());
      if (blockPos.method_10264() >= -1000000 && blockPos.method_10264() <= 1000000) {
         return this.method_8392(chunkX, chunkZ).method_8320(blockPos);
      } else {
         throw new ArrayIndexOutOfBoundsException(
            "Attempted to getBlockState outside the DH defined Y bounds [-1_000_000, 1_000_000]: ["
               + blockPos
               + "]. This is likely a mod compatibility issue, there is no reason to try getting a block this far outside the world during world gen."
         );
      }
   }

   public boolean method_8649(@NotNull class_1297 entity) {
      return true;
   }

   @NotNull
   public class_2791 method_8392(int chunkX, int chunkZ) {
      class_2791 var3;
      try {
         this.getChunkLock.lock();
         var3 = this.method_22342(chunkX, chunkZ, class_2806.field_12798);
      } finally {
         this.getChunkLock.unlock();
      }

      return var3;
   }

   @NotNull
   public class_2791 method_22342(int chunkX, int chunkZ, @NotNull class_2806 chunkStatus) {
      class_2791 var5;
      try {
         this.getChunkLock.lock();
         class_2791 chunk = this.method_8402(chunkX, chunkZ, chunkStatus, true);
         if (chunk == null) {
            LodUtil.assertNotReach("getChunk shouldn't return null values");
         }

         var5 = chunk;
      } finally {
         this.getChunkLock.unlock();
      }

      return var5;
   }

   @Nullable
   public class_2791 method_8402(int chunkX, int chunkZ, @NotNull class_2806 chunkStatus, boolean returnNonNull) {
      class_2791 chunk = this.getChunkAccess(chunkX, chunkZ, chunkStatus, returnNonNull);
      if (chunk instanceof class_2818) {
         chunk = new class_2821((class_2818)chunk, false);
      }

      return chunk;
   }

   private class_2791 getChunkAccess(int chunkX, int chunkZ, class_2806 chunkStatus, boolean returnNonNull) {
      class_2791 chunk = null;
      if (this.dhHasChunk(chunkX, chunkZ)) {
         chunk = this.dhGetChunk(chunkX, chunkZ);
      }

      if (chunk != null && ChunkWrapper_fabric.getStatus(chunk).method_12165(chunkStatus)) {
         return chunk;
      } else if (!returnNonNull) {
         return null;
      } else {
         if (chunk == null) {
            long chunkPosAsLong = class_1923.method_8331(chunkX, chunkZ);
            chunk = (class_2791)this.chunkMap.get(chunkPosAsLong);
            if (chunk == null) {
               chunk = this.generator.getChunk(chunkX, chunkZ);
               if (chunk == null) {
                  throw new NullPointerException("The provided generator should not return null!");
               }

               this.chunkMap.put(chunkPosAsLong, chunk);
            }
         }

         if (chunkStatus != class_2806.field_12798 && chunkStatus != debugTriggeredForStatus) {
            debugTriggeredForStatus = chunkStatus;
         }

         return chunk;
      }
   }

   public boolean dhHasChunk(int x, int z) {
      int xOffset = x - this.firstPos.getX();
      int zOffset = z - this.firstPos.getZ();
      return zOffset >= 0 && zOffset < this.size && xOffset >= 0 && xOffset < this.size;
   }

   private class_2791 dhGetChunk(int x, int z) {
      int xOffset = x - this.firstPos.getX();
      int zOffset = z - this.firstPos.getZ();
      return this.chunkCacheList.get(xOffset + zOffset * this.size);
   }

   @NotNull
   public class_3568 method_22336() {
      return this.lightEngine;
   }

   public int method_8314(@NotNull class_1944 lightLayer, @NotNull class_2338 blockPos) {
      return 0;
   }

   public int method_22335(@NotNull class_2338 blockPos, int i) {
      return 0;
   }

   public boolean method_8311(@NotNull class_2338 blockPos) {
      return this.method_8314(class_1944.field_9284, blockPos) >= 15;
   }

   public int method_23752(@NotNull class_2338 blockPos, @NotNull class_6539 colorResolver) {
      return this.calculateBlockTint(blockPos, colorResolver);
   }

   private class_1959 _getBiome(class_2338 pos) {
      return (class_1959)this.method_23753(pos).comp_349();
   }

   public int calculateBlockTint(class_2338 blockPos, class_6539 colorResolver) {
      int i = (Integer)class_310.method_1551().field_1690.method_41805().method_41753();
      if (i == 0) {
         return colorResolver.getColor(this._getBiome(blockPos), blockPos.method_10263(), blockPos.method_10260());
      } else {
         int j = (i * 2 + 1) * (i * 2 + 1);
         int k = 0;
         int l = 0;
         int m = 0;
         class_3980 cursor3D = new class_3980(
            blockPos.method_10263() - i,
            blockPos.method_10264(),
            blockPos.method_10260() - i,
            blockPos.method_10263() + i,
            blockPos.method_10264(),
            blockPos.method_10260() + i
         );
         class_2339 mutableBlockPos = new class_2339();

         while (cursor3D.method_17963()) {
            mutableBlockPos.method_10103(cursor3D.method_18671(), cursor3D.method_18672(), cursor3D.method_18673());
            int n = colorResolver.getColor(this._getBiome(mutableBlockPos), mutableBlockPos.method_10263(), mutableBlockPos.method_10260());
            k += (n & 0xFF0000) >> 16;
            l += (n & 0xFF00) >> 8;
            m += n & 0xFF;
         }

         return (k / j & 0xFF) << 16 | (l / j & 0xFF) << 8 | m / j & 0xFF;
      }
   }
}
