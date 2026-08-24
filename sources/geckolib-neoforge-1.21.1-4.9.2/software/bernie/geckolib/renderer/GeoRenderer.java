package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer.Double;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.OutlineBufferSource.EntityOutlineGenerator;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.loading.math.MolangQueries;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.Color;
import software.bernie.geckolib.util.RenderUtil;

public interface GeoRenderer<T extends GeoAnimatable> {
   GeoModel<T> getGeoModel();

   T getAnimatable();

   default ResourceLocation getTextureLocation(T animatable) {
      return this.getGeoModel().getTextureResource(animatable, this);
   }

   default List<GeoRenderLayer<T>> getRenderLayers() {
      return List.of();
   }

   @Nullable
   default RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      return this.getGeoModel().getRenderType(animatable, texture);
   }

   default Color getRenderColor(T animatable, float partialTick, int packedLight) {
      return Color.WHITE;
   }

   default int getPackedOverlay(T animatable, float u, float partialTick) {
      return OverlayTexture.NO_OVERLAY;
   }

   default long getInstanceId(T animatable) {
      return animatable.hashCode();
   }

   default float getMotionAnimThreshold(T animatable) {
      return 0.015F;
   }

   default void defaultRender(
      PoseStack poseStack,
      T animatable,
      MultiBufferSource bufferSource,
      @Nullable RenderType renderType,
      @Nullable VertexConsumer buffer,
      float yaw,
      float partialTick,
      int packedLight
   ) {
      poseStack.pushPose();
      int renderColor = this.getRenderColor(animatable, partialTick, packedLight).argbInt();
      int packedOverlay = this.getPackedOverlay(animatable, 0.0F, partialTick);
      BakedGeoModel model = this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(animatable, this));
      if (renderType == null) {
         renderType = this.getRenderType(animatable, this.getTextureLocation(animatable), bufferSource, partialTick);
      }

      if (buffer == null && renderType != null) {
         buffer = bufferSource.getBuffer(renderType);
      }

      this.preRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, renderColor);
      if (this.firePreRenderEvent(poseStack, model, bufferSource, partialTick, packedLight)) {
         this.preApplyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
         this.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, renderColor);
         this.applyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
         this.postRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, renderColor);
         this.firePostRenderEvent(poseStack, model, bufferSource, partialTick, packedLight);
      }

      poseStack.popPose();
      this.renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, renderColor);
      this.doPostRenderCleanup();
      MolangQueries.clearActor();
   }

   default void reRender(
      BakedGeoModel model,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      T animatable,
      RenderType renderType,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      poseStack.pushPose();
      this.preRender(poseStack, animatable, model, bufferSource, buffer, true, partialTick, packedLight, packedOverlay, colour);
      this.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, true, partialTick, packedLight, packedOverlay, colour);
      this.postRender(poseStack, animatable, model, bufferSource, buffer, true, partialTick, packedLight, packedOverlay, colour);
      poseStack.popPose();
   }

   default void actuallyRender(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel model,
      @Nullable RenderType renderType,
      MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      if (buffer == null) {
         if (renderType == null) {
            return;
         }

         buffer = bufferSource.getBuffer(renderType);
      }

      this.updateAnimatedTextureFrame(animatable);

      for (GeoBone group : model.topLevelBones()) {
         this.renderRecursively(poseStack, animatable, group, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
      }
   }

   default void preApplyRenderLayers(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel model,
      @Nullable RenderType renderType,
      MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      for (GeoRenderLayer<T> renderLayer : this.getRenderLayers()) {
         renderLayer.preRender(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
      }
   }

   default void applyRenderLayersForBone(
      PoseStack poseStack,
      T animatable,
      GeoBone bone,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      for (GeoRenderLayer<T> renderLayer : this.getRenderLayers()) {
         renderLayer.renderForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
      }
   }

   default void applyRenderLayers(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel model,
      @Nullable RenderType renderType,
      MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      for (GeoRenderLayer<T> renderLayer : this.getRenderLayers()) {
         renderLayer.render(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
      }
   }

   default void preRender(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
   }

   default void postRender(
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
   }

   default void renderFinal(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel model,
      MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
   }

   default void doPostRenderCleanup() {
   }

   default void renderRecursively(
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
      RenderUtil.prepMatrixForBone(poseStack, bone);
      buffer = this.checkAndRefreshBuffer(isReRender, buffer, bufferSource, renderType);
      this.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, colour);
      if (!isReRender) {
         this.applyRenderLayersForBone(poseStack, this.getAnimatable(), bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
      }

      this.renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
      poseStack.popPose();
   }

   default void renderCubesOfBone(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, int colour) {
      if (!bone.isHidden()) {
         for (GeoCube cube : bone.getCubes()) {
            poseStack.pushPose();
            this.renderCube(poseStack, cube, buffer, packedLight, packedOverlay, colour);
            poseStack.popPose();
         }
      }
   }

   default void renderChildBones(
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
      if (!bone.isHidingChildren()) {
         for (GeoBone childBone : bone.getChildBones()) {
            this.renderRecursively(
               poseStack, animatable, childBone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour
            );
         }
      }
   }

   default void renderCube(PoseStack poseStack, GeoCube cube, VertexConsumer buffer, int packedLight, int packedOverlay, int colour) {
      RenderUtil.translateToPivotPoint(poseStack, cube);
      RenderUtil.rotateMatrixAroundCube(poseStack, cube);
      RenderUtil.translateAwayFromPivotPoint(poseStack, cube);
      Matrix3f normalisedPoseState = poseStack.last().normal();
      Matrix4f poseState = new Matrix4f(poseStack.last().pose());

      for (GeoQuad quad : cube.quads()) {
         if (quad != null) {
            Vector3f normal = normalisedPoseState.transform(new Vector3f(quad.normal()));
            RenderUtil.fixInvertedFlatCube(cube, normal);
            this.createVerticesOfQuad(quad, poseState, normal, buffer, packedLight, packedOverlay, colour);
         }
      }
   }

   default void createVerticesOfQuad(GeoQuad quad, Matrix4f poseState, Vector3f normal, VertexConsumer buffer, int packedLight, int packedOverlay, int colour) {
      for (GeoVertex vertex : quad.vertices()) {
         Vector3f position = vertex.position();
         Vector4f vector4f = poseState.transform(new Vector4f(position.x(), position.y(), position.z(), 1.0F));
         buffer.addVertex(
            vector4f.x(), vector4f.y(), vector4f.z(), colour, vertex.texU(), vertex.texV(), packedOverlay, packedLight, normal.x(), normal.y(), normal.z()
         );
      }
   }

   void fireCompileRenderLayersEvent();

   boolean firePreRenderEvent(PoseStack var1, BakedGeoModel var2, MultiBufferSource var3, float var4, int var5);

   void firePostRenderEvent(PoseStack var1, BakedGeoModel var2, MultiBufferSource var3, float var4, int var5);

   default void scaleModelForRender(
      float widthScale,
      float heightScale,
      PoseStack poseStack,
      T animatable,
      BakedGeoModel model,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      if (!isReRender && (widthScale != 1.0F || heightScale != 1.0F)) {
         poseStack.scale(widthScale, heightScale, widthScale);
      }
   }

   void updateAnimatedTextureFrame(T var1);

   @Internal
   default VertexConsumer checkAndRefreshBuffer(boolean isReRender, VertexConsumer buffer, MultiBufferSource bufferSource, RenderType renderType) {
      if (isReRender) {
         return buffer;
      } else {
         Objects.requireNonNull(buffer);

         return (VertexConsumer)(switch (buffer) {
            case BufferBuilder builder when !builder.building -> bufferSource.getBuffer(renderType);
            case EntityOutlineGenerator outlines when this.bufferNeedsRefresh(outlines.delegate()) -> new EntityOutlineGenerator(
               bufferSource.getBuffer(renderType), outlines.color()
            );
            case Double pair when this.bufferNeedsRefresh(pair.first) || this.bufferNeedsRefresh(pair.second) -> new Double(
               this.bufferNeedsRefresh(pair.first) ? bufferSource.getBuffer(renderType) : pair.first,
               this.bufferNeedsRefresh(pair.second) ? bufferSource.getBuffer(renderType) : pair.second
            );
            default -> buffer;
         });
      }
   }

   @Deprecated(
      forRemoval = true
   )
   @Internal
   private boolean bufferNeedsRefresh(VertexConsumer buffer) {
      return switch (buffer) {
         case BufferBuilder builder -> !builder.building;
         case EntityOutlineGenerator outlines -> this.bufferNeedsRefresh(outlines.delegate());
         case Double pair -> this.bufferNeedsRefresh(pair.first) || this.bufferNeedsRefresh(pair.second);
         default -> false;
      };
   }
}
