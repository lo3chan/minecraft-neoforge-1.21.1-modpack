/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.renderer.texture.HttpTexture
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package traben.entity_texture_features.mixin.mixins;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.HttpTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.features.player.ETFPlayerTexture;

@Mixin(value={HttpTexture.class})
public abstract class MixinPlayerSkinTexture {
    @Shadow
    private static void setNoAlpha(NativeImage image, int x, int y, int width, int height) {
    }

    @Inject(method={"setNoAlpha"}, cancellable=true, at={@At(value="HEAD")})
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

    @Inject(method={"processLegacySkin"}, cancellable=true, require=0, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/texture/HttpTexture;setNoAlpha(Lcom/mojang/blaze3d/platform/NativeImage;IIII)V", shift=At.Shift.BEFORE, ordinal=0)})
    private void etf$differentAlpha(NativeImage image, CallbackInfoReturnable<NativeImage> cir) {
        if (ETF.config().getConfig() != null && ETF.config().getConfig().skinTransparencyInExtraPixels) {
            MixinPlayerSkinTexture.etf$alpha(image, cir);
        }
    }

    @Unique
    private static void etf$alpha(NativeImage image, CallbackInfoReturnable<NativeImage> cir) {
        MixinPlayerSkinTexture.setNoAlpha(image, 8, 0, 24, 8);
        MixinPlayerSkinTexture.setNoAlpha(image, 0, 8, 32, 16);
        MixinPlayerSkinTexture.setNoAlpha(image, 4, 16, 12, 20);
        MixinPlayerSkinTexture.setNoAlpha(image, 20, 16, 36, 20);
        MixinPlayerSkinTexture.setNoAlpha(image, 44, 16, 52, 20);
        MixinPlayerSkinTexture.setNoAlpha(image, 0, 20, 64, 32);
        MixinPlayerSkinTexture.setNoAlpha(image, 20, 48, 28, 52);
        MixinPlayerSkinTexture.setNoAlpha(image, 36, 48, 44, 52);
        MixinPlayerSkinTexture.setNoAlpha(image, 16, 52, 48, 64);
        cir.setReturnValue((Object)image);
    }
}

