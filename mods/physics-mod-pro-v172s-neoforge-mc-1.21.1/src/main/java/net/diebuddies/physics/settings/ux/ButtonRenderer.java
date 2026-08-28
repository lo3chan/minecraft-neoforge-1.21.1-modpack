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
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractButton
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
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
import net.diebuddies.math.Math;
import net.diebuddies.mixins.guiphysics.MixinAbstractWidgetAccessor;
import net.diebuddies.physics.settings.ux.Animatable;
import net.diebuddies.physics.settings.ux.Animator;
import net.diebuddies.physics.settings.ux.BaseColors;
import net.diebuddies.physics.settings.ux.TextAlignment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public class ButtonRenderer
extends Animator {
    private TextAlignment alignment;
    private boolean renderTooltips = true;
    private ChatFormatting chatFormatting;
    private ResourceLocation image;

    public ButtonRenderer(TextAlignment alignment, ChatFormatting chatFormatting) {
        this.chatFormatting = chatFormatting;
        this.alignment = alignment;
    }

    public ButtonRenderer(TextAlignment alignment) {
        this(alignment, null);
    }

    public ButtonRenderer() {
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
        int color = BaseColors.BACKGROUND_COLOR;
        float x = animatable.getAnimX();
        float y = animatable.getAnimY();
        float width = animatable.getAnimWidth();
        float height = animatable.getAnimHeight();
        float depth = animatable.getAnimDepth();
        PoseStack matrices = guiGraphics.pose();
        matrices.pushPose();
        matrices.translate(0.0f, 0.0f, depth);
        depth = -2.0f;
        AbstractButton button = (AbstractButton)animatable;
        if (!button.active) {
            color = BaseColors.INACTIVE_COLOR;
        }
        Matrix4f pose = matrices.last().pose();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        ButtonRenderer.drawRect(bufferBuilder, pose, x, y, width, height, depth, color);
        BufferUploader.drawWithShader((MeshData)bufferBuilder.build());
        if (this.image != null) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture((int)0, (ResourceLocation)this.image);
            RenderSystem.setShaderColor((float)animatable.getAnimRed(), (float)animatable.getAnimGreen(), (float)animatable.getAnimBlue(), (float)animatable.getAnimAlpha());
            bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            ButtonRenderer.drawRect(bufferBuilder, pose, x, y, width, height, depth, 0.0f, 1.0f, 0.0f, 1.0f);
            BufferUploader.drawWithShader((MeshData)bufferBuilder.build());
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
        RenderSystem.disableBlend();
        FormattedCharSequence formattedCharSequence = (this.chatFormatting == null ? button.getMessage() : button.getMessage().copy().withStyle(this.chatFormatting)).getVisualOrderText();
        Font font = Minecraft.getInstance().font;
        float xText = x + 7.0f;
        if (this.alignment == TextAlignment.CENTER) {
            xText = x + width * 0.5f - (float)font.width(formattedCharSequence) * 0.5f;
        } else if (this.alignment == TextAlignment.RIGHT) {
            xText = x + width - (float)font.width(formattedCharSequence) - 7.0f;
        }
        ButtonRenderer.drawText(guiGraphics, font, formattedCharSequence, Math.fastRound(xText), Math.fastRound(y + (height - 8.0f) / 2.0f));
        matrices.popPose();
        if (this.renderTooltips && animatable instanceof MixinAbstractWidgetAccessor) {
            AbstractWidget widget;
            MixinAbstractWidgetAccessor invoker = (MixinAbstractWidgetAccessor)((Object)animatable);
            if (animatable instanceof AbstractWidget && (widget = (AbstractWidget)animatable).isHoveredOrFocused() && widget.getTooltip() != null) {
                ((MixinAbstractWidgetAccessor)widget).getTooltipHolder().refreshTooltipForNextRenderPass(widget.isHovered(), widget.isFocused(), widget.getRectangle());
            }
        }
        return true;
    }

    public ButtonRenderer setRenderTooltips(boolean renderTooltips) {
        this.renderTooltips = renderTooltips;
        return this;
    }

    public boolean isRenderingTooltips() {
        return this.renderTooltips;
    }

    public ResourceLocation getImage() {
        return this.image;
    }

    public ButtonRenderer setImage(ResourceLocation image) {
        this.image = image;
        return this;
    }
}

