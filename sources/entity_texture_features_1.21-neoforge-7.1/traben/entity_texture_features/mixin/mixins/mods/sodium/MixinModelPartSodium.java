package traben.entity_texture_features.mixin.mixins.mods.sodium;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.entity_texture_features.utils.ETFVertexConsumer;

@Pseudo
@Mixin(
   targets = {"me/jellysquid/mods/sodium/client/render/immediate/model/EntityRenderer"}
)
public abstract class MixinModelPartSodium {
   @Unique
   private static boolean once = true;

   @Shadow
   public static void render(PoseStack matrixStack, VertexBufferWriter writer, ModelPart part, int light, int overlay, int color) {
   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private static void etf$findOutIfInitialModelPart(
      PoseStack matrixStack, VertexBufferWriter writer, ModelPart part, int light, int overlay, int color, CallbackInfo ci
   ) {
      ETFRenderContext.incrementCurrentModelPartDepth();
   }

   @Unique
   private static VertexBufferWriter etf$convertOrLog(VertexConsumer consumer) {
      if (consumer instanceof VertexBufferWriter writer && writer.canUseIntrinsics()) {
         return writer;
      } else {
         if (once) {
            once = false;
            ETFUtils2.logWarn("Bad consumer for sodium MixinModelPartSodium");
         }

         return null;
      }
   }

   @Inject(
      method = {"render"},
      at = {@At("RETURN")}
   )
   private static void etf$doEmissiveIfInitialPart(
      PoseStack matrixStack, VertexBufferWriter writer, ModelPart part, int light, int overlay, int color, CallbackInfo ci
   ) {
      if (ETFRenderContext.getCurrentModelPartDepth() > 1) {
         ETFRenderContext.decrementCurrentModelPartDepth();
      } else {
         if (ETFRenderContext.isCurrentlyRenderingEntity() && writer instanceof ETFVertexConsumer etfVertexConsumer) {
            ETFTexture texture = etfVertexConsumer.etf$getETFTexture();
            if (texture != null && (texture.isEmissive() || texture.isEnchanted())) {
               MultiBufferSource provider = etfVertexConsumer.etf$getProvider();
               RenderType layer = etfVertexConsumer.etf$getRenderLayer();
               if (provider != null && layer != null) {
                  ETFUtils2.RenderMethodForOverlay renderer = (a, b) -> {
                     VertexBufferWriter a2 = etf$convertOrLog(a);
                     if (a2 != null) {
                        render(matrixStack, a2, part, b, overlay, color);
                     }
                  };
                  if (ETFUtils2.renderEmissive(texture, provider, renderer) | ETFUtils2.renderEnchanted(texture, provider, light, renderer)) {
                  }
               }
            }
         }

         ETFRenderContext.resetCurrentModelPartDepth();
      }
   }

   @ModifyVariable(
      method = {"render"},
      at = @At("HEAD"),
      ordinal = 0,
      argsOnly = true
   )
   private static VertexBufferWriter etf$modify(VertexBufferWriter value) {
      if (value instanceof BufferBuilder builder
         && !builder.building
         && value instanceof ETFVertexConsumer etf
         && etf.etf$getRenderLayer() != null
         && etf.etf$getProvider() != null) {
         VertexConsumer a = etf.etf$getProvider().getBuffer(etf.etf$getRenderLayer());
         VertexBufferWriter a2 = etf$convertOrLog(a);
         if (a2 != null) {
            return a2;
         }
      }

      return value;
   }
}
