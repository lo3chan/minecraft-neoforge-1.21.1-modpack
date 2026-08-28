/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.config.structure.EnumOption
 *  net.caffeinemc.mods.sodium.client.config.structure.Option
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.narration.NarratedElementType
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option;

import java.util.List;
import me.flashyreese.mods.reeses_sodium_options.client.config.ReeseSodiumOptionsConfig;
import me.flashyreese.mods.reeses_sodium_options.client.gui.control.ControlGuide;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.AbstractOptionRow;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionStateStore;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiTheme;
import net.caffeinemc.mods.sodium.client.config.structure.EnumOption;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

final class EnumOptionRow<E extends Enum<E>>
extends AbstractOptionRow {
    private static final int MAX_CONTENT_WIDTH = 70;
    private final EnumOption<E> option;

    EnumOptionRow(LayoutBounds dim, GuiTheme theme, OptionStateStore optionStateStore, EnumOption<E> option) {
        super(dim, theme, optionStateStore, (Option)option);
        this.option = option;
    }

    public EnumOption<E> getOption() {
        return this.option;
    }

    @Override
    protected int controlContentWidth() {
        return Math.min(70, this.font.width((FormattedText)this.displayValue()));
    }

    @Override
    public List<ControlGuide> controlGuides() {
        return this.canShowControlGuide() ? List.of(ControlGuide.press((Component)Component.translatable((String)"rso.controller.guide.next_value"))) : List.of();
    }

    @Override
    protected void renderControl(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (!this.option.showControl()) {
            return;
        }
        Component value = this.displayValue();
        int valueWidth = this.font.width((FormattedText)value);
        int x = this.rightAlignedControlX(valueWidth);
        int y = this.centeredTextY();
        this.drawString(guiGraphics, value, x, y, -1);
        if (this.option.isEnabled()) {
            this.requestPointerCursorIfHovered(guiGraphics);
        }
    }

    @Override
    protected boolean controlMouseClicked(double mouseX, double mouseY, int button) {
        boolean reverse = Screen.hasShiftDown();
        if (button == 1) {
            if (!ReeseSodiumOptionsConfig.config().isReverseCyclingControls()) {
                return false;
            }
            reverse = true;
        } else if (button != 0) {
            return false;
        }
        if (!(this.option.isEnabled() && this.option.showControl() && this.isMouseOverRow(mouseX, mouseY))) {
            return false;
        }
        this.cycleControl(reverse);
        return true;
    }

    @Override
    protected boolean controlKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.isRowFocused() || !EnumOptionRow.isSelectionKey(keyCode)) {
            return false;
        }
        this.cycleControl(Screen.hasShiftDown());
        return true;
    }

    @Override
    protected boolean activateControl() {
        this.cycleControl(Screen.hasShiftDown());
        return true;
    }

    private Component displayValue() {
        Component value = this.option.getElementName((Enum)this.option.getValidatedValue());
        return this.option.isEnabled() ? value : this.formatDisabledControlValue(value);
    }

    @Override
    protected Component narrationValue() {
        return this.option.showControl() ? this.option.getElementName((Enum)this.option.getValidatedValue()) : null;
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
        Component nextValue = this.option.getElementName(this.nextValue(false));
        if (this.isFocused()) {
            builder.add(NarratedElementType.USAGE, (Component)Component.translatable((String)"narration.cycle_button.usage.focused", (Object[])new Object[]{nextValue}));
        } else if (this.isHovered()) {
            builder.add(NarratedElementType.USAGE, (Component)Component.translatable((String)"narration.cycle_button.usage.hovered", (Object[])new Object[]{nextValue}));
        }
    }

    private void cycleControl(boolean reverse) {
        E nextValue = this.nextValue(reverse);
        if (nextValue == this.option.getValidatedValue()) {
            return;
        }
        this.option.modifyValue(nextValue);
        this.playClickSound();
    }

    private E nextValue(boolean reverse) {
        int i;
        Enum[] values = (Enum[])this.option.getEnumClass().getEnumConstants();
        Enum currentValue = (Enum)this.option.getValidatedValue();
        int valueIndex = 0;
        for (i = 0; i < values.length; ++i) {
            if (values[i] != currentValue) continue;
            valueIndex = i;
            break;
        }
        for (i = 0; i < values.length; ++i) {
            valueIndex = reverse ? (valueIndex + values.length - 1) % values.length : (valueIndex + 1) % values.length;
            Enum nextValue = values[valueIndex];
            if (!this.option.isValueAllowed(nextValue)) continue;
            return (E)nextValue;
        }
        return (E)currentValue;
    }
}

