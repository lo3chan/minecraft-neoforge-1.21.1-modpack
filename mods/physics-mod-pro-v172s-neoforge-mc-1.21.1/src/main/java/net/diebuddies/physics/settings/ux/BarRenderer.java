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
 *  org.joml.Math
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
import net.diebuddies.mixins.guiphysics.MixinAbstractWidgetAccessor;
import net.diebuddies.physics.settings.ux.Animatable;
import net.diebuddies.physics.settings.ux.Animator;
import net.diebuddies.physics.settings.ux.BaseColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.FastColor;
import org.joml.Math;
import org.joml.Matrix4f;

public class BarRenderer
extends Animator {
    private float oldHoveredPercent;
    private float currentHoveredPercent;
    private float animationSpeed = 0.6f;
    private boolean highlightBarAnimation = false;
    private BarAlignment barAlignment;
    private int hoveredColor = BaseColors.HIGHLIGHT_COLOR;
    private int disabledColor = BaseColors.DISABLED_COLOR;
    private int darkenColor = FastColor.ARGB32.color((int)255, (int)200, (int)200, (int)200);
    private int barColor = BaseColors.BAR_COLOR;
    private boolean active;
    private float barSize;

    public BarRenderer(BarAlignment barAlignment, boolean highlightBarAnimation, float barSize) {
        this.highlightBarAnimation = highlightBarAnimation;
        this.barAlignment = barAlignment;
        this.barSize = barSize;
    }

    @Override
    public boolean render(Animatable animatable, GuiGraphics guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
        AbstractWidget widget;
        boolean hovered = false;
        if (animatable instanceof MixinAbstractWidgetAccessor) {
            MixinAbstractWidgetAccessor accessor = (MixinAbstractWidgetAccessor)((Object)animatable);
            hovered = accessor.getIsHovered();
        }
        this.active = true;
        boolean focused = false;
        if (animatable instanceof AbstractWidget) {
            AbstractWidget widget2 = (AbstractWidget)animatable;
            this.active = widget2.isActive();
            focused = widget2.isFocused();
        }
        Matrix4f pose = guiGraphics.pose().last().pose();
        int color = BaseColors.BACKGROUND_COLOR;
        float x = animatable.getAnimX();
        float y = animatable.getAnimY();
        float width = animatable.getAnimWidth();
        float height = animatable.getAnimHeight();
        float depth = animatable.getAnimDepth() + 2.0f;
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        depth += 1.0f;
        int n = color = this.active ? this.barColor : this.disabledColor;
        if (!hovered && focused) {
            color = FastColor.ARGB32.multiply((int)color, (int)this.darkenColor);
        }
        if (this.barAlignment == BarAlignment.BOTTOM) {
            BarRenderer.drawRect(bufferBuilder, pose, x, y + height - this.barSize, width, this.barSize, depth, color);
        } else if (this.barAlignment == BarAlignment.RIGHT) {
            BarRenderer.drawRect(bufferBuilder, pose, x + width - this.barSize, y, this.barSize, height, depth, color);
        } else if (this.barAlignment == BarAlignment.TOP) {
            BarRenderer.drawRect(bufferBuilder, pose, x, y + height, width, this.barSize, depth, color);
        } else if (this.barAlignment == BarAlignment.LEFT) {
            BarRenderer.drawRect(bufferBuilder, pose, x, y, this.barSize, height, depth, color);
        }
        if (animatable instanceof AbstractWidget && (widget = (AbstractWidget)animatable).isHoveredOrFocused()) {
            depth += 1.0f;
            int n2 = color = this.active ? this.hoveredColor : this.disabledColor;
            if (!hovered && focused) {
                color = FastColor.ARGB32.multiply((int)color, (int)this.darkenColor);
            }
            float hoverPercent = Math.lerp((float)this.oldHoveredPercent, (float)this.currentHoveredPercent, (float)renderPercent) * 0.5f;
            if (!this.isHighlightBarAnimation()) {
                hoverPercent = 0.5f;
            }
            if (this.barAlignment == BarAlignment.BOTTOM) {
                BarRenderer.drawRect(bufferBuilder, pose, x + width * 0.5f - width * hoverPercent, y + height - this.barSize, width * hoverPercent * 2.0f, this.barSize, depth, color);
            } else if (this.barAlignment == BarAlignment.RIGHT) {
                BarRenderer.drawRect(bufferBuilder, pose, x + width - this.barSize, y + height * 0.5f - height * hoverPercent, this.barSize, height * hoverPercent * 2.0f, depth, color);
            } else if (this.barAlignment == BarAlignment.TOP) {
                BarRenderer.drawRect(bufferBuilder, pose, x + width * 0.5f - width * hoverPercent, y, width * hoverPercent * 2.0f, this.barSize, depth, color);
            } else if (this.barAlignment == BarAlignment.LEFT) {
                BarRenderer.drawRect(bufferBuilder, pose, x, y + height * 0.5f - height * hoverPercent, this.barSize, height * hoverPercent * 2.0f, depth, color);
            }
        }
        BufferUploader.drawWithShader((MeshData)bufferBuilder.build());
        RenderSystem.disableBlend();
        return false;
    }

    public static void renderHighlightBar(BarAlignment barAlignment, Matrix4f pose, float x, float y, float width, float height, float depth, float barSize, int color) {
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        if (barAlignment == BarAlignment.BOTTOM) {
            BarRenderer.drawRect(bufferBuilder, pose, x, y + height - barSize, width, barSize, depth, color);
        } else if (barAlignment == BarAlignment.RIGHT) {
            BarRenderer.drawRect(bufferBuilder, pose, x + width - barSize, y, barSize, height, depth, color);
        } else if (barAlignment == BarAlignment.TOP) {
            BarRenderer.drawRect(bufferBuilder, pose, x, y + height, width, barSize, depth, color);
        } else if (barAlignment == BarAlignment.LEFT) {
            BarRenderer.drawRect(bufferBuilder, pose, x, y, barSize, height, depth, color);
        }
        BufferUploader.drawWithShader((MeshData)bufferBuilder.build());
        RenderSystem.disableBlend();
    }

    @Override
    public void tick(Animatable animatable) {
        MixinAbstractWidgetAccessor accessor;
        super.tick(animatable);
        this.oldHoveredPercent = this.currentHoveredPercent;
        float hovered = 0.0f;
        if (animatable instanceof MixinAbstractWidgetAccessor && (accessor = (MixinAbstractWidgetAccessor)((Object)animatable)).getIsHovered()) {
            hovered = 1.0f;
        }
        this.currentHoveredPercent = Math.lerp((float)this.currentHoveredPercent, (float)hovered, (float)this.animationSpeed);
    }

    public int getActiveColor() {
        if (this.active) {
            return this.hoveredColor;
        }
        return this.disabledColor;
    }

    public BarRenderer setAnimationSpeed(float animationSpeed) {
        this.animationSpeed = animationSpeed;
        return this;
    }

    public float getAnimationSpeed() {
        return this.animationSpeed;
    }

    public BarRenderer setBarAlignment(BarAlignment barAlignment) {
        this.barAlignment = barAlignment;
        return this;
    }

    public BarAlignment getBarAlignment() {
        return this.barAlignment;
    }

    public BarRenderer setHighlightBarAnimation(boolean highlightBarAnimation) {
        this.highlightBarAnimation = highlightBarAnimation;
        return this;
    }

    public boolean isHighlightBarAnimation() {
        return this.highlightBarAnimation;
    }

    public BarRenderer setHoveredColor(int hoveredColor) {
        this.hoveredColor = hoveredColor;
        return this;
    }

    public BarRenderer setDisabledColor(int disabledColor) {
        this.disabledColor = disabledColor;
        return this;
    }

    public BarRenderer setBarColor(int barColor) {
        this.barColor = barColor;
        return this;
    }

    public BarRenderer setDarkenColor(int darkenColor) {
        this.darkenColor = darkenColor;
        return this;
    }

    public static enum BarAlignment {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM;

    }
}

