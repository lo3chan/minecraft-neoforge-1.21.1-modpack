/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DestFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SourceFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.network.chat.CommonComponents
 */
package mezz.jei.gui.elements;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.buttons.IButtonState;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.common.Internal;
import mezz.jei.common.gui.elements.DrawableBlank;
import mezz.jei.common.gui.elements.ScalableDrawable;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;

class InternalIconButton
extends Button
implements IButtonState {
    private IDrawable icon = DrawableBlank.EMPTY;
    private boolean pressed = false;
    private boolean forcePressed = false;

    public InternalIconButton() {
        super(0, 0, 0, 0, CommonComponents.EMPTY, b -> {}, Button.DEFAULT_NARRATION);
    }

    public void updateBounds(ImmutableRect2i area) {
        this.setX(area.getX());
        this.setY(area.getY());
        this.width = area.getWidth();
        this.height = area.getHeight();
    }

    public void setHeight(int value) {
        this.height = value;
    }

    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        boolean hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        RenderSystem.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Textures textures = Internal.getTextures();
        boolean isPressed = this.pressed || this.forcePressed;
        ScalableDrawable texture = textures.getButtonForState(isPressed, this.active, hovered);
        texture.draw(guiGraphics, this.getX(), this.getY(), this.width, this.height);
        int color = -2039584;
        if (!this.active) {
            color = -6250336;
        } else if (hovered) {
            color = -1;
        }
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float blue = (float)(color >> 8 & 0xFF) / 255.0f;
        float green = (float)(color & 0xFF) / 255.0f;
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        RenderSystem.setShaderColor((float)red, (float)blue, (float)green, (float)alpha);
        double xOffset = (double)this.getX() + (double)(this.width - this.icon.getWidth()) / 2.0;
        double yOffset = (double)this.getY() + (double)(this.height - this.icon.getHeight()) / 2.0;
        if (isPressed) {
            xOffset += 0.5;
            yOffset += 0.5;
        }
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(xOffset, yOffset, 0.0);
        this.icon.draw(guiGraphics);
        poseStack.popPose();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    @Override
    public void setForcePressed(boolean forcePressed) {
        this.forcePressed = forcePressed;
    }

    public boolean clicked(double x, double y) {
        return super.clicked(x, y);
    }

    public boolean isValidClickButton(int mouseButton) {
        return super.isValidClickButton(mouseButton);
    }

    @Override
    public void setIcon(IDrawable icon) {
        this.icon = icon;
    }

    @Override
    public void setActive(boolean value) {
        this.active = value;
    }

    @Override
    public void setVisible(boolean value) {
        this.visible = value;
    }
}

