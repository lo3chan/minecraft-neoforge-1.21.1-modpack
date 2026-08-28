/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package net.irisshaders.iris.uniforms;

import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.minecraft.client.Minecraft;

public final class ViewportUniforms {
    private ViewportUniforms() {
    }

    public static void addViewportUniforms(UniformHolder uniforms) {
        uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME, "viewHeight", () -> Minecraft.getInstance().getMainRenderTarget().height).uniform1f(UniformUpdateFrequency.PER_FRAME, "viewWidth", () -> Minecraft.getInstance().getMainRenderTarget().width).uniform1f(UniformUpdateFrequency.PER_FRAME, "aspectRatio", ViewportUniforms::getAspectRatio);
    }

    private static float getAspectRatio() {
        return (float)Minecraft.getInstance().getMainRenderTarget().width / (float)Minecraft.getInstance().getMainRenderTarget().height;
    }
}

