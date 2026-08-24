package com.seibel.distanthorizons.core.file.structure;

import com.seibel.distanthorizons.api.interfaces.override.levelHandling.IDhApiSaveStructure;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import com.seibel.distanthorizons.coreapi.DependencyInjection.OverrideInjector;
import com.seibel.distanthorizons.coreapi.util.StringUtil;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class LocalSaveStructure implements ISaveStructure {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private final ConcurrentHashMap<ILevelWrapper, File> levelWrapperToFileMap = new ConcurrentHashMap<>();

   @Override
   public File getSaveFolder(ILevelWrapper levelWrapper) {
      return this.levelWrapperToFileMap.computeIfAbsent(levelWrapper, newLevelWrapper -> {
         IServerLevelWrapper serverLevelWrapper = (IServerLevelWrapper)levelWrapper;
         File saveFolder = serverLevelWrapper.getMcSaveFolder();
         IDhApiSaveStructure saveStructureOverride = OverrideInjector.INSTANCE.get(IDhApiSaveStructure.class);
         if (saveStructureOverride != null) {
            File overrideFile = saveStructureOverride.overrideFilePath(saveFolder, levelWrapper);
            if (overrideFile != null) {
               LOGGER.info("Save folder overridden from [" + saveFolder.getPath() + "] -> [" + overrideFile.getPath() + "].");
               saveFolder = overrideFile;
            }
         }

         return saveFolder;
      });
   }

   @Override
   public File getPre23SaveFolder(ILevelWrapper levelWrapper) {
      return this.getSaveFolder(levelWrapper);
   }

   @Override
   public void close() throws Exception {
   }

   @Override
   public String toString() {
      return "[" + this.getClass().getSimpleName() + "@(" + StringUtil.join(";", this.levelWrapperToFileMap.values()) + ")]";
   }
}
