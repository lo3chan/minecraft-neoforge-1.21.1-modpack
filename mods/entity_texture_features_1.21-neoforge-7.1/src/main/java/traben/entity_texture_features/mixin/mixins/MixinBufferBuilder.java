/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 */
package traben.entity_texture_features.mixin.mixins;

import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.Optional;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFRenderLayerWithTexture;
import traben.entity_texture_features.utils.ETFVertexConsumer;

@Mixin(value={BufferBuilder.class})
public class MixinBufferBuilder
implements ETFVertexConsumer {
    @Unique
    private MultiBufferSource etf$provider = null;
    @Unique
    private RenderType etf$renderLayer = null;
    @Unique
    private ETFTexture etf$ETFTexture = null;

    @Override
    public ETFTexture etf$getETFTexture() {
        return this.etf$ETFTexture;
    }

    @Override
    public MultiBufferSource etf$getProvider() {
        return this.etf$provider;
    }

    @Override
    public RenderType etf$getRenderLayer() {
        return this.etf$renderLayer;
    }

    @Override
    public void etf$initETFVertexConsumer(MultiBufferSource provider, RenderType renderLayer) {
        this.etf$provider = provider;
        this.etf$renderLayer = renderLayer;
        if (renderLayer instanceof ETFRenderLayerWithTexture) {
            ETFRenderLayerWithTexture etfRenderLayerWithTexture = (ETFRenderLayerWithTexture)renderLayer;
            Optional<ResourceLocation> possibleId = etfRenderLayerWithTexture.etf$getId();
            possibleId.ifPresent(identifier -> {
                this.etf$ETFTexture = ETFManager.getInstance().getETFTextureNoVariation((ResourceLocation)identifier);
            });
        }
    }
}

