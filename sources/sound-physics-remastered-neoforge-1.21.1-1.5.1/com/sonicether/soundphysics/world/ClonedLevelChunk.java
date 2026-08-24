package com.sonicether.soundphysics.world;

import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ChunkAccess.TicksToSave;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.TickContainerAccess;

public class ClonedLevelChunk extends ChunkAccess {
   private final LevelChunkTicks<Block> blockTicks;
   private final LevelChunkTicks<Fluid> fluidTicks;

   public ClonedLevelChunk(Level level, ChunkPos chunkPos, @Nullable LevelChunkSection[] levelChunkSections) {
      super(chunkPos, null, new ClonedLevelHeightAccessor(level), level.registryAccess().registryOrThrow(Registries.BIOME), 0L, levelChunkSections, null);

      for (Types types : Types.values()) {
         if (ChunkStatus.FULL.heightmapsAfter().contains(types)) {
            this.heightmaps.put(types, new Heightmap(this, types));
         }
      }

      this.blockTicks = new LevelChunkTicks();
      this.fluidTicks = new LevelChunkTicks();
   }

   public BlockEntity getBlockEntity(@Nonnull BlockPos blockPos) {
      return (BlockEntity)this.blockEntities.get(blockPos);
   }

   public BlockState getBlockState(@Nonnull BlockPos blockPos) {
      return this.withLevelChunkSectionAtPosition(
         blockPos,
         section -> section != null && !section.hasOnlyAir()
            ? section.getBlockState(blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15)
            : Blocks.AIR.defaultBlockState()
      );
   }

   public FluidState getFluidState(@Nonnull BlockPos blockPos) {
      return this.withLevelChunkSectionAtPosition(
         blockPos,
         section -> section != null && !section.hasOnlyAir()
            ? section.getFluidState(blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15)
            : Fluids.EMPTY.defaultFluidState()
      );
   }

   private <T> T withLevelChunkSectionAtPosition(BlockPos blockPos, Function<LevelChunkSection, T> block) {
      try {
         int sectionIndex = this.getSectionIndex(blockPos.getY());
         if (sectionIndex >= 0 && sectionIndex < this.sections.length) {
            LevelChunkSection section = this.sections[sectionIndex];
            return block.apply(section);
         } else {
            return block.apply(null);
         }
      } catch (Throwable var6) {
         CrashReport crashReport = CrashReport.forThrowable(var6, "Getting section in cloned level chunk");
         CrashReportCategory crashReportCategory = crashReport.addCategory("Chunk Section Get");
         crashReportCategory.setDetail("Location", () -> CrashReportCategory.formatLocation(this, blockPos.getX(), blockPos.getY(), blockPos.getZ()));
         throw new ReportedException(crashReport);
      }
   }

   public TickContainerAccess<Block> getBlockTicks() {
      return this.blockTicks;
   }

   public TickContainerAccess<Fluid> getFluidTicks() {
      return this.fluidTicks;
   }

   public TicksToSave getTicksForSerialization() {
      return new TicksToSave(this.blockTicks, this.fluidTicks);
   }

   public ChunkStatus getPersistedStatus() {
      return ChunkStatus.FULL;
   }

   public void addEntity(@Nonnull Entity entity) {
      throw new UnsupportedOperationException("Can not add entity to read-only level clone");
   }

   public CompoundTag getBlockEntityNbtForSaving(BlockPos blockPos, Provider provider) {
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
