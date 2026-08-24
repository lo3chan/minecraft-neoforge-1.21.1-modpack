package com.seibel.distanthorizons.common.wrappers.block;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiBlockMaterial;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBlockStateWrapperCreatedEvent;
import com.seibel.distanthorizons.common.wrappers.WrapperFactory_neoforge;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class BlockStateWrapper_neoforge implements IBlockStateWrapper {
   public static final String RESOURCE_LOCATION_SEPARATOR = ":";
   public static final String STATE_STRING_SEPARATOR = "_STATE_";
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final ConcurrentHashMap<BlockState, BlockStateWrapper_neoforge> WRAPPER_BY_BLOCK_STATE = new ConcurrentHashMap<>();
   public static final ConcurrentHashMap<String, BlockStateWrapper_neoforge> WRAPPER_BY_RESOURCE_LOCATION = new ConcurrentHashMap<>();
   public static final String AIR_STRING = "AIR";
   public static final BlockStateWrapper_neoforge AIR = new BlockStateWrapper_neoforge(null, null, null);
   public static final String DIRT_RESOURCE_LOCATION_STRING = "minecraft:dirt";
   public static final String WATER_RESOURCE_LOCATION_STRING = "minecraft:water";
   public static ObjectOpenHashSet<IBlockStateWrapper> rendererIgnoredBlocks = null;
   public static ObjectOpenHashSet<IBlockStateWrapper> rendererIgnoredCaveBlocks = null;
   public static ObjectOpenHashSet<IBlockStateWrapper> waterSubsurfaceReplacementBlocks = null;
   public static ObjectOpenHashSet<IBlockStateWrapper> waterSurfaceReplacementBlocks = null;
   public static IBlockStateWrapper waterBlock = null;
   private static final HashSet<ResourceLocation> BROKEN_RESOURCE_LOCATIONS = new HashSet<>();
   @Nullable
   public final BlockState blockState;
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

   public static BlockStateWrapper_neoforge fromBlockState(BlockState blockState, ILevelWrapper levelWrapper, IBlockStateWrapper guess) {
      if (guess == null) {
         return fromBlockState(blockState, levelWrapper);
      } else {
         BlockStateWrapper_neoforge wrapperGuess = (BlockStateWrapper_neoforge)guess;
         BlockState guessBlockState;
         if (isAir(wrapperGuess.blockState)) {
            guessBlockState = null;
         } else {
            guessBlockState = (BlockState)guess.getWrappedMcObject();
         }

         BlockState inputBlockState;
         if (isAir(blockState)) {
            inputBlockState = null;
         } else {
            inputBlockState = blockState;
         }

         return guessBlockState == inputBlockState ? (BlockStateWrapper_neoforge)guess : fromBlockState(blockState, levelWrapper);
      }
   }

   public static BlockStateWrapper_neoforge fromBlockState(@Nullable BlockState blockState, ILevelWrapper levelWrapper) {
      if (isAir(blockState)) {
         return AIR;
      } else {
         BlockStateWrapper_neoforge existingWrapper = WRAPPER_BY_BLOCK_STATE.get(blockState);
         if (existingWrapper != null) {
            return existingWrapper;
         } else {
            synchronized (WRAPPER_BY_BLOCK_STATE) {
               existingWrapper = WRAPPER_BY_BLOCK_STATE.get(blockState);
               if (existingWrapper != null) {
                  return existingWrapper;
               } else {
                  BlockStateWrapper_neoforge apiWrapper = new BlockStateWrapper_neoforge(blockState, levelWrapper, null);
                  DhApiBlockStateWrapperCreatedEvent.EventParam eventParam = new DhApiBlockStateWrapperCreatedEvent.EventParam(apiWrapper);
                  ApiEventInjector.INSTANCE.fireAllEvents(DhApiBlockStateWrapperCreatedEvent.class, eventParam);
                  if (!eventParam.getOverridesSet()) {
                     WRAPPER_BY_BLOCK_STATE.putIfAbsent(blockState, apiWrapper);
                     return apiWrapper;
                  } else {
                     BlockStateWrapper_neoforge returnWrapper = new BlockStateWrapper_neoforge(blockState, levelWrapper, eventParam);
                     WRAPPER_BY_BLOCK_STATE.putIfAbsent(blockState, returnWrapper);
                     return returnWrapper;
                  }
               }
            }
         }
      }
   }

   private BlockStateWrapper_neoforge(
      @Nullable BlockState blockState, ILevelWrapper levelWrapper, @Nullable DhApiBlockStateWrapperCreatedEvent.EventParam overrideEventParam
   ) {
      this.blockState = blockState;
      this.serialString = serialize(blockState, levelWrapper);
      this.hashCode = Objects.hash(this.serialString);
      String lowerCaseSerial = this.serialString.toLowerCase();
      if (!this.isAir() && this.blockState != null) {
         this.isLiquid = !this.blockState.getFluidState().isEmpty();
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
         Block block = this.blockState.getBlock();
         if (block instanceof BeaconBeamBlock) {
            int colorInt = ((BeaconBeamBlock)block).getColor().getMapColor().col;
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
         int var13 = this.blockState.getMapColor(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).col;
         this.mapColor = ColorUtil.toColorObjRGB(var13);
      } else {
         this.mapColor = new Color(0, 0, 0, 0);
      }

      if (!this.isAir() && this.blockState != null) {
         this.isSolid = !this.blockState.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).isEmpty();
      } else {
         this.isSolid = false;
      }
   }

   private static EDhApiBlockMaterial calculateEDhApiBlockMaterialId(@Nullable BlockState blockState, String lowercaseSerialString, boolean isLiquid) {
      if (isAir(blockState)) {
         return EDhApiBlockMaterial.AIR;
      } else {
         boolean isLeafBlock = blockState.is(BlockTags.LEAVES);
         if (!isLeafBlock
            && !lowercaseSerialString.contains("bamboo")
            && !lowercaseSerialString.contains("cactus")
            && !lowercaseSerialString.contains("chorus_flower")
            && !lowercaseSerialString.contains("mushroom")) {
            boolean isLavaBlock = blockState.is(Blocks.LAVA);
            if (isLavaBlock) {
               return EDhApiBlockMaterial.LAVA;
            } else {
               boolean isWaterBlock = blockState.is(Blocks.WATER);
               if (!isLiquid && !isWaterBlock) {
                  boolean isWoodSoundingBlock = blockState.getSoundType() == SoundType.WOOD;
                  boolean isCherryWood = blockState.getSoundType() == SoundType.CHERRY_WOOD;
                  if (!isWoodSoundingBlock && !lowercaseSerialString.contains("root") && !isCherryWood) {
                     boolean isMetalSoundingBlock = blockState.getSoundType() == SoundType.METAL;
                     boolean isCopperSounding = blockState.getSoundType() == SoundType.COPPER
                        || blockState.getSoundType() == SoundType.COPPER_BULB
                        || blockState.getSoundType() == SoundType.COPPER_GRATE;
                     if (isMetalSoundingBlock || isCopperSounding) {
                        return EDhApiBlockMaterial.METAL;
                     } else if (lowercaseSerialString.contains("grass_block") || lowercaseSerialString.contains("grass_slab")) {
                        return EDhApiBlockMaterial.GRASS;
                     } else if (!lowercaseSerialString.contains("dirt")
                        && !lowercaseSerialString.contains("gravel")
                        && !lowercaseSerialString.contains("mud")
                        && !lowercaseSerialString.contains("podzol")
                        && !lowercaseSerialString.contains("mycelium")) {
                        if (blockState.getSoundType() != SoundType.DEEPSLATE
                           && blockState.getSoundType() != SoundType.DEEPSLATE_BRICKS
                           && blockState.getSoundType() != SoundType.DEEPSLATE_TILES
                           && blockState.getSoundType() != SoundType.POLISHED_DEEPSLATE
                           && !lowercaseSerialString.contains("deepslate")) {
                           boolean isNetherRack = blockState.is(BlockTags.BASE_STONE_NETHER);
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

   private static int calculateOpacity(@Nullable BlockState blockState, boolean isAir, boolean isLiquid) {
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

   private static boolean getCanOcclude(@Nullable BlockState blockState) {
      boolean canOcclude = false;
      if (blockState != null) {
         canOcclude = blockState.canOcclude();
      }

      return canOcclude;
   }

   private static boolean getPropagatesSkyLightDown(@Nullable BlockState blockState) {
      boolean propagatesSkyLightDown = true;
      if (blockState != null) {
         propagatesSkyLightDown = blockState.propagatesSkylightDown(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
      }

      return propagatesSkyLightDown;
   }

   private static boolean blockTagInCsv(@Nullable BlockState blockState, String blockTagsCsv) {
      if (blockState == null) {
         return false;
      } else {
         Stream<TagKey<Block>> tags = blockState.getTags();
         blockTagsCsv = blockTagsCsv.toLowerCase();
         List<String> sideBlockTagList = Arrays.asList(blockTagsCsv.split(","));
         return tags.anyMatch(tag -> {
            String lowerTag = tag.location().getPath().toLowerCase();

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
         waterBlock = WrapperFactory_neoforge.INSTANCE.deserializeBlockStateWrapperOrGetDefault("minecraft:water", levelWrapper);
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
                  BlockStateWrapper_neoforge defaultBlockStateToIgnore = (BlockStateWrapper_neoforge)deserialize(cleanedResourceLocation, levelWrapper);
                  blockStateWrappers.add(defaultBlockStateToIgnore);
                  if (defaultBlockStateToIgnore != AIR) {
                     for (BlockState blockState : defaultBlockStateToIgnore.blockState.getBlock().getStateDefinition().getPossibleStates()) {
                        BlockStateWrapper_neoforge newBlockToIgnore = fromBlockState(blockState, levelWrapper);
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

   public static int getLightEmission(BlockState blockState) {
      return blockState == null ? 0 : blockState.getLightEmission();
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

   public static boolean isAir(BlockState blockState) {
      return blockState == null ? true : blockState.isAir();
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

   private static String serialize(BlockState blockState, ILevelWrapper levelWrapper) {
      if (blockState == null) {
         return "AIR";
      } else {
         Level level = (Level)levelWrapper.getWrappedMcObject();
         RegistryAccess registryAccess = level.registryAccess();
         ResourceLocation resourceLocation = registryAccess.registryOrThrow(Registries.BLOCK).getKey(blockState.getBlock());
         if (resourceLocation == null) {
            LOGGER.warn("No ResourceLocation found, unable to serialize: " + blockState);
            return "AIR";
         } else {
            return resourceLocation.getNamespace() + ":" + resourceLocation.getPath() + "_STATE_" + serializeBlockStateProperties(blockState);
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
         BlockStateWrapper_neoforge foundWrapper = AIR;

         BlockStateWrapper_neoforge foundState;
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

            ResourceLocation resourceLocation;
            try {
               resourceLocation = ResourceLocation.fromNamespaceAndPath(
                  resourceStateString.substring(0, separatorIndex), resourceStateString.substring(separatorIndex + 1)
               );
            } catch (Exception var20) {
               throw new IOException("No Resource Location found for the string: [" + resourceStateString + "] Error: [" + var20.getMessage() + "].");
            }

            try {
               LodUtil.assertTrue(levelWrapper != null && levelWrapper.getWrappedMcObject() != null);
               Level level = (Level)levelWrapper.getWrappedMcObject();
               RegistryAccess registryAccess = level.registryAccess();
               Block block = (Block)registryAccess.registryOrThrow(Registries.BLOCK).get(resourceLocation);
               if (block != null) {
                  BlockState foundStatex = null;
                  if (blockStatePropertiesString != null) {
                     for (BlockState possibleState : block.getStateDefinition().getPossibleStates()) {
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

                     foundStatex = block.defaultBlockState();
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

   private static String serializeBlockStateProperties(BlockState blockState) {
      Collection<Property<?>> blockPropertyCollection = blockState.getProperties();
      List<Property<?>> sortedBlockPropteryList = new ArrayList<>(blockPropertyCollection);
      sortedBlockPropteryList.sort((a, b) -> a.getName().compareTo(b.getName()));
      StringBuilder stringBuilder = new StringBuilder();

      for (Property<?> property : sortedBlockPropteryList) {
         String propertyName = property.getName();
         String value = "NULL";
         if (blockState.hasProperty(property)) {
            value = blockState.getValue(property).toString();
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
         BlockStateWrapper_neoforge that = (BlockStateWrapper_neoforge)obj;
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
