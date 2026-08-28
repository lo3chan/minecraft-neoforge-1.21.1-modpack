/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.color.block.BlockColors
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.block.BlockModelShaper
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.ModelResourceLocation
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.Biome$Precipitation
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.DoublePlantBlock
 *  net.minecraft.world.level.block.FlowerBlock
 *  net.minecraft.world.level.block.SlabBlock
 *  net.minecraft.world.level.block.SnowyDirtBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.DoubleBlockHalf
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.SlabType
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.phys.Vec3
 */
package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.client.render.FlowerDensity;
import com.leonardoinc22.shortgrass.client.render.GrassClumpField;
import com.leonardoinc22.shortgrass.client.render.GrassDrawDispatcher;
import com.leonardoinc22.shortgrass.client.render.GrassGeometry;
import com.leonardoinc22.shortgrass.client.render.GrassHueNoise;
import com.leonardoinc22.shortgrass.client.render.GrassSectionBuildBuffers;
import com.leonardoinc22.shortgrass.client.render.GrassSectionMesh;
import com.leonardoinc22.shortgrass.client.render.HiddenGrass;
import com.leonardoinc22.shortgrass.config.GrassConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.Vec3;

final class GrassSectionBuilder {
    private static final float SNOW_TIP_START = 0.42f;
    private static final float SNOW_TIP_END = 0.9f;
    private static final float FULL_SNOW_MAX_LENGTH = 0.58f;
    private static final float FULL_SNOW_FADE_END = 0.75f;
    private static final float GREEN_PATCH_SNOW_STRENGTH = 0.62f;
    private static final float PURE_WHITE_NOISE_START = 0.12f;
    private static final float PURE_WHITE_NOISE_END = 0.38f;
    private static final int SNOW_TIP_TINT = 0xFFFFFF;
    private static final RandomSource BLADE_RANDOM = RandomSource.create();
    private static final Block GRASS_SLAB = BuiltInRegistries.BLOCK.getOptional(ResourceLocation.fromNamespaceAndPath((String)"terrain_slabs", (String)"grass_slab")).orElse(null);
    private static final int BLOCK_STRIDE = 8;
    private static final RandomSource PLANT_RANDOM = RandomSource.create();

    private GrassSectionBuilder() {
    }

    static GrassSectionMesh prepareMesh(long key, long now, boolean computeMode, Vec3 cameraPos) {
        int tier;
        int originX = SectionPos.sectionToBlockCoord((int)SectionPos.x((long)key));
        int originY = SectionPos.sectionToBlockCoord((int)SectionPos.y((long)key));
        int originZ = SectionPos.sectionToBlockCoord((int)SectionPos.z((long)key));
        GrassSectionMesh mesh = new GrassSectionMesh();
        mesh.bounds = GrassDrawDispatcher.sectionBounds(originX, originY, originZ);
        mesh.builtAtTick = now;
        mesh.lodTier = tier = GrassGeometry.lodTier(GrassSectionBuilder.horizontalSectionDistanceSqr(originX, originZ, cameraPos), GrassConfig.renderRadius);
        mesh.anim = computeMode && tier > 0 ? GrassSectionMesh.Anim.BAKE_PENDING : GrassSectionMesh.Anim.ANIMATED;
        return mesh;
    }

    static void emit(GrassSectionBuildBuffers buffers, BlockAndTintGetter region, ClientLevel climateLevel, long key, int tier) {
        int originX = SectionPos.sectionToBlockCoord((int)SectionPos.x((long)key));
        int originY = SectionPos.sectionToBlockCoord((int)SectionPos.y((long)key));
        int originZ = SectionPos.sectionToBlockCoord((int)SectionPos.z((long)key));
        GrassSectionBuilder.emitSection(buffers, region, climateLevel, originX, originY, originZ, tier);
    }

    static boolean sectionMayHaveContent(ClientLevel level, long key) {
        int sectionX = SectionPos.x((long)key);
        int sectionY = SectionPos.y((long)key);
        int sectionZ = SectionPos.z((long)key);
        LevelChunk chunk = level.getChunk(sectionX, sectionZ);
        int index = level.getSectionIndexFromSectionY(sectionY);
        LevelChunkSection[] sections = chunk.getSections();
        if (index < 0 || index >= sections.length) {
            return false;
        }
        LevelChunkSection section = sections[index];
        return !section.hasOnlyAir() && section.maybeHas(GrassGeometry.RENDERS_CONTENT);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void emitSection(GrassSectionBuildBuffers buffers, BlockAndTintGetter level, ClientLevel climateLevel, int originX, int originY, int originZ, int tier) {
        float configuredBladeHeight = GrassConfig.bladeHeight;
        float grassSparsity = GrassConfig.grassSparsity();
        float bladeHueJitterDegrees = GrassConfig.bladeHueJitterDegrees();
        int baseBladesPerBlock = GrassGeometry.lodBladesPerBlock(GrassConfig.bladesPerBlock, tier);
        int segments = GrassGeometry.LOD_SEGMENTS[tier];
        GrassConfig.GrassStyle style = GrassConfig.grassStyle;
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos above = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos below = new BlockPos.MutableBlockPos();
        for (int dx = 0; dx < 16; ++dx) {
            for (int dz = 0; dz < 16; ++dz) {
                for (int dy = 0; dy < 16; ++dy) {
                    BlockState belowState;
                    int irisLocalY;
                    BlockState irisBlockState;
                    boolean lightAbove;
                    int heightClass;
                    float heightMultiplier;
                    float baseOffset;
                    int worldX = originX + dx;
                    int worldY = originY + dy;
                    int worldZ = originZ + dz;
                    pos.set(worldX, worldY, worldZ);
                    BlockState state = level.getBlockState((BlockPos)pos);
                    if (GrassConfig.isPlantBlacklisted(state.getBlock())) continue;
                    BlockState tintState = state;
                    if (state.is(Blocks.GRASS_BLOCK)) {
                        above.set(worldX, worldY + 1, worldZ);
                        aboveState = level.getBlockState((BlockPos)above);
                        boolean grassThroughSnow = GrassConfig.grassThroughSnow && aboveState.is(Blocks.SNOW);
                        convertedShortGrass = GrassSectionBuilder.isConvertedShortGrass(aboveState);
                        if (state.getOptionalValue((Property)SnowyDirtBlock.SNOWY).orElse(false).booleanValue() && !grassThroughSnow || !aboveState.isAir() && !grassThroughSnow && !convertedShortGrass) continue;
                        baseOffset = 1.0f;
                        heightMultiplier = 1.0f;
                        heightClass = 0;
                        lightAbove = true;
                        irisBlockState = Blocks.GRASS_BLOCK.defaultBlockState();
                        irisLocalY = dy + 1;
                    } else if (GRASS_SLAB != null && state.is(GRASS_SLAB)) {
                        above.set(worldX, worldY + 1, worldZ);
                        aboveState = level.getBlockState((BlockPos)above);
                        boolean grassThroughSnow = GrassConfig.grassThroughSnow && aboveState.is(Blocks.SNOW);
                        convertedShortGrass = GrassSectionBuilder.isConvertedShortGrass(aboveState);
                        if (state.getOptionalValue((Property)SnowyDirtBlock.SNOWY).orElse(false).booleanValue() && !grassThroughSnow || !aboveState.isAir() && !grassThroughSnow && !convertedShortGrass) continue;
                        baseOffset = state.getValue((Property)SlabBlock.TYPE) == SlabType.BOTTOM ? 0.5f : 1.0f;
                        heightMultiplier = 1.0f;
                        heightClass = 0;
                        lightAbove = true;
                        irisBlockState = Blocks.GRASS_BLOCK.defaultBlockState();
                        irisLocalY = dy + 1;
                    } else if (GrassSectionBuilder.isConvertedShortGrass(state)) {
                        below.set(worldX, worldY - 1, worldZ);
                        belowState = level.getBlockState((BlockPos)below);
                        if (belowState.is(Blocks.GRASS_BLOCK) || GRASS_SLAB != null && belowState.is(GRASS_SLAB)) continue;
                        baseOffset = 0.0f;
                        heightMultiplier = 1.0f;
                        heightClass = 0;
                        lightAbove = false;
                        irisBlockState = Blocks.GRASS_BLOCK.defaultBlockState();
                        irisLocalY = dy;
                    } else if (HiddenGrass.isBladeGrassPlant(state) && state.is(Blocks.TALL_GRASS) && state.getValue((Property)DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER) {
                        baseOffset = 0.0f;
                        heightMultiplier = 3.0f;
                        heightClass = 2;
                        lightAbove = false;
                        irisBlockState = Blocks.GRASS_BLOCK.defaultBlockState();
                        irisLocalY = dy;
                    } else if (GrassConfig.isPlantWhitelisted(state.getBlock())) {
                        baseOffset = 0.0f;
                        heightMultiplier = 1.0f;
                        heightClass = 1;
                        lightAbove = false;
                        irisBlockState = Blocks.GRASS_BLOCK.defaultBlockState();
                        irisLocalY = dy;
                        tintState = Blocks.GRASS_BLOCK.defaultBlockState();
                    } else {
                        ModelResourceLocation mrl;
                        BakedModel original;
                        if (!HiddenGrass.isSwayingPlant(state) || (original = HiddenGrass.originalModel(mrl = BlockModelShaper.stateToModelLocation((BlockState)state))) == null) continue;
                        int plantLight = LevelRenderer.getLightColor((BlockAndTintGetter)level, (BlockPos)pos);
                        buffers.beginPlantLightRun(dx, dy, dz);
                        float plantNoiseX = GrassGeometry.worldNoiseCoord((float)worldX + 0.5f);
                        float plantNoiseZ = GrassGeometry.worldNoiseCoord((float)worldZ + 0.5f);
                        float baseFraction = 0.0f;
                        float topFraction = 1.0f;
                        if (state.getBlock() instanceof DoublePlantBlock) {
                            boolean lower = state.getValue((Property)DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER;
                            baseFraction = lower ? 0.0f : 0.5f;
                            topFraction = lower ? 0.5f : 1.0f;
                        }
                        buffers.beginPlantIrisBlock(state, dx, dy, dz);
                        try {
                            Vec3 base = state.getOffset((BlockGetter)level, (BlockPos)pos);
                            GrassSectionBuilder.emitPlant(buffers, original, level, (BlockPos)pos, state, blockColors, dx, dy, dz, plantLight, plantNoiseX, plantNoiseZ, baseFraction, topFraction, base);
                            if (!GrassConfig.denseFlowers || !(state.getBlock() instanceof FlowerBlock)) continue;
                            ArrayList<Vec3> extras = new ArrayList<Vec3>();
                            FlowerDensity.collectExtras((BlockGetter)level, (BlockPos)pos, base, extras);
                            for (Vec3 extra : extras) {
                                GrassSectionBuilder.emitPlant(buffers, original, level, (BlockPos)pos, state, blockColors, dx, dy, dz, plantLight, plantNoiseX, plantNoiseZ, baseFraction, topFraction, extra);
                            }
                            continue;
                        }
                        finally {
                            buffers.endPlantIrisBlock();
                            buffers.finishPlantLightRun();
                        }
                    }
                    if (!lightAbove) {
                        below.set(worldX, worldY - 1, worldZ);
                        belowState = level.getBlockState((BlockPos)below);
                        if (GRASS_SLAB != null && belowState.is(GRASS_SLAB) && belowState.getValue((Property)SlabBlock.TYPE) == SlabType.BOTTOM) {
                            baseOffset -= 0.5f;
                        }
                    }
                    int sampleDy = lightAbove ? dy + 1 : dy;
                    int light = LevelRenderer.getLightColor((BlockAndTintGetter)level, (BlockPos)(lightAbove ? above : pos));
                    float height = configuredBladeHeight * heightMultiplier;
                    int tint = blockColors.getColor(tintState, level, (BlockPos)pos, 0);
                    boolean snowyClimate = ((Biome)climateLevel.getBiome((BlockPos)pos).value()).getPrecipitationAt((BlockPos)pos) == Biome.Precipitation.SNOW;
                    buffers.beginBladeLightRun(dx, sampleDy, dz);
                    buffers.beginBladeIrisBlock(irisBlockState, dx, irisLocalY, dz, GrassGeometry.segmentBandScale(heightMultiplier));
                    try {
                        GrassSectionBuilder.emitBlock(buffers, worldX, worldY, worldZ, dx, dy, dz, light, height, tint, baseBladesPerBlock, style, segments, baseOffset, heightClass, grassSparsity, bladeHueJitterDegrees, snowyClimate);
                        continue;
                    }
                    finally {
                        buffers.endBladeIrisBlock();
                        buffers.finishBladeLightRun();
                    }
                }
            }
        }
    }

    private static boolean isConvertedShortGrass(BlockState state) {
        return state.is(Blocks.SHORT_GRASS) && HiddenGrass.isBladeGrassPlant(state) && !GrassConfig.isPlantBlacklisted(state.getBlock());
    }

    private static double horizontalSectionDistanceSqr(int originX, int originZ, Vec3 cameraPos) {
        double dx = (double)originX + 8.0 - cameraPos.x;
        double dz = (double)originZ + 8.0 - cameraPos.z;
        return dx * dx + dz * dz;
    }

    private static void emitBlock(GrassSectionBuildBuffers buffers, int worldX, int worldY, int worldZ, int localX, int localY, int localZ, int light, float configuredHeight, int tint, int bladesPerBlock, GrassConfig.GrassStyle style, int segments, float baseOffset, int heightClass, float grassSparsity, float bladeHueJitterDegrees, boolean snowyClimate) {
        long seed = BlockPos.asLong((int)worldX, (int)worldY, (int)worldZ);
        BLADE_RANDOM.setSeed(seed);
        float baseLocalY = (float)localY + baseOffset;
        for (int blade = 0; blade < bladesPerBlock; ++blade) {
            float bx = 0.08f + BLADE_RANDOM.nextFloat();
            float bz = 0.08f + BLADE_RANDOM.nextFloat();
            float angle = GrassSectionBuilder.quantizedBladeAngle(BLADE_RANDOM.nextFloat() * ((float)Math.PI * 2));
            float height = configuredHeight;
            float cx = (float)localX + bx;
            float cz = (float)localZ + bz;
            float dirX = Mth.cos((float)angle);
            float dirZ = Mth.sin((float)angle);
            float noiseX = GrassGeometry.worldNoiseCoord((float)worldX + bx);
            float noiseZ = GrassGeometry.worldNoiseCoord((float)worldZ + bz);
            if (grassSparsity > 0.0f && !GrassClumpField.keepBlade(noiseX, noiseZ, grassSparsity, GrassSectionBuilder.bladeSparsityRandom(worldX, worldY, worldZ, blade))) continue;
            if (style == GrassConfig.GrassStyle.SEGMENTED) {
                buffers.setBladeBandColumn(GrassGeometry.segmentBandColumnU(worldX, worldZ, blade));
            }
            int bladeTint = GrassHueNoise.shiftHue(tint, (float)worldX + bx, (float)worldZ + bz, angle / ((float)Math.PI * 2), bladeHueJitterDegrees);
            float bladeLengthMultiplier = snowyClimate ? GrassClumpField.bladeLengthMultiplier(noiseX, noiseZ, angle, heightClass) : 1.0f;
            float snowCoverage = snowyClimate ? GrassClumpField.snowCoverage(noiseX, noiseZ) : 0.5f;
            float tipY = baseLocalY + height * (style == GrassConfig.GrassStyle.SEGMENTED ? 1.45f : 1.45f);
            if (segments >= 3) {
                float prevY = baseLocalY;
                float prevT = 0.0f;
                float prevWidth = GrassGeometry.bladeWidthAt(0.0f, style);
                for (int section = 1; section <= segments; ++section) {
                    float t = (float)section / (float)segments;
                    float y = Mth.lerp((float)t, (float)baseLocalY, (float)tipY);
                    float width = GrassGeometry.bladeWidthAt(t, style);
                    GrassSectionBuilder.emitBladeSection(buffers, cx, cz, dirX, dirZ, angle, noiseX, noiseZ, light, bladeTint, snowyClimate, bladeLengthMultiplier, snowCoverage, prevY, prevT, prevWidth, y, t, width, heightClass);
                    prevY = y;
                    prevT = t;
                    prevWidth = width;
                }
                continue;
            }
            if (segments == 2) {
                float splitY = Mth.lerp((float)0.6666667f, (float)baseLocalY, (float)tipY);
                GrassSectionBuilder.emitBladeSection(buffers, cx, cz, dirX, dirZ, angle, noiseX, noiseZ, light, bladeTint, snowyClimate, bladeLengthMultiplier, snowCoverage, baseLocalY, 0.0f, GrassGeometry.bladeWidthAt(0.0f, style), splitY, 0.6666667f, GrassGeometry.bladeWidthAt(0.6666667f, style), heightClass);
                GrassSectionBuilder.emitBladeSection(buffers, cx, cz, dirX, dirZ, angle, noiseX, noiseZ, light, bladeTint, snowyClimate, bladeLengthMultiplier, snowCoverage, splitY, 0.6666667f, GrassGeometry.bladeWidthAt(0.6666667f, style), tipY, 1.0f, GrassGeometry.bladeWidthAt(1.0f, style), heightClass);
                continue;
            }
            GrassSectionBuilder.emitBladeSection(buffers, cx, cz, dirX, dirZ, angle, noiseX, noiseZ, light, bladeTint, snowyClimate, bladeLengthMultiplier, snowCoverage, baseLocalY, 0.0f, GrassGeometry.bladeWidthAt(0.0f, style), tipY, 1.0f, GrassGeometry.bladeWidthAt(1.0f, style), heightClass);
        }
    }

    private static float bladeSparsityRandom(int worldX, int worldY, int worldZ, int blade) {
        long hash = BlockPos.asLong((int)worldX, (int)worldY, (int)worldZ) ^ (long)blade * -7046029254386353131L;
        hash = (hash ^ hash >>> 30) * -4658895280553007687L;
        hash = (hash ^ hash >>> 27) * -7723592293110705685L;
        hash ^= hash >>> 31;
        return (float)(hash >>> 40) * 5.9604645E-8f;
    }

    private static void emitBladeSection(GrassSectionBuildBuffers buffers, float cx, float cz, float dirX, float dirZ, float angle, float noiseX, float noiseZ, int light, int tint, boolean snowyClimate, float bladeLengthMultiplier, float snowCoverage, float lowerY, float lowerT, float lowerWidth, float upperY, float upperT, float upperWidth, int heightClass) {
        float upperSnow;
        float lowerSideX = dirX * lowerWidth;
        float lowerSideZ = dirZ * lowerWidth;
        float upperSideX = dirX * upperWidth;
        float upperSideZ = dirZ * upperWidth;
        float lowerLeftX = cx - lowerSideX;
        float lowerLeftZ = cz - lowerSideZ;
        float lowerRightX = cx + lowerSideX;
        float lowerRightZ = cz + lowerSideZ;
        float upperLeftX = cx - upperSideX;
        float upperLeftZ = cz - upperSideZ;
        float upperRightX = cx + upperSideX;
        float upperRightZ = cz + upperSideZ;
        float colorBrightness = buffers.bladeColorBrightness();
        float lowerSnow = GrassSectionBuilder.snowBlend(snowyClimate, bladeLengthMultiplier, snowCoverage, lowerT);
        boolean snowSurface = lowerSnow + (upperSnow = GrassSectionBuilder.snowBlend(snowyClimate, bladeLengthMultiplier, snowCoverage, upperT)) >= 1.0f;
        int lowerTint = GrassSectionBuilder.bladeTint(tint, lowerSnow, colorBrightness);
        int upperTint = GrassSectionBuilder.bladeTint(tint, upperSnow, colorBrightness);
        buffers.bladeVertex(lowerLeftX, lowerY, lowerLeftZ, 0.0f, lowerT, -1.0f, angle, noiseX, noiseZ, light, lowerTint, heightClass, snowSurface, lowerSnow);
        buffers.bladeVertex(lowerRightX, lowerY, lowerRightZ, 1.0f, lowerT, 1.0f, angle, noiseX, noiseZ, light, lowerTint, heightClass, snowSurface, lowerSnow);
        buffers.bladeVertex(upperRightX, upperY, upperRightZ, 1.0f, upperT, 1.0f, angle, noiseX, noiseZ, light, upperTint, heightClass, snowSurface, upperSnow);
        buffers.bladeVertex(upperLeftX, upperY, upperLeftZ, 0.0f, upperT, -1.0f, angle, noiseX, noiseZ, light, upperTint, heightClass, snowSurface, upperSnow);
    }

    private static float snowBlend(boolean snowyClimate, float bladeLengthMultiplier, float snowCoverage, float t) {
        if (!snowyClimate) {
            return 0.0f;
        }
        float coverageOffset = snowCoverage - 0.5f;
        float tipStart = 0.42f - coverageOffset * 0.24f;
        float tipEnd = 0.9f - coverageOffset * 0.1f;
        float blend = Mth.clamp((float)((t - tipStart) / (tipEnd - tipStart)), (float)0.0f, (float)1.0f);
        blend = blend * blend * (3.0f - 2.0f * blend);
        float fullSnowLength = 0.58f + coverageOffset * 0.18f;
        float fullSnowFadeEnd = 0.75f + coverageOffset * 0.14f;
        float shortBladeBlend = 1.0f - Mth.clamp((float)((bladeLengthMultiplier - fullSnowLength) / (fullSnowFadeEnd - fullSnowLength)), (float)0.0f, (float)1.0f);
        shortBladeBlend = shortBladeBlend * shortBladeBlend * (3.0f - 2.0f * shortBladeBlend);
        float pureWhite = Mth.clamp((float)((snowCoverage - 0.12f) / 0.26f), (float)0.0f, (float)1.0f);
        pureWhite = pureWhite * pureWhite * (3.0f - 2.0f * pureWhite);
        float patchStrength = Mth.lerp((float)pureWhite, (float)0.62f, (float)1.0f);
        return Math.max(blend, shortBladeBlend) * patchStrength;
    }

    private static int bladeTint(int tint, float blend, float colorBrightness) {
        int red = Math.round(Mth.lerp((float)blend, (float)Mth.clamp((float)((float)(tint >> 16 & 0xFF) * colorBrightness), (float)0.0f, (float)255.0f), (float)255.0f));
        int green = Math.round(Mth.lerp((float)blend, (float)Mth.clamp((float)((float)(tint >> 8 & 0xFF) * colorBrightness), (float)0.0f, (float)255.0f), (float)255.0f));
        int blue = Math.round(Mth.lerp((float)blend, (float)Mth.clamp((float)((float)(tint & 0xFF) * colorBrightness), (float)0.0f, (float)255.0f), (float)255.0f));
        return red << 16 | green << 8 | blue;
    }

    private static void emitPlant(GrassSectionBuildBuffers buffers, BakedModel model, BlockAndTintGetter level, BlockPos pos, BlockState state, BlockColors blockColors, int localX, int localY, int localZ, int light, float noiseX, float noiseZ, float baseFraction, float topFraction, Vec3 offset) {
        float normalX = noiseX * 2.0f - 1.0f;
        float normalZ = noiseZ * 2.0f - 1.0f;
        float baseX = (float)localX + (float)offset.x;
        float baseY = (float)localY + (float)offset.y;
        float baseZ = (float)localZ + (float)offset.z;
        long seed = pos.asLong();
        PLANT_RANDOM.setSeed(seed);
        GrassSectionBuilder.emitPlantQuads(buffers, model.getQuads(state, null, PLANT_RANDOM), level, pos, state, blockColors, baseX, baseY, baseZ, light, normalX, normalZ, baseFraction, topFraction);
        for (Direction direction : Direction.values()) {
            PLANT_RANDOM.setSeed(seed);
            GrassSectionBuilder.emitPlantQuads(buffers, model.getQuads(state, direction, PLANT_RANDOM), level, pos, state, blockColors, baseX, baseY, baseZ, light, normalX, normalZ, baseFraction, topFraction);
        }
    }

    private static void emitPlantQuads(GrassSectionBuildBuffers buffers, List<BakedQuad> quads, BlockAndTintGetter level, BlockPos pos, BlockState state, BlockColors blockColors, float baseX, float baseY, float baseZ, int light, float normalX, float normalZ, float baseFraction, float topFraction) {
        for (BakedQuad quad : quads) {
            int tintIndex = quad.getTintIndex();
            int tint = tintIndex == -1 ? -1 : blockColors.getColor(state, level, pos, tintIndex);
            int red = tint >> 16 & 0xFF;
            int green = tint >> 8 & 0xFF;
            int blue = tint & 0xFF;
            int[] verts = quad.getVertices();
            for (int vertex = 0; vertex < 4; ++vertex) {
                int o = vertex * 8;
                float x = Float.intBitsToFloat(verts[o]);
                float y = Float.intBitsToFloat(verts[o + 1]);
                float z = Float.intBitsToFloat(verts[o + 2]);
                float u = GrassSectionBuilder.atlasU(quad, Float.intBitsToFloat(verts[o + 4]));
                float v = GrassSectionBuilder.atlasV(quad, Float.intBitsToFloat(verts[o + 5]));
                float heightFraction = baseFraction + Mth.clamp((float)y, (float)0.0f, (float)1.0f) * (topFraction - baseFraction);
                buffers.plantVertex(baseX + x, baseY + y, baseZ + z, u, v, heightFraction, normalX, normalZ, light, red, green, blue);
            }
        }
    }

    private static float atlasU(BakedQuad quad, float u) {
        return GrassSectionBuilder.isSpriteRelativeUv(u) ? quad.getSprite().getU(u / 16.0f) : u;
    }

    private static float atlasV(BakedQuad quad, float v) {
        return GrassSectionBuilder.isSpriteRelativeUv(v) ? quad.getSprite().getV(v / 16.0f) : v;
    }

    private static boolean isSpriteRelativeUv(float uv) {
        return uv < 0.0f || uv > 1.0f;
    }

    private static float quantizedBladeAngle(float angle) {
        int angleBucket = Mth.clamp((int)((int)(GrassSectionBuilder.positiveAngle(angle) / ((float)Math.PI * 2) * 31.0f)), (int)0, (int)31);
        return (float)angleBucket / 31.0f * ((float)Math.PI * 2);
    }

    private static float positiveAngle(float angle) {
        return angle - (float)Mth.floor((float)(angle / ((float)Math.PI * 2))) * ((float)Math.PI * 2);
    }
}

