package com.seibel.distanthorizons.core.level;

import com.seibel.distanthorizons.api.interfaces.override.worldGenerator.IDhApiWorldGenerator;
import com.seibel.distanthorizons.core.file.fullDatafile.GeneratedFullDataSourceProvider;
import com.seibel.distanthorizons.core.file.structure.ISaveStructure;
import com.seibel.distanthorizons.core.generation.BatchGenerator;
import com.seibel.distanthorizons.core.generation.queues.AbstractLodRequestState;
import com.seibel.distanthorizons.core.generation.queues.IFullDataSourceRetrievalQueue;
import com.seibel.distanthorizons.core.generation.queues.LodRequestModule;
import com.seibel.distanthorizons.core.generation.queues.WorldGenerationQueue;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.coreapi.DependencyInjection.WorldGeneratorInjector;
import java.io.IOException;
import java.sql.SQLException;

public class ServerLevelModule implements AutoCloseable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private final IDhServerLevel parentServerLevel;
   public final ISaveStructure saveStructure;
   public final GeneratedFullDataSourceProvider fullDataFileHandler;
   public final LodRequestModule lodRequestModule;

   public ServerLevelModule(IDhServerLevel parentServerLevel, ISaveStructure saveStructure) throws SQLException, IOException {
      this.parentServerLevel = parentServerLevel;
      this.saveStructure = saveStructure;
      this.fullDataFileHandler = new GeneratedFullDataSourceProvider(parentServerLevel, saveStructure);
      this.lodRequestModule = new LodRequestModule(
         this.parentServerLevel, this.parentServerLevel, this.fullDataFileHandler, () -> new ServerLevelModule.LodRequestState(this.parentServerLevel)
      );
   }

   @Override
   public void close() {
      this.lodRequestModule.close();
      this.fullDataFileHandler.close();
   }

   public static class LodRequestState extends AbstractLodRequestState {
      LodRequestState(IDhServerLevel level) {
         super(level, createRetrievalQueue(level));
      }

      private static IFullDataSourceRetrievalQueue createRetrievalQueue(IDhServerLevel level) {
         IDhApiWorldGenerator worldGenerator = WorldGeneratorInjector.INSTANCE.get(level.getLevelWrapper());
         if (worldGenerator == null) {
            worldGenerator = new BatchGenerator(level);
            WorldGeneratorInjector.INSTANCE.bind(level.getLevelWrapper(), worldGenerator);
         }

         return new WorldGenerationQueue(worldGenerator, level);
      }
   }
}
