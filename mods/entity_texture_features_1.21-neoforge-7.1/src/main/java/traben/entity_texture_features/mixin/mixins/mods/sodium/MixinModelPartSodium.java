/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
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
@Mixin(targets={"me/jellysquid/mods/sodium/client/render/immediate/model/EntityRenderer"})
public abstract class MixinModelPartSodium {
    @Unique
    private static boolean once = true;

    @Shadow
    public static void render(PoseStack matrixStack, VertexBufferWriter writer, ModelPart part, int light, int overlay, int color) {
    }

    @Inject(method={"render"}, at={@At(value="HEAD")})
    private static void etf$findOutIfInitialModelPart(PoseStack matrixStack, VertexBufferWriter writer, ModelPart part, int light, int overlay, int color, CallbackInfo ci) {
        ETFRenderContext.incrementCurrentModelPartDepth();
    }

    @Unique
    private static VertexBufferWriter etf$convertOrLog(VertexConsumer consumer) {
        VertexBufferWriter writer;
        if (consumer instanceof VertexBufferWriter && (writer = (VertexBufferWriter)consumer).canUseIntrinsics()) {
            return writer;
        }
        if (once) {
            once = false;
            ETFUtils2.logWarn("Bad consumer for sodium MixinModelPartSodium");
        }
        return null;
    }

    @Inject(method={"render"}, at={@At(value="RETURN")})
    private static void etf$doEmissiveIfInitialPart(PoseStack matrixStack, VertexBufferWriter writer, ModelPart part, int light, int overlay, int color, CallbackInfo ci) {
        if (ETFRenderContext.getCurrentModelPartDepth() > 1) {
            ETFRenderContext.decrementCurrentModelPartDepth();
        } else {
            ETFVertexConsumer etfVertexConsumer;
            ETFTexture texture;
            if (ETFRenderContext.isCurrentlyRenderingEntity() && writer instanceof ETFVertexConsumer && (texture = (etfVertexConsumer = (ETFVertexConsumer)writer).etf$getETFTexture()) != null && (texture.isEmissive() || texture.isEnchanted())) {
                ETFUtils2.RenderMethodForOverlay renderer;
                MultiBufferSource provider = etfVertexConsumer.etf$getProvider();
                RenderType layer = etfVertexConsumer.etf$getRenderLayer();
                if (provider == null || layer == null || ETFUtils2.renderEmissive(texture, provider, renderer = (a, b) -> {
                    VertexBufferWriter a2 = MixinModelPartSodium.etf$convertOrLog(a);
                    if (a2 == null) {
                        return;
                    }
                    MixinModelPartSodium.render(matrixStack, a2, part, b, overlay, color);
                }) | ETFUtils2.renderEnchanted(texture, provider, light, renderer)) {
                    // empty if block
                }
            }
            ETFRenderContext.resetCurrentModelPartDepth();
        }
    }

    @ModifyVariable(method={"render"}, at=@At(value="HEAD"), ordinal=0, argsOnly=true)
    private static VertexBufferWriter etf$modify(VertexBufferWriter value) {
        if (value instanceof BufferBuilder) {
            VertexConsumer a;
            VertexBufferWriter a2;
            ETFVertexConsumer etf;
            BufferBuilder builder = (BufferBuilder)value;
            if (!builder.building && value instanceof ETFVertexConsumer && (etf = (ETFVertexConsumer)value).etf$getRenderLayer() != null && etf.etf$getProvider() != null && (a2 = MixinModelPartSodium.etf$convertOrLog(a = etf.etf$getProvider().getBuffer(etf.etf$getRenderLayer()))) != null) {
                return a2;
            }
        }
        return value;
    }
}

