package com.seibel.distanthorizons.common.wrappers.block;

import com.seibel.distanthorizons.core.dataObjects.render.textures.BlockFaceTexture;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.coreapi.util.ColorUtil;
import com.seibel.distanthorizons.coreapi.util.MathUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import javax.imageio.ImageIO;
import net.minecraft.class_1058;
import net.minecraft.class_310;
import net.minecraft.class_777;
import org.jetbrains.annotations.Nullable;

public class ClientBlockStateTextureCache_fabric {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final class_310 MC = class_310.method_1551();
   public static final int TEXTURE_WIDTH_AND_HEIGHT = 16;
   private static final EDhDirection[] FACE_DIRECTIONS = new EDhDirection[]{
      EDhDirection.DOWN, EDhDirection.UP, EDhDirection.NORTH, EDhDirection.SOUTH, EDhDirection.WEST, EDhDirection.EAST
   };
   private static final ReentrantLock BAKE_LOCK = new ReentrantLock();
   private static final ConcurrentHashMap<BlockStateWrapper_fabric, BlockFaceTexture[]> TEXTURES_BY_BLOCK_WRAPPER = new ConcurrentHashMap<>();
   private static final boolean WRITE_TEXTURES_TO_FILE_FOR_DEBUGGING = false;
   private static final String TEST_TEXTURE_OUTPUT_FOLDER_PATH = "C:/Users/James_Seibel/Desktop/tex_output/";

   public static BlockFaceTexture getFaceTexture(BlockStateWrapper_fabric blockStateWrapper, EDhDirection direction) {
      BlockFaceTexture[] faceTextures = TEXTURES_BY_BLOCK_WRAPPER.computeIfAbsent(
         blockStateWrapper, newBlockStateWrapper -> bakeAllFaceTextures(newBlockStateWrapper)
      );
      return faceTextures[direction.faceIndex];
   }

   public static void clearCache() {
      TEXTURES_BY_BLOCK_WRAPPER.clear();
   }

   private static BlockFaceTexture[] bakeAllFaceTextures(BlockStateWrapper_fabric blockStateWrapper) {
      BlockFaceTexture[] faceTextures = new BlockFaceTexture[FACE_DIRECTIONS.length];
      if (!blockStateWrapper.renderTexture()) {
         for (int faceIndex = 0; faceIndex < faceTextures.length; faceIndex++) {
            faceTextures[faceIndex] = BlockFaceTexture.createSolidColor(ColorUtil.INVISIBLE);
         }

         return faceTextures;
      } else {
         try {
            BAKE_LOCK.lock();
            if (BlockStateWrapper_fabric.isAir(blockStateWrapper.blockState)) {
               BlockFaceTexture invisibleTexture = BlockFaceTexture.createSolidColor(ColorUtil.INVISIBLE);
               Arrays.fill(faceTextures, invisibleTexture);
               return faceTextures;
            }

            if (blockStateWrapper.isLiquid()) {
               BlockFaceTexture liquidTexture = bakeSpriteTexture(getParticleSprite(blockStateWrapper), true);
               Arrays.fill(faceTextures, liquidTexture);
               return faceTextures;
            }

            for (int faceIndex = 0; faceIndex < FACE_DIRECTIONS.length; faceIndex++) {
               faceTextures[faceIndex] = bakeFaceTexture(blockStateWrapper, FACE_DIRECTIONS[faceIndex]);
            }
         } catch (Exception var7) {
            LOGGER.warn(
               "Failed to bake face textures for block ["
                  + blockStateWrapper.getSerialString()
                  + "], error: ["
                  + var7.getMessage()
                  + "], block will render as hot pink.",
               var7
            );
         } finally {
            BAKE_LOCK.unlock();
         }

         for (int faceIndex = 0; faceIndex < faceTextures.length; faceIndex++) {
            if (faceTextures[faceIndex] == null) {
               faceTextures[faceIndex] = BlockFaceTexture.createErrorGridTexture();
            }
         }

         return faceTextures;
      }
   }

   private static BlockFaceTexture bakeFaceTexture(BlockStateWrapper_fabric blockStateWrapper, EDhDirection dhDirection) {
      ArrayList<class_777> rasterQuadList = new ArrayList<>();

      try {
         List<class_777> bakedQuads;
         if (blockStateWrapper.useBottomTextureForSides() && dhDirection != EDhDirection.UP && dhDirection != EDhDirection.DOWN) {
            bakedQuads = QuadWrapper_fabric.getQuadsForDirection(blockStateWrapper.blockState, EDhDirection.DOWN);
         } else if (dhDirection != EDhDirection.UP) {
            bakedQuads = QuadWrapper_fabric.getQuadsForDirection(blockStateWrapper.blockState, dhDirection);
         } else {
            bakedQuads = QuadWrapper_fabric.getUnculledQuads(blockStateWrapper.blockState);
            if (bakedQuads == null || bakedQuads.isEmpty()) {
               bakedQuads = QuadWrapper_fabric.getQuadsForDirection(blockStateWrapper.blockState, dhDirection);
            }
         }

         if (!blockStateWrapper.alwaysRasterizeTexture() && bakedQuads != null && !bakedQuads.isEmpty()) {
            class_777 faceQuad = pickFaceQuad(bakedQuads);
            class_1058 quadSprite = getQuadSprite(faceQuad);
            boolean isQuadTinted = isQuadTinted(faceQuad);
            return bakeSpriteTexture(quadSprite, isQuadTinted);
         }

         List<class_777> unculledQuads = QuadWrapper_fabric.getUnculledQuads(blockStateWrapper.blockState);
         if (unculledQuads != null) {
            rasterQuadList.addAll(unculledQuads);
         }
      } catch (Exception var7) {
      }

      if (rasterQuadList.isEmpty()) {
         class_1058 particleSprite = getParticleSprite(blockStateWrapper);
         return bakeSpriteTexture(particleSprite, false);
      } else {
         return createTextureByRasterizingQuads(blockStateWrapper, dhDirection, rasterQuadList);
      }
   }

   private static BlockFaceTexture createTextureByRasterizingQuads(
      BlockStateWrapper_fabric blockStateWrapper, EDhDirection dhDirection, ArrayList<class_777> quadList
   ) {
      ArrayList<ClientBlockStateTextureCache$QuadGeometry_fabric> geometryList = new ArrayList<>(quadList.size());
      boolean anyQuadTinted = false;
      boolean anyQuadUntinted = false;

      for (int quadIndex = 0; quadIndex < quadList.size(); quadIndex++) {
         ClientBlockStateTextureCache$QuadGeometry_fabric quadGeometry = decodeQuad(quadList.get(quadIndex), dhDirection);
         geometryList.add(quadGeometry);
         anyQuadTinted |= quadGeometry.tinted;
         anyQuadUntinted |= !quadGeometry.tinted;
      }

      boolean skipTintedQuads = anyQuadTinted && anyQuadUntinted;
      boolean textureTinted = anyQuadTinted && !anyQuadUntinted;
      geometryList.sort(Comparator.comparingDouble(ClientBlockStateTextureCache$QuadGeometry_fabric::getAverageDepth));
      int[] pixels = new int[256];
      boolean anyPixelDrawn = false;

      for (int geometryIndex = 0; geometryIndex < geometryList.size(); geometryIndex++) {
         ClientBlockStateTextureCache$QuadGeometry_fabric geometry = geometryList.get(geometryIndex);
         if (!skipTintedQuads || !geometry.tinted) {
            anyPixelDrawn |= rasterizeQuad(geometry, pixels);
         }
      }

      return !anyPixelDrawn ? bakeSpriteTexture(getParticleSprite(blockStateWrapper), false) : BlockFaceTexture.createTexture(16, 16, pixels, textureTinted);
   }

   private static BlockFaceTexture bakeSpriteTexture(@Nullable class_1058 sprite, boolean tinted) {
      if (sprite == null) {
         return BlockFaceTexture.createErrorGridTexture();
      } else {
         int spriteWidth = TextureAtlasSpriteWrapper_fabric.getWidth(sprite);
         int spriteHeight = TextureAtlasSpriteWrapper_fabric.getHeight(sprite);
         if (spriteWidth > 0 && spriteHeight > 0) {
            int[] pixels = new int[256];

            for (int u = 0; u < 16; u++) {
               for (int v = 0; v < 16; v++) {
                  int texelX = u * spriteWidth / 16;
                  int texelY = v * spriteHeight / 16;
                  pixels[v * 16 + u] = TextureAtlasSpriteWrapper_fabric.getPixelARGB(sprite, 0, texelX, texelY);
               }
            }

            return BlockFaceTexture.createTexture(16, 16, pixels, tinted);
         } else {
            return BlockFaceTexture.createErrorGridTexture();
         }
      }
   }

   private static boolean rasterizeQuad(ClientBlockStateTextureCache$QuadGeometry_fabric geometry, int[] pixels) {
      boolean anyPixelDrawn = rasterizeTriangle(geometry, 0, 1, 2, pixels);
      return anyPixelDrawn | rasterizeTriangle(geometry, 0, 2, 3, pixels);
   }

   private static boolean rasterizeTriangle(
      ClientBlockStateTextureCache$QuadGeometry_fabric geometry, int vertexIndexA, int vertexIndexB, int vertexIndexC, int[] pixels
   ) {
      float faceAU = geometry.faceUByVertex[vertexIndexA];
      float faceAV = geometry.faceVByVertex[vertexIndexA];
      float faceBU = geometry.faceUByVertex[vertexIndexB];
      float faceBV = geometry.faceVByVertex[vertexIndexB];
      float faceCU = geometry.faceUByVertex[vertexIndexC];
      float faceCV = geometry.faceVByVertex[vertexIndexC];
      float area = (faceBU - faceAU) * (faceCV - faceAV) - (faceBV - faceAV) * (faceCU - faceAU);
      if (Math.abs(area) < 1.0E-6F) {
         return false;
      } else {
         int spriteWidth = TextureAtlasSpriteWrapper_fabric.getWidth(geometry.sprite);
         int spriteHeight = TextureAtlasSpriteWrapper_fabric.getHeight(geometry.sprite);
         if (spriteWidth > 0 && spriteHeight > 0) {
            int minPixelU = Math.max((int)Math.floor(Math.min(faceAU, Math.min(faceBU, faceCU)) * 16.0F), 0);
            int maxPixelU = Math.min((int)Math.ceil(Math.max(faceAU, Math.max(faceBU, faceCU)) * 16.0F), 15);
            int minPixelV = Math.max((int)Math.floor(Math.min(faceAV, Math.min(faceBV, faceCV)) * 16.0F), 0);
            int maxPixelV = Math.min((int)Math.ceil(Math.max(faceAV, Math.max(faceBV, faceCV)) * 16.0F), 15);
            boolean anyPixelDrawn = false;

            for (int pixelV = minPixelV; pixelV <= maxPixelV; pixelV++) {
               for (int pixelU = minPixelU; pixelU <= maxPixelU; pixelU++) {
                  float sampleU = (pixelU + 0.5F) / 16.0F;
                  float sampleV = (pixelV + 0.5F) / 16.0F;
                  float weightB = ((sampleU - faceAU) * (faceCV - faceAV) - (sampleV - faceAV) * (faceCU - faceAU)) / area;
                  float weightC = ((faceBU - faceAU) * (sampleV - faceAV) - (faceBV - faceAV) * (sampleU - faceAU)) / area;
                  float weightA = 1.0F - weightB - weightC;
                  if (!(weightA < 0.0F) && !(weightB < 0.0F) && !(weightC < 0.0F)) {
                     float spriteU = weightA * geometry.spriteUByVertex[vertexIndexA]
                        + weightB * geometry.spriteUByVertex[vertexIndexB]
                        + weightC * geometry.spriteUByVertex[vertexIndexC];
                     float spriteV = weightA * geometry.spriteVByVertex[vertexIndexA]
                        + weightB * geometry.spriteVByVertex[vertexIndexB]
                        + weightC * geometry.spriteVByVertex[vertexIndexC];
                     int texelX = MathUtil.clamp(0, (int)(spriteU * spriteWidth), spriteWidth - 1);
                     int texelY = MathUtil.clamp(0, (int)(spriteV * spriteHeight), spriteHeight - 1);
                     int argbSourceColor = TextureAtlasSpriteWrapper_fabric.getPixelARGB(geometry.sprite, 0, texelX, texelY);
                     if (ColorUtil.getAlpha(argbSourceColor) != 0) {
                        int pixelIndex = pixelV * 16 + pixelU;
                        pixels[pixelIndex] = blendSourceOver(argbSourceColor, pixels[pixelIndex]);
                        anyPixelDrawn = true;
                     }
                  }
               }
            }

            return anyPixelDrawn;
         } else {
            return false;
         }
      }
   }

   private static int blendSourceOver(int sourceArgb, int destArgb) {
      int sourceAlpha = ColorUtil.getAlpha(sourceArgb);
      if (sourceAlpha == 255) {
         return sourceArgb;
      } else {
         int destAlpha = ColorUtil.getAlpha(destArgb);
         int inverseSourceAlpha = 255 - sourceAlpha;
         int outAlpha = sourceAlpha + destAlpha * inverseSourceAlpha / 255;
         if (outAlpha == 0) {
            return ColorUtil.INVISIBLE;
         } else {
            int outRed = (ColorUtil.getRed(sourceArgb) * sourceAlpha + ColorUtil.getRed(destArgb) * destAlpha * inverseSourceAlpha / 255) / outAlpha;
            int outGreen = (ColorUtil.getGreen(sourceArgb) * sourceAlpha + ColorUtil.getGreen(destArgb) * destAlpha * inverseSourceAlpha / 255) / outAlpha;
            int outBlue = (ColorUtil.getBlue(sourceArgb) * sourceAlpha + ColorUtil.getBlue(destArgb) * destAlpha * inverseSourceAlpha / 255) / outAlpha;
            return ColorUtil.argbToInt(outAlpha, outRed, outGreen, outBlue);
         }
      }
   }

   private static ClientBlockStateTextureCache$QuadGeometry_fabric decodeQuad(class_777 quad, EDhDirection dhDirection) {
      ClientBlockStateTextureCache$QuadGeometry_fabric geometry = new ClientBlockStateTextureCache$QuadGeometry_fabric();
      geometry.sprite = getQuadSprite(quad);
      geometry.tinted = isQuadTinted(quad);

      for (int vertexIndex = 0; vertexIndex < 4; vertexIndex++) {
         int[] vertexData = quad.method_3357();
         int vertexOffset = vertexIndex * 8;
         float x = Float.intBitsToFloat(vertexData[vertexOffset]);
         float y = Float.intBitsToFloat(vertexData[vertexOffset + 1]);
         float z = Float.intBitsToFloat(vertexData[vertexOffset + 2]);
         float u = Float.intBitsToFloat(vertexData[vertexOffset + 4]);
         float v = Float.intBitsToFloat(vertexData[vertexOffset + 5]);
         float minU = TextureAtlasSpriteWrapper_fabric.getMinU(geometry.sprite);
         float maxU = TextureAtlasSpriteWrapper_fabric.getMaxU(geometry.sprite);
         float minV = TextureAtlasSpriteWrapper_fabric.getMinV(geometry.sprite);
         float maxV = TextureAtlasSpriteWrapper_fabric.getMaxV(geometry.sprite);
         geometry.spriteUByVertex[vertexIndex] = maxU != minU ? (u - minU) / (maxU - minU) : 0.0F;
         geometry.spriteVByVertex[vertexIndex] = maxV != minV ? (v - minV) / (maxV - minV) : 0.0F;
         projectOntoQuadFace(dhDirection, x, y, z, geometry, vertexIndex);
      }

      return geometry;
   }

   private static void projectOntoQuadFace(
      EDhDirection dhDirection, float x, float y, float z, ClientBlockStateTextureCache$QuadGeometry_fabric geometry, int vertexIndex
   ) {
      float faceU;
      float faceV;
      float depth;
      switch (dhDirection) {
         case UP:
            faceU = x;
            faceV = z;
            depth = y;
            break;
         case DOWN:
            faceU = x;
            faceV = 1.0F - z;
            depth = 1.0F - y;
            break;
         case NORTH:
            faceU = 1.0F - x;
            faceV = 1.0F - y;
            depth = 1.0F - z;
            break;
         case SOUTH:
            faceU = x;
            faceV = 1.0F - y;
            depth = z;
            break;
         case WEST:
            faceU = z;
            faceV = 1.0F - y;
            depth = 1.0F - x;
            break;
         case EAST:
            faceU = 1.0F - z;
            faceV = 1.0F - y;
            depth = x;
            break;
         default:
            throw new IllegalArgumentException("No face projection for direction [" + dhDirection + "].");
      }

      geometry.faceUByVertex[vertexIndex] = faceU;
      geometry.faceVByVertex[vertexIndex] = faceV;
      geometry.depthByVertex[vertexIndex] = depth;
   }

   @Nullable
   private static class_1058 getParticleSprite(BlockStateWrapper_fabric blockStateWrapper) {
      if (blockStateWrapper.blockState == null) {
         return null;
      } else {
         try {
            return MC.method_1554().method_4743().method_3339(blockStateWrapper.blockState);
         } catch (Exception var2) {
            LOGGER.warn("Failed to get particle sprite for block [" + blockStateWrapper.getSerialString() + "], error: [" + var2.getMessage() + "].", var2);
            return null;
         }
      }
   }

   private static class_777 pickFaceQuad(List<class_777> quadList) {
      for (int i = 0; i < quadList.size(); i++) {
         if (!isQuadTinted(quadList.get(i))) {
            return quadList.get(i);
         }
      }

      return quadList.get(0);
   }

   private static class_1058 getQuadSprite(class_777 quad) {
      return quad.method_35788();
   }

   private static boolean isQuadTinted(class_777 quad) {
      return quad.method_3360();
   }

   private static void writeTopAndNorthTexturesToFile(BlockStateWrapper_fabric blockStateWrapper, BlockFaceTexture[] blockFaceTextures) {
      for (int i = 0; i < blockFaceTextures.length; i++) {
         EDhDirection dir = FACE_DIRECTIONS[i];
         if (dir == EDhDirection.UP || dir == EDhDirection.NORTH) {
            BlockFaceTexture faceTexture = blockFaceTextures[dir.faceIndex];
            String blockSerial = blockStateWrapper.getSerialString().replace(":", "-").replace("{", "[").replace("}", "]");
            String filePath = "C:/Users/James_Seibel/Desktop/tex_output/" + blockSerial + "_" + dir + ".png";

            try {
               writeArgbPixelsToPng(faceTexture, filePath);
            } catch (Exception var8) {
               LOGGER.error("failed to save file [" + filePath + "], error: [" + var8.getMessage() + "]");
            }
         }
      }
   }

   public static void writeArgbPixelsToPng(BlockFaceTexture faceTexture, String outputPath) throws IOException {
      int scale = 8;
      BufferedImage image = new BufferedImage(faceTexture.width * scale, faceTexture.height * scale, 2);

      for (int u = 0; u < faceTexture.width; u++) {
         for (int v = 0; v < faceTexture.height; v++) {
            int argb = faceTexture.argbPixels[v * faceTexture.width + u];

            for (int uScale = 0; uScale < scale; uScale++) {
               for (int vScale = 0; vScale < scale; vScale++) {
                  image.setRGB(u * scale + uScale, v * scale + vScale, argb);
               }
            }
         }
      }

      File outputFile = new File(outputPath);
      outputFile.mkdirs();
      if (!ImageIO.write(image, "png", outputFile)) {
         throw new IOException("No PNG writer found, javax.imageio may not be available.");
      }
   }
}
