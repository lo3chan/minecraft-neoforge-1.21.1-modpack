package com.seibel.distanthorizons.common.wrappers.worldGeneration.step;

import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.BatchGenerationEnvironment_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject.DhLitWorldGenRegion_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.params.ThreadWorldGenParams_fabric;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.gridList.ArrayGridList;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.class_2791;
import net.minecraft.class_2806;

public final class StepStructureStart_fabric extends AbstractWorldGenStep_fabric {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final class_2806 STATUS = class_2806.field_16423;
   private static final ReentrantLock STRUCTURE_PLACEMENT_LOCK = new ReentrantLock();
   private final BatchGenerationEnvironment_fabric environment;

   public StepStructureStart_fabric(BatchGenerationEnvironment_fabric batchGenerationEnvironment) {
      this.environment = batchGenerationEnvironment;
   }

   @Override
   public class_2806 getChunkStatus() {
      return STATUS;
   }

   @Override
   public void generateGroup(ThreadWorldGenParams_fabric tParams, DhLitWorldGenRegion_fabric worldGenRegion, ArrayGridList<ChunkWrapper_fabric> chunkWrappers) {
      ArrayList<ChunkWrapper_fabric> chunksToGen = this.getChunkWrappersToGenerate(chunkWrappers);
      if (this.environment.globalParams.worldOptions.method_28029()) {
         for (ChunkWrapper_fabric chunkWrapper : chunksToGen) {
            class_2791 chunk = chunkWrapper.getChunk();
            STRUCTURE_PLACEMENT_LOCK.lock();
            this.environment
               .globalParams
               .generator
               .method_16129(
                  this.environment.globalParams.registry,
                  this.environment.globalParams.mcServerLevel.method_14178().method_46642(),
                  tParams.structFeatManager,
                  chunk,
                  this.environment.globalParams.structures
               );

            try {
               tParams.structCheck.method_39833(chunk.method_12004(), chunk.method_12016());
            } catch (ArrayIndexOutOfBoundsException var11) {
               tParams.recreateStructureCheck();

               try {
                  tParams.structCheck.method_39833(chunk.method_12004(), chunk.method_12016());
               } catch (ArrayIndexOutOfBoundsException var10) {
                  LOGGER.error(
                     "Unable to create structure starts for "
                        + chunk.method_12004()
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
