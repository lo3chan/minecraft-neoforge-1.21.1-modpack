package com.seibel.distanthorizons.common.wrappers.worldGeneration.step;

import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.BatchGenerationEnvironment_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject.DhLitWorldGenRegion_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.params.ThreadWorldGenParams_neoforge;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.gridList.ArrayGridList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;

public final class StepFeatures_neoforge extends AbstractWorldGenStep_neoforge {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final ChunkStatus STATUS = ChunkStatus.FEATURES;
   private final BatchGenerationEnvironment_neoforge environment;
   public static final Set<String> LOGGED_ERRORS = Collections.newSetFromMap(new ConcurrentHashMap<>());

   public StepFeatures_neoforge(BatchGenerationEnvironment_neoforge batchGenerationEnvironment) {
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
      for (ChunkWrapper_neoforge chunkWrapper : this.getChunkWrappersToGenerate(chunkWrappers)) {
         ChunkAccess chunk = chunkWrapper.getChunk();

         try {
            if (worldGenRegion.hasChunk(chunkWrapper.getChunkPos().getX(), chunkWrapper.getChunkPos().getZ())) {
               this.environment.globalParams.generator.applyBiomeDecoration(worldGenRegion, chunk, tParams.structFeatManager.forWorldGenRegion(worldGenRegion));
            } else {
               LOGGER.warn("Unable to generate features for chunk at pos [" + chunkWrapper.getChunkPos() + "], world gen region doesn't contain the chunk.");
            }

            Heightmap.primeHeightmaps(chunk, STATUS.heightmapsAfter());
         } catch (ConcurrentModificationException var10) {
            String message = "Concurrency issue when generating features for chunk at pos ["
               + chunkWrapper.getChunkPos()
               + "], error: ["
               + var10.getMessage()
               + "], this message will only be logged once. This issue cannot be resolved from DH's end.";
            if (LOGGED_ERRORS.add(message)) {
               LOGGER.warn(message, var10);
            }
         } catch (Exception var11) {
            String messagex = "Unexpected issue when generating features for chunk at pos ["
               + chunkWrapper.getChunkPos()
               + "], error: ["
               + var11.getMessage()
               + "].";
            if (LOGGED_ERRORS.add(messagex)) {
               LOGGER.warn(messagex, var11);
            }
         }
      }
   }
}
