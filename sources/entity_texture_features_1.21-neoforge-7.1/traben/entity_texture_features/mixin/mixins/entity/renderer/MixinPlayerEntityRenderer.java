package traben.entity_texture_features.mixin.mixins.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.player.ETFPlayerFeatureRenderer;
import traben.entity_texture_features.features.player.ETFPlayerSkinHolder;
import traben.entity_texture_features.features.player.ETFPlayerTexture;

@Mixin({PlayerRenderer.class})
public abstract class MixinPlayerEntityRenderer
   extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>
   implements ETFPlayerSkinHolder {
   @Unique
   private ETFPlayerTexture etf$ETFPlayerTexture = null;

   public MixinPlayerEntityRenderer(Context ctx, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
      super(ctx, model, shadowRadius);
   }

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void etf$addFeatures(Context ctx, boolean slim, CallbackInfo ci) {
      this.addLayer(new ETFPlayerFeatureRenderer(this));
   }

   @Inject(
      method = {"renderHand"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/model/PlayerModel;setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
         shift = Shift.AFTER
      )},
      cancellable = true
   )
   private void etf$redirectNicely(
      PoseStack matrices, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer player, ModelPart arm, ModelPart sleeve, CallbackInfo ci
   ) {
      if (ETF.config().getConfig().skinFeaturesEnabled) {
         ETFPlayerTexture thisETFPlayerTexture = ETFManager.getInstance().getPlayerTexture(player, player.getSkin().texture());
         if (thisETFPlayerTexture != null && thisETFPlayerTexture.hasFeatures) {
            ResourceLocation etfTexture = thisETFPlayerTexture.getBaseTextureIdentifierOrNullForVanilla(player);
            if (etfTexture != null) {
               ETFRenderContext.preventRenderLayerTextureModify();
               arm.xRot = 0.0F;
               sleeve.xRot = 0.0F;
               VertexConsumer vc1 = vertexConsumers.getBuffer(RenderType.entityTranslucent(etfTexture));
               this.etf$renderOnce(matrices, vc1, light, player, arm, sleeve);
               ETFRenderContext.startSpecialRenderOverlayPhase();
               ResourceLocation emissive = thisETFPlayerTexture.getBaseTextureEmissiveIdentifierOrNullForNone();
               if (emissive != null) {
                  VertexConsumer vc2 = vertexConsumers.getBuffer(RenderType.entityTranslucent(emissive));
                  this.etf$renderOnce(matrices, vc2, 15728882, player, arm, sleeve);
               }

               if (thisETFPlayerTexture.baseEnchantIdentifier != null) {
                  VertexConsumer vc3 = ItemRenderer.getArmorFoilBuffer(
                     vertexConsumers, RenderType.armorCutoutNoCull(thisETFPlayerTexture.baseEnchantIdentifier), true
                  );
                  this.etf$renderOnce(matrices, vc3, light, player, arm, sleeve);
               }

               ETFRenderContext.endSpecialRenderOverlayPhase();
               ETFRenderContext.allowRenderLayerTextureModify();
               ci.cancel();
            }
         }
      }
   }

   @Unique
   private void etf$renderOnce(PoseStack matrixStack, VertexConsumer consumer, int light, AbstractClientPlayer player, ModelPart arm, ModelPart sleeve) {
      arm.render(matrixStack, consumer, light, OverlayTexture.NO_OVERLAY);
      sleeve.render(matrixStack, consumer, light, OverlayTexture.NO_OVERLAY);
   }

   @Inject(
      method = {"getTextureLocation(Lnet/minecraft/client/player/AbstractClientPlayer;)Lnet/minecraft/resources/ResourceLocation;"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void etf$getTexture(AbstractClientPlayer abstractClientPlayerEntity, CallbackInfoReturnable<ResourceLocation> cir) {
      if (ETF.config().getConfig().skinFeaturesEnabled) {
         this.etf$ETFPlayerTexture = ETFManager.getInstance().getPlayerTexture(abstractClientPlayerEntity, (ResourceLocation)cir.getReturnValue());
         if (this.etf$ETFPlayerTexture != null && this.etf$ETFPlayerTexture.hasFeatures) {
            ResourceLocation texture = this.etf$ETFPlayerTexture.getBaseTextureIdentifierOrNullForVanilla(abstractClientPlayerEntity);
            if (texture != null) {
               cir.setReturnValue(texture);
            }
         }
      } else {
         this.etf$ETFPlayerTexture = null;
      }
   }

   @Nullable
   @Override
   public ETFPlayerTexture etf$getETFPlayerTexture() {
      return this.etf$ETFPlayerTexture;
   }
}
