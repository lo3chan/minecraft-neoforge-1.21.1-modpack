/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.client.render.HiddenGrass;
import com.mojang.blaze3d.platform.NativeImage;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

final class GrassTrailField {
    private static final ResourceLocation TRAIL_TEXTURE = ResourceLocation.fromNamespaceAndPath((String)"grassiergrass", (String)"textures/effect/trail_field");
    private static final int TRAIL_TEXTURE_SIZE = 128;
    private static final float TRAIL_WORLD_SIZE = 48.0f;
    private static final float TRAIL_CELL_SIZE = 0.375f;
    private static final int TRAIL_RECENTER_STEP_CELLS = 8;
    private static final float TRAIL_STAMP_RADIUS = 0.5f;
    private static final float TRAIL_STAMP_STRENGTH = 0.06f;
    private static final float TRAIL_RECOVERY_PER_FRAME = 0.998f;
    private static final float TRAIL_REFERENCE_ENTITY_WIDTH = 0.6f;
    private static final float TRAIL_MIN_FOOTPRINT_SCALE = 0.35f;
    private static final float TRAIL_MAX_FOOTPRINT_SCALE = 4.0f;
    private static final float TRAIL_VERTICAL_REACH = 1.0f;
    private static final float TRAIL_MAX_SURFACE_GAP = 1.15f;
    private static final float TRAIL_SHADER_STRENGTH = 0.6f;
    private static final float[] TRAIL_DIR_X = new float[16384];
    private static final float[] TRAIL_DIR_Z = new float[16384];
    private static final float[] TRAIL_STRENGTH = new float[16384];
    private static final int[] TRAIL_ACTIVE_INDICES = new int[16384];
    private static final boolean[] TRAIL_ACTIVE = new boolean[16384];
    private static NativeImage trailImage;
    private static DynamicTexture trailTexture;
    private static boolean trailTextureDirty;
    private static int trailDirtyMinX;
    private static int trailDirtyMinZ;
    private static int trailDirtyMaxX;
    private static int trailDirtyMaxZ;
    private static int trailActiveCount;
    private static int trailOriginCellX;
    private static int trailOriginCellZ;
    private static float trailOriginWorldX;
    private static float trailOriginWorldZ;

    GrassTrailField() {
    }

    static void update(ClientLevel level, Vec3 cameraPos, float partialTick) {
        GrassTrailField.ensureTrailTexture();
        GrassTrailField.recenterTrailField(cameraPos);
        GrassTrailField.decayTrailField();
        AABB trailBounds = new AABB((double)trailOriginWorldX, cameraPos.y - 1.0 - 16.0, (double)trailOriginWorldZ, (double)(trailOriginWorldX + 48.0f), cameraPos.y + 1.0 + 16.0, (double)(trailOriginWorldZ + 48.0f));
        for (Entity entity : level.entitiesForRendering()) {
            if (!entity.isAlive() || !trailBounds.intersects(entity.getBoundingBox())) continue;
            Vec3 position = entity.getPosition(partialTick);
            AABB bounds = entity.getBoundingBox();
            float horizontalSize = (float)Math.max(bounds.getXsize(), bounds.getZsize());
            float radiusScale = Mth.clamp((float)(horizontalSize / 0.6f), (float)0.35f, (float)4.0f);
            float radius = Math.max(0.5f * radiusScale, entity.getBbWidth() * 0.9f);
            float footY = (float)(position.y + bounds.minY - entity.getY());
            if (!GrassTrailField.nearGrassSurface(level, (float)position.x, (float)position.z, footY, radius)) continue;
            float strengthScale = Mth.clamp((float)(0.65f + radiusScale * 0.35f), (float)0.5f, (float)1.6f);
            GrassTrailField.stampTrail((float)position.x, (float)position.z, radius, strengthScale);
        }
        if (trailTextureDirty || GrassTrailField.hasTrailDirtyBounds()) {
            GrassTrailField.uploadTrailField();
        }
    }

    private static void ensureTrailTexture() {
        if (trailImage != null && trailTexture != null) {
            return;
        }
        trailImage = new NativeImage(128, 128, true);
        trailTexture = new DynamicTexture(trailImage);
        Minecraft.getInstance().getTextureManager().register(TRAIL_TEXTURE, (AbstractTexture)trailTexture);
        trailTextureDirty = true;
    }

    private static boolean recenterTrailField(Vec3 cameraPos) {
        int newOriginCellX = Mth.floor((double)((cameraPos.x - 24.0) / 0.375));
        int newOriginCellZ = Mth.floor((double)((cameraPos.z - 24.0) / 0.375));
        if (trailOriginCellX != Integer.MIN_VALUE && Math.abs(newOriginCellX - trailOriginCellX) < 8 && Math.abs(newOriginCellZ - trailOriginCellZ) < 8) {
            return false;
        }
        if (trailOriginCellX == Integer.MIN_VALUE || Math.abs(newOriginCellX - trailOriginCellX) >= 128 || Math.abs(newOriginCellZ - trailOriginCellZ) >= 128) {
            GrassTrailField.clearTrailArrays();
        } else {
            GrassTrailField.shiftTrailArrays(trailOriginCellX - newOriginCellX, trailOriginCellZ - newOriginCellZ);
        }
        trailOriginCellX = newOriginCellX;
        trailOriginCellZ = newOriginCellZ;
        trailOriginWorldX = (float)trailOriginCellX * 0.375f;
        trailOriginWorldZ = (float)trailOriginCellZ * 0.375f;
        GrassTrailField.markTrailDirtyFull();
        return true;
    }

    private static void shiftTrailArrays(int offsetX, int offsetZ) {
        GrassTrailField.shiftTrailArray(TRAIL_DIR_X, offsetX, offsetZ);
        GrassTrailField.shiftTrailArray(TRAIL_DIR_Z, offsetX, offsetZ);
        GrassTrailField.shiftTrailArray(TRAIL_STRENGTH, offsetX, offsetZ);
        GrassTrailField.rebuildActiveTrailCells();
    }

    private static void shiftTrailArray(float[] values, int offsetX, int offsetZ) {
        int copyWidth = 128 - Math.abs(offsetX);
        int copyHeight = 128 - Math.abs(offsetZ);
        if (copyWidth <= 0 || copyHeight <= 0) {
            Arrays.fill(values, 0.0f);
            return;
        }
        int srcX = Math.max(0, -offsetX);
        int dstX = Math.max(0, offsetX);
        int srcZ = Math.max(0, -offsetZ);
        int dstZ = Math.max(0, offsetZ);
        if (offsetZ > 0) {
            for (row = copyHeight - 1; row >= 0; --row) {
                System.arraycopy(values, (srcZ + row) * 128 + srcX, values, (dstZ + row) * 128 + dstX, copyWidth);
            }
        } else {
            for (row = 0; row < copyHeight; ++row) {
                System.arraycopy(values, (srcZ + row) * 128 + srcX, values, (dstZ + row) * 128 + dstX, copyWidth);
            }
        }
        GrassTrailField.clearTrailRows(values, 0, dstZ);
        GrassTrailField.clearTrailRows(values, dstZ + copyHeight, 128);
        for (int z = dstZ; z < dstZ + copyHeight; ++z) {
            int rowStart = z * 128;
            if (dstX > 0) {
                Arrays.fill(values, rowStart, rowStart + dstX, 0.0f);
            }
            if (dstX + copyWidth >= 128) continue;
            Arrays.fill(values, rowStart + dstX + copyWidth, rowStart + 128, 0.0f);
        }
    }

    private static void clearTrailRows(float[] values, int startZ, int endZ) {
        for (int z = startZ; z < endZ; ++z) {
            int rowStart = z * 128;
            Arrays.fill(values, rowStart, rowStart + 128, 0.0f);
        }
    }

    private static void decayTrailField() {
        int dirtyMinX = 128;
        int dirtyMinZ = 128;
        int dirtyMaxX = -1;
        int dirtyMaxZ = -1;
        int i = 0;
        while (i < trailActiveCount) {
            int index = TRAIL_ACTIVE_INDICES[i];
            float oldStrength = TRAIL_STRENGTH[index];
            if (oldStrength <= 0.0f) {
                GrassTrailField.removeActiveTrailCellAt(i);
                continue;
            }
            float strength = oldStrength * 0.998f;
            if (strength < 0.004f) {
                GrassTrailField.TRAIL_DIR_X[index] = 0.0f;
                GrassTrailField.TRAIL_DIR_Z[index] = 0.0f;
                GrassTrailField.TRAIL_STRENGTH[index] = 0.0f;
                GrassTrailField.removeActiveTrailCellAt(i);
            } else {
                GrassTrailField.TRAIL_STRENGTH[index] = strength;
                ++i;
            }
            int x = index % 128;
            int z = index / 128;
            dirtyMinX = Math.min(dirtyMinX, x);
            dirtyMinZ = Math.min(dirtyMinZ, z);
            dirtyMaxX = Math.max(dirtyMaxX, x);
            dirtyMaxZ = Math.max(dirtyMaxZ, z);
        }
        if (dirtyMaxX >= dirtyMinX) {
            GrassTrailField.markTrailDirty(dirtyMinX, dirtyMinZ, dirtyMaxX, dirtyMaxZ);
        }
    }

    private static boolean nearGrassSurface(ClientLevel level, float x, float z, float footY, float radius) {
        float sampleOffset = Math.min(radius * 0.55f, 0.65f);
        return GrassTrailField.nearGrassSurfaceAt(level, x, z, footY) || GrassTrailField.nearGrassSurfaceAt(level, x + sampleOffset, z, footY) || GrassTrailField.nearGrassSurfaceAt(level, x - sampleOffset, z, footY) || GrassTrailField.nearGrassSurfaceAt(level, x, z + sampleOffset, footY) || GrassTrailField.nearGrassSurfaceAt(level, x, z - sampleOffset, footY);
    }

    private static boolean nearGrassSurfaceAt(ClientLevel level, float x, float z, float footY) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int blockX = Mth.floor((float)x);
        int blockZ = Mth.floor((float)z);
        int maxY = Mth.floor((float)(footY + 0.25f));
        int minY = Mth.floor((float)(footY - 1.15f)) - 1;
        for (int y = maxY; y >= minY; --y) {
            float surfaceY;
            pos.set(blockX, y, blockZ);
            BlockState state = level.getBlockState((BlockPos)pos);
            if (state.is(Blocks.GRASS_BLOCK)) {
                surfaceY = (float)y + 1.0f;
            } else {
                if (!HiddenGrass.isTrailVegetation(state)) continue;
                surfaceY = y;
            }
            if (!(Math.abs(footY - surfaceY) <= 1.15f)) continue;
            return true;
        }
        return false;
    }

    private static boolean stampTrail(float entityX, float entityZ, float radius, float strengthScale) {
        boolean changed = false;
        int minX = Mth.floor((float)((entityX - radius - trailOriginWorldX) / 0.375f));
        int maxX = Mth.floor((float)((entityX + radius - trailOriginWorldX) / 0.375f));
        int minZ = Mth.floor((float)((entityZ - radius - trailOriginWorldZ) / 0.375f));
        int maxZ = Mth.floor((float)((entityZ + radius - trailOriginWorldZ) / 0.375f));
        minX = Mth.clamp((int)minX, (int)0, (int)127);
        maxX = Mth.clamp((int)maxX, (int)0, (int)127);
        minZ = Mth.clamp((int)minZ, (int)0, (int)127);
        maxZ = Mth.clamp((int)maxZ, (int)0, (int)127);
        for (int z = minZ; z <= maxZ; ++z) {
            float worldZ = trailOriginWorldZ + ((float)z + 0.5f) * 0.375f;
            for (int x = minX; x <= maxX; ++x) {
                float worldX = trailOriginWorldX + ((float)x + 0.5f) * 0.375f;
                float awayX = worldX - entityX;
                float awayZ = worldZ - entityZ;
                float distance = Mth.sqrt((float)(awayX * awayX + awayZ * awayZ));
                if (distance >= radius) continue;
                int index = z * 128 + x;
                float radialX = distance > 0.001f ? awayX / distance : TRAIL_DIR_X[index];
                float radialZ = distance > 0.001f ? awayZ / distance : TRAIL_DIR_Z[index];
                float dirLength = Math.max(Mth.sqrt((float)(radialX * radialX + radialZ * radialZ)), 0.001f);
                float dirX = radialX / dirLength;
                float dirZ = radialZ / dirLength;
                float falloff = 1.0f - GrassTrailField.smoothstep(0.15f, 1.0f, distance / radius);
                float newStrength = falloff * 0.06f * strengthScale;
                float oldStrength = TRAIL_STRENGTH[index];
                float combinedStrength = Mth.clamp((float)(oldStrength + newStrength * (1.0f - oldStrength)), (float)0.0f, (float)1.0f);
                float combinedX = TRAIL_DIR_X[index] * oldStrength + dirX * newStrength;
                float combinedZ = TRAIL_DIR_Z[index] * oldStrength + dirZ * newStrength;
                float combinedLength = Mth.sqrt((float)(combinedX * combinedX + combinedZ * combinedZ));
                if (combinedLength > 0.001f) {
                    GrassTrailField.TRAIL_DIR_X[index] = combinedX / combinedLength;
                    GrassTrailField.TRAIL_DIR_Z[index] = combinedZ / combinedLength;
                }
                GrassTrailField.TRAIL_STRENGTH[index] = combinedStrength;
                if (combinedStrength > 0.0f) {
                    GrassTrailField.addActiveTrailCell(index);
                }
                changed = true;
            }
        }
        if (changed) {
            GrassTrailField.markTrailDirty(minX, minZ, maxX, maxZ);
        }
        return changed;
    }

    private static float smoothstep(float edge0, float edge1, float value) {
        float t = Mth.clamp((float)((value - edge0) / (edge1 - edge0)), (float)0.0f, (float)1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    private static void uploadTrailField() {
        int maxZ;
        int minX = trailTextureDirty ? 0 : trailDirtyMinX;
        int minZ = trailTextureDirty ? 0 : trailDirtyMinZ;
        int maxX = trailTextureDirty ? 127 : trailDirtyMaxX;
        int n = maxZ = trailTextureDirty ? 127 : trailDirtyMaxZ;
        if (maxX < minX || maxZ < minZ) {
            return;
        }
        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                int index = z * 128 + x;
                int red = Math.round(Mth.clamp((float)(TRAIL_DIR_X[index] * 0.5f + 0.5f), (float)0.0f, (float)1.0f) * 255.0f);
                int green = Math.round(Mth.clamp((float)(TRAIL_DIR_Z[index] * 0.5f + 0.5f), (float)0.0f, (float)1.0f) * 255.0f);
                int blue = Math.round(Mth.clamp((float)TRAIL_STRENGTH[index], (float)0.0f, (float)1.0f) * 255.0f);
                trailImage.setPixelRGBA(x, z, 0xFF000000 | blue << 16 | green << 8 | red);
            }
        }
        if (trailTextureDirty) {
            trailTexture.upload();
        } else {
            trailTexture.bind();
            trailImage.upload(0, minX, minZ, minX, minZ, maxX - minX + 1, maxZ - minZ + 1, false, false);
        }
        trailTextureDirty = false;
        GrassTrailField.clearTrailDirtyBounds();
    }

    private static void clearTrailArrays() {
        Arrays.fill(TRAIL_DIR_X, 0.0f);
        Arrays.fill(TRAIL_DIR_Z, 0.0f);
        Arrays.fill(TRAIL_STRENGTH, 0.0f);
        Arrays.fill(TRAIL_ACTIVE, false);
        trailActiveCount = 0;
        GrassTrailField.markTrailDirtyFull();
    }

    private static void rebuildActiveTrailCells() {
        Arrays.fill(TRAIL_ACTIVE, false);
        trailActiveCount = 0;
        for (int i = 0; i < TRAIL_STRENGTH.length; ++i) {
            if (!(TRAIL_STRENGTH[i] > 0.0f)) continue;
            GrassTrailField.addActiveTrailCell(i);
        }
    }

    private static void addActiveTrailCell(int index) {
        if (TRAIL_ACTIVE[index]) {
            return;
        }
        GrassTrailField.TRAIL_ACTIVE[index] = true;
        GrassTrailField.TRAIL_ACTIVE_INDICES[GrassTrailField.trailActiveCount++] = index;
    }

    private static void removeActiveTrailCellAt(int activeIndex) {
        int lastActiveIndex;
        int index = TRAIL_ACTIVE_INDICES[activeIndex];
        GrassTrailField.TRAIL_ACTIVE[index] = false;
        if (activeIndex != (lastActiveIndex = --trailActiveCount)) {
            GrassTrailField.TRAIL_ACTIVE_INDICES[activeIndex] = TRAIL_ACTIVE_INDICES[lastActiveIndex];
        }
    }

    private static void markTrailDirtyFull() {
        trailTextureDirty = true;
        GrassTrailField.clearTrailDirtyBounds();
    }

    private static void markTrailDirty(int minX, int minZ, int maxX, int maxZ) {
        trailDirtyMinX = Math.min(trailDirtyMinX, Mth.clamp((int)minX, (int)0, (int)127));
        trailDirtyMinZ = Math.min(trailDirtyMinZ, Mth.clamp((int)minZ, (int)0, (int)127));
        trailDirtyMaxX = Math.max(trailDirtyMaxX, Mth.clamp((int)maxX, (int)0, (int)127));
        trailDirtyMaxZ = Math.max(trailDirtyMaxZ, Mth.clamp((int)maxZ, (int)0, (int)127));
    }

    private static boolean hasTrailDirtyBounds() {
        return trailDirtyMaxX >= trailDirtyMinX && trailDirtyMaxZ >= trailDirtyMinZ;
    }

    private static void clearTrailDirtyBounds() {
        trailDirtyMinX = 128;
        trailDirtyMinZ = 128;
        trailDirtyMaxX = -1;
        trailDirtyMaxZ = -1;
    }

    static void reset(boolean clearTexture) {
        trailOriginCellX = Integer.MIN_VALUE;
        trailOriginCellZ = Integer.MIN_VALUE;
        trailOriginWorldX = 0.0f;
        trailOriginWorldZ = 0.0f;
        GrassTrailField.clearTrailArrays();
        if (clearTexture && trailImage != null && trailTexture != null) {
            GrassTrailField.uploadTrailField();
        }
    }

    static ResourceLocation textureLocation() {
        return TRAIL_TEXTURE;
    }

    static int textureId() {
        GrassTrailField.ensureTrailTexture();
        return trailTexture.getId();
    }

    static float originOffsetX(Vec3 cameraPos) {
        return trailOriginWorldX - (float)cameraPos.x;
    }

    static float originOffsetZ(Vec3 cameraPos) {
        return trailOriginWorldZ - (float)cameraPos.z;
    }

    static float inverseWorldSize() {
        return 0.020833334f;
    }

    static double trailOriginWorldXExact() {
        return (double)trailOriginCellX * 0.375;
    }

    static double trailOriginWorldZExact() {
        return (double)trailOriginCellZ * 0.375;
    }

    static float shaderStrength() {
        return 0.6f;
    }

    static void close() {
        if (trailTexture != null) {
            trailTexture.close();
            trailTexture = null;
            trailImage = null;
        } else if (trailImage != null) {
            trailImage.close();
            trailImage = null;
        }
    }

    static {
        trailDirtyMinX = 128;
        trailDirtyMinZ = 128;
        trailDirtyMaxX = -1;
        trailDirtyMaxZ = -1;
        trailOriginCellX = Integer.MIN_VALUE;
        trailOriginCellZ = Integer.MIN_VALUE;
    }
}

