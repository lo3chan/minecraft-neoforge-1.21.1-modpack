/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  org.joml.Vector2f
 */
package net.diebuddies.render.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.diebuddies.opengl.FBO;
import net.diebuddies.opengl.Texture;
import net.diebuddies.opengl.VAO;
import net.diebuddies.render.shader.GaussianDepthBlurShader;
import org.joml.Vector2f;

public class GaussianDepthBlurEffect {
    private GaussianDepthBlurShader shader;
    private FBO firstBlur;
    private FBO image;
    private double[] gaussianKernel;
    private Vector2f vertical = new Vector2f(0.0f, 1.0f);
    private Vector2f horizontal = new Vector2f(1.0f, 0.0f);

    public GaussianDepthBlurEffect(double sigma, int kernelSize) {
        int i;
        this.gaussianKernel = new double[kernelSize / 2 + 1];
        double sum = 0.0;
        double eulerPart = 1.0 / (Math.PI * 2 * sigma * sigma);
        for (i = 0; i < this.gaussianKernel.length; ++i) {
            this.gaussianKernel[i] = eulerPart * Math.exp(-((double)(i * i) / (2.0 * sigma * sigma)));
            sum += this.gaussianKernel[i] * (double)(i != 0 ? 2 : 1);
        }
        i = 0;
        while (i < this.gaussianKernel.length) {
            int n = i++;
            this.gaussianKernel[n] = this.gaussianKernel[n] / sum;
        }
        this.shader = new GaussianDepthBlurShader(this.gaussianKernel.length);
    }

    public void render(VAO emptyVAO) {
        RenderSystem.disableDepthTest();
        this.firstBlur.bind();
        this.shader.bind();
        this.shader.uploadTexelSize(new Vector2f(1.0f / (float)this.image.getTexture().getWidth(), 1.0f / (float)this.image.getTexture().getWidth()));
        this.shader.uploadKernel(this.gaussianKernel);
        this.shader.uploadOffset(this.vertical);
        this.shader.uploadImage(this.image.getTexture().getID());
        emptyVAO.renderEmptyTriangle();
        this.image.bind();
        this.shader.uploadOffset(this.horizontal);
        this.shader.uploadImage(this.firstBlur.getTexture().getID());
        emptyVAO.renderEmptyTriangle();
        RenderSystem.enableDepthTest();
    }

    public void setImage(FBO image) {
        if (this.firstBlur == null || this.firstBlur.getTexture().getWidth() != image.getTexture().getWidth() || this.firstBlur.getTexture().getHeight() != image.getTexture().getHeight()) {
            if (this.firstBlur != null) {
                this.firstBlur.destroy();
            }
            this.firstBlur = new FBO();
            this.firstBlur.attachColorBuffer(Texture.createTexture(image.getTexture().getWidth(), image.getTexture().getHeight(), 33326, 6403, 5126));
        }
        this.image = image;
    }

    public void destroy() {
        this.shader.destroy();
        if (this.firstBlur != null) {
            this.firstBlur.destroy();
        }
    }

    public void resize(int width, int height) {
    }
}

