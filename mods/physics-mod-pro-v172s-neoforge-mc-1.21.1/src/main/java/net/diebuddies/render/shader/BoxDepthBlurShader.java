/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  org.joml.Vector2f
 */
package net.diebuddies.render.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.diebuddies.opengl.Shader;
import org.joml.Vector2f;

public class BoxDepthBlurShader
extends Shader {
    public static final String VERTEX_SHADER = "/assets/physicsmod/shaders/core/box.vsh";
    public static final String FRAGMENT_SHADER = "/assets/physicsmod/shaders/core/box.fsh";

    public BoxDepthBlurShader() {
        super(VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public void uploadOffset(Vector2f offset) {
        this.setUniform2(this.getUniformLocation("offset"), offset);
    }

    public void uploadTexelSize(Vector2f texelSize) {
        this.setUniform2(this.getUniformLocation("texelSize"), texelSize);
    }

    public void uploadNearAndFar(float near, float far) {
        this.setUniform1(this.getUniformLocation("near"), near);
        this.setUniform1(this.getUniformLocation("far"), far);
    }

    public void uploadImage(int textureID) {
        this.setUniform1(this.getUniformLocation("imageMap"), 0);
        RenderSystem.setShaderTexture((int)0, (int)textureID);
        RenderSystem.activeTexture((int)33984);
        RenderSystem.bindTexture((int)textureID);
    }
}

