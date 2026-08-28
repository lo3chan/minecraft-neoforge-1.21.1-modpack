/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer
 *  net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.entity.Entity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.compat.sodium.mixin;

import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={SodiumWorldRenderer.class})
public class MixinSodiumWorldRenderer {
    @Unique
    private float lastSunAngle;

    @Redirect(method={"setupTerrain"}, remap=false, at=@At(value="INVOKE", target="Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSectionManager;needsUpdate()Z", ordinal=0, remap=false))
    private boolean iris$forceChunkGraphRebuildInShadowPass(RenderSectionManager instance) {
        float sunAngle;
        if (ShadowRenderingState.areShadowsCurrentlyBeingRendered() && this.lastSunAngle != (sunAngle = Minecraft.getInstance().level.getSunAngle(CapturedRenderingState.INSTANCE.getTickDelta()))) {
            this.lastSunAngle = sunAngle;
            return true;
        }
        return instance.needsUpdate();
    }

    @Redirect(method={"setupTerrain"}, remap=false, at=@At(value="INVOKE", target="Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSectionManager;needsUpdate()Z", ordinal=1, remap=false))
    private boolean iris$forceEndGraphRebuild(RenderSectionManager instance) {
        if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
            return false;
        }
        return instance.needsUpdate();
    }

    @Inject(method={"isEntityVisible"}, at={@At(value="HEAD")}, cancellable=true)
    private void iris$skipEntityCheck(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
            cir.setReturnValue((Object)true);
        }
    }
}

