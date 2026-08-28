/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.llamalad7.mixinextras.sugar.Share
 *  com.llamalad7.mixinextras.sugar.ref.LocalRef
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.LivingEntityRenderer
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.world.entity.LivingEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package traben.entity_texture_features.mixin.mixins.entity.renderer;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin(value={LivingEntityRenderer.class})
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends EntityRenderer<T>
implements RenderLayerParent<T, M> {
    private static final String RENDER = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V";

    protected MixinLivingEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Inject(method={"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at={@At(value="INVOKE", target="Ljava/util/List;iterator()Ljava/util/Iterator;")})
    private void etf$markFeatures(CallbackInfo ci, @Share(value="shareState") LocalRef<ETFEntityRenderState> etf$heldEntity, @Local(argsOnly=true) LivingEntity entity) {
        etf$heldEntity.set((Object)ETFEntityRenderState.forEntity((ETFEntity)entity));
        ETFRenderContext.allowRenderLayerTextureModify();
        ETFRenderContext.setRenderingFeatures(true);
    }

    @Inject(method={"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at={@At(value="INVOKE", target="Ljava/util/Iterator;next()Ljava/lang/Object;")})
    private void etf$markFeaturesLoopEnd(CallbackInfo ci, @Share(value="shareState") LocalRef<ETFEntityRenderState> etf$heldEntity) {
        ETFRenderContext.setCurrentEntity((ETFEntityRenderState)etf$heldEntity.get());
        ETFRenderContext.allowRenderLayerTextureModify();
        ETFRenderContext.endSpecialRenderOverlayPhase();
    }

    @Inject(method={"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at={@At(value="INVOKE", target="Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V")})
    private void etf$markFeaturesEnd(CallbackInfo ci) {
        ETFRenderContext.setRenderingFeatures(false);
    }
}

