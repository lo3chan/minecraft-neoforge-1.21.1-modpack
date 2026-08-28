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
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.util.FastColor$ARGB32
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
import net.diebuddies.physics.settings.ux.Animatable;
import net.diebuddies.physics.settings.ux.Animator;
import net.diebuddies.physics.settings.ux.BaseColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.FastColor;
import org.joml.Matrix4f;

public class HighlightButtonRenderer
extends Animator {
    private float time;

    @Override
    public boolean render(Animatable animatable, GuiGraphics guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
        this.time += delta;
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        float x = animatable.getAnimX();
        float y = animatable.getAnimY();
        float width = animatable.getAnimWidth() - 1.0f;
        float height = animatable.getAnimHeight() - 1.0f;
        if (animatable instanceof AbstractWidget) {
            AbstractWidget widget = (AbstractWidget)animatable;
            x = widget.getX();
            y = widget.getY();
            width = widget.getWidth();
            height = widget.getHeight();
        }
        float depth = 100.0f;
        int offset = (int)(Math.abs(Math.sin((double)this.time * 5.0)) * 3.0) + 1;
        int lineLength = 3;
        Matrix4f pose = guiGraphics.pose().last().pose();
        int color = BaseColors.HIGHLIGHT_COLOR;
        int backgroundColor = FastColor.ARGB32.color((int)255, (int)60, (int)90, (int)60);
        HighlightButtonRenderer.drawLine(bufferBuilder, pose, x - (float)offset + 1.0f, y - (float)offset + 1.0f, x + (float)lineLength - (float)offset + 1.0f, y - (float)offset + 1.0f, depth, backgroundColor);
        HighlightButtonRenderer.drawLine(bufferBuilder, pose, x - (float)offset + 1.0f, y - (float)offset + 1.0f, x - (float)offset + 1.0f, y - (float)offset + (float)lineLength + 1.0f, depth, backgroundColor);
        HighlightButtonRenderer.drawLine(bufferBuilder, pose, x - (float)offset, y - (float)offset, x + (float)lineLength - (float)offset, y - (float)offset, depth, color);
        HighlightButtonRenderer.drawLine(bufferBuilder, pose, x - (float)offset, y - (float)offset, x - (float)offset, y - (float)offset + (float)lineLength, depth, color);
        HighlightButtonRenderer.drawLine(bufferBuilder, pose, x + (float)offset + width - (float)lineLength + 1.0f, y - (float)offset + 1.0f, x + (float)offset + width + 1.0f, y - (float)offset + 1.0f, depth, backgroundColor);
        HighlightButtonRenderer.drawLine(bufferBuilder, pose, x + (float)offset + width + 1.0f, y - (float)offset + 1.0f, x + (float)offset + width + 1.0f, y - (float)offset + (float)lineLength + 1.0f, depth, backgroundColor);
        HighlightButtonRenderer.drawLine(bufferBuilder, pose, x + (float)offset + width - (float)lineLength, y - (float)offset, x + (float)offset + width, y - (float)offset, depth, color);
        HighlightButtonRenderer.drawLine(bufferBuilder, pose, x + (float)offset + width, y - (float)offset, x + (float)offset + width, y - (float)offset + (float)lineLength, depth, color);
        HighlightButtonRenderer.drawLine(bufferBuilder, pose, x - (float)offset + 1.0f, y + (float)offset + height + 1.0f, x + (float)lineLength - (float)offset + 1.0f, y + (float)offset + height + 1.0f, depth, backgroundColor);
        HighlightButtonRenderer.drawLine(bufferBuilder, pose, x - (float)offset + 1.0f, y + (float)offset - (float)lineLength + height + 1.0f, x - (float)offset + 1.0f, y + (float)offset + height + 1.0f, depth, backgroundColor);
        HighlightButtonRenderer.drawLine(bufferBuilder, pose, x - (float)offset, y + (float)offset + height, x + (float)lineLength - (float)offset, y + (float)offset + height, depth, color);
        HighlightButtonRenderer.drawLine(bufferBuilder, pose, x - (float)offset, y + (float)offset - (float)lineLength + height, x - (float)offset, y + (float)offset + height, depth, color);
        HighlightButtonRenderer.drawLine(bufferBuilder, pose, x + (float)offset + width - (float)lineLength + 1.0f, y + (float)offset + height + 1.0f, x + (float)offset + width + 1.0f, y + (float)offset + height + 1.0f, depth, backgroundColor);
        HighlightButtonRenderer.drawLine(bufferBuilder, pose, x + (float)offset + width + 1.0f, y + (float)offset - (float)lineLength + height + 1.0f, x + (float)offset + width + 1.0f, y + (float)offset + height + 1.0f, depth, backgroundColor);
        HighlightButtonRenderer.drawLine(bufferBuilder, pose, x + (float)offset + width - (float)lineLength, y + (float)offset + height, x + (float)offset + width, y + (float)offset + height, depth, color);
        HighlightButtonRenderer.drawLine(bufferBuilder, pose, x + (float)offset + width, y + (float)offset - (float)lineLength + height, x + (float)offset + width, y + (float)offset + height, depth, color);
        BufferUploader.drawWithShader((MeshData)bufferBuilder.build());
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        return super.render(animatable, guiGraphics, mouseX, mouseY, renderPercent, delta);
    }
}

