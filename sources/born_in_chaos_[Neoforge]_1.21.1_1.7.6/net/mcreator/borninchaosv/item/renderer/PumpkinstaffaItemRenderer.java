package net.mcreator.borninchaosv.item.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashSet;
import java.util.Set;
import net.mcreator.borninchaosv.item.PumpkinstaffaItem;
import net.mcreator.borninchaosv.item.model.PumpkinstaffaItemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PumpkinstaffaItemRenderer extends GeoItemRenderer<PumpkinstaffaItem> {
   private static final float SCALE_RECIPROCAL = 0.0625F;
   protected boolean renderArms = false;
   protected MultiBufferSource currentBuffer;
   protected RenderType renderType;
   public ItemDisplayContext transformType;
   protected PumpkinstaffaItem animatable;
   private final Set<String> hiddenBones = new HashSet<>();
   private final Set<String> suppressedBones = new HashSet<>();

   public PumpkinstaffaItemRenderer() {
      super(new PumpkinstaffaItemModel());
   }

   public RenderType getRenderType(PumpkinstaffaItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(this.getTextureLocation(animatable));
   }

   public void renderByItem(
      ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int p_239207_6_
   ) {
      this.transformType = transformType;
      super.renderByItem(stack, transformType, matrixStack, bufferIn, combinedLightIn, p_239207_6_);
   }

   public void actuallyRender(
      PoseStack matrixStackIn,
      PumpkinstaffaItem animatable,
      BakedGeoModel model,
      RenderType type,
      MultiBufferSource renderTypeBuffer,
      VertexConsumer vertexBuilder,
      boolean isRenderer,
      float partialTicks,
      int packedLightIn,
      int packedOverlayIn,
      int color
   ) {
      this.currentBuffer = renderTypeBuffer;
      this.renderType = type;
      this.animatable = animatable;
      super.actuallyRender(
         matrixStackIn, animatable, model, type, renderTypeBuffer, vertexBuilder, isRenderer, partialTicks, packedLightIn, packedOverlayIn, color
      );
      if (this.renderArms) {
         this.renderArms = false;
      }
   }

   public ResourceLocation getTextureLocation(PumpkinstaffaItem instance) {
      return super.getTextureLocation(instance);
   }
}
