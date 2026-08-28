/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexMultiConsumer$Double
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package traben.entity_texture_features.mixin.mixins;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFVertexConsumer;

@Mixin(value={VertexMultiConsumer.Double.class})
public class MixinVertexMultiConsumer$Double
implements ETFVertexConsumer {
    @Shadow
    @Final
    private VertexConsumer first;
    @Shadow
    @Final
    private VertexConsumer second;

    @Override
    public ETFTexture etf$getETFTexture() {
        VertexConsumer vertexConsumer = this.second;
        if (vertexConsumer instanceof ETFVertexConsumer) {
            ETFVertexConsumer etfSecond = (ETFVertexConsumer)vertexConsumer;
            return etfSecond.etf$getETFTexture();
        }
        vertexConsumer = this.first;
        if (vertexConsumer instanceof ETFVertexConsumer) {
            ETFVertexConsumer etfFirst = (ETFVertexConsumer)vertexConsumer;
            return etfFirst.etf$getETFTexture();
        }
        return null;
    }

    @Override
    public MultiBufferSource etf$getProvider() {
        VertexConsumer vertexConsumer = this.second;
        if (vertexConsumer instanceof ETFVertexConsumer) {
            ETFVertexConsumer etfSecond = (ETFVertexConsumer)vertexConsumer;
            return etfSecond.etf$getProvider();
        }
        vertexConsumer = this.first;
        if (vertexConsumer instanceof ETFVertexConsumer) {
            ETFVertexConsumer etfFirst = (ETFVertexConsumer)vertexConsumer;
            return etfFirst.etf$getProvider();
        }
        return null;
    }

    @Override
    public RenderType etf$getRenderLayer() {
        VertexConsumer vertexConsumer = this.second;
        if (vertexConsumer instanceof ETFVertexConsumer) {
            ETFVertexConsumer etfSecond = (ETFVertexConsumer)vertexConsumer;
            return etfSecond.etf$getRenderLayer();
        }
        vertexConsumer = this.first;
        if (vertexConsumer instanceof ETFVertexConsumer) {
            ETFVertexConsumer etfFirst = (ETFVertexConsumer)vertexConsumer;
            return etfFirst.etf$getRenderLayer();
        }
        return null;
    }

    @Override
    public void etf$initETFVertexConsumer(MultiBufferSource provider, RenderType renderLayer) {
    }
}

