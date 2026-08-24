package com.yungnickyoung.minecraft.yungsapi.world.structure.processor;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public interface ISafeWorldModifier {
   default FluidState getFluidStateSafe(LevelChunkSection chunkSection, BlockPos pos) {
      return chunkSection == null
         ? Fluids.EMPTY.defaultFluidState()
         : chunkSection.getFluidState(SectionPos.sectionRelative(pos.getX()), SectionPos.sectionRelative(pos.getY()), SectionPos.sectionRelative(pos.getZ()));
   }

   default FluidState getFluidStateSafe(LevelReader world, BlockPos pos) {
      ChunkPos chunkPos = new ChunkPos(pos);
      ChunkAccess chunk = world.getChunk(chunkPos.x, chunkPos.z);
      int sectionYIndex = world.getSectionIndex(pos.getY());
      LevelChunkSection chunkSection = chunk.getSection(sectionYIndex);
      return this.getFluidStateSafe(chunkSection, pos);
   }

   default Optional<BlockState> getBlockStateSafe(LevelChunkSection chunkSection, BlockPos pos) {
      return chunkSection == null
         ? Optional.empty()
         : Optional.of(
            chunkSection.getBlockState(SectionPos.sectionRelative(pos.getX()), SectionPos.sectionRelative(pos.getY()), SectionPos.sectionRelative(pos.getZ()))
         );
   }

   default Optional<BlockState> getBlockStateSafe(LevelReader world, BlockPos pos) {
      ChunkPos chunkPos = new ChunkPos(pos);
      ChunkAccess chunk = world.getChunk(chunkPos.x, chunkPos.z);
      int sectionYIndex = world.getSectionIndex(pos.getY());
      LevelChunkSection chunkSection = chunk.getSection(sectionYIndex);
      return this.getBlockStateSafe(chunkSection, pos);
   }

   default boolean isBlockStateAirSafe(LevelReader world, BlockPos pos) {
      Optional<BlockState> blockState = this.getBlockStateSafe(world, pos);
      return blockState.isPresent() && blockState.get().isAir();
   }

   default boolean isMaterialLiquidSafe(LevelReader world, BlockPos pos) {
      Optional<BlockState> blockState = this.getBlockStateSafe(world, pos);
      return blockState.isPresent() && blockState.get().liquid();
   }

   default Optional<BlockState> setBlockStateSafe(LevelChunkSection chunkSection, BlockPos pos, BlockState state) {
      return chunkSection == null
         ? Optional.empty()
         : Optional.of(
            chunkSection.setBlockState(
               SectionPos.sectionRelative(pos.getX()), SectionPos.sectionRelative(pos.getY()), SectionPos.sectionRelative(pos.getZ()), state, false
            )
         );
   }

   default Optional<BlockState> setBlockStateSafe(LevelReader world, BlockPos pos, BlockState state) {
      ChunkPos chunkPos = new ChunkPos(pos);
      ChunkAccess chunk = world.getChunk(chunkPos.x, chunkPos.z);
      int sectionYIndex = chunk.getSectionIndex(pos.getY());
      LevelChunkSection chunkSection = chunk.getSection(sectionYIndex);
      return this.setBlockStateSafe(chunkSection, pos, state);
   }
}
