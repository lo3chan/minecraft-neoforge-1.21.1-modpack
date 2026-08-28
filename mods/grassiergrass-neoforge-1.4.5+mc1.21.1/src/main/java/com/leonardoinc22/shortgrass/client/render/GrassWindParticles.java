/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.DoublePlantBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.DoubleBlockHalf
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.phys.Vec3
 */
package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.client.render.GrassBladeParticle;
import com.leonardoinc22.shortgrass.client.render.GrassGeometry;
import com.leonardoinc22.shortgrass.config.GrassConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

final class GrassWindParticles {
    private static final ResourceLocation BLADE_SPRITE = ResourceLocation.fromNamespaceAndPath((String)"grassiergrass", (String)"block/wind_blade");
    private static final int SPAWN_INTERVAL_TICKS = 3;
    private static final int MAX_ATTEMPTS = 8;
    private static final int HORIZONTAL_RADIUS = 16;
    private static final float BLADE_SIZE = 0.2f;
    private static final float MIN_WIND_SPEED = 20.0f;
    private static final float GLIDE_SPEED = 0.32f;
    private static final RandomSource RANDOM = RandomSource.create();
    private static long lastSpawnTick;

    private GrassWindParticles() {
    }

    static void tick(ClientLevel level, Vec3 cameraPos, long gameTime) {
        if (!GrassConfig.bladeParticles) {
            return;
        }
        if (gameTime >= lastSpawnTick && gameTime - lastSpawnTick < 3L) {
            return;
        }
        lastSpawnTick = gameTime;
        float windSpeed = GrassConfig.effectiveWindSpeed();
        if (windSpeed < 20.0f) {
            return;
        }
        float baseGlide = 0.32f * windSpeed / 100.0f;
        int attempts = Mth.clamp((int)Mth.ceil((float)(8.0f * windSpeed / 150.0f)), (int)1, (int)8);
        Minecraft minecraft = Minecraft.getInstance();
        TextureAtlasSprite sprite = (TextureAtlasSprite)minecraft.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(BLADE_SPRITE);
        int camX = Mth.floor((double)cameraPos.x);
        int camZ = Mth.floor((double)cameraPos.z);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < attempts; ++i) {
            int tint;
            int x = camX + RANDOM.nextInt(33) - 16;
            int z = camZ + RANDOM.nextInt(33) - 16;
            int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
            pos.set(x, surfaceY - 1, z);
            BlockState state = level.getBlockState((BlockPos)pos);
            if (!GrassGeometry.RENDERS_GRASS.test(state) || (tint = minecraft.getBlockColors().getColor(state, (BlockAndTintGetter)level, (BlockPos)pos, 0)) == -1) continue;
            double sx = (double)x + RANDOM.nextDouble();
            double sz = (double)z + RANDOM.nextDouble();
            double sy = GrassWindParticles.grassCanopyTopY(state, surfaceY - 1);
            float glideSpeed = baseGlide * (0.75f + RANDOM.nextFloat() * 0.5f);
            minecraft.particleEngine.add((Particle)new GrassBladeParticle(level, sx, sy, sz, glideSpeed, tint, 0.2f, sprite));
        }
    }

    private static double grassCanopyTopY(BlockState state, int blockY) {
        float canopy = GrassGeometry.visualBladeHeight();
        if (state.is(Blocks.GRASS_BLOCK)) {
            return (float)(blockY + 1) + canopy;
        }
        if (state.is(Blocks.TALL_GRASS)) {
            int baseY = state.getValue((Property)DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? blockY - 1 : blockY;
            return (float)baseY + canopy * 3.0f;
        }
        return (float)blockY + canopy;
    }
}

