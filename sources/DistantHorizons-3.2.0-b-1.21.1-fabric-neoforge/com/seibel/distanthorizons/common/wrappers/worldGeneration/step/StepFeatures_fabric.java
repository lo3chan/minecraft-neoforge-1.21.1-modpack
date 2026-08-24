package com.seibel.distanthorizons.common.wrappers.worldGeneration.step;

import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.BatchGenerationEnvironment_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject.DhLitWorldGenRegion_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.params.ThreadWorldGenParams_fabric;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.gridList.ArrayGridList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.class_2791;
import net.minecraft.class_2806;
import net.minecraft.class_2902;

public final class StepFeatures_fabric extends AbstractWorldGenStep_fabric {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final class_2806 STATUS = class_2806.field_12795;
   private final BatchGenerationEnvironment_fabric environment;
   public static final Set<String> LOGGED_ERRORS = Collections.newSetFromMap(new ConcurrentHashMap<>());

   public StepFeatures_fabric(BatchGenerationEnvironment_fabric batchGenerationEnvironment) {
      this.environment = batchGenerationEnvironment;
   }

   @Override
   public class_2806 getChunkStatus() {
      return STATUS;
   }

   @Override
   public void generateGroup(ThreadWorldGenParams_fabric tParams, DhLitWorldGenRegion_fabric worldGenRegion, ArrayGridList<ChunkWrapper_fabric> chunkWrappers) {
      for (ChunkWrapper_fabric chunkWrapper : this.getChunkWrappersToGenerate(chunkWrappers)) {
         class_2791 chunk = chunkWrapper.getChunk();

         try {
            if (worldGenRegion.method_8393(chunkWrapper.getChunkPos().getX(), chunkWrapper.getChunkPos().getZ())) {
               this.environment.globalParams.generator.method_12102(worldGenRegion, chunk, tParams.structFeatManager.forWorldGenRegion(worldGenRegion));
            } else {
               LOGGER.warn("Unable to generate features for chunk at pos [" + chunkWrapper.getChunkPos() + "], world gen region doesn't contain the chunk.");
            }

            class_2902.method_16684(chunk, STATUS.method_12160());
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
