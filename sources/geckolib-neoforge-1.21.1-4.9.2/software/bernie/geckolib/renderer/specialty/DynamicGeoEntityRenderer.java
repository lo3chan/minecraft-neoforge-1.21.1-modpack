package software.bernie.geckolib.renderer.specialty;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtil;

public abstract class DynamicGeoEntityRenderer<T extends Entity & GeoAnimatable> extends GeoEntityRenderer<T> {
   protected static Map<ResourceLocation, IntIntPair> TEXTURE_DIMENSIONS_CACHE = new Object2ObjectOpenHashMap();
   protected ResourceLocation textureOverride = null;

   public DynamicGeoEntityRenderer(Context renderManager, GeoModel<T> model) {
      super(renderManager, model);
   }

   @Nullable
   protected ResourceLocation getTextureOverrideForBone(GeoBone bone, T animatable, float partialTick) {
      return null;
   }

   @Nullable
   protected RenderType getRenderTypeOverrideForBone(
      GeoBone bone, T animatable, ResourceLocation texturePath, MultiBufferSource bufferSource, float partialTick
   ) {
      return null;
   }

   protected boolean boneRenderOverride(
      PoseStack poseStack,
      GeoBone bone,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      return false;
   }

   @Override
   public void renderRecursively(
      PoseStack poseStack,
      T animatable,
      GeoBone bone,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      poseStack.pushPose();
      RenderUtil.translateMatrixToBone(poseStack, bone);
      RenderUtil.translateToPivotPoint(poseStack, bone);
      RenderUtil.rotateMatrixAroundBone(poseStack, bone);
      RenderUtil.scaleMatrixForBone(poseStack, bone);
      if (bone.isTrackingMatrices()) {
         Matrix4f poseState = new Matrix4f(poseStack.last().pose());
         Matrix4f localMatrix = RenderUtil.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations);
         bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
         localMatrix.translate(new Vector3f(this.getRenderOffset(this.animatable, 1.0F).toVector3f()));
         bone.setLocalSpaceMatrix(localMatrix);
         Matrix4f worldState = new Matrix4f(localMatrix);
         worldState.translate(new Vector3f(this.animatable.position().toVector3f()));
         bone.setWorldSpaceMatrix(worldState);
      }

      RenderUtil.translateAwayFromPivotPoint(poseStack, bone);
      this.textureOverride = this.getTextureOverrideForBone(bone, this.animatable, partialTick);
      ResourceLocation texture = this.textureOverride == null ? this.getTextureLocation(this.animatable) : this.textureOverride;
      RenderType renderTypeOverride = this.getRenderTypeOverrideForBone(bone, this.animatable, texture, bufferSource, partialTick);
      if (texture != null && renderTypeOverride == null) {
         renderTypeOverride = this.getRenderType(this.animatable, texture, bufferSource, partialTick);
      }

      if (renderTypeOverride != null) {
         buffer = bufferSource.getBuffer(renderTypeOverride);
      }

      if (!this.boneRenderOverride(poseStack, bone, bufferSource, buffer, partialTick, packedLight, packedOverlay, colour)) {
         super.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, colour);
      }

      if (renderTypeOverride != null) {
         buffer = bufferSource.getBuffer(renderType);
      }

      if (!isReRender) {
         this.applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
      }

      buffer = this.checkAndRefreshBuffer(isReRender, buffer, bufferSource, renderType);
      super.renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
      poseStack.popPose();
   }

   public void postRender(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel model,
      MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      this.textureOverride = null;
      super.postRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
   }

   @Override
   public void createVerticesOfQuad(GeoQuad quad, Matrix4f poseState, Vector3f normal, VertexConsumer buffer, int packedLight, int packedOverlay, int colour) {
      if (this.textureOverride == null) {
         super.createVerticesOfQuad(quad, poseState, normal, buffer, packedLight, packedOverlay, colour);
      } else {
         IntIntPair boneTextureSize = this.computeTextureSize(this.textureOverride);
         IntIntPair entityTextureSize = this.computeTextureSize(this.getTextureLocation(this.animatable));
         if (boneTextureSize != null && entityTextureSize != null) {
            for (GeoVertex vertex : quad.vertices()) {
               Vector4f vector4f = poseState.transform(new Vector4f(vertex.position().x(), vertex.position().y(), vertex.position().z(), 1.0F));
               float texU = vertex.texU() * entityTextureSize.firstInt() / boneTextureSize.firstInt();
               float texV = vertex.texV() * entityTextureSize.secondInt() / boneTextureSize.secondInt();
               buffer.addVertex(vector4f.x(), vector4f.y(), vector4f.z(), colour, texU, texV, packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
            }
         } else {
            super.createVerticesOfQuad(quad, poseState, normal, buffer, packedLight, packedOverlay, colour);
         }
      }
   }

   protected IntIntPair computeTextureSize(ResourceLocation texture) {
      return TEXTURE_DIMENSIONS_CACHE.computeIfAbsent(texture, RenderUtil::getTextureDimensions);
   }
}
