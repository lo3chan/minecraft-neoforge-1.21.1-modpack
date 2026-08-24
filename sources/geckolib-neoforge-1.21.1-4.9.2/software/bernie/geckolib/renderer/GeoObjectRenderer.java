package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Matrix4f;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayersContainer;
import software.bernie.geckolib.util.RenderUtil;

public class GeoObjectRenderer<T extends GeoAnimatable> implements GeoRenderer<T> {
   protected final GeoRenderLayersContainer<T> renderLayers = new GeoRenderLayersContainer<>(this);
   protected final GeoModel<T> model;
   protected T animatable;
   protected float scaleWidth = 1.0F;
   protected float scaleHeight = 1.0F;
   protected Matrix4f objectRenderTranslations = new Matrix4f();
   protected Matrix4f modelRenderTranslations = new Matrix4f();

   public GeoObjectRenderer(GeoModel<T> model) {
      this.model = model;
   }

   @Override
   public GeoModel<T> getGeoModel() {
      return this.model;
   }

   @Override
   public T getAnimatable() {
      return this.animatable;
   }

   @Override
   public ResourceLocation getTextureLocation(T animatable) {
      return GeoRenderer.super.getTextureLocation(animatable);
   }

   @Override
   public List<GeoRenderLayer<T>> getRenderLayers() {
      return this.renderLayers.getRenderLayers();
   }

   public GeoObjectRenderer<T> addRenderLayer(GeoRenderLayer<T> renderLayer) {
      this.renderLayers.addLayer(renderLayer);
      return this;
   }

   public GeoObjectRenderer<T> withScale(float scale) {
      return this.withScale(scale, scale);
   }

   public GeoObjectRenderer<T> withScale(float scaleWidth, float scaleHeight) {
      this.scaleWidth = scaleWidth;
      this.scaleHeight = scaleHeight;
      return this;
   }

   @Internal
   public void render(
      PoseStack poseStack,
      T animatable,
      @Nullable MultiBufferSource bufferSource,
      @Nullable RenderType renderType,
      @Nullable VertexConsumer buffer,
      int packedLight,
      float partialTick
   ) {
      this.animatable = animatable;
      if (buffer == null) {
         bufferSource = Minecraft.getInstance().levelRenderer.renderBuffers.bufferSource();
      }

      this.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, 0.0F, partialTick, packedLight);
   }

   @Override
   public void preRender(
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
      this.objectRenderTranslations = new Matrix4f(poseStack.last().pose());
      this.scaleModelForRender(this.scaleWidth, this.scaleHeight, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
      poseStack.translate(0.5F, 0.51F, 0.5F);
   }

   @Override
   public void actuallyRender(
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
      poseStack.pushPose();
      if (!isReRender) {
         AnimationState<T> animationState = new AnimationState<>(animatable, 0.0F, 0.0F, partialTick, false);
         long instanceId = this.getInstanceId(animatable);
         GeoModel<T> currentModel = this.getGeoModel();
         currentModel.addAdditionalStateData(animatable, instanceId, animationState::setData);
         currentModel.handleAnimations(animatable, instanceId, animationState, partialTick);
      }

      this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());
      if (buffer != null) {
         GeoRenderer.super.actuallyRender(
            poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour
         );
      }

      poseStack.popPose();
   }

   @Override
   public void doPostRenderCleanup() {
      this.animatable = null;
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
      if (bone.isTrackingMatrices()) {
         Matrix4f poseState = new Matrix4f(poseStack.last().pose());
         bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
         bone.setLocalSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.objectRenderTranslations));
      }

      GeoRenderer.super.renderRecursively(
         poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour
      );
   }

   @Override
   public void updateAnimatedTextureFrame(T animatable) {
      AnimatableTexture.setAndUpdate(this.getTextureLocation(animatable));
   }

   @Override
   public void fireCompileRenderLayersEvent() {
      GeckoLibServices.Client.EVENTS.fireCompileObjectRenderLayers(this);
   }

   @Override
   public boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
      return GeckoLibServices.Client.EVENTS.fireObjectPreRender(this, poseStack, model, bufferSource, partialTick, packedLight);
   }

   @Override
   public void firePostRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
      GeckoLibServices.Client.EVENTS.fireObjectPostRender(this, poseStack, model, bufferSource, partialTick, packedLight);
   }
}
