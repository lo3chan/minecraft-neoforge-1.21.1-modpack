/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package traben.entity_texture_features.mixin.mixins;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.entity_texture_features.utils.ETFVertexConsumer;

@Mixin(value={ModelPart.class}, priority=2000)
public abstract class MixinModelPart {
    @Shadow
    public abstract void render(PoseStack var1, VertexConsumer var2, int var3, int var4, int var5);

    @Inject(method={"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"}, at={@At(value="HEAD")})
    private void etf$findOutIfInitialModelPart(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, int k, CallbackInfo ci) {
        ETFRenderContext.incrementCurrentModelPartDepth();
    }

    @Inject(method={"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"}, at={@At(value="RETURN")})
    private void etf$doEmissiveIfInitialPart(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int k, CallbackInfo ci) {
        if (ETFRenderContext.getCurrentModelPartDepth() > 1) {
            ETFRenderContext.decrementCurrentModelPartDepth();
        } else {
            ETFVertexConsumer etfVertexConsumer;
            ETFTexture texture;
            if (ETFRenderContext.isCurrentlyRenderingEntity() && vertices instanceof ETFVertexConsumer && (texture = (etfVertexConsumer = (ETFVertexConsumer)vertices).etf$getETFTexture()) != null && (texture.isEmissive() || texture.isEnchanted())) {
                ETFUtils2.RenderMethodForOverlay renderer;
                MultiBufferSource provider = etfVertexConsumer.etf$getProvider();
                RenderType layer = etfVertexConsumer.etf$getRenderLayer();
                if (provider == null || layer == null || ETFUtils2.renderEmissive(texture, provider, renderer = (a, b) -> this.render(matrices, a, b, overlay, k)) | ETFUtils2.renderEnchanted(texture, provider, light, renderer)) {
                    // empty if block
                }
            }
            ETFRenderContext.resetCurrentModelPartDepth();
        }
    }

    @ModifyVariable(method={"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"}, at=@At(value="HEAD"), ordinal=0, argsOnly=true)
    private VertexConsumer etf$modify(VertexConsumer value) {
        if (value instanceof BufferBuilder) {
            ETFVertexConsumer etf;
            BufferBuilder builder = (BufferBuilder)value;
            if (!builder.building && value instanceof ETFVertexConsumer && (etf = (ETFVertexConsumer)value).etf$getRenderLayer() != null && etf.etf$getProvider() != null) {
                return etf.etf$getProvider().getBuffer(etf.etf$getRenderLayer());
            }
        }
        return value;
    }
}

