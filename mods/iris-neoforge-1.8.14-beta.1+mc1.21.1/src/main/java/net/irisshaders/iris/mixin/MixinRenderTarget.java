/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.irisshaders.iris.gl.GLDebug;
import net.irisshaders.iris.targets.Blaze3dRenderTargetExt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderTarget.class})
public class MixinRenderTarget
implements Blaze3dRenderTargetExt {
    @Shadow
    protected int depthBufferId;
    @Shadow
    protected int colorTextureId;
    @Shadow
    public int frameBufferId;
    @Unique
    private int iris$depthBufferVersion;
    @Unique
    private int iris$colorBufferVersion;

    @Inject(method={"destroyBuffers()V"}, at={@At(value="HEAD")})
    private void iris$onDestroyBuffers(CallbackInfo ci) {
        ++this.iris$depthBufferVersion;
        ++this.iris$colorBufferVersion;
    }

    @Inject(method={"createBuffers"}, at={@At(value="RETURN")})
    private void nameDepthBuffer(int i, int j, boolean bl, CallbackInfo ci) {
        GLDebug.nameObject(5890, this.depthBufferId, "Main depth texture");
        GLDebug.nameObject(5890, this.colorTextureId, "Main color texture");
        GLDebug.nameObject(36160, this.frameBufferId, "Main framebuffer");
    }

    @Override
    public int iris$getDepthBufferVersion() {
        return this.iris$depthBufferVersion;
    }

    @Override
    public int iris$getColorBufferVersion() {
        return this.iris$colorBufferVersion;
    }
}

