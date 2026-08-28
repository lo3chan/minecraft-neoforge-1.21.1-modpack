/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.api.config.option.SteppedValidator
 *  net.caffeinemc.mods.sodium.client.config.structure.IntegerOption
 *  net.caffeinemc.mods.sodium.client.config.structure.Option
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.narration.NarratedElementType
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.Mth
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option;

import java.util.List;
import me.flashyreese.mods.reeses_sodium_options.client.config.ReeseSodiumOptionsConfig;
import me.flashyreese.mods.reeses_sodium_options.client.gui.control.ControlGuide;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.AbstractOptionRow;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionStateStore;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiTheme;
import me.flashyreese.mods.reeses_sodium_options.client.gui.widget.BaseWidget;
import net.caffeinemc.mods.sodium.api.config.option.SteppedValidator;
import net.caffeinemc.mods.sodium.client.config.structure.IntegerOption;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;

final class IntegerSliderOptionRow
extends AbstractOptionRow {
    private static final int SLIDER_WIDTH = 90;
    private static final int TRACK_HEIGHT = 10;
    private static final int THUMB_WIDTH = 4;
    private static final int VALUE_GAP = 6;
    private final IntegerOption option;
    private double thumbPosition;
    private boolean sliderHeld;
    private boolean editMode;
    private boolean drawSlider;
    private int contentWidth;

    IntegerSliderOptionRow(LayoutBounds dim, GuiTheme theme, OptionStateStore optionStateStore, IntegerOption option) {
        super(dim, theme, optionStateStore, (Option)option);
        this.option = option;
        this.thumbPosition = this.thumbPositionForValue((Integer)option.getValidatedValue());
        this.contentWidth = this.valueWidth();
    }

    public IntegerOption getOption() {
        return this.option;
    }

    @Override
    protected void prepareRender(int mouseX, int mouseY, float delta) {
        boolean canDrawSlider = this.option.isEnabled() && this.option.showControl();
        this.drawSlider = canDrawSlider && (this.isMouseOverRow(mouseX, mouseY) || this.isRowFocused() || this.sliderHeld);
        int valueWidth = this.valueWidth();
        this.contentWidth = this.drawSlider ? 96 + valueWidth : valueWidth;
    }

    @Override
    protected int controlContentWidth() {
        return this.contentWidth;
    }

    @Override
    public List<ControlGuide> controlGuides() {
        if (!this.canShowControlGuide()) {
            return List.of();
        }
        return this.editMode ? List.of(ControlGuide.navigationLeftRight((Component)Component.translatable((String)"rso.controller.guide.adjust_value")), ControlGuide.press((Component)Component.translatable((String)"rso.controller.guide.done"))) : List.of(ControlGuide.press((Component)Component.translatable((String)"rso.controller.guide.edit_slider")));
    }

    @Override
    protected void renderControl(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (!this.option.showControl()) {
            return;
        }
        Component value = this.displayValue();
        int valueWidth = this.font.width((FormattedText)value);
        int sliderX = this.sliderX();
        int sliderY = this.sliderY();
        if (this.drawSlider) {
            if (!this.sliderHeld) {
                this.thumbPosition = this.thumbPositionForValue((Integer)this.option.getValidatedValue());
            }
            int thumbX = sliderX + (int)(this.thumbPosition * 90.0) - 2;
            int trackY = (int)((double)((float)sliderY + 5.0f) - 0.5);
            this.drawRect(guiGraphics, sliderX, trackY, sliderX + 90, trackY + 1, this.theme.themeLighter);
            this.drawRect(guiGraphics, thumbX, sliderY, thumbX + 4, sliderY + 10, -1);
            if (this.isRowFocused() && this.editMode && BaseWidget.isKeyboardFocusVisible()) {
                this.drawBorder(guiGraphics, thumbX - 1, sliderY - 1, thumbX + 4 + 1, sliderY + 10 + 1, -1);
            }
            this.drawString(guiGraphics, value, sliderX - valueWidth - 6, sliderY + 5 - 4, -1);
        } else {
            this.drawString(guiGraphics, value, sliderX + 90 - valueWidth, sliderY + 5 - 4, -1);
        }
        if (this.isMouseOverSlider(mouseX, mouseY)) {
            // empty if block
        }
    }

    @Override
    protected boolean controlMouseClicked(double mouseX, double mouseY, int button) {
        this.sliderHeld = false;
        if (!(this.option.isEnabled() && this.option.showControl() && button == 0 && this.isMouseOverRow(mouseX, mouseY))) {
            return false;
        }
        if (this.isMouseOverSlider(mouseX, mouseY)) {
            this.setValueFromMouse(mouseX);
            this.sliderHeld = true;
            this.actionButtons.holdLayout(true);
        }
        return true;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!this.sliderHeld || button != 0 || !this.option.isEnabled()) {
            return false;
        }
        this.actionButtons.holdLayout(true);
        this.setValueFromMouse(mouseX);
        return true;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button != 0 || !this.sliderHeld) {
            return false;
        }
        this.sliderHeld = false;
        this.actionButtons.releaseLayoutHold();
        this.playClickSound();
        return true;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!(ReeseSodiumOptionsConfig.config().isShiftScrollSliderAdjustments() && this.option.isEnabled() && this.option.showControl() && Screen.hasShiftDown() && this.isMouseOverSlider(mouseX, mouseY))) {
            return false;
        }
        return this.adjustValue((int)verticalAmount);
    }

    @Override
    protected boolean controlKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.isRowFocused()) {
            return false;
        }
        if (IntegerSliderOptionRow.isSelectionKey(keyCode)) {
            this.editMode = !this.editMode;
            return true;
        }
        if (this.editMode) {
            if (keyCode == 263) {
                return this.adjustValue(-1);
            }
            if (keyCode == 262) {
                return this.adjustValue(1);
            }
        }
        return false;
    }

    @Override
    protected boolean activateControl() {
        this.editMode = !this.editMode;
        return true;
    }

    @Override
    public boolean handleBackNavigation() {
        if (!this.editMode) {
            return false;
        }
        this.editMode = false;
        return true;
    }

    @Override
    protected void releaseMouseHold() {
        this.sliderHeld = false;
    }

    @Override
    protected void onControlFocusLost() {
        this.editMode = false;
    }

    private Component displayValue() {
        Component value = this.option.formatValue(((Integer)this.option.getValidatedValue()).intValue());
        return this.option.isEnabled() ? value : this.formatDisabledControlValue(value);
    }

    @Override
    protected Component narrationValue() {
        return this.option.showControl() ? this.option.formatValue(((Integer)this.option.getValidatedValue()).intValue()) : null;
    }

    @Override
    protected void updateControlNarration(NarrationElementOutput builder) {
        if (!this.option.isEnabled()) {
            builder.add(NarratedElementType.HINT, (Component)Component.translatable((String)"rso.narration.option_unavailable"));
            return;
        }
        if (!this.option.showControl()) {
            return;
        }
        if (this.isFocused()) {
            builder.add(NarratedElementType.USAGE, (Component)Component.translatable((String)(this.editMode ? "narration.slider.usage.focused" : "narration.slider.usage.focused.keyboard_cannot_change_value")));
        } else if (this.isHovered()) {
            builder.add(NarratedElementType.USAGE, (Component)Component.translatable((String)"narration.slider.usage.hovered"));
        }
    }

    private int valueWidth() {
        return this.font.width((FormattedText)this.displayValue());
    }

    private int sliderX() {
        return this.rightAlignedControlX(90);
    }

    private int sliderY() {
        return this.getDimensions().getCenterY() - 5;
    }

    private boolean isMouseOverSlider(double mouseX, double mouseY) {
        int sliderX = this.sliderX();
        int sliderY = this.sliderY();
        return mouseX >= (double)sliderX && mouseX < (double)(sliderX + 90) && mouseY >= (double)sliderY && mouseY < (double)(sliderY + 10);
    }

    private double thumbPositionForValue(int value) {
        SteppedValidator validator = this.option.getSteppedValidator();
        int min = validator.min();
        int max = validator.max();
        if (max == min) {
            return 0.0;
        }
        return Mth.clamp((double)((double)(value - min) / (double)(max - min)), (double)0.0, (double)1.0);
    }

    private int valueForThumbPosition() {
        SteppedValidator validator = this.option.getSteppedValidator();
        int step = validator.step();
        int min = validator.min();
        int max = validator.max();
        return Mth.clamp((int)(min + step * (int)Math.round(this.thumbPosition * (double)(max - min) / (double)step)), (int)min, (int)max);
    }

    private void setValueFromMouse(double mouseX) {
        this.thumbPosition = Mth.clamp((double)((mouseX - (double)this.sliderX()) / 90.0), (double)0.0, (double)1.0);
        this.option.modifyValue((Object)this.valueForThumbPosition());
    }

    private boolean adjustValue(int direction) {
        if (direction == 0) {
            return false;
        }
        SteppedValidator validator = this.option.getSteppedValidator();
        int value = (Integer)this.option.getValidatedValue();
        int nextValue = Mth.clamp((int)(value + validator.step() * direction), (int)validator.min(), (int)validator.max());
        if (nextValue == value) {
            return false;
        }
        this.option.modifyValue((Object)nextValue);
        this.thumbPosition = this.thumbPositionForValue((Integer)this.option.getValidatedValue());
        return true;
    }
}

