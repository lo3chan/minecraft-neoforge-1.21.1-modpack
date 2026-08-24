package com.seibel.distanthorizons.common.wrappers.worldGeneration.step;

import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.BatchGenerationEnvironment_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject.DhLitWorldGenRegion_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.params.ThreadWorldGenParams_neoforge;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.gridList.ArrayGridList;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public final class StepStructureStart_neoforge extends AbstractWorldGenStep_neoforge {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final ChunkStatus STATUS = ChunkStatus.STRUCTURE_STARTS;
   private static final ReentrantLock STRUCTURE_PLACEMENT_LOCK = new ReentrantLock();
   private final BatchGenerationEnvironment_neoforge environment;

   public StepStructureStart_neoforge(BatchGenerationEnvironment_neoforge batchGenerationEnvironment) {
      this.environment = batchGenerationEnvironment;
   }

   @Override
   public ChunkStatus getChunkStatus() {
      return STATUS;
   }

   @Override
   public void generateGroup(
      ThreadWorldGenParams_neoforge tParams, DhLitWorldGenRegion_neoforge worldGenRegion, ArrayGridList<ChunkWrapper_neoforge> chunkWrappers
   ) {
      ArrayList<ChunkWrapper_neoforge> chunksToGen = this.getChunkWrappersToGenerate(chunkWrappers);
      if (this.environment.globalParams.worldOptions.generateStructures()) {
         for (ChunkWrapper_neoforge chunkWrapper : chunksToGen) {
            ChunkAccess chunk = chunkWrapper.getChunk();
            STRUCTURE_PLACEMENT_LOCK.lock();
            this.environment
               .globalParams
               .generator
               .createStructures(
                  this.environment.globalParams.registry,
                  this.environment.globalParams.mcServerLevel.getChunkSource().getGeneratorState(),
                  tParams.structFeatManager,
                  chunk,
                  this.environment.globalParams.structures
               );

            try {
               tParams.structCheck.onStructureLoad(chunk.getPos(), chunk.getAllStarts());
            } catch (ArrayIndexOutOfBoundsException var11) {
               tParams.recreateStructureCheck();

               try {
                  tParams.structCheck.onStructureLoad(chunk.getPos(), chunk.getAllStarts());
               } catch (ArrayIndexOutOfBoundsException var10) {
                  LOGGER.error(
                     "Unable to create structure starts for "
                        + chunk.getPos()
                        + ". This is an error with MC's world generation. Ignoring and continuing generation. Error: "
                        + var10.getMessage()
                  );
               }
            }

            STRUCTURE_PLACEMENT_LOCK.unlock();
         }
      }
   }
}
