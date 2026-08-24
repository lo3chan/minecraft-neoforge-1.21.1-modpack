package com.seibel.distanthorizons.core.file.structure;

import com.google.common.net.PercentEscaper;
import com.seibel.distanthorizons.api.enums.config.EDhApiServerFolderNameMode;
import com.seibel.distanthorizons.api.interfaces.override.levelHandling.IDhApiSaveStructure;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.level.IServerKeyedClientLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.objects.ParsedIp;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftSharedWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import com.seibel.distanthorizons.coreapi.DependencyInjection.OverrideInjector;
import com.seibel.distanthorizons.coreapi.util.StringUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ClientOnlySaveStructure implements ISaveStructure {
   public static final String SERVER_DATA_FOLDER_NAME = "Distant_Horizons_server_data";
   public static final String REPLAY_SERVER_FOLDER_NAME = "REPLAY";
   public static final String INVALID_FILE_CHARACTERS_REGEX = "[\\\\/:*?\"<>|]";
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IMinecraftClientWrapper MC_CLIENT = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   private static final IMinecraftSharedWrapper MC_SHARED = SingletonInjector.INSTANCE.get(IMinecraftSharedWrapper.class);
   private final ConcurrentHashMap<ILevelWrapper, File> levelWrapperToFileMap = new ConcurrentHashMap<>();

   @Override
   public File getSaveFolder(ILevelWrapper levelWrapper) {
      return this.levelWrapperToFileMap.computeIfAbsent(levelWrapper, newLevelWrapper -> {
         File saveFolder;
         if (newLevelWrapper instanceof IServerKeyedClientLevel) {
            IServerKeyedClientLevel keyedClientLevel = (IServerKeyedClientLevel)newLevelWrapper;
            LOGGER.info("Loading level [" + newLevelWrapper.getDhIdentifier() + "] with key: [" + keyedClientLevel.getServerLevelKey() + "].");
            String serverKey = keyedClientLevel.getServerKey();
            if (serverKey.isEmpty()) {
               serverKey = getServerFolderName();
            }

            saveFolder = getSaveFolderByLevelId(serverKey, keyedClientLevel.getServerLevelKey());
         } else {
            saveFolder = getSaveFolderByLevelId(getServerFolderName(), levelWrapper.getDhIdentifier());
         }

         IDhApiSaveStructure saveStructureOverride = OverrideInjector.INSTANCE.get(IDhApiSaveStructure.class);
         if (saveStructureOverride != null) {
            File overrideFile = saveStructureOverride.overrideFilePath(saveFolder, newLevelWrapper);
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
      IDhApiSaveStructure saveStructureOverride = OverrideInjector.INSTANCE.get(IDhApiSaveStructure.class);
      return saveStructureOverride != null
         ? this.getSaveFolder(levelWrapper)
         : getSaveFolderByLevelId(getServerFolderName(), levelWrapper.getDimensionType().getName());
   }

   private static ArrayList<File> getValidDhDimensionFolders(File potentialFolder) {
      ArrayList<File> subDimSaveFolders = new ArrayList<>();
      if (!potentialFolder.isDirectory()) {
         return subDimSaveFolders;
      } else {
         File[] potentialLevelFolders = potentialFolder.listFiles();
         if (potentialLevelFolders != null) {
            for (File potentialFile : potentialLevelFolders) {
               if (potentialFile.isDirectory()) {
                  File[] dataFolders = potentialFile.listFiles();
                  if (dataFolders != null) {
                     boolean isValidDhLevelFolder = false;

                     for (File dataFolder : dataFolders) {
                        if (dataFolder.getName().equalsIgnoreCase("DistantHorizons.sqlite")) {
                           isValidDhLevelFolder = true;
                           break;
                        }
                     }

                     if (isValidDhLevelFolder) {
                        subDimSaveFolders.add(potentialFile);
                     }
                  }
               }
            }
         }

         return subDimSaveFolders;
      }
   }

   private static File getSaveFolderByLevelId(String folderName, String dimensionName) {
      String path = MC_SHARED.getInstallationDirectory().getPath()
         + File.separatorChar
         + "Distant_Horizons_server_data"
         + File.separatorChar
         + folderName
         + File.separatorChar
         + dimensionName.replaceAll(":", "@@");
      return new File(path);
   }

   private static String getServerFolderName() {
      if (MC_CLIENT.connectedToReplay()) {
         return "REPLAY";
      } else {
         ParsedIp parsedIp = new ParsedIp(MC_CLIENT.getCurrentServerIp());
         String serverIpCleaned = parsedIp.ip.replaceAll("[\\\\/:*?\"<>|]", "");
         String serverPortCleaned = parsedIp.port != null ? parsedIp.port.replaceAll("[\\\\/:*?\"<>|]", "") : "";
         EDhApiServerFolderNameMode folderNameMode = Config.Client.Advanced.Multiplayer.serverFolderNameMode.get();
         String serverName = MC_CLIENT.getCurrentServerName().replaceAll("[\\\\/:*?\"<>|]", "");
         String serverMcVersion = MC_CLIENT.getCurrentServerVersion().replaceAll("[\\\\/:*?\"<>|]", "");
         String folderName;
         switch (folderNameMode) {
            case NAME_ONLY:
            default:
               folderName = serverName;
               break;
            case IP_ONLY:
               folderName = serverIpCleaned;
               break;
            case NAME_IP:
               folderName = serverName + ", IP " + serverIpCleaned;
               break;
            case NAME_IP_PORT:
               folderName = serverName + ", IP " + serverIpCleaned + (serverPortCleaned.length() != 0 ? "-" + serverPortCleaned : "");
               break;
            case NAME_IP_PORT_MC_VERSION:
               folderName = serverName
                  + ", IP "
                  + serverIpCleaned
                  + (serverPortCleaned.length() != 0 ? "-" + serverPortCleaned : "")
                  + ", GameVersion "
                  + serverMcVersion;
         }

         return new PercentEscaper("", true).escape(folderName);
      }
   }

   @Override
   public void close() {
   }

   @Override
   public String toString() {
      return "[" + this.getClass().getSimpleName() + "@(" + StringUtil.join(";", this.levelWrapperToFileMap.values()) + ")]";
   }
}
