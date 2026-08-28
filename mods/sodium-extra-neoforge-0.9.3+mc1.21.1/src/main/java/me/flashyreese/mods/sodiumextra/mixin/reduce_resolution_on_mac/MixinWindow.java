/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  org.lwjgl.glfw.GLFW
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.flashyreese.mods.sodiumextra.mixin.reduce_resolution_on_mac;

import com.mojang.blaze3d.platform.Window;
import me.flashyreese.mods.sodiumextra.client.util.MacReducedResolution;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Window.class})
public class MixinWindow {
    @Shadow
    private int width;
    @Shadow
    private int height;
    @Shadow
    private int framebufferWidth;
    @Shadow
    private int framebufferHeight;

    @Redirect(at=@At(value="INVOKE", target="Lorg/lwjgl/glfw/GLFW;glfwDefaultWindowHints()V"), method={"<init>"}, remap=false)
    private void onDefaultWindowHints() {
        GLFW.glfwDefaultWindowHints();
        MacReducedResolution.useOpenGlBackend();
        if (MacReducedResolution.isEnabled()) {
            GLFW.glfwWindowHint((int)143361, (int)0);
        }
    }

    @Inject(at={@At(value="RETURN")}, method={"refreshFramebufferSize"})
    private void afterUpdateFrameBufferSize(CallbackInfo ci) {
        this.scaleInitialFramebufferSize();
    }

    @Inject(method={"onFramebufferResize"}, at={@At(value="FIELD", target="Lcom/mojang/blaze3d/platform/Window;framebufferHeight:I", opcode=181, shift=At.Shift.AFTER)})
    private void afterFramebufferResize(long handle, int newWidth, int newHeight, CallbackInfo ci) {
        this.scaleFramebufferSize();
    }

    @Unique
    private void scaleInitialFramebufferSize() {
        if (MacReducedResolution.shouldUseWindowSizeForInitialFramebuffer()) {
            this.framebufferWidth = Math.max(1, this.width);
            this.framebufferHeight = Math.max(1, this.height);
            return;
        }
        this.scaleFramebufferSize();
    }

    @Unique
    private void scaleFramebufferSize() {
        if (!MacReducedResolution.shouldReduceFramebuffer()) {
            return;
        }
        this.framebufferWidth = MacReducedResolution.reduce(this.framebufferWidth);
        this.framebufferHeight = MacReducedResolution.reduce(this.framebufferHeight);
    }
}

