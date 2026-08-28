/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.resources.ResourceLocation
 *  org.joml.Matrix4f
 */
package net.diebuddies.physics.settings.ux;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.diebuddies.opengl.ResourceManager;
import net.diebuddies.opengl.Texture;
import net.diebuddies.physics.liquid.SimpleTextureDimension;
import net.diebuddies.physics.settings.ux.Animatable;
import net.diebuddies.physics.settings.ux.Animator;
import net.diebuddies.render.MainRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class ImageElement
extends AbstractWidget {
    private ResourceManager resourceManager;
    private float textureWidth;
    private float textureHeight;
    private float textureAspectRatio;
    private Texture texture;
    private ResourceLocation imageLocation;
    private boolean keepAspectRatio;
    private ShaderInstance shader;
    private float time;

    public ImageElement(ResourceLocation imageLocation, float x, float y, float width, float height, boolean keepAspectRatio) {
        super((int)x, (int)y, (int)width, (int)height, null);
        this.imageLocation = imageLocation;
        this.keepAspectRatio = keepAspectRatio;
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        AbstractTexture abstractTexture = textureManager.getTexture(imageLocation);
        abstractTexture.setFilter(true, true);
        if (abstractTexture instanceof SimpleTextureDimension) {
            SimpleTextureDimension textureDimension = (SimpleTextureDimension)abstractTexture;
            this.textureWidth = textureDimension.getWidth();
            this.textureHeight = textureDimension.getHeight();
        } else {
            this.textureWidth = width;
            this.textureHeight = height;
        }
        this.textureAspectRatio = this.textureWidth / this.textureHeight;
        this.shader = MainRenderer.getParallaxShader();
    }

    public ImageElement(ResourceManager resourceManager, ResourceLocation imageLocation, float x, float y, float width, float height, boolean keepAspectRatio) {
        super((int)x, (int)y, (int)width, (int)height, null);
        this.resourceManager = resourceManager;
        this.imageLocation = imageLocation;
        this.keepAspectRatio = keepAspectRatio;
        this.shader = MainRenderer.getParallaxShader();
    }

    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.time += delta / 20.0f;
        Animatable ths = (Animatable)((Object)this);
        RenderSystem.setShader(() -> this.shader);
        float fogStart = RenderSystem.getShaderFogStart();
        RenderSystem.setShaderFogStart((float)this.time);
        if (this.resourceManager != null) {
            RenderSystem.setShaderTexture((int)0, (int)this.texture.getID());
        } else {
            RenderSystem.setShaderTexture((int)0, (ResourceLocation)this.getImageLocation());
        }
        RenderSystem.setShaderColor((float)ths.getAnimRed(), (float)ths.getAnimGreen(), (float)ths.getAnimBlue(), (float)ths.getAnimAlpha());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        float umin = 0.0f;
        float umax = 1.0f;
        float vmin = 0.0f;
        float vmax = 1.0f;
        if (this.keepAspectRatio) {
            float panelAspectRatio = ths.getAnimWidth() / ths.getAnimHeight();
            if (this.textureAspectRatio < panelAspectRatio) {
                multiplier = panelAspectRatio / this.textureAspectRatio;
                half = (1.0f - (vmax /= multiplier)) * 0.5f;
                vmax += half;
                vmin += half;
            } else {
                multiplier = panelAspectRatio / this.textureAspectRatio;
                half = (1.0f - (umax *= multiplier)) * 0.5f;
                umax += half;
                umin += half;
            }
        }
        float x = ths.getAnimX();
        float y = ths.getAnimY();
        float width = ths.getAnimWidth();
        float height = ths.getAnimHeight();
        float depth = ths.getAnimDepth();
        Matrix4f pose = guiGraphics.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Animator.drawRect(bufferBuilder, pose, x, y, width, height, depth, umin, umax, vmin, vmax);
        BufferUploader.drawWithShader((MeshData)bufferBuilder.build());
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderFogStart((float)fogStart);
    }

    public ResourceLocation getImageLocation() {
        return this.imageLocation;
    }

    public void setImageLocation(ResourceLocation imageLocation) {
        this.imageLocation = imageLocation;
    }

    public void updateWidgetNarration(NarrationElementOutput var1) {
    }

    public void destroyLoadedTexture() {
        if (this.resourceManager != null) {
            this.resourceManager.destroyTexture(this.imageLocation);
            this.texture = null;
        }
    }

    public boolean loadImage() {
        if (this.resourceManager == null) {
            return true;
        }
        if (this.texture == null) {
            this.texture = this.resourceManager.getTexture(this.imageLocation, false, Texture.FILTER_LOAD_GUI_TEXTURE);
            return false;
        }
        this.resourceManager.update();
        if (this.texture.getID() == -1) {
            return false;
        }
        this.textureWidth = this.texture.getWidth();
        this.textureHeight = this.texture.getHeight();
        this.textureAspectRatio = this.textureWidth / this.textureHeight;
        return true;
    }

    public ImageElement setShader(ShaderInstance shader) {
        this.shader = shader;
        return this;
    }
}

