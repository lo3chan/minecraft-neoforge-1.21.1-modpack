package com.finndog.moogs_structures.utils;

import com.finndog.moogs_structures.MoogsStructuresCommon;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public final class GeneralUtils {
   private static final Map<BlockState, Boolean> IS_FULLCUBE_MAP = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<GeneralUtils.HeightKey, Integer> CACHED_HEIGHT = new ConcurrentHashMap<>(2048);

   private GeneralUtils() {
   }

   public static <T> T getRandomEntry(List<Pair<T, Integer>> rlList, RandomSource random) {
      double totalWeight = 0.0;

      for (Pair<T, Integer> pair : rlList) {
         totalWeight += ((Integer)pair.getSecond()).intValue();
      }

      int index = 0;

      for (double randomWeightPicked = random.nextFloat() * totalWeight; index < rlList.size() - 1; index++) {
         randomWeightPicked -= ((Integer)rlList.get(index).getSecond()).intValue();
         if (randomWeightPicked <= 0.0) {
            break;
         }
      }

      return (T)rlList.get(index).getFirst();
   }

   public static boolean isFullCube(BlockGetter world, BlockPos pos, BlockState state) {
      return state == null ? false : IS_FULLCUBE_MAP.computeIfAbsent(state, stateIn -> Block.isShapeFullBlock(stateIn.getOcclusionShape(world, pos)));
   }

   public static BlockState copyBlockProperties(BlockState oldBlockState, BlockState newBlockState) {
      for (Property<?> property : oldBlockState.getProperties()) {
         if (newBlockState.hasProperty(property)) {
            newBlockState = getStateWithProperty(newBlockState, oldBlockState, property);
         }
      }

      return newBlockState;
   }

   public static <T extends Comparable<T>> BlockState getStateWithProperty(BlockState state, BlockState stateToCopy, Property<T> property) {
      return (BlockState)state.setValue(property, stateToCopy.getValue(property));
   }

   public static ItemStack enchantRandomly(RegistryAccess registryAccess, RandomSource random, ItemStack itemToEnchant, float chance) {
      if (random.nextFloat() < chance) {
         List<Reference<Enchantment>> list = registryAccess.registryOrThrow(Registries.ENCHANTMENT)
            .holders()
            .filter(holder -> ((Enchantment)holder.value()).canEnchant(itemToEnchant))
            .toList();
         if (!list.isEmpty()) {
            Reference<Enchantment> enchantment = list.get(random.nextInt(list.size()));
            int enchantmentLevel = random.nextInt(
               Mth.nextInt(random, ((Enchantment)enchantment.value()).getMinLevel(), ((Enchantment)enchantment.value()).getMaxLevel()) + 1
            );
            itemToEnchant.enchant(enchantment, enchantmentLevel);
         }
      }

      return itemToEnchant;
   }

   public static int getMaxTerrainLimit(ChunkGenerator chunkGenerator) {
      return chunkGenerator.getMinY() + chunkGenerator.getGenDepth();
   }

   public static BlockPos getHighestLand(
      ChunkGenerator chunkGenerator, RandomState randomState, BoundingBox boundingBox, LevelHeightAccessor heightLimitView, boolean canBeOnLiquid
   ) {
      MutableBlockPos mutable = new MutableBlockPos()
         .set(boundingBox.getCenter().getX(), getMaxTerrainLimit(chunkGenerator) - 40, boundingBox.getCenter().getZ());
      NoiseColumn blockView = chunkGenerator.getBaseColumn(mutable.getX(), mutable.getZ(), heightLimitView, randomState);

      while (mutable.getY() > chunkGenerator.getSeaLevel()) {
         BlockState currentBlockstate = blockView.getBlock(mutable.getY());
         if (currentBlockstate.canOcclude()) {
            if (blockView.getBlock(mutable.getY() + 3).isAir() && (canBeOnLiquid ? !currentBlockstate.isAir() : currentBlockstate.canOcclude())) {
               return mutable;
            }

            mutable.move(Direction.DOWN);
         } else {
            mutable.move(Direction.DOWN);
         }
      }

      return mutable;
   }

   public static BlockPos getLowestLand(
      ChunkGenerator chunkGenerator, RandomState randomState, BoundingBox boundingBox, LevelHeightAccessor heightLimitView, boolean canBeOnLiquid
   ) {
      MutableBlockPos mutable = new MutableBlockPos().set(boundingBox.getCenter().getX(), chunkGenerator.getSeaLevel() + 1, boundingBox.getCenter().getZ());
      NoiseColumn blockView = chunkGenerator.getBaseColumn(mutable.getX(), mutable.getZ(), heightLimitView, randomState);

      for (BlockState currentBlockstate = blockView.getBlock(mutable.getY());
         mutable.getY() <= getMaxTerrainLimit(chunkGenerator) - 40;
         currentBlockstate = blockView.getBlock(mutable.getY())
      ) {
         if ((canBeOnLiquid ? !currentBlockstate.isAir() : currentBlockstate.canOcclude())
            && blockView.getBlock(mutable.getY() + 1).isAir()
            && blockView.getBlock(mutable.getY() + 5).isAir()) {
            mutable.move(Direction.UP);
            return mutable;
         }

         mutable.move(Direction.UP);
      }

      return mutable.set(mutable.getX(), chunkGenerator.getSeaLevel(), mutable.getZ());
   }

   public static int getFirstLandYFromPos(LevelReader worldView, BlockPos pos) {
      MutableBlockPos mutable = new MutableBlockPos();
      mutable.set(pos);
      ChunkAccess currentChunk = worldView.getChunk(mutable);

      for (BlockState currentState = currentChunk.getBlockState(mutable);
         mutable.getY() >= worldView.getMinBuildHeight() && isReplaceableByStructures(currentState);
         currentState = currentChunk.getBlockState(mutable)
      ) {
         mutable.move(Direction.DOWN);
      }

      return mutable.getY();
   }

   private static boolean isReplaceableByStructures(BlockState blockState) {
      return blockState.isAir() || !blockState.getFluidState().isEmpty() || blockState.is(BlockTags.REPLACEABLE_BY_TREES);
   }

   public static void centerAllPieces(BlockPos targetPos, List<? extends StructurePiece> pieces) {
      if (!pieces.isEmpty()) {
         Vec3i structureCenter = pieces.get(0).getBoundingBox().getCenter();
         int xOffset = targetPos.getX() - structureCenter.getX();
         int zOffset = targetPos.getZ() - structureCenter.getZ();

         for (StructurePiece structurePiece : pieces) {
            structurePiece.move(xOffset, 0, zOffset);
         }
      }
   }

   public static boolean canJigsawsAttach(StructureBlockInfo jigsaw1, StructureBlockInfo jigsaw2) {
      FrontAndTop prop1 = (FrontAndTop)jigsaw1.state().getValue(JigsawBlock.ORIENTATION);
      FrontAndTop prop2 = (FrontAndTop)jigsaw2.state().getValue(JigsawBlock.ORIENTATION);
      return prop1.front() == prop2.front().getOpposite()
         && (prop1.top() == prop2.top() || isRollableJoint(jigsaw1, prop1))
         && getStringMicroOptimised(jigsaw1.nbt(), "target").equals(getStringMicroOptimised(jigsaw2.nbt(), "name"));
   }

   private static boolean isRollableJoint(StructureBlockInfo jigsaw1, FrontAndTop prop1) {
      String joint = getStringMicroOptimised(jigsaw1.nbt(), "joint");
      return !joint.equals("rollable") && !joint.equals("aligned") ? !prop1.front().getAxis().isHorizontal() : joint.equals("rollable");
   }

   public static String getStringMicroOptimised(CompoundTag tag, String key) {
      return tag.get(key) instanceof StringTag stringTag ? stringTag.getAsString() : "";
   }

   public static Map<ResourceLocation, List<JsonElement>> getAllDatapacksJSONElement(
      ResourceManager resourceManager, Gson gson, String dataType, int fileSuffixLength
   ) {
      Map<ResourceLocation, List<JsonElement>> map = new HashMap<>();
      int dataTypeLength = dataType.length() + 1;

      for (Entry<ResourceLocation, List<Resource>> resourceStackEntry : resourceManager.listResourceStacks(
            dataType, fileString -> fileString.toString().endsWith(".json")
         )
         .entrySet()) {
         String identifierPath = resourceStackEntry.getKey().getPath();
         ResourceLocation fileID = ResourceLocation.fromNamespaceAndPath(
            resourceStackEntry.getKey().getNamespace(), identifierPath.substring(dataTypeLength, identifierPath.length() - fileSuffixLength)
         );

         try {
            for (Resource resource : resourceStackEntry.getValue()) {
               InputStream fileStream = resource.open();

               try (Reader bufferedReader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8))) {
                  JsonElement countsJSONElement = (JsonElement)GsonHelper.fromJson(gson, bufferedReader, JsonElement.class);
                  if (countsJSONElement != null) {
                     if (!map.containsKey(fileID)) {
                        map.put(fileID, new ArrayList<>());
                     }

                     map.get(fileID).add(countsJSONElement);
                  } else {
                     MoogsStructuresCommon.LOGGER
                        .error(
                           "(Moog's Structure Lib {} MERGER) Couldn't load data file {} from {} as it's null or empty", dataType, fileID, resourceStackEntry
                        );
                  }
               }
            }
         } catch (IOException | JsonParseException | IllegalArgumentException var18) {
            MoogsStructuresCommon.LOGGER
               .error("(Moog's Structure Lib {} MERGER) Couldn't parse data file {} from {}", dataType, fileID, resourceStackEntry, var18);
         }
      }

      return map;
   }

   public static int getCachedFreeHeight(
      ChunkGenerator chunkGenerator, int x, int z, Types types, LevelHeightAccessor levelHeightAccessor, RandomState randomState
   ) {
      GeneralUtils.HeightKey key = new GeneralUtils.HeightKey(chunkGenerator, x, z);
      Integer y = CACHED_HEIGHT.get(key);
      if (y == null) {
         if (CACHED_HEIGHT.size() >= 2048) {
            CACHED_HEIGHT.clear();
         }

         y = chunkGenerator.getFirstFreeHeight(x, z, types, levelHeightAccessor, randomState);
         CACHED_HEIGHT.put(key, y);
      }

      return y;
   }

   private record HeightKey(ChunkGenerator chunkGenerator, int x, int z) {
   }
}
