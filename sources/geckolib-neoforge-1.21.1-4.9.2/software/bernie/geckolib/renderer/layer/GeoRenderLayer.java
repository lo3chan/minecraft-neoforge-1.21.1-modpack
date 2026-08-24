package software.bernie.geckolib.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public abstract class GeoRenderLayer<T extends GeoAnimatable> {
   protected final GeoRenderer<T> renderer;

   public GeoRenderLayer(GeoRenderer<T> entityRendererIn) {
      this.renderer = entityRendererIn;
   }

   public GeoModel<T> getGeoModel() {
      return this.renderer.getGeoModel();
   }

   public BakedGeoModel getDefaultBakedModel(T animatable) {
      return this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(animatable, this.getRenderer()));
   }

   public GeoRenderer<T> getRenderer() {
      return this.renderer;
   }

   protected ResourceLocation getTextureResource(T animatable) {
      return this.getRenderer().getTextureLocation(animatable);
   }

   public void preRender(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel bakedModel,
      @Nullable RenderType renderType,
      MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
   }

   public void render(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel bakedModel,
      @Nullable RenderType renderType,
      MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
   }

   public void renderForBone(
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
   }
}
