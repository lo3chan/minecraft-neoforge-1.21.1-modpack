package com.finndog.moogs_structures.world.processors;

import com.finndog.moogs_structures.modinit.MoogsStructuresProcessors;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Plane;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class RemoveFloatingBlocksProcessor extends StructureProcessor {
   public static final MapCodec<RemoveFloatingBlocksProcessor> CODEC = MapCodec.unit(RemoveFloatingBlocksProcessor::new);

   private RemoveFloatingBlocksProcessor() {
   }

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos pos,
      BlockPos blockPos,
      StructureBlockInfo structureBlockInfoLocal,
      StructureBlockInfo structureBlockInfoWorld,
      StructurePlaceSettings structurePlacementData
   ) {
      MutableBlockPos mutable = new MutableBlockPos().set(structureBlockInfoWorld.pos());
      if (levelReader instanceof WorldGenRegion worldGenRegion && !worldGenRegion.getCenter().equals(new ChunkPos(mutable))) {
         return structureBlockInfoWorld;
      } else {
         ChunkAccess cachedChunk = levelReader.getChunk(mutable);
         if (structureBlockInfoWorld.state().isAir() || !structureBlockInfoWorld.state().getFluidState().isEmpty()) {
            cachedChunk.setBlockState(mutable, structureBlockInfoWorld.state(), false);

            for (BlockState aboveWorldState = levelReader.getBlockState(mutable.move(Direction.UP));
               mutable.getY() < levelReader.getHeight() && !aboveWorldState.canSurvive(levelReader, mutable);
               aboveWorldState = levelReader.getBlockState(mutable.move(Direction.UP))
            ) {
               cachedChunk.setBlockState(mutable, structureBlockInfoWorld.state(), false);
            }

            for (Direction direction : Plane.HORIZONTAL) {
               mutable.set(structureBlockInfoWorld.pos());
               mutable.move(direction);
               ChunkPos chunkPos = new ChunkPos(mutable);
               ChunkAccess chunkAccess2 = cachedChunk;
               if (!chunkPos.equals(cachedChunk.getPos())) {
                  chunkAccess2 = levelReader.getChunk(mutable);
               }

               BlockState sideBlock = chunkAccess2.getBlockState(mutable);
               if (!sideBlock.canSurvive(levelReader, mutable)) {
                  chunkAccess2.setBlockState(mutable, structureBlockInfoWorld.state(), false);
               }
            }
         }

         return structureBlockInfoWorld;
      }
   }

   protected StructureProcessorType<?> getType() {
      return MoogsStructuresProcessors.REMOVE_FLOATING_BLOCKS_PROCESSOR.get();
   }
}
