/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.Font$DisplayMode
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.util.FastColor$ARGB32
 *  net.minecraft.util.FormattedCharSequence
 *  org.joml.Matrix4f
 */
package net.diebuddies.physics.settings.ux;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.diebuddies.physics.settings.ux.Animatable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public abstract class Animator {
    public boolean render(Animatable animatable, GuiGraphics guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
        return false;
    }

    public boolean renderToolTip(Animatable animatable, GuiGraphics guiGraphics, int mouseX, int mouseY, float renderPercent) {
        return false;
    }

    public void tick(Animatable animatable) {
    }

    public void init(Animatable animatable) {
    }

    public static final void drawRect(BufferBuilder bufferBuilder, Matrix4f pose, float x, float y, float width, float height, float depth, int color) {
        bufferBuilder.addVertex(pose, x, y + height, depth).setColor(color);
        bufferBuilder.addVertex(pose, x + width, y + height, depth).setColor(color);
        bufferBuilder.addVertex(pose, x + width, y, depth).setColor(color);
        bufferBuilder.addVertex(pose, x, y, depth).setColor(color);
    }

    public static final void drawLine(BufferBuilder bufferBuilder, Matrix4f pose, float x1, float y1, float x2, float y2, float depth, int color) {
        bufferBuilder.addVertex(pose, x1, y2 + 1.0f, depth).setColor(color);
        bufferBuilder.addVertex(pose, x2 + 1.0f, y2 + 1.0f, depth).setColor(color);
        bufferBuilder.addVertex(pose, x2 + 1.0f, y1, depth).setColor(color);
        bufferBuilder.addVertex(pose, x1, y1, depth).setColor(color);
    }

    public static final void drawRect(BufferBuilder bufferBuilder, Matrix4f pose, float x, float y, float width, float height, float depth, float umin, float umax, float vmin, float vmax) {
        bufferBuilder.addVertex(pose, x, y + height, depth).setUv(umin, vmax);
        bufferBuilder.addVertex(pose, x + width, y + height, depth).setUv(umax, vmax);
        bufferBuilder.addVertex(pose, x + width, y, depth).setUv(umax, vmin);
        bufferBuilder.addVertex(pose, x, y, depth).setUv(umin, vmin);
    }

    public static final void drawRect(BufferBuilder bufferBuilder, Matrix4f pose, float x, float y, float width, float height, float depth, float umin, float umax, float vmin, float vmax, int color) {
        bufferBuilder.addVertex(pose, x, y + height, depth).setUv(umin, vmax).setColor(color);
        bufferBuilder.addVertex(pose, x + width, y + height, depth).setUv(umax, vmax).setColor(color);
        bufferBuilder.addVertex(pose, x + width, y, depth).setUv(umax, vmin).setColor(color);
        bufferBuilder.addVertex(pose, x, y, depth).setUv(umin, vmin).setColor(color);
    }

    public static final void drawText(GuiGraphics guiGraphics, Font font, FormattedCharSequence formattedCharSequence, float x, float y) {
        Matrix4f transformation = guiGraphics.pose().last().pose();
        font.drawInBatch(formattedCharSequence, x + 1.0f, y + 1.0f, FastColor.ARGB32.color((int)255, (int)0, (int)0, (int)0), false, transformation, (MultiBufferSource)guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 0xF000F0);
        guiGraphics.flush();
        font.drawInBatch(formattedCharSequence, x, y, FastColor.ARGB32.color((int)255, (int)255, (int)255, (int)255), false, transformation, (MultiBufferSource)guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 0xF000F0);
        guiGraphics.flush();
    }
}

