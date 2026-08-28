/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.FormattedCharSequence
 *  org.joml.Matrix4f
 */
package net.diebuddies.physics.settings.ux;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import javax.annotation.Nullable;
import net.diebuddies.physics.settings.ux.Animatable;
import net.diebuddies.physics.settings.ux.Animator;
import net.diebuddies.physics.settings.ux.BarRenderer;
import net.diebuddies.physics.settings.ux.BaseColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public class MainToolTipRenderer {
    public static void renderToolTip(TooltipAlignment alignment, @Nullable Animatable animatable, List<FormattedCharSequence> list, GuiGraphics guiGraphics, float x, float width, float y, float barSize, int backgroundColor) {
        BarRenderer bar;
        Font font = Minecraft.getInstance().font;
        int padding = 10;
        int heightPerRow = 10;
        int height = list.size() * heightPerRow + padding * 2;
        PoseStack matrices = guiGraphics.pose();
        matrices.pushPose();
        matrices.translate(0.0f, 0.0f, 200.0f);
        Matrix4f pose = matrices.last().pose();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        int color = backgroundColor;
        float depth = -120.0f;
        int yOffset = height;
        if (alignment == TooltipAlignment.TOP) {
            yOffset = 0;
        }
        Animator.drawRect(bufferBuilder, pose, x, y - (float)yOffset, width, height, depth, color);
        BufferUploader.drawWithShader((MeshData)bufferBuilder.build());
        RenderSystem.disableBlend();
        color = BaseColors.HIGHLIGHT_COLOR;
        if (animatable != null && (bar = animatable.getAnimator(BarRenderer.class)) != null) {
            color = bar.getActiveColor();
        }
        BarRenderer.renderHighlightBar(BarRenderer.BarAlignment.BOTTOM, pose, x, y - (float)yOffset, width, height, depth + 1.0f, barSize, color);
        float xText = x + (float)padding;
        for (int i = 0; i < list.size(); ++i) {
            float yText = y + (float)(i * heightPerRow) + (float)padding - (float)yOffset;
            Animator.drawText(guiGraphics, font, list.get(i), xText, yText);
        }
        matrices.popPose();
    }

    public static void renderToolTip(@Nullable Animatable animatable, List<FormattedCharSequence> list, GuiGraphics guiGraphics, float x, float width, float y, float barSize, int color) {
        MainToolTipRenderer.renderToolTip(TooltipAlignment.BOTTOM, animatable, list, guiGraphics, x, width, y, barSize, color);
    }

    public static void renderToolTip(@Nullable Animatable animatable, List<FormattedCharSequence> list, GuiGraphics guiGraphics, float x, float width, float y, float barSize) {
        MainToolTipRenderer.renderToolTip(TooltipAlignment.BOTTOM, animatable, list, guiGraphics, x, width, y, barSize, BaseColors.BACKGROUND_COLOR);
    }

    public static void renderToolTip(TooltipAlignment alignment, @Nullable Animatable animatable, Component component, GuiGraphics guiGraphics, float x, float width, float y, float barSize, int color) {
        Font font = Minecraft.getInstance().font;
        int padding = 10;
        List list = font.split((FormattedText)component, (int)width - padding * 2);
        MainToolTipRenderer.renderToolTip(alignment, animatable, list, guiGraphics, x, width, y, barSize, color);
    }

    public static void renderToolTip(TooltipAlignment alignment, @Nullable Animatable animatable, Component component, GuiGraphics guiGraphics, float x, float width, float y, float barSize) {
        MainToolTipRenderer.renderToolTip(alignment, animatable, component, guiGraphics, x, width, y, barSize, BaseColors.BACKGROUND_COLOR);
    }

    public static void renderToolTip(@Nullable Animatable animatable, Component component, GuiGraphics guiGraphics, float x, float width, float y, float barSize, int color) {
        Font font = Minecraft.getInstance().font;
        int padding = 10;
        List list = font.split((FormattedText)component, (int)width - padding * 2);
        MainToolTipRenderer.renderToolTip(animatable, list, guiGraphics, x, width, y, barSize, color);
    }

    public static void renderToolTip(@Nullable Animatable animatable, Component component, GuiGraphics guiGraphics, float x, float width, float y, float barSize) {
        MainToolTipRenderer.renderToolTip(animatable, component, guiGraphics, x, width, y, barSize, BaseColors.BACKGROUND_COLOR);
    }

    public static void renderToolTip(Component component, GuiGraphics guiGraphics, float x, float width, float y, float barSize, int color) {
        MainToolTipRenderer.renderToolTip(null, component, guiGraphics, x, width, y, barSize, color);
    }

    public static void renderToolTip(Component component, GuiGraphics guiGraphics, float x, float width, float y, float barSize) {
        MainToolTipRenderer.renderToolTip(null, component, guiGraphics, x, width, y, barSize, BaseColors.BACKGROUND_COLOR);
    }

    public static void renderToolTip(List<FormattedCharSequence> list, GuiGraphics guiGraphics, float x, float width, float y, float barSize) {
        MainToolTipRenderer.renderToolTip(null, list, guiGraphics, x, width, y, barSize);
    }

    public static enum TooltipAlignment {
        TOP,
        BOTTOM;

    }
}

