/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.client.multiplayer.ClientChunkCache
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 */
package com.sonicether.soundphysics.world;

import com.sonicether.soundphysics.world.ClientLevelProxy;
import com.sonicether.soundphysics.world.ClonedLevelChunk;
import com.sonicether.soundphysics.world.ClonedLevelHeightAccessor;
import java.util.HashMap;
import javax.annotation.Nonnull;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class ClonedClientLevel
implements ClientLevelProxy {
    private final ClonedLevelHeightAccessor heightAccessor;
    private final HashMap<ChunkPos, ClonedLevelChunk> clonedLevelChunks;
    private final BlockPos clonedLevelOrigin;
    private final long clonedLevelTick;

    public ClonedClientLevel(ClientLevel level, BlockPos origin, long tick, int range) {
        ClientChunkCache cache = level.getChunkSource();
        ClonedLevelHeightAccessor heightAccessor = new ClonedLevelHeightAccessor((Level)level);
        HashMap<ChunkPos, ClonedLevelChunk> cachedLevelChunks = new HashMap<ChunkPos, ClonedLevelChunk>();
        ChunkPos originChunkPos = new ChunkPos(origin.getX() >> 4, origin.getZ() >> 4);
        for (int x = -range; x < range; ++x) {
            for (int z = -range; z < range; ++z) {
                ChunkPos chunkPos = new ChunkPos(originChunkPos.x + x, originChunkPos.z + z);
                LevelChunk chunk = cache.getChunk(chunkPos.x, chunkPos.z, false);
                if (chunk == null) continue;
                ClonedLevelChunk clonedChunk = new ClonedLevelChunk((Level)level, chunkPos, chunk.getSections());
                cachedLevelChunks.put(chunkPos, clonedChunk);
            }
        }
        this.heightAccessor = heightAccessor;
        this.clonedLevelOrigin = origin;
        this.clonedLevelTick = tick;
        this.clonedLevelChunks = cachedLevelChunks;
    }

    public BlockPos getOrigin() {
        return this.clonedLevelOrigin;
    }

    public long getTick() {
        return this.clonedLevelTick;
    }

    public ClonedLevelChunk getChunk(int x, int z) {
        ChunkPos chunkPos = new ChunkPos(x, z);
        return this.clonedLevelChunks.get(chunkPos);
    }

    public BlockState getBlockState(@Nonnull BlockPos blockPos) {
        if (this.isOutsideBuildHeight(blockPos)) {
            return Blocks.VOID_AIR.defaultBlockState();
        }
        ChunkPos chunkPos = new ChunkPos(blockPos.getX() >> 4, blockPos.getZ() >> 4);
        ClonedLevelChunk levelChunk = this.clonedLevelChunks.get(chunkPos);
        if (levelChunk == null) {
            return Blocks.VOID_AIR.defaultBlockState();
        }
        return levelChunk.getBlockState(blockPos);
    }

    public FluidState getFluidState(@Nonnull BlockPos blockPos) {
        if (this.isOutsideBuildHeight(blockPos)) {
            return Fluids.EMPTY.defaultFluidState();
        }
        ChunkPos chunkPos = new ChunkPos(blockPos.getX() >> 4, blockPos.getZ() >> 4);
        ClonedLevelChunk levelChunk = this.clonedLevelChunks.get(chunkPos);
        if (levelChunk == null) {
            return Fluids.EMPTY.defaultFluidState();
        }
        return levelChunk.getFluidState(blockPos);
    }

    public int getHeight() {
        return this.heightAccessor.getHeight();
    }

    public int getMinBuildHeight() {
        return this.heightAccessor.getMinBuildHeight();
    }

    public BlockEntity getBlockEntity(@Nonnull BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos.getX() >> 4, blockPos.getZ() >> 4);
        ClonedLevelChunk levelChunk = this.clonedLevelChunks.get(chunkPos);
        if (levelChunk == null) {
            return null;
        }
        return levelChunk.getBlockEntity(blockPos);
    }
}

