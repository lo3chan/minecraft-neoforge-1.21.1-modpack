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
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Button$OnPress
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  org.joml.Matrix4f
 */
package net.diebuddies.physics.settings.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.diebuddies.physics.settings.ButtonSettings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class FunctionButton
extends Button {
    protected ResourceLocation texture;

    public FunctionButton(int x, int y, int width, int button, Component component, Button.OnPress onPress, ResourceLocation texture) {
        super(x, y, width, button, component, onPress, Button.DEFAULT_NARRATION);
        this.texture = texture;
        ButtonSettings.addCustomButtonStyle(this);
    }

    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        super.renderWidget(guiGraphics, i, j, f);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)this.texture);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)this.alpha);
        Matrix4f matrix = guiGraphics.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix, (float)this.getX(), (float)(this.getY() + 20 - 1), 100.0f).setUv(0.0f, 1.0f);
        bufferBuilder.addVertex(matrix, (float)(this.getX() + 20), (float)(this.getY() + 20 - 1), 100.0f).setUv(1.0f, 1.0f);
        bufferBuilder.addVertex(matrix, (float)(this.getX() + 20), (float)(this.getY() - 1), 100.0f).setUv(1.0f, 0.0f);
        bufferBuilder.addVertex(matrix, (float)this.getX(), (float)(this.getY() - 1), 100.0f).setUv(0.0f, 0.0f);
        BufferUploader.drawWithShader((MeshData)bufferBuilder.build());
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }
}

