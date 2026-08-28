/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.layers.EyesLayer
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 */
package traben.entity_texture_features.mixin.mixins.entity.renderer.feature;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.utils.ETFRenderLayerWithTexture;
import traben.entity_texture_features.utils.ETFUtils2;

@Mixin(value={EyesLayer.class})
public abstract class MixinEyeFeatureRenderer {
    @ModifyArg(method={"render"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"), index=0)
    private RenderType etf$allowModifiableEyes(RenderType layer) {
        ResourceLocation variant;
        ResourceLocation id;
        ETFRenderLayerWithTexture etf;
        if (layer instanceof ETFRenderLayerWithTexture && (etf = (ETFRenderLayerWithTexture)layer).etf$getId().isPresent() && !(id = etf.etf$getId().get()).equals((Object)(variant = ETFUtils2.getETFVariantNotNullForInjector(id)))) {
            boolean allowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
            ETFRenderContext.preventRenderLayerTextureModify();
            RenderType layer2 = RenderType.eyes((ResourceLocation)variant);
            if (allowed) {
                ETFRenderContext.allowRenderLayerTextureModify();
            }
            return layer2;
        }
        return layer;
    }
}

