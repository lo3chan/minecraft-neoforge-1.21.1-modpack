/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.world.entity.Entity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package traben.entity_texture_features.mixin.mixins.entity.renderer;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFRenderContext;

@Mixin(value={EntityRenderer.class})
public abstract class MixinEntityRenderer<T extends Entity> {
    private static final String RENDER = "render";

    @Inject(method={"getPackedLightCoords"}, at={@At(value="RETURN")}, cancellable=true)
    private void etf$vanillaLightOverrideCancel(T entity, float tickDelta, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((Object)ETF.config().getConfig().getLightOverride((Entity)entity, tickDelta, (Integer)cir.getReturnValue()));
    }

    @Inject(method={"render"}, at={@At(value="HEAD")})
    private void etf$protectPostRenderersLikeNametag(CallbackInfo ci) {
        ETFRenderContext.preventRenderLayerTextureModify();
    }

    @Inject(method={"render"}, at={@At(value="TAIL")})
    private void etf$revertForRenderersThatCallSuperFirst(CallbackInfo ci) {
        ETFRenderContext.allowRenderLayerTextureModify();
    }
}

