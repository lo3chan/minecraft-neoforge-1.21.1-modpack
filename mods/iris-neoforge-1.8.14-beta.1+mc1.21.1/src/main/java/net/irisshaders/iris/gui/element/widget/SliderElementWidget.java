/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.navigation.ScreenAxis
 *  net.minecraft.client.gui.navigation.ScreenDirection
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.Mth
 */
package net.irisshaders.iris.gui.element.widget;

import net.irisshaders.iris.gui.GuiUtil;
import net.irisshaders.iris.gui.element.widget.StringElementWidget;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuStringOptionElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;

public class SliderElementWidget
extends StringElementWidget {
    private static final int PREVIEW_SLIDER_WIDTH = 4;
    private static final int ACTIVE_SLIDER_WIDTH = 6;
    private boolean mouseDown = false;

    public SliderElementWidget(OptionMenuStringOptionElement element) {
        super(element);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta, boolean hovered) {
        this.updateRenderParams(35);
        if (!hovered && !this.isFocused()) {
            if (this.usedKeyboard) {
                this.usedKeyboard = false;
                this.mouseDown = false;
            }
            this.renderOptionWithValue(guiGraphics, false, (float)this.valueIndex / (float)(this.valueCount - 1), 4);
        } else {
            this.renderSlider(guiGraphics);
        }
        if (this.usedKeyboard) {
            if (Screen.hasShiftDown()) {
                this.renderTooltip(guiGraphics, SET_TO_DEFAULT, this.bounds.getBoundInDirection(ScreenDirection.RIGHT), this.bounds.position().y(), hovered);
            } else if (!this.screen.isDisplayingComment()) {
                this.renderTooltip(guiGraphics, (Component)this.unmodifiedLabel, this.bounds.getBoundInDirection(ScreenDirection.RIGHT), this.bounds.position().y(), hovered);
            }
        } else if (Screen.hasShiftDown()) {
            this.renderTooltip(guiGraphics, SET_TO_DEFAULT, mouseX, mouseY, hovered);
        } else if (!this.screen.isDisplayingComment()) {
            this.renderTooltip(guiGraphics, (Component)this.unmodifiedLabel, mouseX, mouseY, hovered);
        }
        if (this.usedKeyboard && !this.isFocused()) {
            this.usedKeyboard = false;
            this.onReleased();
        }
        if (this.mouseDown && !this.usedKeyboard) {
            if (!hovered) {
                this.onReleased();
            }
            this.whileDragging(mouseX);
        }
    }

    private void renderSlider(GuiGraphics guiGraphics) {
        GuiUtil.bindIrisWidgetsTexture();
        GuiUtil.drawButton(guiGraphics, this.bounds.position().x(), this.bounds.position().y(), this.bounds.width(), this.bounds.height(), this.isFocused(), false);
        GuiUtil.drawButton(guiGraphics, this.bounds.position().x() + 2, this.bounds.position().y() + 2, this.bounds.width() - 4, this.bounds.height() - 4, false, true);
        int sliderSpace = this.bounds.width() - 8 - 6;
        int sliderPos = this.bounds.position().x() + 4 + (int)((float)this.valueIndex / (float)(this.valueCount - 1) * (float)sliderSpace);
        GuiUtil.drawButton(guiGraphics, sliderPos, this.bounds.position().y() + 4, 6, this.bounds.height() - 8, this.mouseDown, false);
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, this.valueLabel, this.bounds.getCenterInAxis(ScreenAxis.HORIZONTAL) - (int)((double)font.width((FormattedText)this.valueLabel) * 0.5), this.bounds.position().y() + 7, 0xFFFFFF);
    }

    private void whileDragging(int mouseX) {
        float mousePositionAcrossWidget = Mth.clamp((float)((float)(mouseX - (this.bounds.position().x() + 4)) / (float)(this.bounds.width() - 8)), (float)0.0f, (float)1.0f);
        int newValueIndex = Math.min(this.valueCount - 1, (int)(mousePositionAcrossWidget * (float)this.valueCount));
        if (this.valueIndex != newValueIndex) {
            this.valueIndex = newValueIndex;
            this.updateLabels();
        }
    }

    private void onReleased() {
        this.mouseDown = false;
        this.queue();
        this.navigation.refresh();
        GuiUtil.playButtonClickSound();
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            if (Screen.hasShiftDown()) {
                if (this.applyOriginalValue()) {
                    this.navigation.refresh();
                }
                GuiUtil.playButtonClickSound();
                return true;
            }
            this.mouseDown = true;
            GuiUtil.playButtonClickSound();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keycode, int scancode, int modifiers) {
        if (keycode == 257) {
            if (Screen.hasShiftDown()) {
                if (this.applyOriginalValue()) {
                    this.navigation.refresh();
                }
                GuiUtil.playButtonClickSound();
                return true;
            }
            this.mouseDown = !this.mouseDown;
            this.usedKeyboard = true;
            GuiUtil.playButtonClickSound();
            return true;
        }
        if (this.mouseDown && this.usedKeyboard) {
            if (keycode == 263) {
                this.valueIndex = Math.max(0, this.valueIndex - 1);
                this.updateLabels();
                return true;
            }
            if (keycode == 262) {
                this.valueIndex = Math.min(this.valueCount - 1, this.valueIndex + 1);
                this.updateLabels();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (button == 0) {
            this.onReleased();
            return true;
        }
        return super.mouseReleased(mx, my, button);
    }
}

