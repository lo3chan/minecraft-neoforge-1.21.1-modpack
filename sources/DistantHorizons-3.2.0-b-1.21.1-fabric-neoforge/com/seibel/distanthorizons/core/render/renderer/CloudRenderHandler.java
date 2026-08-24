package com.seibel.distanthorizons.core.render.renderer;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiBlockMaterial;
import com.seibel.distanthorizons.api.interfaces.render.IDhApiCustomRenderObjectFactory;
import com.seibel.distanthorizons.api.interfaces.render.IDhApiRenderableBoxGroup;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;
import com.seibel.distanthorizons.api.objects.math.DhApiVec3d;
import com.seibel.distanthorizons.api.objects.render.DhApiRenderableBox;
import com.seibel.distanthorizons.api.objects.render.DhApiRenderableBoxGroupShading;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.level.IDhClientLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.math.DhVec3d;
import com.seibel.distanthorizons.core.util.math.DhVec3f;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhGenericRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class CloudRenderHandler {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   private static final IDhApiCustomRenderObjectFactory GENERIC_OBJECT_FACTORY = SingletonInjector.INSTANCE.get(IDhApiCustomRenderObjectFactory.class);
   private static final String CLOUD_RESOURCE_TEXTURE_PATH = "assets/distanthorizons/textures/clouds.png";
   private static final boolean DEBUG_BORDER_COLORS = false;
   private static final int CLOUD_BOX_WIDTH = 128;
   private static final int CLOUD_BOX_THICKNESS = 32;
   private static final int CLOUD_INSTANCE_RADIUS_COUNT = 5;
   private static final int CLOUD_LAYER_COUNT = 3;
   private static final float MOVE_SPEED_IN_BLOCKS_PER_SECOND = 6.0F;
   private final IDhApiRenderableBoxGroup[][][] boxGroupByOffset = new IDhApiRenderableBoxGroup[3][11][11];
   private final IDhClientLevel level;
   private final IDhGenericRenderer renderer;
   private final DhVec3d[] cullingCorners = new DhVec3d[]{new DhVec3d(), new DhVec3d(), new DhVec3d(), new DhVec3d()};

   public CloudRenderHandler(IDhClientLevel level, IDhGenericRenderer renderer) {
      this.level = level;
      this.renderer = renderer;
      boolean[][] cloudLocations = new boolean[1][1];

      try {
         cloudLocations = getCloudsFromTexture();
      } catch (FileNotFoundException var18) {
         LOGGER.error(var18.getMessage(), var18);
      } catch (IOException var19) {
         LOGGER.error("Unexpected issue getting cloud texture, error: [" + var19.getMessage() + "].", var19);
      }

      if (cloudLocations.length != 0 && cloudLocations.length != cloudLocations[0].length) {
         LOGGER.warn("Non-square cloud texture found, some parts of the texture will be clipped off.");
      }

      int textureWidth = cloudLocations.length;
      ArrayList<DhApiRenderableBox> boxList = new ArrayList<>(512);

      for (int x = 0; x < textureWidth; x++) {
         for (int z = 0; z < textureWidth; z++) {
            if (cloudLocations[x][z]) {
               int startZ = z;
               int endZ = z;
               int endX = x + 1;

               while (endZ < textureWidth && cloudLocations[x][endZ]) {
                  endZ++;
               }

               z = endZ - 1;

               for (int currentX = x + 1; currentX < textureWidth; currentX++) {
                  boolean canMergeInXDir = true;

                  for (int adjacentZ = startZ; adjacentZ < endZ; adjacentZ++) {
                     if (!cloudLocations[currentX][adjacentZ]) {
                        canMergeInXDir = false;
                        break;
                     }
                  }

                  if (!canMergeInXDir) {
                     break;
                  }

                  for (int currentZ = startZ; currentZ < endZ; currentZ++) {
                     cloudLocations[currentX][currentZ] = false;
                  }

                  endX = currentX + 1;
               }

               int minXBlockPos = x * 128;
               int minZBlockPos = startZ * 128;
               int maxXBlockPos = endX * 128;
               int maxZBlockPos = endZ * 128;
               Color color = new Color(255, 255, 255, 255);
               DhApiRenderableBox box = new DhApiRenderableBox(
                  new DhApiVec3d(minXBlockPos, 0.0, minZBlockPos), new DhApiVec3d(maxXBlockPos, 32.0, maxZBlockPos), color, EDhApiBlockMaterial.UNKNOWN
               );
               boxList.add(box);
            }
         }
      }

      DhApiRenderableBoxGroupShading cloudShading = DhApiRenderableBoxGroupShading.getUnshaded();
      cloudShading.north = 0.9F;
      cloudShading.south = cloudShading.north;
      cloudShading.east = 0.8F;
      cloudShading.west = cloudShading.east;
      cloudShading.top = 1.0F;
      cloudShading.bottom = 0.7F;

      for (int y = 2; y >= 0; y--) {
         for (int x = -5; x <= 5; x++) {
            for (int zx = -5; zx <= 5; zx++) {
               IDhApiRenderableBoxGroup boxGroup = GENERIC_OBJECT_FACTORY.createRelativePositionedGroup(
                  "DistantHorizons:Clouds", new DhApiVec3d(0.0, 0.0, 0.0), boxList
               );
               boxGroup.setBlockLight(15);
               boxGroup.setSkyLight(15);
               boxGroup.setSsaoEnabled(false);
               boxGroup.setShading(cloudShading);
               CloudRenderHandler.CloudParams cloudParams = new CloudRenderHandler.CloudParams(textureWidth, y, x, zx);
               boxGroup.setPreRenderFunc(renderParam -> this.preRender(renderParam, cloudParams));
               this.renderer.add(boxGroup);
               this.boxGroupByOffset[y][x + 5][zx + 5] = boxGroup;
            }
         }
      }
   }

   private void preRender(DhApiRenderParam renderParam, CloudRenderHandler.CloudParams cloudParams) {
      IDhApiRenderableBoxGroup boxGroup = this.boxGroupByOffset[cloudParams.instanceOffsetY][cloudParams.instanceOffsetX + 5][cloudParams.instanceOffsetZ + 5];
      boolean renderClouds = Config.Client.Advanced.Graphics.GenericRendering.enableCloudRendering.get();
      boolean renderSingleCloudLayer = !Config.Client.Advanced.Graphics.GenericRendering.enableMultiLayerClouds.get();
      if (renderSingleCloudLayer && cloudParams.instanceOffsetY != 0) {
         renderClouds = false;
      }

      boxGroup.setActive(renderClouds);
      if (renderClouds) {
         IClientLevelWrapper clientLevelWrapper = this.level.getClientLevelWrapper();
         if (clientLevelWrapper != null) {
            long currentTime = System.currentTimeMillis();
            float deltaTime = (float)(currentTime - cloudParams.lastFrameTime) / 1000.0F;
            cloudParams.lastFrameTime = currentTime;
            float deltaX = (6.0F + cloudParams.heightSpeedOffset) * deltaTime;
            cloudParams.deltaOffsetX -= deltaX;
            cloudParams.deltaOffsetX = cloudParams.deltaOffsetX % cloudParams.widthInBlocks;
            int cameraPosX = (int)MC_RENDER.getCameraExactPosition().x;
            int cameraPosZ = (int)MC_RENDER.getCameraExactPosition().z;
            if (cameraPosX < 0) {
               cameraPosX -= cloudParams.widthInBlocks;
            }

            if (cameraPosZ < 0) {
               cameraPosZ -= cloudParams.widthInBlocks;
            }

            int cloudInstanceOffsetCountX = cameraPosX / cloudParams.widthInBlocks;
            int cloudInstanceOffsetCountZ = cameraPosZ / cloudParams.widthInBlocks;
            float instanceOffsetX = cloudInstanceOffsetCountX * cloudParams.widthInBlocks;
            float instanceOffsetZ = cloudInstanceOffsetCountZ * cloudParams.widthInBlocks;
            float newMinPosX = cloudParams.deltaOffsetX
               + cloudParams.instanceOffsetX * cloudParams.widthInBlocks
               + instanceOffsetX
               + cloudParams.halfWidthInBlocks;
            float newMinPosY = this.level.getLevelWrapper().getMaxHeight() + 100 + cloudParams.heightOffset;
            float newMinPosZ = cloudParams.deltaOffsetZ
               + cloudParams.instanceOffsetZ * cloudParams.widthInBlocks
               + instanceOffsetZ
               + cloudParams.halfWidthInBlocks;
            boolean cullCloud = this.shouldCloudBeCulled(newMinPosX, newMinPosY, newMinPosZ, cloudParams);
            if (cullCloud) {
               boxGroup.setActive(false);
            }

            boxGroup.setOriginBlockPos(new DhApiVec3d(newMinPosX, newMinPosY, newMinPosZ));
            Color newCloudColor = clientLevelWrapper.getCloudColor(renderParam.partialTicks);
            if (!newCloudColor.equals(cloudParams.previousColor)) {
               for (DhApiRenderableBox box : boxGroup) {
                  box.color = newCloudColor;
               }

               cloudParams.previousColor = newCloudColor;
               boxGroup.triggerBoxChange();
            }
         }
      }
   }

   private float mixColors(float x, float y, float a) {
      return x * (1.0F - a) + y * a;
   }

   private boolean shouldCloudBeCulled(float minPosX, float minPosY, float minPosZ, CloudRenderHandler.CloudParams cloudParams) {
      if (cloudParams.instanceOffsetX >= -1 && cloudParams.instanceOffsetX <= 1 && cloudParams.instanceOffsetZ >= -1 && cloudParams.instanceOffsetZ <= 1) {
         return false;
      } else {
         this.cullingCorners[0].x = minPosX;
         this.cullingCorners[0].y = minPosY;
         this.cullingCorners[0].z = minPosZ;
         this.cullingCorners[1].x = minPosX;
         this.cullingCorners[1].y = minPosY;
         this.cullingCorners[1].z = minPosZ + cloudParams.widthInBlocks;
         this.cullingCorners[2].x = minPosX + cloudParams.widthInBlocks;
         this.cullingCorners[2].y = minPosY;
         this.cullingCorners[2].z = minPosZ;
         this.cullingCorners[3].x = minPosX + cloudParams.widthInBlocks;
         this.cullingCorners[3].y = minPosY;
         this.cullingCorners[3].z = minPosZ + cloudParams.widthInBlocks;
         DhVec3d cameraPos = MC_RENDER.getCameraExactPosition();
         DhVec3f cameraLookAtVector = MC_RENDER.getLookAtVector();
         cameraLookAtVector.normalize();
         double renderDistance = Math.max(Config.Client.Advanced.Graphics.Quality.lodChunkRenderDistanceRadius.get(), 256) * 16 * 1.5;
         boolean allOutsideRenderDistance = true;
         boolean allBehindCamera = true;

         for (DhVec3d corner : this.cullingCorners) {
            DhVec3d cornerNoHeight = new DhVec3d(corner);
            cornerNoHeight.y = 0.0;
            DhVec3d cameraPosNoHeight = new DhVec3d(cameraPos);
            cameraPosNoHeight.y = 0.0;
            double cornerDistance = cornerNoHeight.getDistance(cameraPosNoHeight);
            if (cornerDistance <= renderDistance) {
               allOutsideRenderDistance = false;
            }

            DhVec3f toCorner = new DhVec3f((float)(corner.x - cameraPos.x), (float)(corner.y - cameraPos.y), (float)(corner.z - cameraPos.z));
            toCorner.normalize();
            if (cameraLookAtVector.dotProduct(toCorner) > 0.0F) {
               allBehindCamera = false;
            }
         }

         return allOutsideRenderDistance || allBehindCamera;
      }
   }

   private static boolean[][] getCloudsFromTexture() throws FileNotFoundException, IOException {
      ClassLoader loader = CloudRenderHandler.class.getClassLoader();
      boolean[][] whitePixels = null;
      InputStream imageInputStream = loader.getResourceAsStream("assets/distanthorizons/textures/clouds.png");

      try {
         if (imageInputStream == null) {
            throw new FileNotFoundException("Unable to find cloud texture at resource path: [assets/distanthorizons/textures/clouds.png].");
         }

         BufferedImage image = ImageIO.read(imageInputStream);
         int width = image.getWidth();
         int height = image.getHeight();
         whitePixels = new boolean[width][height];

         for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
               Color color = new Color(image.getRGB(x, z));
               whitePixels[x][z] = color.equals(Color.WHITE);
            }
         }
      } catch (Throwable var10) {
         if (imageInputStream != null) {
            try {
               imageInputStream.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (imageInputStream != null) {
         imageInputStream.close();
      }

      return whitePixels;
   }

   private static class CloudParams {
      public final int textureWidth;
      public final int widthInBlocks;
      public final int halfWidthInBlocks;
      public final int instanceOffsetY;
      public final int instanceOffsetX;
      public final int instanceOffsetZ;
      public final int heightOffset;
      public final float heightSpeedOffset;
      public float deltaOffsetX = 0.0F;
      public float deltaOffsetZ = 0.0F;
      public long lastFrameTime = System.currentTimeMillis();
      public Color previousColor = Color.WHITE;

      public CloudParams(int textureWidth, int instanceOffsetY, int instanceOffsetX, int instanceOffsetZ) {
         this.textureWidth = textureWidth;
         this.widthInBlocks = this.textureWidth * 128;
         this.halfWidthInBlocks = this.widthInBlocks / 2;
         this.instanceOffsetY = instanceOffsetY;
         this.instanceOffsetX = instanceOffsetX;
         this.instanceOffsetZ = instanceOffsetZ;
         this.heightOffset = instanceOffsetY * 512;
         this.heightSpeedOffset = instanceOffsetY * 10.0F;
         this.deltaOffsetX = this.widthInBlocks * instanceOffsetY * 0.75F;
         this.deltaOffsetZ = this.widthInBlocks * instanceOffsetY * 1.5F;
      }
   }
}
