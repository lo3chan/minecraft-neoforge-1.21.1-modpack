package com.seibel.distanthorizons.common.wrappers.block;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiBlockMaterial;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBlockStateWrapperCreatedEvent;
import com.seibel.distanthorizons.common.wrappers.WrapperFactory_fabric;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.types.ConfigEntry;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import com.seibel.distanthorizons.coreapi.DependencyInjection.ApiEventInjector;
import com.seibel.distanthorizons.coreapi.util.ColorUtil;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2498;
import net.minecraft.class_2680;
import net.minecraft.class_2682;
import net.minecraft.class_2769;
import net.minecraft.class_2960;
import net.minecraft.class_3481;
import net.minecraft.class_4275;
import net.minecraft.class_5455;
import net.minecraft.class_6862;
import net.minecraft.class_7924;
import org.jetbrains.annotations.Nullable;

public class BlockStateWrapper_fabric implements IBlockStateWrapper {
   public static final String RESOURCE_LOCATION_SEPARATOR = ":";
   public static final String STATE_STRING_SEPARATOR = "_STATE_";
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final ConcurrentHashMap<class_2680, BlockStateWrapper_fabric> WRAPPER_BY_BLOCK_STATE = new ConcurrentHashMap<>();
   public static final ConcurrentHashMap<String, BlockStateWrapper_fabric> WRAPPER_BY_RESOURCE_LOCATION = new ConcurrentHashMap<>();
   public static final String AIR_STRING = "AIR";
   public static final BlockStateWrapper_fabric AIR = new BlockStateWrapper_fabric(null, null, null);
   public static final String DIRT_RESOURCE_LOCATION_STRING = "minecraft:dirt";
   public static final String WATER_RESOURCE_LOCATION_STRING = "minecraft:water";
   public static ObjectOpenHashSet<IBlockStateWrapper> rendererIgnoredBlocks = null;
   public static ObjectOpenHashSet<IBlockStateWrapper> rendererIgnoredCaveBlocks = null;
   public static ObjectOpenHashSet<IBlockStateWrapper> waterSubsurfaceReplacementBlocks = null;
   public static ObjectOpenHashSet<IBlockStateWrapper> waterSurfaceReplacementBlocks = null;
   public static IBlockStateWrapper waterBlock = null;
   private static final HashSet<class_2960> BROKEN_RESOURCE_LOCATIONS = new HashSet<>();
   @Nullable
   public final class_2680 blockState;
   private String serialString;
   private final int hashCode;
   private final int opacity;
   private byte blockMaterialId = 0;
   private final boolean isBeaconBlock;
   private final boolean isBeaconBaseBlock;
   private final boolean allowsBeaconBeamPassage;
   private final boolean renderTexture;
   private final boolean useBottomTextureForSides;
   private final boolean alwaysRasterizeTexture;
   private final boolean isSolid;
   private final boolean isLiquid;
   private final boolean allowApiColorOverride;
   private final Color beaconTintColor;
   private final Color mapColor;

   public static BlockStateWrapper_fabric fromBlockState(class_2680 blockState, ILevelWrapper levelWrapper, IBlockStateWrapper guess) {
      if (guess == null) {
         return fromBlockState(blockState, levelWrapper);
      } else {
         BlockStateWrapper_fabric wrapperGuess = (BlockStateWrapper_fabric)guess;
         class_2680 guessBlockState;
         if (isAir(wrapperGuess.blockState)) {
            guessBlockState = null;
         } else {
            guessBlockState = (class_2680)guess.getWrappedMcObject();
         }

         class_2680 inputBlockState;
         if (isAir(blockState)) {
            inputBlockState = null;
         } else {
            inputBlockState = blockState;
         }

         return guessBlockState == inputBlockState ? (BlockStateWrapper_fabric)guess : fromBlockState(blockState, levelWrapper);
      }
   }

   public static BlockStateWrapper_fabric fromBlockState(@Nullable class_2680 blockState, ILevelWrapper levelWrapper) {
      if (isAir(blockState)) {
         return AIR;
      } else {
         BlockStateWrapper_fabric existingWrapper = WRAPPER_BY_BLOCK_STATE.get(blockState);
         if (existingWrapper != null) {
            return existingWrapper;
         } else {
            synchronized (WRAPPER_BY_BLOCK_STATE) {
               existingWrapper = WRAPPER_BY_BLOCK_STATE.get(blockState);
               if (existingWrapper != null) {
                  return existingWrapper;
               } else {
                  BlockStateWrapper_fabric apiWrapper = new BlockStateWrapper_fabric(blockState, levelWrapper, null);
                  DhApiBlockStateWrapperCreatedEvent.EventParam eventParam = new DhApiBlockStateWrapperCreatedEvent.EventParam(apiWrapper);
                  ApiEventInjector.INSTANCE.fireAllEvents(DhApiBlockStateWrapperCreatedEvent.class, eventParam);
                  if (!eventParam.getOverridesSet()) {
                     WRAPPER_BY_BLOCK_STATE.putIfAbsent(blockState, apiWrapper);
                     return apiWrapper;
                  } else {
                     BlockStateWrapper_fabric returnWrapper = new BlockStateWrapper_fabric(blockState, levelWrapper, eventParam);
                     WRAPPER_BY_BLOCK_STATE.putIfAbsent(blockState, returnWrapper);
                     return returnWrapper;
                  }
               }
            }
         }
      }
   }

   private BlockStateWrapper_fabric(
      @Nullable class_2680 blockState, ILevelWrapper levelWrapper, @Nullable DhApiBlockStateWrapperCreatedEvent.EventParam overrideEventParam
   ) {
      this.blockState = blockState;
      this.serialString = serialize(blockState, levelWrapper);
      this.hashCode = Objects.hash(this.serialString);
      String lowerCaseSerial = this.serialString.toLowerCase();
      if (!this.isAir() && this.blockState != null) {
         this.isLiquid = !this.blockState.method_26227().method_15769();
      } else {
         this.isLiquid = false;
      }

      if (overrideEventParam != null && overrideEventParam.getBlockMaterial() != null) {
         this.blockMaterialId = overrideEventParam.getBlockMaterial().index;
      } else {
         this.blockMaterialId = calculateEDhApiBlockMaterialId(this.blockState, lowerCaseSerial, this.isLiquid).index;
      }

      if (overrideEventParam != null && overrideEventParam.getOpacity() != null) {
         this.opacity = overrideEventParam.getOpacity();
      } else {
         this.opacity = calculateOpacity(this.blockState, isAir(this.blockState), this.isLiquid);
      }

      if (overrideEventParam != null && overrideEventParam.getAllowApiColorOverride() != null) {
         this.allowApiColorOverride = overrideEventParam.getAllowApiColorOverride();
      } else {
         this.allowApiColorOverride = false;
      }

      if (blockState != null) {
         this.isBeaconBaseBlock = blockTagInCsv(blockState, "beacon_base_blocks");
      } else {
         this.isBeaconBaseBlock = false;
      }

      this.isBeaconBlock = lowerCaseSerial.contains("minecraft:beacon");
      Color beaconTintColor = null;
      if (this.blockState != null && !this.isBeaconBlock) {
         class_2248 block = this.blockState.method_26204();
         if (block instanceof class_4275) {
            int colorInt = ((class_4275)block).method_10622().method_7794().field_16011;
            beaconTintColor = ColorUtil.toColorObjRGB(colorInt);
         }
      }

      this.beaconTintColor = beaconTintColor;
      boolean allowsBeaconBeamPassage;
      if (this.blockState != null) {
         boolean canOcclude = getCanOcclude(this.blockState);
         boolean propagatesSkyLightDown = getPropagatesSkyLightDown(this.blockState);
         if (lowerCaseSerial.contains("minecraft:bedrock")) {
            allowsBeaconBeamPassage = true;
         } else if (lowerCaseSerial.contains("minecraft:tinted_glass")) {
            allowsBeaconBeamPassage = false;
         } else if (!propagatesSkyLightDown && canOcclude) {
            allowsBeaconBeamPassage = this.opacity != 16;
         } else {
            allowsBeaconBeamPassage = true;
         }
      } else {
         allowsBeaconBeamPassage = true;
      }

      this.allowsBeaconBeamPassage = allowsBeaconBeamPassage;
      String dontTextureNamesCsv = Config.Client.Advanced.Graphics.Texture.blocksDontRenderTextureCsv.get();
      this.renderTexture = !blockSerialInCsv(lowerCaseSerial, dontTextureNamesCsv);
      String sideBlockNamesCsv = Config.Client.Advanced.Graphics.Texture.blocksDontUseSideTextureCsv.get();
      allowsBeaconBeamPassage = blockSerialInCsv(lowerCaseSerial, sideBlockNamesCsv);
      String dontUseSideTextureTagsCsv = Config.Client.Advanced.Graphics.Texture.blockTagsDontUseSideTextureCsv.get();
      boolean hasSideIgnoreTags = blockTagInCsv(blockState, dontUseSideTextureTagsCsv);
      this.useBottomTextureForSides = hasSideIgnoreTags || allowsBeaconBeamPassage;
      String alwaysRasterNamesCsv = Config.Client.Advanced.Graphics.Texture.blocksAlwaysRasterizeTextureCsv.get();
      this.alwaysRasterizeTexture = blockSerialInCsv(lowerCaseSerial, alwaysRasterNamesCsv);
      if (this.blockState != null) {
         int mcColor = 0;
         int var13 = this.blockState.method_26205(class_2682.field_12294, class_2338.field_10980).field_16011;
         this.mapColor = ColorUtil.toColorObjRGB(var13);
      } else {
         this.mapColor = new Color(0, 0, 0, 0);
      }

      if (!this.isAir() && this.blockState != null) {
         this.isSolid = !this.blockState.method_26220(class_2682.field_12294, class_2338.field_10980).method_1110();
      } else {
         this.isSolid = false;
      }
   }

   private static EDhApiBlockMaterial calculateEDhApiBlockMaterialId(@Nullable class_2680 blockState, String lowercaseSerialString, boolean isLiquid) {
      if (isAir(blockState)) {
         return EDhApiBlockMaterial.AIR;
      } else {
         boolean isLeafBlock = blockState.method_26164(class_3481.field_15503);
         if (!isLeafBlock
            && !lowercaseSerialString.contains("bamboo")
            && !lowercaseSerialString.contains("cactus")
            && !lowercaseSerialString.contains("chorus_flower")
            && !lowercaseSerialString.contains("mushroom")) {
            boolean isLavaBlock = blockState.method_27852(class_2246.field_10164);
            if (isLavaBlock) {
               return EDhApiBlockMaterial.LAVA;
            } else {
               boolean isWaterBlock = blockState.method_27852(class_2246.field_10382);
               if (!isLiquid && !isWaterBlock) {
                  boolean isWoodSoundingBlock = blockState.method_26231() == class_2498.field_11547;
                  boolean isCherryWood = blockState.method_26231() == class_2498.field_42766;
                  if (!isWoodSoundingBlock && !lowercaseSerialString.contains("root") && !isCherryWood) {
                     boolean isMetalSoundingBlock = blockState.method_26231() == class_2498.field_11533;
                     boolean isCopperSounding = blockState.method_26231() == class_2498.field_27204
                        || blockState.method_26231() == class_2498.field_47085
                        || blockState.method_26231() == class_2498.field_47086;
                     if (isMetalSoundingBlock || isCopperSounding) {
                        return EDhApiBlockMaterial.METAL;
                     } else if (lowercaseSerialString.contains("grass_block") || lowercaseSerialString.contains("grass_slab")) {
                        return EDhApiBlockMaterial.GRASS;
                     } else if (!lowercaseSerialString.contains("dirt")
                        && !lowercaseSerialString.contains("gravel")
                        && !lowercaseSerialString.contains("mud")
                        && !lowercaseSerialString.contains("podzol")
                        && !lowercaseSerialString.contains("mycelium")) {
                        if (blockState.method_26231() != class_2498.field_29033
                           && blockState.method_26231() != class_2498.field_29034
                           && blockState.method_26231() != class_2498.field_29035
                           && blockState.method_26231() != class_2498.field_29036
                           && !lowercaseSerialString.contains("deepslate")) {
                           boolean isNetherRack = blockState.method_26164(class_3481.field_25807);
                           if (isNetherRack) {
                              return EDhApiBlockMaterial.NETHER_STONE;
                           } else if (lowercaseSerialString.contains("snow")) {
                              return EDhApiBlockMaterial.SNOW;
                           } else if (lowercaseSerialString.contains("sand")) {
                              return EDhApiBlockMaterial.SAND;
                           } else if (lowercaseSerialString.contains("terracotta")) {
                              return EDhApiBlockMaterial.TERRACOTTA;
                           } else if (lowercaseSerialString.contains("stone") || lowercaseSerialString.contains("ore")) {
                              return EDhApiBlockMaterial.STONE;
                           } else {
                              return getLightEmission(blockState) > 0 ? EDhApiBlockMaterial.ILLUMINATED : EDhApiBlockMaterial.UNKNOWN;
                           }
                        } else {
                           return EDhApiBlockMaterial.DEEPSLATE;
                        }
                     } else {
                        return EDhApiBlockMaterial.DIRT;
                     }
                  } else {
                     return EDhApiBlockMaterial.WOOD;
                  }
               } else {
                  return EDhApiBlockMaterial.WATER;
               }
            }
         } else {
            return EDhApiBlockMaterial.LEAVES;
         }
      }
   }

   private static int calculateOpacity(@Nullable class_2680 blockState, boolean isAir, boolean isLiquid) {
      boolean canOcclude = getCanOcclude(blockState);
      boolean propagatesSkyLightDown = getPropagatesSkyLightDown(blockState);
      int opacity;
      if (isAir) {
         opacity = 0;
      } else if (isLiquid && !canOcclude) {
         opacity = 1;
      } else if (propagatesSkyLightDown && !canOcclude) {
         opacity = 0;
      } else {
         opacity = 16;
      }

      return opacity;
   }

   private static boolean getCanOcclude(@Nullable class_2680 blockState) {
      boolean canOcclude = false;
      if (blockState != null) {
         canOcclude = blockState.method_26225();
      }

      return canOcclude;
   }

   private static boolean getPropagatesSkyLightDown(@Nullable class_2680 blockState) {
      boolean propagatesSkyLightDown = true;
      if (blockState != null) {
         propagatesSkyLightDown = blockState.method_26167(class_2682.field_12294, class_2338.field_10980);
      }

      return propagatesSkyLightDown;
   }

   private static boolean blockTagInCsv(@Nullable class_2680 blockState, String blockTagsCsv) {
      if (blockState == null) {
         return false;
      } else {
         Stream<class_6862<class_2248>> tags = blockState.method_40144();
         blockTagsCsv = blockTagsCsv.toLowerCase();
         List<String> sideBlockTagList = Arrays.asList(blockTagsCsv.split(","));
         return tags.anyMatch(tag -> {
            String lowerTag = tag.comp_327().method_12832().toLowerCase();

            for (int i = 0; i < sideBlockTagList.size(); i++) {
               String sideBlockTag = sideBlockTagList.get(i);
               if (lowerTag.contains(sideBlockTag)) {
                  return true;
               }
            }

            return false;
         });
      }
   }

   private static boolean blockSerialInCsv(String lowerCaseSerial, String blockNameCsv) {
      boolean blockMatches = false;
      blockNameCsv = blockNameCsv.toLowerCase();
      List<String> blockNameList = Arrays.asList(blockNameCsv.split(","));

      for (int i = 0; i < blockNameList.size(); i++) {
         String baseBlockName = blockNameList.get(i);
         if (lowerCaseSerial.contains(baseBlockName)) {
            blockMatches = true;
            break;
         }
      }

      return blockMatches;
   }

   public static ObjectOpenHashSet<IBlockStateWrapper> getRendererIgnoredBlocks(ILevelWrapper levelWrapper) {
      if (rendererIgnoredBlocks != null) {
         return rendererIgnoredBlocks;
      } else {
         ObjectOpenHashSet<String> baseIgnoredBlock = new ObjectOpenHashSet();
         baseIgnoredBlock.add("AIR");
         rendererIgnoredBlocks = getAllBlockWrappers(Config.Client.Advanced.Graphics.Culling.ignoredRenderBlockCsv, baseIgnoredBlock, levelWrapper);
         return rendererIgnoredBlocks;
      }
   }

   public static ObjectOpenHashSet<IBlockStateWrapper> getRendererIgnoredCaveBlocks(ILevelWrapper levelWrapper) {
      if (rendererIgnoredCaveBlocks != null) {
         return rendererIgnoredCaveBlocks;
      } else {
         ObjectOpenHashSet<String> baseIgnoredBlock = new ObjectOpenHashSet();
         baseIgnoredBlock.add("AIR");
         rendererIgnoredCaveBlocks = getAllBlockWrappers(Config.Client.Advanced.Graphics.Culling.ignoredRenderCaveBlockCsv, baseIgnoredBlock, levelWrapper);
         return rendererIgnoredCaveBlocks;
      }
   }

   public static ObjectOpenHashSet<IBlockStateWrapper> getWaterSurfaceReplacementBlocks(ILevelWrapper levelWrapper) {
      if (waterSurfaceReplacementBlocks != null) {
         return waterSurfaceReplacementBlocks;
      } else {
         ObjectOpenHashSet<String> baseIgnoredBlockResourceSet = new ObjectOpenHashSet();
         waterSurfaceReplacementBlocks = getAllBlockWrappers(
            Config.Client.Advanced.Graphics.Culling.waterSurfaceBlockReplacementCsv, baseIgnoredBlockResourceSet, levelWrapper
         );
         waterSubsurfaceReplacementBlocks.remove(AIR);
         return waterSurfaceReplacementBlocks;
      }
   }

   public static ObjectOpenHashSet<IBlockStateWrapper> getWaterSubsurfaceReplacementBlocks(ILevelWrapper levelWrapper) {
      if (waterSubsurfaceReplacementBlocks != null) {
         return waterSubsurfaceReplacementBlocks;
      } else {
         ObjectOpenHashSet<String> baseIgnoredBlockResourceSet = new ObjectOpenHashSet();
         waterSubsurfaceReplacementBlocks = getAllBlockWrappers(
            Config.Client.Advanced.Graphics.Culling.waterSubSurfaceBlockReplacementCsv, baseIgnoredBlockResourceSet, levelWrapper
         );
         waterSubsurfaceReplacementBlocks.remove(AIR);
         return waterSubsurfaceReplacementBlocks;
      }
   }

   public static IBlockStateWrapper getWaterBlockStateWrapper(ILevelWrapper levelWrapper) {
      if (waterBlock != null) {
         return waterBlock;
      } else {
         waterBlock = WrapperFactory_fabric.INSTANCE.deserializeBlockStateWrapperOrGetDefault("minecraft:water", levelWrapper);
         return waterBlock;
      }
   }

   private static ObjectOpenHashSet<IBlockStateWrapper> getAllBlockWrappers(
      ConfigEntry<String> config, ObjectOpenHashSet<String> baseResourceLocations, ILevelWrapper levelWrapper
   ) {
      ObjectOpenHashSet<String> blockStringList = new ObjectOpenHashSet();
      if (baseResourceLocations != null) {
         blockStringList.addAll(baseResourceLocations);
      }

      String ignoreBlockCsv = config.get();
      if (ignoreBlockCsv != null) {
         blockStringList.addAll(Arrays.asList(ignoreBlockCsv.split(",")));
      }

      return getAllBlockWrappers(blockStringList, levelWrapper);
   }

   private static ObjectOpenHashSet<IBlockStateWrapper> getAllBlockWrappers(ObjectOpenHashSet<String> blockResourceLocationSet, ILevelWrapper levelWrapper) {
      ObjectOpenHashSet<IBlockStateWrapper> blockStateWrappers = new ObjectOpenHashSet();
      ObjectIterator var3 = blockResourceLocationSet.iterator();

      while (var3.hasNext()) {
         String blockResourceLocation = (String)var3.next();

         try {
            if (blockResourceLocation != null) {
               String cleanedResourceLocation = blockResourceLocation.trim();
               if (cleanedResourceLocation.length() != 0) {
                  BlockStateWrapper_fabric defaultBlockStateToIgnore = (BlockStateWrapper_fabric)deserialize(cleanedResourceLocation, levelWrapper);
                  blockStateWrappers.add(defaultBlockStateToIgnore);
                  if (defaultBlockStateToIgnore != AIR) {
                     for (class_2680 blockState : defaultBlockStateToIgnore.blockState.method_26204().method_9595().method_11662()) {
                        BlockStateWrapper_fabric newBlockToIgnore = fromBlockState(blockState, levelWrapper);
                        blockStateWrappers.add(newBlockToIgnore);
                     }
                  } else {
                     blockStateWrappers.add(AIR);
                  }
               }
            }
         } catch (IOException var11) {
            LOGGER.warn("Unable to deserialize block with the resource location: [" + blockResourceLocation + "]. Error: " + var11.getMessage(), var11);
         } catch (Exception var12) {
            LOGGER.warn("Unexpected error deserializing block with the resource location: [" + blockResourceLocation + "]. Error: " + var12.getMessage(), var12);
         }
      }

      return blockStateWrappers;
   }

   public static void clearCachedIgnoreBlocks() {
      rendererIgnoredBlocks = null;
      rendererIgnoredCaveBlocks = null;
      waterSurfaceReplacementBlocks = null;
      waterSubsurfaceReplacementBlocks = null;
      waterBlock = null;
   }

   @Override
   public int getOpacity() {
      return this.opacity;
   }

   @Override
   public int getLightEmission() {
      return getLightEmission(this.blockState);
   }

   public static int getLightEmission(class_2680 blockState) {
      return blockState == null ? 0 : blockState.method_26213();
   }

   @Override
   public String getSerialString() {
      return this.serialString;
   }

   @Override
   public Object getWrappedMcObject() {
      return this.blockState;
   }

   @Override
   public boolean isAir() {
      return isAir(this.blockState);
   }

   public static boolean isAir(class_2680 blockState) {
      return blockState == null ? true : blockState.method_26215();
   }

   @Override
   public boolean isSolid() {
      return this.isSolid;
   }

   @Override
   public boolean isLiquid() {
      return this.isLiquid;
   }

   @Override
   public boolean isBeaconBlock() {
      return this.isBeaconBlock;
   }

   @Override
   public boolean isBeaconBaseBlock() {
      return this.isBeaconBaseBlock;
   }

   @Override
   public boolean isBeaconTintBlock() {
      return this.beaconTintColor != null;
   }

   @Override
   public boolean allowsBeaconBeamPassage() {
      return this.allowsBeaconBeamPassage;
   }

   @Override
   public boolean allowApiColorOverride() {
      return this.allowApiColorOverride;
   }

   @Override
   public boolean renderTexture() {
      return this.renderTexture;
   }

   @Override
   public boolean useBottomTextureForSides() {
      return this.useBottomTextureForSides;
   }

   @Override
   public boolean alwaysRasterizeTexture() {
      return this.alwaysRasterizeTexture;
   }

   @Override
   public Color getMapColor() {
      return this.mapColor;
   }

   @Override
   public Color getBeaconTintColor() {
      return this.beaconTintColor;
   }

   @Override
   public byte getMaterialId() {
      return this.blockMaterialId;
   }

   private static String serialize(class_2680 blockState, ILevelWrapper levelWrapper) {
      if (blockState == null) {
         return "AIR";
      } else {
         class_1937 level = (class_1937)levelWrapper.getWrappedMcObject();
         class_5455 registryAccess = level.method_30349();
         class_2960 resourceLocation = registryAccess.method_30530(class_7924.field_41254).method_10221(blockState.method_26204());
         if (resourceLocation == null) {
            LOGGER.warn("No ResourceLocation found, unable to serialize: " + blockState);
            return "AIR";
         } else {
            return resourceLocation.method_12836() + ":" + resourceLocation.method_12832() + "_STATE_" + serializeBlockStateProperties(blockState);
         }
      }
   }

   public static IBlockStateWrapper deserialize(String resourceStateString, ILevelWrapper levelWrapper) throws IOException {
      String finalResourceStateString = resourceStateString;
      if (resourceStateString.equals("AIR") || resourceStateString.equals("")) {
         return AIR;
      } else if (WRAPPER_BY_RESOURCE_LOCATION.containsKey(resourceStateString)) {
         return WRAPPER_BY_RESOURCE_LOCATION.get(resourceStateString);
      } else {
         BlockStateWrapper_fabric foundWrapper = AIR;

         BlockStateWrapper_fabric foundState;
         try {
            String blockStatePropertiesString = null;
            int stateSeparatorIndex = resourceStateString.indexOf("_STATE_");
            if (stateSeparatorIndex != -1) {
               blockStatePropertiesString = resourceStateString.substring(stateSeparatorIndex + "_STATE_".length());
               resourceStateString = resourceStateString.substring(0, stateSeparatorIndex);
            }

            int separatorIndex = resourceStateString.indexOf(":");
            if (separatorIndex == -1) {
               throw new IOException("Unable to parse Resource Location out of string: [" + resourceStateString + "].");
            }

            class_2960 resourceLocation;
            try {
               resourceLocation = class_2960.method_60655(resourceStateString.substring(0, separatorIndex), resourceStateString.substring(separatorIndex + 1));
            } catch (Exception var20) {
               throw new IOException("No Resource Location found for the string: [" + resourceStateString + "] Error: [" + var20.getMessage() + "].");
            }

            try {
               LodUtil.assertTrue(levelWrapper != null && levelWrapper.getWrappedMcObject() != null);
               class_1937 level = (class_1937)levelWrapper.getWrappedMcObject();
               class_5455 registryAccess = level.method_30349();
               class_2248 block = (class_2248)registryAccess.method_30530(class_7924.field_41254).method_10223(resourceLocation);
               if (block != null) {
                  class_2680 foundStatex = null;
                  if (blockStatePropertiesString != null) {
                     for (class_2680 possibleState : block.method_9595().method_11662()) {
                        String possibleStatePropertiesString = serializeBlockStateProperties(possibleState);
                        if (possibleStatePropertiesString.equals(blockStatePropertiesString)) {
                           foundStatex = possibleState;
                           break;
                        }
                     }
                  }

                  if (foundStatex == null) {
                     if (blockStatePropertiesString != null && !BROKEN_RESOURCE_LOCATIONS.contains(resourceLocation)) {
                        BROKEN_RESOURCE_LOCATIONS.add(resourceLocation);
                        LOGGER.warn(
                           "Unable to find BlockState for Block ["
                              + resourceLocation
                              + "] with properties: ["
                              + blockStatePropertiesString
                              + "]. Using the default block state."
                        );
                     }

                     foundStatex = block.method_9564();
                  }

                  return fromBlockState(foundStatex, levelWrapper);
               }

               if (!BROKEN_RESOURCE_LOCATIONS.contains(resourceLocation)) {
                  BROKEN_RESOURCE_LOCATIONS.add(resourceLocation);
                  LOGGER.warn(
                     "Unable to find BlockState with the resourceLocation ["
                        + resourceLocation
                        + "] and properties: ["
                        + blockStatePropertiesString
                        + "]. Air will be used instead, some data may be lost."
                  );
               }

               foundState = AIR;
            } catch (Exception var21) {
               throw new IOException(
                  "Failed to deserialize the string [" + finalResourceStateString + "] into a BlockStateWrapper: " + var21.getMessage(), var21
               );
            }
         } finally {
            WRAPPER_BY_RESOURCE_LOCATION.putIfAbsent(finalResourceStateString, foundWrapper);
            if (foundWrapper != AIR) {
               WRAPPER_BY_BLOCK_STATE.putIfAbsent(foundWrapper.blockState, foundWrapper);
            }
         }

         return foundState;
      }
   }

   private static String serializeBlockStateProperties(class_2680 blockState) {
      Collection<class_2769<?>> blockPropertyCollection = blockState.method_28501();
      List<class_2769<?>> sortedBlockPropteryList = new ArrayList<>(blockPropertyCollection);
      sortedBlockPropteryList.sort((a, b) -> a.method_11899().compareTo(b.method_11899()));
      StringBuilder stringBuilder = new StringBuilder();

      for (class_2769<?> property : sortedBlockPropteryList) {
         String propertyName = property.method_11899();
         String value = "NULL";
         if (blockState.method_28498(property)) {
            value = blockState.method_11654(property).toString();
         }

         stringBuilder.append("{");
         stringBuilder.append(propertyName).append(":").append(value);
         stringBuilder.append("}");
      }

      return stringBuilder.toString();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         BlockStateWrapper_fabric that = (BlockStateWrapper_fabric)obj;
         return Objects.equals(this.getSerialString(), that.getSerialString());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   @Override
   public String toString() {
      return this.getSerialString();
   }
}
