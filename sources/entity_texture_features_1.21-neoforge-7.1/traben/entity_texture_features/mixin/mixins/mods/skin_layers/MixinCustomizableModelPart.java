package traben.entity_texture_features.mixin.mixins.mods.skin_layers;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.tr7zw.skinlayers.render.CustomizableModelPart;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.entity_texture_features.utils.ETFVertexConsumer;

@Pseudo
@Mixin({CustomizableModelPart.class})
public abstract class MixinCustomizableModelPart {
   @Shadow
   public abstract void render(ModelPart var1, PoseStack var2, VertexConsumer var3, int var4, int var5, int var6);

   @Inject(
      method = {"render(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"},
      at = {@At("HEAD")}
   )
   private void etf$findOutIfInitialModelPart(CallbackInfo ci) {
      if (ETF.config().getConfig().use3DSkinLayerPatch) {
         ETFRenderContext.incrementCurrentModelPartDepth();
      }
   }

   @ModifyVariable(
      method = {"render(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"},
      at = @At("HEAD"),
      ordinal = 0,
      argsOnly = true
   )
   private VertexConsumer etf$modify(VertexConsumer value) {
      return value instanceof BufferBuilder builder
            && !builder.building
            && value instanceof ETFVertexConsumer etf
            && etf.etf$getRenderLayer() != null
            && etf.etf$getProvider() != null
         ? etf.etf$getProvider().getBuffer(etf.etf$getRenderLayer())
         : value;
   }

   @Inject(
      method = {"render(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"},
      at = {@At("RETURN")}
   )
   private void etf$doEmissive(ModelPart vanillaModel, PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, int color, CallbackInfo ci) {
      if (ETF.config().getConfig().use3DSkinLayerPatch) {
         if (ETFRenderContext.getCurrentModelPartDepth() != 1) {
            ETFRenderContext.decrementCurrentModelPartDepth();
         } else {
            if (ETFRenderContext.isCurrentlyRenderingEntity() && vertexConsumer instanceof ETFVertexConsumer etfVertexConsumer) {
               ETFTexture texture = etfVertexConsumer.etf$getETFTexture();
               if (texture != null && (texture.isEmissive() || texture.isEnchanted())) {
                  MultiBufferSource provider = etfVertexConsumer.etf$getProvider();
                  RenderType layer = etfVertexConsumer.etf$getRenderLayer();
                  if (provider != null && layer != null) {
                     ETFUtils2.RenderMethodForOverlay renderer = (a, b) -> this.render(vanillaModel, poseStack, a, b, overlay, color);
                     if (ETFUtils2.renderEmissive(texture, provider, renderer) | ETFUtils2.renderEnchanted(texture, provider, light, renderer)) {
                        provider.getBuffer(layer);
                     }
                  }
               }
            }

            ETFRenderContext.resetCurrentModelPartDepth();
         }
      }
   }
}
