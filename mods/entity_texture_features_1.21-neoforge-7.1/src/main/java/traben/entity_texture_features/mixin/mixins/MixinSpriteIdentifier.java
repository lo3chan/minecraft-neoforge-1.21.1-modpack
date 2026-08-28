/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.SpriteCoordinateExpander
 *  net.minecraft.client.resources.model.Material
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package traben.entity_texture_features.mixin.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SpriteCoordinateExpander;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFUtils2;

@Mixin(value={Material.class})
public class MixinSpriteIdentifier {
    @Inject(method={"buffer(Lnet/minecraft/client/renderer/MultiBufferSource;Ljava/util/function/Function;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"}, at={@At(value="RETURN")}, cancellable=true)
    private void etf$modifyIfRequired(CallbackInfoReturnable<VertexConsumer> cir, @Local(argsOnly=true) MultiBufferSource vertexConsumers, @Local(argsOnly=true) Function<ResourceLocation, RenderType> layerFactory) {
        Object object = cir.getReturnValue();
        if (object instanceof SpriteCoordinateExpander) {
            SpriteCoordinateExpander spriteTexturedVertexConsumer = (SpriteCoordinateExpander)object;
            ResourceLocation rawId = spriteTexturedVertexConsumer.sprite.contents().name();
            ResourceLocation actualTexture = rawId.toString().endsWith(".png") ? rawId : ETFUtils2.res(rawId.getNamespace(), "textures/" + rawId.getPath() + ".png");
            ETFTexture texture = ETFManager.getInstance().getETFTextureVariant(actualTexture, ETFRenderContext.getCurrentEntityState());
            if (!actualTexture.equals((Object)texture.thisIdentifier) || texture.isEmissive() || texture.isEnchanted()) {
                VertexConsumer consumer;
                ETFRenderContext.preventRenderLayerTextureModify();
                RenderType layer = layerFactory.apply(texture.thisIdentifier);
                ETFRenderContext.allowRenderLayerTextureModify();
                if (layer != null && (consumer = vertexConsumers.getBuffer(layer)) != null) {
                    cir.setReturnValue((Object)consumer);
                }
            }
        }
    }
}

