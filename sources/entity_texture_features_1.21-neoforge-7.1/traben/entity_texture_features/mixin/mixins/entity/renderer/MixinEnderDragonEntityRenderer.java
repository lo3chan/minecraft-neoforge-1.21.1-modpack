package traben.entity_texture_features.mixin.mixins.entity.renderer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.utils.ETFUtils2;

@Mixin({EnderDragonRenderer.class})
public abstract class MixinEnderDragonEntityRenderer extends EntityRenderer<EnderDragon> {
   @Final
   @Shadow
   private static ResourceLocation DRAGON_LOCATION;
   @Final
   @Shadow
   private static RenderType RENDER_TYPE;
   @Final
   @Shadow
   private static RenderType DECAL;
   @Final
   @Shadow
   private static ResourceLocation DRAGON_EYES_LOCATION;
   @Final
   @Shadow
   private static RenderType EYES;

   protected MixinEnderDragonEntityRenderer(Context ctx) {
      super(ctx);
   }

   @ModifyArg(
      method = {"render(Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
      )
   )
   private RenderType etf$returnAlteredTexture(RenderType texturedRenderLayer) {
      return getType(texturedRenderLayer);
   }

   @Unique
   @Nullable
   private static RenderType getType(RenderType texturedRenderLayer) {
      if (ETF.config().getConfig().canDoCustomTextures()) {
         try {
            if (DECAL.equals(texturedRenderLayer)) {
               return RenderType.entityDecal(DRAGON_LOCATION);
            }

            if (RENDER_TYPE.equals(texturedRenderLayer)) {
               return RenderType.entityCutoutNoCull(DRAGON_LOCATION);
            }

            if (EYES.equals(texturedRenderLayer)) {
               return RenderType.eyes(DRAGON_EYES_LOCATION);
            }
         } catch (Exception var2) {
            ETFUtils2.logError(var2.toString(), false);
         }
      }

      return texturedRenderLayer;
   }
}
