/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.client.render.HiddenGrass;
import com.leonardoinc22.shortgrass.config.GrassConfig;
import java.util.function.Predicate;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

final class GrassGeometry {
    static final float BASE_WIDTH = 0.06f;
    static final float MIDDLE_WIDTH = 0.045f;
    static final float TIP_WIDTH = 0.0075f;
    static final float BLADE_SPLIT_FRACTION = 0.6666667f;
    static final float TIP_EXTENSION_RATIO = 0.45f;
    static final float TALL_PLANT_HEIGHT_MULTIPLIER = 3.0f;
    static final float BLADE_TOP_Y = 1.0f;
    static final float NOISE_WORLD_TILE_BLOCKS = 32.0f;
    static final float SEGMENTED_WIDTH = 0.05f;
    static final float SEGMENTED_HEIGHT_MULTIPLIER = 1.45f;
    static final float TAPERED_WIDTH_SCALE = 0.8333333f;
    static final float[] LOD_RING_FRACTION = new float[]{0.55f, 0.65f};
    static final int[] LOD_SEGMENTS = new int[]{5, 3, 2};
    static final float[] LOD_DENSITY = new float[]{1.0f, 0.7f, 0.15f};
    static final int LOD_TIERS = LOD_SEGMENTS.length;
    static final int ANIMATE_MAX_TIER = 0;
    static final Predicate<BlockState> RENDERS_GRASS = state -> state.is(Blocks.GRASS_BLOCK) || HiddenGrass.isBladeGrassPlant(state) || GrassConfig.isPlantWhitelisted(state.getBlock());
    static final Predicate<BlockState> RENDERS_CONTENT = state -> RENDERS_GRASS.test((BlockState)state) || HiddenGrass.isSwayingPlant(state);
    static final float SEGMENT_MIN_BAND_HEIGHT = 0.14f;
    static final float SEGMENT_MAX_COUNT = 4.0f;
    static final int SEGMENT_BAND_VARIANTS = 8;

    static int lodTier(double horizontalDistanceSqr, int renderRadius) {
        double r = renderRadius;
        for (int tier = 0; tier < LOD_RING_FRACTION.length; ++tier) {
            double edge = r * (double)LOD_RING_FRACTION[tier];
            if (!(horizontalDistanceSqr <= edge * edge)) continue;
            return tier;
        }
        return LOD_RING_FRACTION.length;
    }

    static int lodBladesPerBlock(int baseBladesPerBlock, int tier) {
        return Math.max(1, Math.round((float)baseBladesPerBlock * LOD_DENSITY[tier]));
    }

    private GrassGeometry() {
    }

    static float bladeWidthAt(float t, GrassConfig.GrassStyle style) {
        if (style == GrassConfig.GrassStyle.SEGMENTED) {
            return 0.05f * GrassConfig.bladeWidth;
        }
        float taperedWidth = GrassConfig.bladeWidth * 0.8333333f;
        if (t <= 0.6666667f) {
            return Mth.lerp((float)(t / 0.6666667f), (float)0.06f, (float)0.045f) * taperedWidth;
        }
        return Mth.lerp((float)((t - 0.6666667f) / 0.3333333f), (float)0.045f, (float)0.0075f) * taperedWidth;
    }

    static float visualBladeHeight() {
        return GrassConfig.bladeHeight * (GrassConfig.grassStyle == GrassConfig.GrassStyle.SEGMENTED ? 1.45f : 1.45f);
    }

    static float segmentBandColumnU(int worldX, int worldZ, int blade) {
        int h = worldX * 374761393 + worldZ * 668265263 + blade * 1013904223;
        h = (h ^ h >>> 13) * 1274126177;
        int col = Math.floorMod(h, 8);
        return ((float)col + 0.5f) / 8.0f;
    }

    static float segmentBandScale(float heightMultiplier) {
        float length = GrassGeometry.visualBladeHeight() * heightMultiplier;
        float count = Mth.clamp((float)((float)Math.floor(length / 0.14f)), (float)1.0f, (float)4.0f);
        return count / 4.0f;
    }

    static float worldNoiseCoord(float worldCoord) {
        return worldCoord / 32.0f - (float)Mth.floor((float)(worldCoord / 32.0f));
    }
}

