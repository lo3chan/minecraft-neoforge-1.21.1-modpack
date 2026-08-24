package software.bernie.geckolib.util;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.GeoReplacedEntityRenderer;

public final class RenderUtil {
   public static void translateMatrixToBone(PoseStack poseStack, GeoBone bone) {
      poseStack.translate(-bone.getPosX() / 16.0F, bone.getPosY() / 16.0F, bone.getPosZ() / 16.0F);
   }

   public static void rotateMatrixAroundBone(PoseStack poseStack, GeoBone bone) {
      if (bone.getRotZ() != 0.0F) {
         poseStack.mulPose(Axis.ZP.rotation(bone.getRotZ()));
      }

      if (bone.getRotY() != 0.0F) {
         poseStack.mulPose(Axis.YP.rotation(bone.getRotY()));
      }

      if (bone.getRotX() != 0.0F) {
         poseStack.mulPose(Axis.XP.rotation(bone.getRotX()));
      }
   }

   public static void rotateMatrixAroundCube(PoseStack poseStack, GeoCube cube) {
      Vec3 rotation = cube.rotation();
      poseStack.mulPose(new Quaternionf().rotationXYZ(0.0F, 0.0F, (float)rotation.z()));
      poseStack.mulPose(new Quaternionf().rotationXYZ(0.0F, (float)rotation.y(), 0.0F));
      poseStack.mulPose(new Quaternionf().rotationXYZ((float)rotation.x(), 0.0F, 0.0F));
   }

   public static void scaleMatrixForBone(PoseStack poseStack, GeoBone bone) {
      poseStack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
   }

   public static void translateToPivotPoint(PoseStack poseStack, GeoCube cube) {
      Vec3 pivot = cube.pivot();
      poseStack.translate(pivot.x() / 16.0, pivot.y() / 16.0, pivot.z() / 16.0);
   }

   public static void translateToPivotPoint(PoseStack poseStack, GeoBone bone) {
      poseStack.translate(bone.getPivotX() / 16.0F, bone.getPivotY() / 16.0F, bone.getPivotZ() / 16.0F);
   }

   public static void translateAwayFromPivotPoint(PoseStack poseStack, GeoCube cube) {
      Vec3 pivot = cube.pivot();
      poseStack.translate(-pivot.x() / 16.0, -pivot.y() / 16.0, -pivot.z() / 16.0);
   }

   public static void translateAwayFromPivotPoint(PoseStack poseStack, GeoBone bone) {
      poseStack.translate(-bone.getPivotX() / 16.0F, -bone.getPivotY() / 16.0F, -bone.getPivotZ() / 16.0F);
   }

   public static void translateAndRotateMatrixForBone(PoseStack poseStack, GeoBone bone) {
      translateToPivotPoint(poseStack, bone);
      rotateMatrixAroundBone(poseStack, bone);
   }

   public static void prepMatrixForBone(PoseStack poseStack, GeoBone bone) {
      translateMatrixToBone(poseStack, bone);
      translateToPivotPoint(poseStack, bone);
      rotateMatrixAroundBone(poseStack, bone);
      scaleMatrixForBone(poseStack, bone);
      translateAwayFromPivotPoint(poseStack, bone);
   }

   public static Matrix4f invertAndMultiplyMatrices(Matrix4f baseMatrix, Matrix4f inputMatrix) {
      inputMatrix = new Matrix4f(inputMatrix);
      inputMatrix.invert();
      inputMatrix.mul(baseMatrix);
      return inputMatrix;
   }

   public static void faceRotation(PoseStack poseStack, Entity animatable, float partialTick) {
      poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) - 90.0F));
      poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));
   }

   public static Matrix4f translateMatrix(Matrix4f matrix, Vector3f vector) {
      return matrix.add(new Matrix4f().m30(vector.x).m31(vector.y).m32(vector.z));
   }

   @Nullable
   public static IntIntPair getTextureDimensions(ResourceLocation texture) {
      if (texture == null) {
         return null;
      } else {
         AbstractTexture originalTexture = null;
         Minecraft mc = Minecraft.getInstance();

         try {
            originalTexture = (AbstractTexture)mc.submit(() -> mc.getTextureManager().getTexture(texture)).get();
         } catch (Exception var6) {
            GeckoLibConstants.LOGGER.warn("Failed to load image for id {}", texture);
            var6.printStackTrace();
         }

         if (originalTexture == null) {
            return null;
         } else {
            NativeImage image = null;

            try {
               image = originalTexture instanceof DynamicTexture dynamicTexture
                  ? dynamicTexture.getPixels()
                  : NativeImage.read(((Resource)mc.getResourceManager().getResource(texture).get()).open());
            } catch (Exception var5) {
               GeckoLibConstants.LOGGER.error("Failed to read image for id {}", texture);
               var5.printStackTrace();
            }

            return image == null ? null : IntIntImmutablePair.of(image.getWidth(), image.getHeight());
         }
      }
   }

   public static double getCurrentSystemTick() {
      return System.nanoTime() / 1000000.0 / 50.0;
   }

   public static double getCurrentTick() {
      return Blaze3D.getTime() * 20.0;
   }

   public static float booleanToFloat(boolean input) {
      return input ? 1.0F : 0.0F;
   }

   public static Vec3 arrayToVec(double[] array) {
      return new Vec3(array[0], array[1], array[2]);
   }

   public static void matchModelPartRot(ModelPart from, GeoBone to) {
      to.updateRotation(-from.xRot, -from.yRot, from.zRot);
   }

   public static void fixInvertedFlatCube(GeoCube cube, Vector3f normal) {
      if (normal.x() < 0.0F && (cube.size().y() == 0.0 || cube.size().z() == 0.0)) {
         normal.mul(-1.0F, 1.0F, 1.0F);
      }

      if (normal.y() < 0.0F && (cube.size().x() == 0.0 || cube.size().z() == 0.0)) {
         normal.mul(1.0F, -1.0F, 1.0F);
      }

      if (normal.z() < 0.0F && (cube.size().x() == 0.0 || cube.size().y() == 0.0)) {
         normal.mul(1.0F, 1.0F, -1.0F);
      }
   }

   public static float getDirectionAngle(Direction direction) {
      return switch (direction) {
         case SOUTH -> 90.0F;
         case NORTH -> 270.0F;
         case EAST -> 180.0F;
         default -> 0.0F;
      };
   }

   public static double lerpYaw(double delta, double start, double end) {
      start = Mth.wrapDegrees(start);
      end = Mth.wrapDegrees(end);
      double diff = start - end;
      end = !(diff > 180.0) && !(diff < -180.0) ? end : start + Math.copySign(360.0 - Math.abs(diff), diff);
      return Mth.lerp(delta, start, end);
   }

   @Nullable
   public static GeoModel<?> getGeoModelForEntityType(EntityType<?> entityType) {
      EntityRenderer<?> renderer = (EntityRenderer<?>)Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(entityType);
      return renderer instanceof GeoRenderer<?> geoRenderer ? geoRenderer.getGeoModel() : null;
   }

   @Nullable
   public static GeoAnimatable getReplacedAnimatable(EntityType<?> entityType) {
      EntityRenderer<?> renderer = (EntityRenderer<?>)Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(entityType);
      return renderer instanceof GeoReplacedEntityRenderer<?, ?> replacedEntityRenderer ? replacedEntityRenderer.getAnimatable() : null;
   }

   @Nullable
   public static GeoModel<?> getGeoModelForEntity(Entity entity) {
      return Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity) instanceof GeoRenderer<?> geoRenderer ? geoRenderer.getGeoModel() : null;
   }

   @Nullable
   public static GeoModel<?> getGeoModelForItem(ItemStack item) {
      return GeckoLibServices.Client.ITEM_RENDERING.getGeoModelForItem(item);
   }

   @Nullable
   public static GeoModel<?> getGeoModelForBlock(BlockEntity blockEntity) {
      return Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity) instanceof GeoRenderer<?> geoRenderer
         ? geoRenderer.getGeoModel()
         : null;
   }

   @Nullable
   public static GeoModel<?> getGeoModelForArmor(ItemStack stack) {
      return GeckoLibServices.Client.ITEM_RENDERING.getGeoModelForArmor(stack);
   }
}
