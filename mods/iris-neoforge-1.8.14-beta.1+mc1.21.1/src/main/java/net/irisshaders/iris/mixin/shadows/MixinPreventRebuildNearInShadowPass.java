/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.client.Camera
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.chunk.SectionRenderDispatcher$RenderSection
 *  net.minecraft.client.renderer.culling.Frustum
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.shadows;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class}, priority=1010)
public abstract class MixinPreventRebuildNearInShadowPass {
    @Shadow
    @Final
    private ObjectArrayList<SectionRenderDispatcher.RenderSection> visibleSections;

    @Inject(method={"setupRender"}, at={@At(value="TAIL")})
    private void iris$preventRebuildNearInShadowPass(Camera camera, Frustum frustum, boolean bl, boolean bl2, CallbackInfo ci) {
        if (ShadowRenderer.ACTIVE) {
            for (SectionRenderDispatcher.RenderSection chunk : this.visibleSections) {
                ShadowRenderer.visibleBlockEntities.addAll(chunk.getCompiled().getRenderableBlockEntities());
            }
        }
    }
}

