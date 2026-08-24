package traben.entity_texture_features.mixin.mixins;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.HttpTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.features.player.ETFPlayerTexture;

@Mixin({HttpTexture.class})
public abstract class MixinPlayerSkinTexture {
   @Shadow
   private static void setNoAlpha(NativeImage image, int x, int y, int width, int height) {
   }

   @Inject(
      method = {"setNoAlpha"},
      cancellable = true,
      at = {@At("HEAD")}
   )
   private static void etf$cancelling(NativeImage image, int x1, int y1, int x2, int y2, CallbackInfo ci) {
      if (ETF.config().getConfig() != null) {
         ETFConfig.SkinTransparencyMode mode = ETF.config().getConfig().skinTransparencyMode;
         if (mode == ETFConfig.SkinTransparencyMode.ETF_SKINS_ONLY && ETFPlayerTexture.remappingETFSkin) {
            ci.cancel();
         } else if (mode == ETFConfig.SkinTransparencyMode.ALL) {
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"processLegacySkin"},
      cancellable = true,
      require = 0,
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/texture/HttpTexture;setNoAlpha(Lcom/mojang/blaze3d/platform/NativeImage;IIII)V",
         shift = Shift.BEFORE,
         ordinal = 0
      )}
   )
   private void etf$differentAlpha(NativeImage image, CallbackInfoReturnable<NativeImage> cir) {
      if (ETF.config().getConfig() != null && ETF.config().getConfig().skinTransparencyInExtraPixels) {
         etf$alpha(image, cir);
      }
   }

   @Unique
   private static void etf$alpha(NativeImage image, CallbackInfoReturnable<NativeImage> cir) {
      setNoAlpha(image, 8, 0, 24, 8);
      setNoAlpha(image, 0, 8, 32, 16);
      setNoAlpha(image, 4, 16, 12, 20);
      setNoAlpha(image, 20, 16, 36, 20);
      setNoAlpha(image, 44, 16, 52, 20);
      setNoAlpha(image, 0, 20, 64, 32);
      setNoAlpha(image, 20, 48, 28, 52);
      setNoAlpha(image, 36, 48, 44, 52);
      setNoAlpha(image, 16, 52, 48, 64);
      cir.setReturnValue(image);
   }
}
