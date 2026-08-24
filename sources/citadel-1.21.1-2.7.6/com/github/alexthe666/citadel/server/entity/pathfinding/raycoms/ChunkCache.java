package com.github.alexthe666.citadel.server.entity.pathfinding.raycoms;

import com.github.alexthe666.citadel.server.world.WorldChunkUtil;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunk.EntityCreationType;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class ChunkCache implements LevelReader {
   private final DimensionType dimType;
   protected int chunkX;
   protected int chunkZ;
   protected LevelChunk[][] chunkArray;
   protected boolean empty;
   protected Level world;
   private final int minBuildHeight;
   private final int maxBuildHeight;

   public ChunkCache(Level worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn, DimensionType type) {
      this.world = worldIn;
      this.chunkX = posFromIn.getX() - subIn >> 4;
      this.chunkZ = posFromIn.getZ() - subIn >> 4;
      int i = posToIn.getX() + subIn >> 4;
      int j = posToIn.getZ() + subIn >> 4;
      this.chunkArray = new LevelChunk[i - this.chunkX + 1][j - this.chunkZ + 1];
      this.empty = true;

      for (int k = this.chunkX; k <= i; k++) {
         for (int l = this.chunkZ; l <= j; l++) {
            if (WorldChunkUtil.isEntityChunkLoaded(this.world, new ChunkPos(k, l)) && worldIn.getChunkSource() instanceof ServerChunkCache serverChunkCache) {
               ChunkHolder holder = serverChunkCache.chunkMap.getVisibleChunkIfPresent(ChunkPos.asLong(k, l));
               if (holder != null) {
                  this.chunkArray[k - this.chunkX][l - this.chunkZ] = (LevelChunk)holder.getFullChunkFuture()
                     .getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK)
                     .orElse(null);
               }
            }
         }
      }

      this.dimType = type;
      this.minBuildHeight = worldIn.getMinBuildHeight();
      this.maxBuildHeight = worldIn.getMaxBuildHeight();
   }

   public boolean isEmpty() {
      return this.empty;
   }

   @Nullable
   public BlockEntity getBlockEntity(@NotNull BlockPos pos) {
      return this.getTileEntity(pos, EntityCreationType.CHECK);
   }

   @Nullable
   public BlockEntity getTileEntity(BlockPos pos, EntityCreationType createType) {
      int i = (pos.getX() >> 4) - this.chunkX;
      int j = (pos.getZ() >> 4) - this.chunkZ;
      return !this.withinBounds(i, j) ? null : this.chunkArray[i][j].getBlockEntity(pos, createType);
   }

   public int getMinBuildHeight() {
      return this.minBuildHeight;
   }

   public int getMaxBuildHeight() {
      return this.maxBuildHeight;
   }

   @NotNull
   public BlockState getBlockState(BlockPos pos) {
      if (pos.getY() >= this.getMinBuildHeight() && pos.getY() < this.getMaxBuildHeight()) {
         int i = (pos.getX() >> 4) - this.chunkX;
         int j = (pos.getZ() >> 4) - this.chunkZ;
         if (i >= 0 && i < this.chunkArray.length && j >= 0 && j < this.chunkArray[i].length) {
            LevelChunk chunk = this.chunkArray[i][j];
            if (chunk != null) {
               return chunk.getBlockState(pos);
            }
         }
      }

      return Blocks.AIR.defaultBlockState();
   }

   public FluidState getFluidState(BlockPos pos) {
      if (pos.getY() >= this.getMinBuildHeight() && pos.getY() < this.getMaxBuildHeight()) {
         int i = (pos.getX() >> 4) - this.chunkX;
         int j = (pos.getZ() >> 4) - this.chunkZ;
         if (i >= 0 && i < this.chunkArray.length && j >= 0 && j < this.chunkArray[i].length) {
            LevelChunk chunk = this.chunkArray[i][j];
            if (chunk != null) {
               return chunk.getFluidState(pos);
            }
         }
      }

      return Fluids.EMPTY.defaultFluidState();
   }

   public Holder<Biome> getUncachedNoiseBiome(int x, int y, int z) {
      return null;
   }

   public boolean isEmptyBlock(BlockPos pos) {
      BlockState state = this.getBlockState(pos);
      return state.isAir();
   }

   @Nullable
   public ChunkAccess getChunk(int x, int z, ChunkStatus requiredStatus, boolean nonnull) {
      int i = x - this.chunkX;
      int j = z - this.chunkZ;
      return i >= 0 && i < this.chunkArray.length && j >= 0 && j < this.chunkArray[i].length ? this.chunkArray[i][j] : null;
   }

   public boolean hasChunk(int chunkX, int chunkZ) {
      return false;
   }

   public BlockPos getHeightmapPos(Types heightmapType, BlockPos pos) {
      return null;
   }

   public int getHeight(Types heightmapType, int x, int z) {
      return 0;
   }

   public int getSkyDarken() {
      return 0;
   }

   public BiomeManager getBiomeManager() {
      return null;
   }

   public WorldBorder getWorldBorder() {
      return null;
   }

   public boolean isUnobstructed(@Nullable Entity entityIn, VoxelShape shape) {
      return false;
   }

   public List<VoxelShape> getEntityCollisions(@org.jetbrains.annotations.Nullable Entity p_186427_, AABB p_186428_) {
      return null;
   }

   public int getDirectSignal(BlockPos pos, Direction direction) {
      return this.getBlockState(pos).getDirectSignal(this, pos, direction);
   }

   public RegistryAccess registryAccess() {
      return RegistryAccess.EMPTY;
   }

   public FeatureFlagSet enabledFeatures() {
      return FeatureFlagSet.of();
   }

   public boolean isClientSide() {
      return false;
   }

   public int getSeaLevel() {
      return 0;
   }

   @NotNull
   public DimensionType dimensionType() {
      return this.dimType;
   }

   private boolean withinBounds(int x, int z) {
      return x >= 0 && x < this.chunkArray.length && z >= 0 && z < this.chunkArray[x].length && this.chunkArray[x][z] != null;
   }

   public float getShade(Direction direction, boolean b) {
      return 0.0F;
   }

   public LevelLightEngine getLightEngine() {
      return null;
   }
}
