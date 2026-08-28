/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.minecraft.CrashReport
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.ReportedException
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.ChunkAccess$TicksToSave
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.chunk.status.ChunkStatus
 *  net.minecraft.world.level.levelgen.Heightmap
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.ticks.LevelChunkTicks
 *  net.minecraft.world.ticks.TickContainerAccess
 */
package com.sonicether.soundphysics.world;

import com.sonicether.soundphysics.world.ClonedLevelHeightAccessor;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.TickContainerAccess;

public class ClonedLevelChunk
extends ChunkAccess {
    private final LevelChunkTicks<Block> blockTicks;
    private final LevelChunkTicks<Fluid> fluidTicks;

    public ClonedLevelChunk(Level level, ChunkPos chunkPos, @Nullable LevelChunkSection[] levelChunkSections) {
        super(chunkPos, null, (LevelHeightAccessor)new ClonedLevelHeightAccessor(level), level.registryAccess().registryOrThrow(Registries.BIOME), 0L, levelChunkSections, null);
        for (Heightmap.Types types : Heightmap.Types.values()) {
            if (!ChunkStatus.FULL.heightmapsAfter().contains(types)) continue;
            this.heightmaps.put(types, new Heightmap((ChunkAccess)this, types));
        }
        this.blockTicks = new LevelChunkTicks();
        this.fluidTicks = new LevelChunkTicks();
    }

    public BlockEntity getBlockEntity(@Nonnull BlockPos blockPos) {
        return (BlockEntity)this.blockEntities.get(blockPos);
    }

    public BlockState getBlockState(@Nonnull BlockPos blockPos) {
        return this.withLevelChunkSectionAtPosition(blockPos, section -> {
            if (section == null || section.hasOnlyAir()) {
                return Blocks.AIR.defaultBlockState();
            }
            return section.getBlockState(blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF);
        });
    }

    public FluidState getFluidState(@Nonnull BlockPos blockPos) {
        return this.withLevelChunkSectionAtPosition(blockPos, section -> {
            if (section == null || section.hasOnlyAir()) {
                return Fluids.EMPTY.defaultFluidState();
            }
            return section.getFluidState(blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF);
        });
    }

    private <T> T withLevelChunkSectionAtPosition(BlockPos blockPos, Function<LevelChunkSection, T> block) {
        try {
            int sectionIndex = this.getSectionIndex(blockPos.getY());
            if (sectionIndex >= 0 && sectionIndex < this.sections.length) {
                LevelChunkSection section = this.sections[sectionIndex];
                return block.apply(section);
            }
            return block.apply(null);
        }
        catch (Throwable exception) {
            CrashReport crashReport = CrashReport.forThrowable((Throwable)exception, (String)"Getting section in cloned level chunk");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Chunk Section Get");
            crashReportCategory.setDetail("Location", () -> CrashReportCategory.formatLocation((LevelHeightAccessor)this, (int)blockPos.getX(), (int)blockPos.getY(), (int)blockPos.getZ()));
            throw new ReportedException(crashReport);
        }
    }

    public TickContainerAccess<Block> getBlockTicks() {
        return this.blockTicks;
    }

    public TickContainerAccess<Fluid> getFluidTicks() {
        return this.fluidTicks;
    }

    public ChunkAccess.TicksToSave getTicksForSerialization() {
        return new ChunkAccess.TicksToSave(this.blockTicks, this.fluidTicks);
    }

    public ChunkStatus getPersistedStatus() {
        return ChunkStatus.FULL;
    }

    public void addEntity(@Nonnull Entity entity) {
        throw new UnsupportedOperationException("Can not add entity to read-only level clone");
    }

    public CompoundTag getBlockEntityNbtForSaving(BlockPos blockPos, HolderLookup.Provider provider) {
        throw new UnsupportedOperationException("Can not read block entity NBT data from read-only level clone");
    }

    public void removeBlockEntity(@Nonnull BlockPos blockPos) {
        throw new UnsupportedOperationException("Can not remove entity from read-only level clone");
    }

    public void setBlockEntity(@Nonnull BlockEntity blockEntity) {
        throw new UnsupportedOperationException("Can not set block entity in read-only level clone");
    }

    public BlockState setBlockState(@Nonnull BlockPos blockPos, @Nonnull BlockState blockState, boolean unknownFlag) {
        throw new UnsupportedOperationException("Can not set block state in read-only level clone");
    }
}

