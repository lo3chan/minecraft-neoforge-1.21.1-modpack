package com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject;

import com.google.common.collect.ImmutableList;
import com.seibel.distanthorizons.common.wrappers.McObjectConverter_neoforge;
import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.BatchGenerationEnvironment$IEmptyChunkRetrievalFunc_neoforge;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.util.LodUtil;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.SectionPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkDependencies;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.BlackholeTickAccess;
import net.minecraft.world.ticks.LevelTickAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DhLitWorldGenRegion_neoforge extends WorldGenRegion {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static ChunkStatus debugTriggeredForStatus = null;
   public final ServerLevel serverLevel;
   public final DummyLightEngine_neoforge lightEngine;
   public final BatchGenerationEnvironment$IEmptyChunkRetrievalFunc_neoforge generator;
   public final int writeRadius;
   public final int size;
   private final DhChunkPos firstPos;
   private final List<ChunkAccess> chunkCacheList;
   private final Long2ObjectOpenHashMap<ChunkAccess> chunkMap = new Long2ObjectOpenHashMap();
   private final ReentrantLock getChunkLock = new ReentrantLock();

   public DhLitWorldGenRegion_neoforge(
      int centerChunkX,
      int centerChunkZ,
      ChunkAccess centerChunk,
      ServerLevel serverLevel,
      DummyLightEngine_neoforge lightEngine,
      List<ChunkAccess> chunkList,
      ChunkStatus chunkStatus,
      int writeRadius,
      BatchGenerationEnvironment$IEmptyChunkRetrievalFunc_neoforge generator
   ) {
      super(
         serverLevel,
         StaticCache2D.create(centerChunkX, centerChunkZ, writeRadius * 2, (x, z) -> new DhGenerationChunkHolder_neoforge(new ChunkPos(x, z))),
         new ChunkStep(
            chunkStatus,
            new ChunkDependencies(ImmutableList.copyOf(ChunkStatus.getStatusList()).reverse()),
            new ChunkDependencies(ImmutableList.copyOf(ChunkStatus.getStatusList()).reverse()),
            writeRadius * 2,
            (var1, var2, var3, var4) -> null
         ),
         centerChunk
      );
      this.firstPos = McObjectConverter_neoforge.convert(chunkList.get(0).getPos());
      this.serverLevel = serverLevel;
      this.generator = generator;
      this.lightEngine = lightEngine;
      this.writeRadius = writeRadius;
      this.chunkCacheList = chunkList;
      this.size = Mth.floor(Math.sqrt(chunkList.size()));
   }

   public boolean ensureCanWrite(BlockPos blockPos) {
      DhChunkPos chunkPos = McObjectConverter_neoforge.convert(this.getCenter());
      int sectionCoordX = SectionPos.blockToSectionCoord(blockPos.getX());
      int sectionCoordZ = SectionPos.blockToSectionCoord(blockPos.getZ());
      int absX = Math.abs(chunkPos.getX() - sectionCoordX);
      int absZ = Math.abs(chunkPos.getZ() - sectionCoordZ);
      if (absX <= this.writeRadius && absZ <= this.writeRadius) {
         ChunkAccess center = this.getChunk(chunkPos.getX(), chunkPos.getZ());
         if (center.isUpgrading()) {
            LevelHeightAccessor levelHeightAccessor = center.getHeightAccessorForGeneration();
            int minY = levelHeightAccessor.getMinBuildHeight();
            int maxY = levelHeightAccessor.getMaxBuildHeight();
            if (blockPos.getY() < minY || blockPos.getY() >= maxY) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @NotNull
   public LevelTickAccess<Block> getBlockTicks() {
      return BlackholeTickAccess.emptyLevelList();
   }

   @NotNull
   public LevelTickAccess<Fluid> getFluidTicks() {
      return BlackholeTickAccess.emptyLevelList();
   }

   public boolean setBlock(BlockPos blockPos, BlockState blockState, int i, int j) {
      ChunkAccess chunkAccess = this.getChunk(blockPos);
      if (chunkAccess instanceof LevelChunk) {
         return true;
      } else {
         chunkAccess.setBlockState(blockPos, blockState, false);
         return true;
      }
   }

   public boolean destroyBlock(BlockPos blockPos, boolean bl, @Nullable Entity entity, int i) {
      BlockState blockState = this.getBlockState(blockPos);
      return blockState.isAir() ? false : this.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3, i);
   }

   public BlockEntity getBlockEntity(BlockPos blockPos) {
      BlockState blockState = this.getBlockState(blockPos);
      return blockState.getBlock() instanceof SpawnerBlock ? ((EntityBlock)blockState.getBlock()).newBlockEntity(blockPos, blockState) : null;
   }

   @NotNull
   public BlockState getBlockState(BlockPos blockPos) {
      int chunkX = SectionPos.blockToSectionCoord(blockPos.getX());
      int chunkZ = SectionPos.blockToSectionCoord(blockPos.getZ());
      if (blockPos.getY() >= -1000000 && blockPos.getY() <= 1000000) {
         return this.getChunk(chunkX, chunkZ).getBlockState(blockPos);
      } else {
         throw new ArrayIndexOutOfBoundsException(
            "Attempted to getBlockState outside the DH defined Y bounds [-1_000_000, 1_000_000]: ["
               + blockPos
               + "]. This is likely a mod compatibility issue, there is no reason to try getting a block this far outside the world during world gen."
         );
      }
   }

   public boolean addFreshEntity(@NotNull Entity entity) {
      return true;
   }

   @NotNull
   public ChunkAccess getChunk(int chunkX, int chunkZ) {
      ChunkAccess var3;
      try {
         this.getChunkLock.lock();
         var3 = this.getChunk(chunkX, chunkZ, ChunkStatus.EMPTY);
      } finally {
         this.getChunkLock.unlock();
      }

      return var3;
   }

   @NotNull
   public ChunkAccess getChunk(int chunkX, int chunkZ, @NotNull ChunkStatus chunkStatus) {
      ChunkAccess var5;
      try {
         this.getChunkLock.lock();
         ChunkAccess chunk = this.getChunk(chunkX, chunkZ, chunkStatus, true);
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
   public ChunkAccess getChunk(int chunkX, int chunkZ, @NotNull ChunkStatus chunkStatus, boolean returnNonNull) {
      ChunkAccess chunk = this.getChunkAccess(chunkX, chunkZ, chunkStatus, returnNonNull);
      if (chunk instanceof LevelChunk) {
         chunk = new ImposterProtoChunk((LevelChunk)chunk, false);
      }

      return chunk;
   }

   private ChunkAccess getChunkAccess(int chunkX, int chunkZ, ChunkStatus chunkStatus, boolean returnNonNull) {
      ChunkAccess chunk = null;
      if (this.dhHasChunk(chunkX, chunkZ)) {
         chunk = this.dhGetChunk(chunkX, chunkZ);
      }

      if (chunk != null && ChunkWrapper_neoforge.getStatus(chunk).isOrAfter(chunkStatus)) {
         return chunk;
      } else if (!returnNonNull) {
         return null;
      } else {
         if (chunk == null) {
            long chunkPosAsLong = ChunkPos.asLong(chunkX, chunkZ);
            chunk = (ChunkAccess)this.chunkMap.get(chunkPosAsLong);
            if (chunk == null) {
               chunk = this.generator.getChunk(chunkX, chunkZ);
               if (chunk == null) {
                  throw new NullPointerException("The provided generator should not return null!");
               }

               this.chunkMap.put(chunkPosAsLong, chunk);
            }
         }

         if (chunkStatus != ChunkStatus.EMPTY && chunkStatus != debugTriggeredForStatus) {
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

   private ChunkAccess dhGetChunk(int x, int z) {
      int xOffset = x - this.firstPos.getX();
      int zOffset = z - this.firstPos.getZ();
      return this.chunkCacheList.get(xOffset + zOffset * this.size);
   }

   @NotNull
   public LevelLightEngine getLightEngine() {
      return this.lightEngine;
   }

   public int getBrightness(@NotNull LightLayer lightLayer, @NotNull BlockPos blockPos) {
      return 0;
   }

   public int getRawBrightness(@NotNull BlockPos blockPos, int i) {
      return 0;
   }

   public boolean canSeeSky(@NotNull BlockPos blockPos) {
      return this.getBrightness(LightLayer.SKY, blockPos) >= 15;
   }

   public int getBlockTint(@NotNull BlockPos blockPos, @NotNull ColorResolver colorResolver) {
      return this.calculateBlockTint(blockPos, colorResolver);
   }

   private Biome _getBiome(BlockPos pos) {
      return (Biome)this.getBiome(pos).value();
   }

   public int calculateBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
      int i = (Integer)Minecraft.getInstance().options.biomeBlendRadius().get();
      if (i == 0) {
         return colorResolver.getColor(this._getBiome(blockPos), blockPos.getX(), blockPos.getZ());
      } else {
         int j = (i * 2 + 1) * (i * 2 + 1);
         int k = 0;
         int l = 0;
         int m = 0;
         Cursor3D cursor3D = new Cursor3D(blockPos.getX() - i, blockPos.getY(), blockPos.getZ() - i, blockPos.getX() + i, blockPos.getY(), blockPos.getZ() + i);
         MutableBlockPos mutableBlockPos = new MutableBlockPos();

         while (cursor3D.advance()) {
            mutableBlockPos.set(cursor3D.nextX(), cursor3D.nextY(), cursor3D.nextZ());
            int n = colorResolver.getColor(this._getBiome(mutableBlockPos), mutableBlockPos.getX(), mutableBlockPos.getZ());
            k += (n & 0xFF0000) >> 16;
            l += (n & 0xFF00) >> 8;
            m += n & 0xFF;
         }

         return (k / j & 0xFF) << 16 | (l / j & 0xFF) << 8 | m / j & 0xFF;
      }
   }
}
