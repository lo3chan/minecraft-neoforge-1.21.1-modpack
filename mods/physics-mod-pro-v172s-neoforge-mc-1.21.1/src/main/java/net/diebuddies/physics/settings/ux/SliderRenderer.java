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
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractSliderButton
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.util.FastColor$ARGB32
 *  net.minecraft.util.FormattedCharSequence
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
import net.diebuddies.math.Math;
import net.diebuddies.mixins.guiphysics.MixinAbstractSliderButtonAccessor;
import net.diebuddies.mixins.guiphysics.MixinAbstractWidgetAccessor;
import net.diebuddies.physics.settings.ux.Animatable;
import net.diebuddies.physics.settings.ux.Animator;
import net.diebuddies.physics.settings.ux.BaseColors;
import net.diebuddies.physics.settings.ux.TextAlignment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public class SliderRenderer
extends Animator {
    private TextAlignment alignment;
    private boolean renderTooltips = true;
    private ChatFormatting chatFormatting;

    public SliderRenderer(TextAlignment alignment, ChatFormatting chatFormatting) {
        this.chatFormatting = chatFormatting;
        this.alignment = alignment;
    }

    public SliderRenderer(TextAlignment alignment) {
        this(alignment, null);
    }

    public SliderRenderer() {
        this(TextAlignment.CENTER);
    }

    @Override
    public boolean render(Animatable animatable, GuiGraphics guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
        boolean hovered = animatable.isInside(mouseX, mouseY);
        if (animatable instanceof MixinAbstractWidgetAccessor) {
            MixinAbstractWidgetAccessor accessor = (MixinAbstractWidgetAccessor)((Object)animatable);
            boolean wasHovered = accessor.getIsHovered();
            accessor.setIsHovered(hovered);
            if (!wasHovered && hovered) {
                Minecraft.getInstance().getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, (float)(Math.random() * 0.2f + 0.9f)));
            }
        }
        AbstractSliderButton slider = (AbstractSliderButton)animatable;
        MixinAbstractSliderButtonAccessor sliderAccessor = (MixinAbstractSliderButtonAccessor)((Object)animatable);
        Matrix4f pose = guiGraphics.pose().last().pose();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        int color = BaseColors.BACKGROUND_COLOR;
        float x = animatable.getAnimX();
        float y = animatable.getAnimY();
        float width = animatable.getAnimWidth();
        float height = animatable.getAnimHeight();
        float depth = animatable.getAnimDepth() + 1.0f;
        SliderRenderer.drawRect(bufferBuilder, pose, x, y, width, height, depth, color);
        double value = sliderAccessor.getValue();
        int barSize = 8;
        float sliderOffset = (float)value * (width - (float)barSize);
        SliderRenderer.drawRect(bufferBuilder, pose, x + sliderOffset, y, barSize, height, depth + 1.0f, FastColor.ARGB32.color((int)100, (int)167, (int)167, (int)167));
        SliderRenderer.drawLine(bufferBuilder, pose, x + sliderOffset, y + 1.0f, x + sliderOffset + (float)barSize, y + 1.0f, depth + 2.0f, FastColor.ARGB32.color((int)255, (int)40, (int)40, (int)40));
        SliderRenderer.drawLine(bufferBuilder, pose, x + sliderOffset + 1.0f, y, x + sliderOffset + 1.0f, y + height - 1.0f, depth + 2.0f, FastColor.ARGB32.color((int)255, (int)40, (int)40, (int)40));
        color = BaseColors.BAR_COLOR;
        if (slider.isHoveredOrFocused()) {
            color = BaseColors.HIGHLIGHT_COLOR;
        }
        if (!slider.active) {
            color = BaseColors.DISABLED_COLOR;
        }
        SliderRenderer.drawLine(bufferBuilder, pose, x + sliderOffset, y, x + sliderOffset + (float)barSize, y, depth + 3.0f, color);
        SliderRenderer.drawLine(bufferBuilder, pose, x + sliderOffset + (float)barSize, y, x + sliderOffset + (float)barSize, y + height - 1.0f, depth + 3.0f, color);
        SliderRenderer.drawLine(bufferBuilder, pose, x + sliderOffset, y + height - 1.0f, x + sliderOffset + (float)barSize, y + height - 1.0f, depth + 3.0f, color);
        SliderRenderer.drawLine(bufferBuilder, pose, x + sliderOffset, y, x + sliderOffset, y + height - 1.0f, depth + 3.0f, color);
        BufferUploader.drawWithShader((MeshData)bufferBuilder.build());
        RenderSystem.disableBlend();
        FormattedCharSequence formattedCharSequence = (this.chatFormatting == null ? slider.getMessage() : slider.getMessage().copy().withStyle(this.chatFormatting)).getVisualOrderText();
        Font font = Minecraft.getInstance().font;
        float xText = x + 7.0f;
        if (this.alignment == TextAlignment.CENTER) {
            xText = x + width * 0.5f - (float)font.width(formattedCharSequence) * 0.5f;
        } else if (this.alignment == TextAlignment.RIGHT) {
            xText = x + width - (float)font.width(formattedCharSequence) - 7.0f;
        }
        SliderRenderer.drawText(guiGraphics, font, formattedCharSequence, Math.fastRound(xText), Math.fastRound(y + (height - 8.0f) / 2.0f));
        if (this.renderTooltips && animatable instanceof MixinAbstractWidgetAccessor) {
            AbstractWidget widget;
            MixinAbstractWidgetAccessor invoker = (MixinAbstractWidgetAccessor)((Object)animatable);
            if (animatable instanceof AbstractWidget && (widget = (AbstractWidget)animatable).isHoveredOrFocused() && widget.getTooltip() != null) {
                ((MixinAbstractWidgetAccessor)widget).getTooltipHolder().refreshTooltipForNextRenderPass(widget.isHovered(), widget.isFocused(), widget.getRectangle());
            }
        }
        return true;
    }

    public void setRenderTooltips(boolean renderTooltips) {
        this.renderTooltips = renderTooltips;
    }

    public boolean isRenderingTooltips() {
        return this.renderTooltips;
    }
}

