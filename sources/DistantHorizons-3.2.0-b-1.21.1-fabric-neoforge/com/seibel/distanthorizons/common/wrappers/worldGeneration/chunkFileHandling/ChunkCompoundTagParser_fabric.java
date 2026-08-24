package com.seibel.distanthorizons.common.wrappers.worldGeneration.chunkFileHandling;

import com.mojang.serialization.Codec;
import com.seibel.distanthorizons.common.wrappers.McObjectConverter_fabric;
import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_fabric;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.level.IDhServerLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.ChunkLightStorage;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.class_1923;
import net.minecraft.class_1936;
import net.minecraft.class_1937;
import net.minecraft.class_1959;
import net.minecraft.class_1972;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2378;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2509;
import net.minecraft.class_2520;
import net.minecraft.class_2680;
import net.minecraft.class_2791;
import net.minecraft.class_2806;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_2841;
import net.minecraft.class_2843;
import net.minecraft.class_2902;
import net.minecraft.class_3611;
import net.minecraft.class_5281;
import net.minecraft.class_6755;
import net.minecraft.class_6880;
import net.minecraft.class_7924;
import net.minecraft.class_2841.class_6563;
import net.minecraft.class_2902.class_2903;

public class ChunkCompoundTagParser_fabric {
   public static final DhLogger LOGGER = new DhLoggerBuilder()
      .name("LOD Chunk Reader")
      .fileLevelConfig(Config.Common.Logging.logWorldGenChunkLoadEventToFile)
      .build();
   private static final AtomicBoolean ZERO_CHUNK_POS_ERROR_LOGGED_REF = new AtomicBoolean(false);
   private static final ConcurrentHashMap<String, Object> LOGGED_ERROR_MESSAGE_MAP = new ConcurrentHashMap<>();
   private static boolean lightingSectionErrorLogged = false;

   public static ChunkWrapper_fabric createFromTag(class_5281 mcWorldGenLevel, IDhServerLevel dhServerLevel, class_1923 chunkPos, class_2487 chunkData) {
      int chunkX = CompoundTagUtil_fabric.getInt(chunkData, "xPos");
      int chunkZ = CompoundTagUtil_fabric.getInt(chunkData, "zPos");
      class_1923 actualChunkPos = new class_1923(chunkX, chunkZ);
      if (!Objects.equals(chunkPos, actualChunkPos)) {
         if (chunkX != 0 || chunkZ != 0) {
            LOGGER.error(
               "Chunk file at ["
                  + chunkPos.toString()
                  + "] is in the wrong location. \nPlease try optimizing your world to fix this issue. \nWorld optimization can be done from the singleplayer world selection screen. \n(Expected pos: ["
                  + chunkPos.toString()
                  + "], actual ["
                  + actualChunkPos.toString()
                  + "]) "
            );
            return null;
         }

         if (!ZERO_CHUNK_POS_ERROR_LOGGED_REF.getAndSet(true)) {
            LOGGER.warn(
               "Chunk file at ["
                  + chunkPos.toString()
                  + "] doesn't have a chunk pos. \nThis might happen if the world was created using an external program. \nDH will attempt to parse the chunk anyway and won't log this message again.\nIf issues arise please try optimizing your world to fix this issue. \nWorld optimization can be done from the singleplayer world selection screen. "
            );
         }
      }

      class_6755<class_2248> blockTicks = new class_6755();
      class_6755<class_3611> fluidTicks = new class_6755();
      int sectionYCount = mcWorldGenLevel.method_32890();
      class_2826[] chunkSections = new class_2826[sectionYCount];
      boolean hasBlocks = readAndPopulateSections(mcWorldGenLevel, chunkPos, chunkData, chunkSections);
      if (!hasBlocks) {
         return null;
      } else {
         long inhabitedTime = CompoundTagUtil_fabric.getLong(chunkData, "InhabitedTime");
         boolean isLightOn = CompoundTagUtil_fabric.getBoolean(chunkData, "isLightOn");
         class_2818 chunk = new class_2818(
            (class_1937)mcWorldGenLevel, chunkPos, class_2843.field_12950, blockTicks, fluidTicks, inhabitedTime, chunkSections, null, null
         );
         chunk.method_12020(isLightOn);
         boolean hasHeightmapData = readHeightmaps(chunk, chunkData);
         ChunkWrapper_fabric chunkWrapper = new ChunkWrapper_fabric(chunk, dhServerLevel.getServerLevelWrapper());
         chunkWrapper.createDhHeightMaps();
         boolean chunkHasBlocks = false;
         int serverMinHeight = dhServerLevel.getServerLevelWrapper().getMinHeight();

         for (int x = 0; x < 16 && !chunkHasBlocks; x++) {
            for (int z = 0; z < 16 && !chunkHasBlocks; z++) {
               int heightMap = Math.max(chunkWrapper.getLightBlockingHeightMapValue(x, z), chunkWrapper.getSolidHeightMapValue(x, z));
               if (heightMap != serverMinHeight) {
                  chunkHasBlocks = true;
               }
            }
         }

         return chunkHasBlocks ? chunkWrapper : null;
      }
   }

   private static boolean readAndPopulateSections(class_1936 level, class_1923 chunkPos, class_2487 chunkData, class_2826[] chunkSections) {
      int sectionYCount = level.method_32890();
      class_2499 tagSections = CompoundTagUtil_fabric.getListTag(chunkData, "Sections", 10);
      if (tagSections == null || tagSections.isEmpty()) {
         tagSections = CompoundTagUtil_fabric.getListTag(chunkData, "sections", 10);
      }

      boolean blocksFound = false;
      if (tagSections != null) {
         for (int i = 0; i < tagSections.size(); i++) {
            class_2487 tagSection = CompoundTagUtil_fabric.getCompoundTag(tagSections, i);
            if (tagSection != null) {
               int sectionYPos = CompoundTagUtil_fabric.getByte(tagSection, "Y");
               int sectionId = level.method_31603(sectionYPos);
               if (sectionId >= 0 && sectionId < chunkSections.length) {
                  boolean containsBlockStates = CompoundTagUtil_fabric.contains(tagSection, "block_states", 10);
                  class_2841<class_2680> blockStateContainer;
                  if (containsBlockStates) {
                     Codec<class_2841<class_2680>> blockStateCodec = getBlockStateCodec(level);
                     blockStateContainer = (class_2841<class_2680>)blockStateCodec.parse(
                           class_2509.field_11560, CompoundTagUtil_fabric.getCompoundTag(tagSection, "block_states")
                        )
                        .promotePartial(string -> logBlockDeserializationWarning(chunkPos, sectionYPos, string))
                        .getOrThrow(message -> logErrorAndReturnException(message));
                     blocksFound = true;
                  } else {
                     blockStateContainer = new class_2841(class_2248.field_10651, class_2246.field_10124.method_9564(), class_6563.field_34569);
                  }

                  class_2378<class_1959> biomeRegistry = getBiomeRegistry(level);
                  Codec<class_2841<class_6880<class_1959>>> biomeCodec = getBiomeCodec(level, biomeRegistry);
                  class_2487 biomeTag = CompoundTagUtil_fabric.getCompoundTag(tagSection, "biomeRegistry");
                  if (biomeTag == null) {
                     biomeTag = CompoundTagUtil_fabric.getCompoundTag(tagSection, "biomes");
                  }

                  class_2841<class_6880<class_1959>> biomeContainer;
                  if (biomeTag != null && !biomeTag.method_33133()) {
                     biomeContainer = (class_2841<class_6880<class_1959>>)biomeCodec.parse(class_2509.field_11560, biomeTag)
                        .promotePartial(string -> logBiomeDeserializationWarning(chunkPos, sectionYCount, string))
                        .getOrThrow(message -> logErrorAndReturnException(message));
                  } else {
                     biomeContainer = new class_2841(biomeRegistry.method_40295(), biomeRegistry.method_40290(class_1972.field_9451), class_6563.field_34570);
                  }

                  chunkSections[sectionId] = new class_2826(blockStateContainer, biomeContainer);
               }
            }
         }
      }

      return blocksFound;
   }

   private static Codec<class_2841<class_2680>> getBlockStateCodec(class_1936 level) {
      return class_2841.method_44343(class_2248.field_10651, class_2680.field_24734, class_6563.field_34569, class_2246.field_10124.method_9564());
   }

   private static class_2378<class_1959> getBiomeRegistry(class_1936 level) {
      return level.method_30349().method_30530(class_7924.field_41236);
   }

   private static Codec<class_2841<class_6880<class_1959>>> getBiomeCodec(class_1936 level, class_2378<class_1959> biomeRegistry) {
      return class_2841.method_44343(
         biomeRegistry.method_40295(), biomeRegistry.method_40294(), class_6563.field_34570, biomeRegistry.method_40290(class_1972.field_9451)
      );
   }

   private static boolean readHeightmaps(class_2818 chunk, class_2487 chunkData) {
      class_2487 tagHeightmaps = CompoundTagUtil_fabric.getCompoundTag(chunkData, "Heightmaps");
      if (tagHeightmaps == null) {
         return false;
      } else {
         for (class_2903 type : class_2806.field_12803.method_12160()) {
            String heightmapKey = type.method_12605();
            if (tagHeightmaps.method_10573(heightmapKey, 12)) {
               chunk.method_12037(type, tagHeightmaps.method_10565(heightmapKey));
            }
         }

         class_2902.method_16684(chunk, class_2806.field_12803.method_12160());
         return true;
      }
   }

   public static ChunkCompoundTagParser$CombinedChunkLightStorage_fabric readLight(class_2791 chunk, class_2487 chunkData) {
      ChunkCompoundTagParser$CombinedChunkLightStorage_fabric combinedStorage = new ChunkCompoundTagParser$CombinedChunkLightStorage_fabric(
         ChunkWrapper_fabric.getInclusiveMinBuildHeight(chunk), ChunkWrapper_fabric.getExclusiveMaxBuildHeight(chunk)
      );
      ChunkLightStorage blockLightStorage = combinedStorage.blockLightStorage;
      ChunkLightStorage skyLightStorage = combinedStorage.skyLightStorage;
      boolean foundSkyLight = false;
      class_2520 chunkSectionTags = chunkData.method_10580("sections");
      if (chunkSectionTags == null) {
         if (!lightingSectionErrorLogged) {
            lightingSectionErrorLogged = true;
            LOGGER.error("No sections found for chunk at pos [" + chunk.method_12004() + "] chunk data may be out of date.");
         }

         return null;
      } else if (!(chunkSectionTags instanceof class_2499 chunkSectionListTag)) {
         if (!lightingSectionErrorLogged) {
            lightingSectionErrorLogged = true;
            LOGGER.error(
               "Chunk section tag list have unexpected type [" + chunkSectionTags.getClass().getName() + "], expected [" + class_2499.class.getName() + "]."
            );
         }

         return null;
      } else {
         for (int sectionIndex = 0; sectionIndex < chunkSectionListTag.size(); sectionIndex++) {
            class_2520 chunkSectionTag = chunkSectionListTag.method_10534(sectionIndex);
            if (!(chunkSectionTag instanceof class_2487 chunkSectionCompoundTag)) {
               if (!lightingSectionErrorLogged) {
                  lightingSectionErrorLogged = true;
                  LOGGER.error(
                     "Chunk section tag has an unexpected type [" + chunkSectionTag.getClass().getName() + "], expected [" + class_2487.class.getName() + "]."
                  );
               }

               return null;
            }

            byte[] blockLightNibbleArray = CompoundTagUtil_fabric.getByteArray(chunkSectionCompoundTag, "BlockLight");
            byte[] skyLightNibbleArray = CompoundTagUtil_fabric.getByteArray(chunkSectionCompoundTag, "SkyLight");
            if (blockLightNibbleArray != null && skyLightNibbleArray != null) {
               if (skyLightNibbleArray.length != 0) {
                  foundSkyLight = true;
               }

               for (int relX = 0; relX < 16; relX++) {
                  for (int relZ = 0; relZ < 16; relZ++) {
                     for (int relY = 0; relY < 16; relY++) {
                        int blockPosIndex = relY * 16 * 16 + relZ * 16 + relX;
                        byte blockLight = blockLightNibbleArray.length == 0 ? 0 : getNibbleAtIndex(blockLightNibbleArray, blockPosIndex);
                        byte skyLight = skyLightNibbleArray.length == 0 ? 0 : getNibbleAtIndex(skyLightNibbleArray, blockPosIndex);
                        if (skyLightNibbleArray.length == 0 && foundSkyLight) {
                           skyLight = 15;
                        }

                        int y = relY + sectionIndex * 16 + ChunkWrapper_fabric.getInclusiveMinBuildHeight(chunk);
                        blockLightStorage.set(relX, y, relZ, blockLight);
                        skyLightStorage.set(relX, y, relZ, skyLight);
                     }
                  }
               }
            }
         }

         return combinedStorage;
      }
   }

   private static byte getNibbleAtIndex(byte[] arr, int index) {
      return index % 2 == 0 ? (byte)(arr[index / 2] & 15) : (byte)(arr[index / 2] >> 4 & 15);
   }

   private static void logBlockDeserializationWarning(class_1923 chunkPos, int sectionYIndex, String message) {
      LOGGED_ERROR_MESSAGE_MAP.computeIfAbsent(
         message,
         newMessage -> {
            DhChunkPos dhChunkPos = McObjectConverter_fabric.convert(chunkPos);
            LOGGER.warn(
               "Unable to deserialize blocks for chunk section ["
                  + dhChunkPos.getX()
                  + ", "
                  + sectionYIndex
                  + ", "
                  + dhChunkPos.getZ()
                  + "], error: ["
                  + newMessage
                  + "]. This can probably be ignored, although if your world looks wrong, optimizing it via the single player menu then deleting your DH database(s) should fix the problem."
            );
            return (Object)newMessage;
         }
      );
   }

   private static void logBiomeDeserializationWarning(class_1923 chunkPos, int sectionYIndex, String message) {
      LOGGED_ERROR_MESSAGE_MAP.computeIfAbsent(
         message,
         newMessage -> {
            DhChunkPos dhChunkPos = McObjectConverter_fabric.convert(chunkPos);
            LOGGER.warn(
               "Unable to deserialize biomes for chunk section ["
                  + dhChunkPos.getX()
                  + ", "
                  + sectionYIndex
                  + ", "
                  + dhChunkPos.getZ()
                  + "], error: ["
                  + newMessage
                  + "]. This can probably be ignored, although if your world looks wrong, optimizing it via the single player menu then deleting your DH database(s) should fix the problem."
            );
            return (Object)newMessage;
         }
      );
   }

   private static void logParsingWarningOnce(String message) {
      logParsingWarningOnce(message, null);
   }

   private static void logParsingWarningOnce(String message, Exception e) {
      if (message != null) {
         LOGGED_ERROR_MESSAGE_MAP.computeIfAbsent(
            message,
            newMessage -> {
               LOGGER.warn(
                  "Parsing error: ["
                     + newMessage
                     + "]. This can probably be ignored, although if your world looks wrong, optimizing it via the single player menu then deleting your DH database(s) should fix the problem.",
                  e
               );
               return (Object)newMessage;
            }
         );
      }
   }

   private static RuntimeException logErrorAndReturnException(String message) {
      LOGGED_ERROR_MESSAGE_MAP.computeIfAbsent(
         message,
         newMessage -> {
            LOGGER.warn(
               "Parsing error: ["
                  + newMessage
                  + "]. This can probably be ignored, although if your world looks wrong, optimizing it via the single player menu then deleting your DH database(s) should fix the problem."
            );
            return (Object)newMessage;
         }
      );
      return null;
   }
}
