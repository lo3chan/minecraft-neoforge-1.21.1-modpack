package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.config.GrassConfig;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class HiddenGrass {
   public static final Set<ResourceLocation> BLOCKS = Set.of(
      ResourceLocation.withDefaultNamespace("short_grass"),
      ResourceLocation.withDefaultNamespace("fern"),
      ResourceLocation.withDefaultNamespace("tall_grass"),
      ResourceLocation.withDefaultNamespace("large_fern")
   );
   private static final Map<ModelResourceLocation, BakedModel> ORIGINAL_PLANT_MODELS = new ConcurrentHashMap<>();

   public static boolean isSwayingPlant(BlockState state) {
      return isSwayingPlant(state.getBlock());
   }

   public static boolean isSwayingPlant(Block block) {
      return canBeSwayingPlant(block) && !GrassConfig.isPlantBlacklisted(block);
   }

   public static boolean canBeSwayingPlant(BlockState state) {
      return canBeSwayingPlant(state.getBlock());
   }

   public static boolean canBeSwayingPlant(Block block) {
      return block instanceof BushBlock && !isBladeGrassPlant(block);
   }

   public static boolean isBladeGrassPlant(BlockState state) {
      return isBladeGrassPlant(state.getBlock());
   }

   private static boolean isBladeGrassPlant(Block block) {
      return GrassConfig.grassPlantsAsBlades && isConvertibleGrassPlant(block);
   }

   private static boolean isConvertibleGrassPlant(Block block) {
      return block == Blocks.SHORT_GRASS || block == Blocks.TALL_GRASS;
   }

   public static boolean isTrailVegetation(BlockState state) {
      Block block = state.getBlock();
      return isBladeGrassPlant(block) || isSwayingPlant(block) || GrassConfig.isPlantWhitelisted(block);
   }

   public static boolean shouldHide(ModelResourceLocation location) {
      return location.variant().equals("inventory") ? false : shouldHide(location.id());
   }

   public static boolean shouldHide(ResourceLocation blockId) {
      return canBeHidden(blockId) && !GrassConfig.isPlantBlacklisted(blockId);
   }

   public static boolean canBeHidden(ModelResourceLocation location) {
      return !location.variant().equals("inventory") && canBeHidden(location.id());
   }

   public static boolean canBeHidden(ResourceLocation blockId) {
      return BLOCKS.contains(blockId) || GrassConfig.isPlantWhitelisted(blockId) || canBeSwayingPlant((Block)BuiltInRegistries.BLOCK.get(blockId));
   }

   public static void rememberOriginalModel(ModelResourceLocation location, BakedModel original) {
      Block block = (Block)BuiltInRegistries.BLOCK.get(location.id());
      if (canBeSwayingPlant(block) || isConvertibleGrassPlant(block)) {
         ORIGINAL_PLANT_MODELS.put(location, original);
      }
   }

   public static BakedModel originalModel(ModelResourceLocation location) {
      return ORIGINAL_PLANT_MODELS.get(location);
   }

   public static void clearOriginalModels() {
      ORIGINAL_PLANT_MODELS.clear();
   }

   private HiddenGrass() {
   }
}
