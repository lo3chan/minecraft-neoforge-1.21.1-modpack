/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.ModelResourceLocation
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.BushBlock
 *  net.minecraft.world.level.block.state.BlockState
 */
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
    public static final Set<ResourceLocation> BLOCKS = Set.of(ResourceLocation.withDefaultNamespace((String)"short_grass"), ResourceLocation.withDefaultNamespace((String)"fern"), ResourceLocation.withDefaultNamespace((String)"tall_grass"), ResourceLocation.withDefaultNamespace((String)"large_fern"));
    private static final Map<ModelResourceLocation, BakedModel> ORIGINAL_PLANT_MODELS = new ConcurrentHashMap<ModelResourceLocation, BakedModel>();

    public static boolean isSwayingPlant(BlockState state) {
        return HiddenGrass.isSwayingPlant(state.getBlock());
    }

    public static boolean isSwayingPlant(Block block) {
        return HiddenGrass.canBeSwayingPlant(block) && !GrassConfig.isPlantBlacklisted(block);
    }

    public static boolean canBeSwayingPlant(BlockState state) {
        return HiddenGrass.canBeSwayingPlant(state.getBlock());
    }

    public static boolean canBeSwayingPlant(Block block) {
        return block instanceof BushBlock && !HiddenGrass.isBladeGrassPlant(block);
    }

    public static boolean isBladeGrassPlant(BlockState state) {
        return HiddenGrass.isBladeGrassPlant(state.getBlock());
    }

    private static boolean isBladeGrassPlant(Block block) {
        return GrassConfig.grassPlantsAsBlades && HiddenGrass.isConvertibleGrassPlant(block);
    }

    private static boolean isConvertibleGrassPlant(Block block) {
        return block == Blocks.SHORT_GRASS || block == Blocks.TALL_GRASS;
    }

    public static boolean isTrailVegetation(BlockState state) {
        Block block = state.getBlock();
        return HiddenGrass.isBladeGrassPlant(block) || HiddenGrass.isSwayingPlant(block) || GrassConfig.isPlantWhitelisted(block);
    }

    public static boolean shouldHide(ModelResourceLocation location) {
        if (location.variant().equals("inventory")) {
            return false;
        }
        return HiddenGrass.shouldHide(location.id());
    }

    public static boolean shouldHide(ResourceLocation blockId) {
        return HiddenGrass.canBeHidden(blockId) && !GrassConfig.isPlantBlacklisted(blockId);
    }

    public static boolean canBeHidden(ModelResourceLocation location) {
        return !location.variant().equals("inventory") && HiddenGrass.canBeHidden(location.id());
    }

    public static boolean canBeHidden(ResourceLocation blockId) {
        return BLOCKS.contains(blockId) || GrassConfig.isPlantWhitelisted(blockId) || HiddenGrass.canBeSwayingPlant((Block)BuiltInRegistries.BLOCK.get(blockId));
    }

    public static void rememberOriginalModel(ModelResourceLocation location, BakedModel original) {
        Block block = (Block)BuiltInRegistries.BLOCK.get(location.id());
        if (HiddenGrass.canBeSwayingPlant(block) || HiddenGrass.isConvertibleGrassPlant(block)) {
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

