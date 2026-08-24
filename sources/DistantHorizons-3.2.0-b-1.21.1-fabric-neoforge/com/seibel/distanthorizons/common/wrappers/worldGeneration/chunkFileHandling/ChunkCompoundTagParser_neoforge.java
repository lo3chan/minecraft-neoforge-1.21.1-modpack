package com.seibel.distanthorizons.common.wrappers.worldGeneration.chunkFileHandling;

import com.mojang.serialization.Codec;
import com.seibel.distanthorizons.common.wrappers.McObjectConverter_neoforge;
import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_neoforge;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.level.IDhServerLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.ChunkLightStorage;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.PalettedContainer.Strategy;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.LevelChunkTicks;

public class ChunkCompoundTagParser_neoforge {
   public static final DhLogger LOGGER = new DhLoggerBuilder()
      .name("LOD Chunk Reader")
      .fileLevelConfig(Config.Common.Logging.logWorldGenChunkLoadEventToFile)
      .build();
   private static final AtomicBoolean ZERO_CHUNK_POS_ERROR_LOGGED_REF = new AtomicBoolean(false);
   private static final ConcurrentHashMap<String, Object> LOGGED_ERROR_MESSAGE_MAP = new ConcurrentHashMap<>();
   private static boolean lightingSectionErrorLogged = false;

   public static ChunkWrapper_neoforge createFromTag(WorldGenLevel mcWorldGenLevel, IDhServerLevel dhServerLevel, ChunkPos chunkPos, CompoundTag chunkData) {
      int chunkX = CompoundTagUtil_neoforge.getInt(chunkData, "xPos");
      int chunkZ = CompoundTagUtil_neoforge.getInt(chunkData, "zPos");
      ChunkPos actualChunkPos = new ChunkPos(chunkX, chunkZ);
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

      LevelChunkTicks<Block> blockTicks = new LevelChunkTicks();
      LevelChunkTicks<Fluid> fluidTicks = new LevelChunkTicks();
      int sectionYCount = mcWorldGenLevel.getSectionsCount();
      LevelChunkSection[] chunkSections = new LevelChunkSection[sectionYCount];
      boolean hasBlocks = readAndPopulateSections(mcWorldGenLevel, chunkPos, chunkData, chunkSections);
      if (!hasBlocks) {
         return null;
      } else {
         long inhabitedTime = CompoundTagUtil_neoforge.getLong(chunkData, "InhabitedTime");
         boolean isLightOn = CompoundTagUtil_neoforge.getBoolean(chunkData, "isLightOn");
         LevelChunk chunk = new LevelChunk(
            (Level)mcWorldGenLevel, chunkPos, UpgradeData.EMPTY, blockTicks, fluidTicks, inhabitedTime, chunkSections, null, null
         );
         chunk.setLightCorrect(isLightOn);
         boolean hasHeightmapData = readHeightmaps(chunk, chunkData);
         ChunkWrapper_neoforge chunkWrapper = new ChunkWrapper_neoforge(chunk, dhServerLevel.getServerLevelWrapper());
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

   private static boolean readAndPopulateSections(LevelAccessor level, ChunkPos chunkPos, CompoundTag chunkData, LevelChunkSection[] chunkSections) {
      int sectionYCount = level.getSectionsCount();
      ListTag tagSections = CompoundTagUtil_neoforge.getListTag(chunkData, "Sections", 10);
      if (tagSections == null || tagSections.isEmpty()) {
         tagSections = CompoundTagUtil_neoforge.getListTag(chunkData, "sections", 10);
      }

      boolean blocksFound = false;
      if (tagSections != null) {
         for (int i = 0; i < tagSections.size(); i++) {
            CompoundTag tagSection = CompoundTagUtil_neoforge.getCompoundTag(tagSections, i);
            if (tagSection != null) {
               int sectionYPos = CompoundTagUtil_neoforge.getByte(tagSection, "Y");
               int sectionId = level.getSectionIndexFromSectionY(sectionYPos);
               if (sectionId >= 0 && sectionId < chunkSections.length) {
                  boolean containsBlockStates = CompoundTagUtil_neoforge.contains(tagSection, "block_states", 10);
                  PalettedContainer<BlockState> blockStateContainer;
                  if (containsBlockStates) {
                     Codec<PalettedContainer<BlockState>> blockStateCodec = getBlockStateCodec(level);
                     blockStateContainer = (PalettedContainer<BlockState>)blockStateCodec.parse(
                           NbtOps.INSTANCE, CompoundTagUtil_neoforge.getCompoundTag(tagSection, "block_states")
                        )
                        .promotePartial(string -> logBlockDeserializationWarning(chunkPos, sectionYPos, string))
                        .getOrThrow(message -> logErrorAndReturnException(message));
                     blocksFound = true;
                  } else {
                     blockStateContainer = new PalettedContainer(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), Strategy.SECTION_STATES);
                  }

                  Registry<Biome> biomeRegistry = getBiomeRegistry(level);
                  Codec<PalettedContainer<Holder<Biome>>> biomeCodec = getBiomeCodec(level, biomeRegistry);
                  CompoundTag biomeTag = CompoundTagUtil_neoforge.getCompoundTag(tagSection, "biomeRegistry");
                  if (biomeTag == null) {
                     biomeTag = CompoundTagUtil_neoforge.getCompoundTag(tagSection, "biomes");
                  }

                  PalettedContainer<Holder<Biome>> biomeContainer;
                  if (biomeTag != null && !biomeTag.isEmpty()) {
                     biomeContainer = (PalettedContainer<Holder<Biome>>)biomeCodec.parse(NbtOps.INSTANCE, biomeTag)
                        .promotePartial(string -> logBiomeDeserializationWarning(chunkPos, sectionYCount, string))
                        .getOrThrow(message -> logErrorAndReturnException(message));
                  } else {
                     biomeContainer = new PalettedContainer(
                        biomeRegistry.asHolderIdMap(), biomeRegistry.getHolderOrThrow(Biomes.PLAINS), Strategy.SECTION_BIOMES
                     );
                  }

                  chunkSections[sectionId] = new LevelChunkSection(blockStateContainer, biomeContainer);
               }
            }
         }
      }

      return blocksFound;
   }

   private static Codec<PalettedContainer<BlockState>> getBlockStateCodec(LevelAccessor level) {
      return PalettedContainer.codecRW(Block.BLOCK_STATE_REGISTRY, BlockState.CODEC, Strategy.SECTION_STATES, Blocks.AIR.defaultBlockState());
   }

   private static Registry<Biome> getBiomeRegistry(LevelAccessor level) {
      return level.registryAccess().registryOrThrow(Registries.BIOME);
   }

   private static Codec<PalettedContainer<Holder<Biome>>> getBiomeCodec(LevelAccessor level, Registry<Biome> biomeRegistry) {
      return PalettedContainer.codecRW(
         biomeRegistry.asHolderIdMap(), biomeRegistry.holderByNameCodec(), Strategy.SECTION_BIOMES, biomeRegistry.getHolderOrThrow(Biomes.PLAINS)
      );
   }

   private static boolean readHeightmaps(LevelChunk chunk, CompoundTag chunkData) {
      CompoundTag tagHeightmaps = CompoundTagUtil_neoforge.getCompoundTag(chunkData, "Heightmaps");
      if (tagHeightmaps == null) {
         return false;
      } else {
         for (Types type : ChunkStatus.FULL.heightmapsAfter()) {
            String heightmapKey = type.getSerializationKey();
            if (tagHeightmaps.contains(heightmapKey, 12)) {
               chunk.setHeightmap(type, tagHeightmaps.getLongArray(heightmapKey));
            }
         }

         Heightmap.primeHeightmaps(chunk, ChunkStatus.FULL.heightmapsAfter());
         return true;
      }
   }

   public static ChunkCompoundTagParser$CombinedChunkLightStorage_neoforge readLight(ChunkAccess chunk, CompoundTag chunkData) {
      ChunkCompoundTagParser$CombinedChunkLightStorage_neoforge combinedStorage = new ChunkCompoundTagParser$CombinedChunkLightStorage_neoforge(
         ChunkWrapper_neoforge.getInclusiveMinBuildHeight(chunk), ChunkWrapper_neoforge.getExclusiveMaxBuildHeight(chunk)
      );
      ChunkLightStorage blockLightStorage = combinedStorage.blockLightStorage;
      ChunkLightStorage skyLightStorage = combinedStorage.skyLightStorage;
      boolean foundSkyLight = false;
      Tag chunkSectionTags = chunkData.get("sections");
      if (chunkSectionTags == null) {
         if (!lightingSectionErrorLogged) {
            lightingSectionErrorLogged = true;
            LOGGER.error("No sections found for chunk at pos [" + chunk.getPos() + "] chunk data may be out of date.");
         }

         return null;
      } else if (!(chunkSectionTags instanceof ListTag chunkSectionListTag)) {
         if (!lightingSectionErrorLogged) {
            lightingSectionErrorLogged = true;
            LOGGER.error(
               "Chunk section tag list have unexpected type [" + chunkSectionTags.getClass().getName() + "], expected [" + ListTag.class.getName() + "]."
            );
         }

         return null;
      } else {
         for (int sectionIndex = 0; sectionIndex < chunkSectionListTag.size(); sectionIndex++) {
            Tag chunkSectionTag = chunkSectionListTag.get(sectionIndex);
            if (!(chunkSectionTag instanceof CompoundTag chunkSectionCompoundTag)) {
               if (!lightingSectionErrorLogged) {
                  lightingSectionErrorLogged = true;
                  LOGGER.error(
                     "Chunk section tag has an unexpected type [" + chunkSectionTag.getClass().getName() + "], expected [" + CompoundTag.class.getName() + "]."
                  );
               }

               return null;
            }

            byte[] blockLightNibbleArray = CompoundTagUtil_neoforge.getByteArray(chunkSectionCompoundTag, "BlockLight");
            byte[] skyLightNibbleArray = CompoundTagUtil_neoforge.getByteArray(chunkSectionCompoundTag, "SkyLight");
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

                        int y = relY + sectionIndex * 16 + ChunkWrapper_neoforge.getInclusiveMinBuildHeight(chunk);
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

   private static void logBlockDeserializationWarning(ChunkPos chunkPos, int sectionYIndex, String message) {
      LOGGED_ERROR_MESSAGE_MAP.computeIfAbsent(
         message,
         newMessage -> {
            DhChunkPos dhChunkPos = McObjectConverter_neoforge.convert(chunkPos);
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

   private static void logBiomeDeserializationWarning(ChunkPos chunkPos, int sectionYIndex, String message) {
      LOGGED_ERROR_MESSAGE_MAP.computeIfAbsent(
         message,
         newMessage -> {
            DhChunkPos dhChunkPos = McObjectConverter_neoforge.convert(chunkPos);
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
