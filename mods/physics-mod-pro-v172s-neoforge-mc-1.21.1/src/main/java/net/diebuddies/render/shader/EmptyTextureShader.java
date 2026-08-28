/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  org.joml.Matrix4f
 */
package net.diebuddies.render.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.diebuddies.opengl.Shader;
import org.joml.Matrix4f;

public class EmptyTextureShader
extends Shader {
    public static final String VERTEX_SHADER = "/assets/physicsmod/shaders/core/empty.vsh";
    public static final String FRAGMENT_SHADER = "/assets/physicsmod/shaders/core/empty.fsh";

    public EmptyTextureShader() {
        super(VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public void uploadTexture(int textureID) {
        this.setUniform1(this.getUniformLocation("diffuseMap"), 0);
        RenderSystem.setShaderTexture((int)0, (int)textureID);
        RenderSystem.activeTexture((int)33984);
        RenderSystem.bindTexture((int)textureID);
    }

    public void uploadInvProjectionMatrix(Matrix4f invProjectionMatrix) {
        this.uploadMatrix(this.getUniformLocation("invProjectionMatrix"), invProjectionMatrix);
    }

    public void uploadInvViewMatrix(Matrix4f invViewMatrix) {
        this.uploadMatrix(this.getUniformLocation("invViewMatrix"), invViewMatrix);
    }
}

