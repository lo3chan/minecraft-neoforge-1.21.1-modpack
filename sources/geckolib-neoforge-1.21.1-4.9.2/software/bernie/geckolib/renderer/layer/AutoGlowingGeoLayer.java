package software.bernie.geckolib.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.texture.AutoGlowingTexture;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.util.ClientUtil;

public class AutoGlowingGeoLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {
   public AutoGlowingGeoLayer(GeoRenderer<T> renderer) {
      super(renderer);
   }

   @Deprecated(
      forRemoval = true
   )
   protected RenderType getRenderType(T animatable) {
      return this.getRenderType(animatable, null);
   }

   @Nullable
   protected RenderType getRenderType(T animatable, @Nullable MultiBufferSource bufferSource) {
      if (animatable instanceof Entity entity) {
         boolean invisible = entity.isInvisible();
         ResourceLocation texture = AutoGlowingTexture.getEmissiveResource(this.getTextureResource(animatable));
         if (invisible && !entity.isInvisibleTo(ClientUtil.getClientPlayer())) {
            return RenderType.itemEntityTranslucentCull(texture);
         } else if (Minecraft.getInstance().shouldEntityAppearGlowing(entity)) {
            return invisible ? RenderType.outline(texture) : AutoGlowingTexture.getOutlineRenderType(this.getTextureResource(animatable));
         } else {
            return invisible ? null : AutoGlowingTexture.getRenderType(this.getTextureResource(animatable));
         }
      } else {
         return AutoGlowingTexture.getRenderType(this.getTextureResource(animatable));
      }
   }

   @Override
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
      renderType = this.getRenderType(animatable);
      if (renderType != null) {
         this.getRenderer()
            .reRender(
               bakedModel,
               poseStack,
               bufferSource,
               animatable,
               renderType,
               bufferSource.getBuffer(renderType),
               partialTick,
               15728640,
               packedOverlay,
               this.getRenderer().getRenderColor(animatable, partialTick, packedLight).argbInt()
            );
      }
   }
}
