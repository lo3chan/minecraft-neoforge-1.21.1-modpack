package com.leonardoinc22.shortgrass.client.render;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

final class GrassTrailField {
   private static final ResourceLocation TRAIL_TEXTURE = ResourceLocation.fromNamespaceAndPath("grassiergrass", "textures/effect/trail_field");
   private static final int TRAIL_TEXTURE_SIZE = 128;
   private static final float TRAIL_WORLD_SIZE = 48.0F;
   private static final float TRAIL_CELL_SIZE = 0.375F;
   private static final int TRAIL_RECENTER_STEP_CELLS = 8;
   private static final float TRAIL_STAMP_RADIUS = 0.5F;
   private static final float TRAIL_STAMP_STRENGTH = 0.06F;
   private static final float TRAIL_RECOVERY_PER_FRAME = 0.998F;
   private static final float TRAIL_REFERENCE_ENTITY_WIDTH = 0.6F;
   private static final float TRAIL_MIN_FOOTPRINT_SCALE = 0.35F;
   private static final float TRAIL_MAX_FOOTPRINT_SCALE = 4.0F;
   private static final float TRAIL_VERTICAL_REACH = 1.0F;
   private static final float TRAIL_MAX_SURFACE_GAP = 1.15F;
   private static final float TRAIL_SHADER_STRENGTH = 0.6F;
   private static final float[] TRAIL_DIR_X = new float[16384];
   private static final float[] TRAIL_DIR_Z = new float[16384];
   private static final float[] TRAIL_STRENGTH = new float[16384];
   private static final int[] TRAIL_ACTIVE_INDICES = new int[16384];
   private static final boolean[] TRAIL_ACTIVE = new boolean[16384];
   private static NativeImage trailImage;
   private static DynamicTexture trailTexture;
   private static boolean trailTextureDirty;
   private static int trailDirtyMinX = 128;
   private static int trailDirtyMinZ = 128;
   private static int trailDirtyMaxX = -1;
   private static int trailDirtyMaxZ = -1;
   private static int trailActiveCount;
   private static int trailOriginCellX = -2147483648;
   private static int trailOriginCellZ = -2147483648;
   private static float trailOriginWorldX;
   private static float trailOriginWorldZ;

   static void update(ClientLevel level, Vec3 cameraPos, float partialTick) {
      ensureTrailTexture();
      recenterTrailField(cameraPos);
      decayTrailField();
      AABB trailBounds = new AABB(
         trailOriginWorldX, cameraPos.y - 1.0 - 16.0, trailOriginWorldZ, trailOriginWorldX + 48.0F, cameraPos.y + 1.0 + 16.0, trailOriginWorldZ + 48.0F
      );

      for (Entity entity : level.entitiesForRendering()) {
         if (entity.isAlive() && trailBounds.intersects(entity.getBoundingBox())) {
            Vec3 position = entity.getPosition(partialTick);
            AABB bounds = entity.getBoundingBox();
            float horizontalSize = (float)Math.max(bounds.getXsize(), bounds.getZsize());
            float radiusScale = Mth.clamp(horizontalSize / 0.6F, 0.35F, 4.0F);
            float radius = Math.max(0.5F * radiusScale, entity.getBbWidth() * 0.9F);
            float footY = (float)(position.y + bounds.minY - entity.getY());
            if (nearGrassSurface(level, (float)position.x, (float)position.z, footY, radius)) {
               float strengthScale = Mth.clamp(0.65F + radiusScale * 0.35F, 0.5F, 1.6F);
               stampTrail((float)position.x, (float)position.z, radius, strengthScale);
            }
         }
      }

      if (trailTextureDirty || hasTrailDirtyBounds()) {
         uploadTrailField();
      }
   }

   private static void ensureTrailTexture() {
      if (trailImage == null || trailTexture == null) {
         trailImage = new NativeImage(128, 128, true);
         trailTexture = new DynamicTexture(trailImage);
         Minecraft.getInstance().getTextureManager().register(TRAIL_TEXTURE, trailTexture);
         trailTextureDirty = true;
      }
   }

   private static boolean recenterTrailField(Vec3 cameraPos) {
      int newOriginCellX = Mth.floor((cameraPos.x - 24.0) / 0.375);
      int newOriginCellZ = Mth.floor((cameraPos.z - 24.0) / 0.375);
      if (trailOriginCellX != -2147483648 && Math.abs(newOriginCellX - trailOriginCellX) < 8 && Math.abs(newOriginCellZ - trailOriginCellZ) < 8) {
         return false;
      } else {
         if (trailOriginCellX != -2147483648 && Math.abs(newOriginCellX - trailOriginCellX) < 128 && Math.abs(newOriginCellZ - trailOriginCellZ) < 128) {
            shiftTrailArrays(trailOriginCellX - newOriginCellX, trailOriginCellZ - newOriginCellZ);
         } else {
            clearTrailArrays();
         }

         trailOriginCellX = newOriginCellX;
         trailOriginCellZ = newOriginCellZ;
         trailOriginWorldX = trailOriginCellX * 0.375F;
         trailOriginWorldZ = trailOriginCellZ * 0.375F;
         markTrailDirtyFull();
         return true;
      }
   }

   private static void shiftTrailArrays(int offsetX, int offsetZ) {
      shiftTrailArray(TRAIL_DIR_X, offsetX, offsetZ);
      shiftTrailArray(TRAIL_DIR_Z, offsetX, offsetZ);
      shiftTrailArray(TRAIL_STRENGTH, offsetX, offsetZ);
      rebuildActiveTrailCells();
   }

   private static void shiftTrailArray(float[] values, int offsetX, int offsetZ) {
      int copyWidth = 128 - Math.abs(offsetX);
      int copyHeight = 128 - Math.abs(offsetZ);
      if (copyWidth > 0 && copyHeight > 0) {
         int srcX = Math.max(0, -offsetX);
         int dstX = Math.max(0, offsetX);
         int srcZ = Math.max(0, -offsetZ);
         int dstZ = Math.max(0, offsetZ);
         if (offsetZ > 0) {
            for (int row = copyHeight - 1; row >= 0; row--) {
               System.arraycopy(values, (srcZ + row) * 128 + srcX, values, (dstZ + row) * 128 + dstX, copyWidth);
            }
         } else {
            for (int row = 0; row < copyHeight; row++) {
               System.arraycopy(values, (srcZ + row) * 128 + srcX, values, (dstZ + row) * 128 + dstX, copyWidth);
            }
         }

         clearTrailRows(values, 0, dstZ);
         clearTrailRows(values, dstZ + copyHeight, 128);

         for (int z = dstZ; z < dstZ + copyHeight; z++) {
            int rowStart = z * 128;
            if (dstX > 0) {
               Arrays.fill(values, rowStart, rowStart + dstX, 0.0F);
            }

            if (dstX + copyWidth < 128) {
               Arrays.fill(values, rowStart + dstX + copyWidth, rowStart + 128, 0.0F);
            }
         }
      } else {
         Arrays.fill(values, 0.0F);
      }
   }

   private static void clearTrailRows(float[] values, int startZ, int endZ) {
      for (int z = startZ; z < endZ; z++) {
         int rowStart = z * 128;
         Arrays.fill(values, rowStart, rowStart + 128, 0.0F);
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
         if (oldStrength <= 0.0F) {
            removeActiveTrailCellAt(i);
         } else {
            float strength = oldStrength * 0.998F;
            if (strength < 0.004F) {
               TRAIL_DIR_X[index] = 0.0F;
               TRAIL_DIR_Z[index] = 0.0F;
               TRAIL_STRENGTH[index] = 0.0F;
               removeActiveTrailCellAt(i);
            } else {
               TRAIL_STRENGTH[index] = strength;
               i++;
            }

            int x = index % 128;
            int z = index / 128;
            dirtyMinX = Math.min(dirtyMinX, x);
            dirtyMinZ = Math.min(dirtyMinZ, z);
            dirtyMaxX = Math.max(dirtyMaxX, x);
            dirtyMaxZ = Math.max(dirtyMaxZ, z);
         }
      }

      if (dirtyMaxX >= dirtyMinX) {
         markTrailDirty(dirtyMinX, dirtyMinZ, dirtyMaxX, dirtyMaxZ);
      }
   }

   private static boolean nearGrassSurface(ClientLevel level, float x, float z, float footY, float radius) {
      float sampleOffset = Math.min(radius * 0.55F, 0.65F);
      return nearGrassSurfaceAt(level, x, z, footY)
         || nearGrassSurfaceAt(level, x + sampleOffset, z, footY)
         || nearGrassSurfaceAt(level, x - sampleOffset, z, footY)
         || nearGrassSurfaceAt(level, x, z + sampleOffset, footY)
         || nearGrassSurfaceAt(level, x, z - sampleOffset, footY);
   }

   private static boolean nearGrassSurfaceAt(ClientLevel level, float x, float z, float footY) {
      MutableBlockPos pos = new MutableBlockPos();
      int blockX = Mth.floor(x);
      int blockZ = Mth.floor(z);
      int maxY = Mth.floor(footY + 0.25F);
      int minY = Mth.floor(footY - 1.15F) - 1;

      for (int y = maxY; y >= minY; y--) {
         pos.set(blockX, y, blockZ);
         BlockState state = level.getBlockState(pos);
         float surfaceY;
         if (state.is(Blocks.GRASS_BLOCK)) {
            surfaceY = y + 1.0F;
         } else {
            if (!HiddenGrass.isTrailVegetation(state)) {
               continue;
            }

            surfaceY = y;
         }

         if (Math.abs(footY - surfaceY) <= 1.15F) {
            return true;
         }
      }

      return false;
   }

   private static boolean stampTrail(float entityX, float entityZ, float radius, float strengthScale) {
      boolean changed = false;
      int minX = Mth.floor((entityX - radius - trailOriginWorldX) / 0.375F);
      int maxX = Mth.floor((entityX + radius - trailOriginWorldX) / 0.375F);
      int minZ = Mth.floor((entityZ - radius - trailOriginWorldZ) / 0.375F);
      int maxZ = Mth.floor((entityZ + radius - trailOriginWorldZ) / 0.375F);
      minX = Mth.clamp(minX, 0, 127);
      maxX = Mth.clamp(maxX, 0, 127);
      minZ = Mth.clamp(minZ, 0, 127);
      maxZ = Mth.clamp(maxZ, 0, 127);

      for (int z = minZ; z <= maxZ; z++) {
         float worldZ = trailOriginWorldZ + (z + 0.5F) * 0.375F;

         for (int x = minX; x <= maxX; x++) {
            float worldX = trailOriginWorldX + (x + 0.5F) * 0.375F;
            float awayX = worldX - entityX;
            float awayZ = worldZ - entityZ;
            float distance = Mth.sqrt(awayX * awayX + awayZ * awayZ);
            if (!(distance >= radius)) {
               int index = z * 128 + x;
               float radialX = distance > 0.001F ? awayX / distance : TRAIL_DIR_X[index];
               float radialZ = distance > 0.001F ? awayZ / distance : TRAIL_DIR_Z[index];
               float dirLength = Math.max(Mth.sqrt(radialX * radialX + radialZ * radialZ), 0.001F);
               float dirX = radialX / dirLength;
               float dirZ = radialZ / dirLength;
               float falloff = 1.0F - smoothstep(0.15F, 1.0F, distance / radius);
               float newStrength = falloff * 0.06F * strengthScale;
               float oldStrength = TRAIL_STRENGTH[index];
               float combinedStrength = Mth.clamp(oldStrength + newStrength * (1.0F - oldStrength), 0.0F, 1.0F);
               float combinedX = TRAIL_DIR_X[index] * oldStrength + dirX * newStrength;
               float combinedZ = TRAIL_DIR_Z[index] * oldStrength + dirZ * newStrength;
               float combinedLength = Mth.sqrt(combinedX * combinedX + combinedZ * combinedZ);
               if (combinedLength > 0.001F) {
                  TRAIL_DIR_X[index] = combinedX / combinedLength;
                  TRAIL_DIR_Z[index] = combinedZ / combinedLength;
               }

               TRAIL_STRENGTH[index] = combinedStrength;
               if (combinedStrength > 0.0F) {
                  addActiveTrailCell(index);
               }

               changed = true;
            }
         }
      }

      if (changed) {
         markTrailDirty(minX, minZ, maxX, maxZ);
      }

      return changed;
   }

   private static float smoothstep(float edge0, float edge1, float value) {
      float t = Mth.clamp((value - edge0) / (edge1 - edge0), 0.0F, 1.0F);
      return t * t * (3.0F - 2.0F * t);
   }

   private static void uploadTrailField() {
      int minX = trailTextureDirty ? 0 : trailDirtyMinX;
      int minZ = trailTextureDirty ? 0 : trailDirtyMinZ;
      int maxX = trailTextureDirty ? 127 : trailDirtyMaxX;
      int maxZ = trailTextureDirty ? 127 : trailDirtyMaxZ;
      if (maxX >= minX && maxZ >= minZ) {
         for (int z = minZ; z <= maxZ; z++) {
            for (int x = minX; x <= maxX; x++) {
               int index = z * 128 + x;
               int red = Math.round(Mth.clamp(TRAIL_DIR_X[index] * 0.5F + 0.5F, 0.0F, 1.0F) * 255.0F);
               int green = Math.round(Mth.clamp(TRAIL_DIR_Z[index] * 0.5F + 0.5F, 0.0F, 1.0F) * 255.0F);
               int blue = Math.round(Mth.clamp(TRAIL_STRENGTH[index], 0.0F, 1.0F) * 255.0F);
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
         clearTrailDirtyBounds();
      }
   }

   private static void clearTrailArrays() {
      Arrays.fill(TRAIL_DIR_X, 0.0F);
      Arrays.fill(TRAIL_DIR_Z, 0.0F);
      Arrays.fill(TRAIL_STRENGTH, 0.0F);
      Arrays.fill(TRAIL_ACTIVE, false);
      trailActiveCount = 0;
      markTrailDirtyFull();
   }

   private static void rebuildActiveTrailCells() {
      Arrays.fill(TRAIL_ACTIVE, false);
      trailActiveCount = 0;

      for (int i = 0; i < TRAIL_STRENGTH.length; i++) {
         if (TRAIL_STRENGTH[i] > 0.0F) {
            addActiveTrailCell(i);
         }
      }
   }

   private static void addActiveTrailCell(int index) {
      if (!TRAIL_ACTIVE[index]) {
         TRAIL_ACTIVE[index] = true;
         TRAIL_ACTIVE_INDICES[trailActiveCount++] = index;
      }
   }

   private static void removeActiveTrailCellAt(int activeIndex) {
      int index = TRAIL_ACTIVE_INDICES[activeIndex];
      TRAIL_ACTIVE[index] = false;
      int lastActiveIndex = --trailActiveCount;
      if (activeIndex != lastActiveIndex) {
         TRAIL_ACTIVE_INDICES[activeIndex] = TRAIL_ACTIVE_INDICES[lastActiveIndex];
      }
   }

   private static void markTrailDirtyFull() {
      trailTextureDirty = true;
      clearTrailDirtyBounds();
   }

   private static void markTrailDirty(int minX, int minZ, int maxX, int maxZ) {
      trailDirtyMinX = Math.min(trailDirtyMinX, Mth.clamp(minX, 0, 127));
      trailDirtyMinZ = Math.min(trailDirtyMinZ, Mth.clamp(minZ, 0, 127));
      trailDirtyMaxX = Math.max(trailDirtyMaxX, Mth.clamp(maxX, 0, 127));
      trailDirtyMaxZ = Math.max(trailDirtyMaxZ, Mth.clamp(maxZ, 0, 127));
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
      trailOriginCellX = -2147483648;
      trailOriginCellZ = -2147483648;
      trailOriginWorldX = 0.0F;
      trailOriginWorldZ = 0.0F;
      clearTrailArrays();
      if (clearTexture && trailImage != null && trailTexture != null) {
         uploadTrailField();
      }
   }

   static ResourceLocation textureLocation() {
      return TRAIL_TEXTURE;
   }

   static int textureId() {
      ensureTrailTexture();
      return trailTexture.getId();
   }

   static float originOffsetX(Vec3 cameraPos) {
      return trailOriginWorldX - (float)cameraPos.x;
   }

   static float originOffsetZ(Vec3 cameraPos) {
      return trailOriginWorldZ - (float)cameraPos.z;
   }

   static float inverseWorldSize() {
      return 0.020833334F;
   }

   static double trailOriginWorldXExact() {
      return trailOriginCellX * 0.375;
   }

   static double trailOriginWorldZExact() {
      return trailOriginCellZ * 0.375;
   }

   static float shaderStrength() {
      return 0.6F;
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
}
