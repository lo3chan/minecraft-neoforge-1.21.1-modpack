package com.seibel.distanthorizons.core.jar;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.sql.dto.FullDataSourceV2DTO;
import com.seibel.distanthorizons.core.sql.repo.FullDataSourceV2Repo;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

public class JarMain {
   public static final DhLogger LOGGER = new DhLoggerBuilder().build();

   public static void main(String[] args) {
      List<String> argList = Arrays.asList(args);
      if (!argList.contains("--no-custom-logger")) {
         LoggerContext context = (LoggerContext)LogManager.getContext(false);

         try {
            context.setConfigLocation(JarUtils.accessFileURI("/log4jConfig.xml"));
         } catch (Exception var14) {
            LOGGER.error("Failed to set log4j config. Try running with the [--no-custom-logger] argument", var14);
         }
      }

      LOGGER.debug("Running Distant Horizons standalone jar");
      LOGGER.warn("The standalone jar is still a massive WIP, expect bugs");
      LOGGER.debug("Java version " + System.getProperty("java.version"));
      Byte exportDetailLevel = null;
      Long exportPos = null;
      boolean showHelp = argList.contains("help");
      if (!showHelp) {
         showHelp = true;
         if (argList.size() == 1) {
            showHelp = false;
         } else if (argList.size() == 2) {
            String detailLevelString = argList.get(1);

            try {
               exportDetailLevel = Byte.parseByte(detailLevelString);
               showHelp = false;
            } catch (NumberFormatException var13) {
               LOGGER.error("Unable to parse detail level [" + detailLevelString + "], error: [" + var13.getMessage() + "].");
            }
         } else if (argList.size() == 4) {
            String detailLevelString = argList.get(1);
            String posXString = argList.get(2);
            String posZString = argList.get(3);

            try {
               byte detailLevel = Byte.parseByte(detailLevelString);
               int posX = Integer.parseInt(posXString);
               int posZ = Integer.parseInt(posZString);
               exportPos = DhSectionPos.encode(detailLevel, posX, posZ);
               showHelp = false;
            } catch (NumberFormatException var12) {
               LOGGER.error(
                  "Unable to parse position [" + detailLevelString + "], [" + posXString + "], [" + posZString + "], error: [" + var12.getMessage() + "]."
               );
            }
         }
      }

      if (showHelp) {
         LOGGER.info(
            "--export parses the 'DistantHorizons.sqlite' file next to this jar and exports the given data into a CSV file. \nUsage: \n--export [LOD position Detail Level] [LOD position X] [LOD position Z] \n\tExport the given position's data if present. \n\tThe detail level should be absolute, IE 0 = block sized, 1 = 2x2 blocks, etc. \n--export [LOD position Detail Level]\n\tExport all data for a given detail level.\n\tThe detail level should be absolute, IE 0 = block sized, 1 = 2x2 blocks, etc. \n--export\n\tExport the entire database.\n"
         );
      } else {
         File dbFile = new File("./DistantHorizons.sqlite");
         if (!dbFile.exists()) {
            LOGGER.error("Unable to find a database to parse at: [" + dbFile.getAbsolutePath() + "].");
         } else {
            File exportFile = new File("DistantHorizons-export.csv");
            if (exportFile.isDirectory()) {
               LOGGER.error("Export file can't be a folder. Given path: [" + exportFile + "].");
            } else {
               try {
                  boolean ignored = exportFile.mkdirs();
                  if (exportFile.exists()) {
                     LOGGER.error("Export file already exists: [" + exportFile.getAbsolutePath() + "].");
                     return;
                  }

                  if (exportFile.createNewFile()) {
                     LOGGER.error("Failed to create file: [" + exportFile.getAbsolutePath() + "].");
                     return;
                  }
               } catch (Exception var15) {
                  LOGGER.error("Unable to create export file: [" + exportFile.getAbsolutePath() + "].");
                  return;
               }

               LOGGER.info("LOD data will be exported to [" + exportFile.getAbsolutePath() + "].");

               FullDataSourceV2Repo repo;
               try {
                  repo = new FullDataSourceV2Repo("jdbc:dh_sqlite", dbFile);
               } catch (IOException | SQLException var11) {
                  LOGGER.error(
                     "Failed to initialize connection with database: [" + exportFile.getAbsolutePath() + "], error: [" + var11.getMessage() + "].", var11
                  );
                  return;
               }

               if (exportPos != null) {
                  exportLodDataAtPosition(repo, exportFile, exportPos);
               } else if (exportDetailLevel != null) {
                  exportAllAtDetailLevel(repo, exportFile, exportDetailLevel);
               } else {
                  exportEntireDatabase(repo, exportFile);
               }
            }
         }
      }
   }

   private static void exportLodDataAtPosition(FullDataSourceV2Repo repo, File exportFile, long pos) {
      FullDataSourceV2DTO dto = repo.getByKey(pos);
      if (dto == null) {
         LOGGER.error("Unable to find any data at the position [" + DhSectionPos.toString(pos) + "].");
      }
   }

   private static void exportAllAtDetailLevel(FullDataSourceV2Repo repo, File exportFile, byte detailLevel) {
      throw new UnsupportedOperationException("Method Not Implemented");
   }

   private static void exportEntireDatabase(FullDataSourceV2Repo repo, File exportFile) {
      throw new UnsupportedOperationException("Method Not Implemented");
   }
}
