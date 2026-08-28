/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.config.structure.ExternalButtonOption
 *  net.caffeinemc.mods.sodium.client.config.structure.Option
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.narration.NarratedElementType
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.Style
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option;

import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.AbstractOptionRow;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionStateStore;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiTheme;
import net.caffeinemc.mods.sodium.client.config.structure.ExternalButtonOption;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

final class ExternalButtonOptionRow
extends AbstractOptionRow {
    private static final int CONTENT_WIDTH = 65;
    private static final Component BASE_BUTTON_TEXT = Component.translatable((String)"sodium.options.open_external_page_button");
    private final Screen screen;
    private final ExternalButtonOption option;

    ExternalButtonOptionRow(Screen screen, LayoutBounds dim, GuiTheme theme, OptionStateStore optionStateStore, ExternalButtonOption option) {
        super(dim, theme, optionStateStore, (Option)option);
        this.screen = screen;
        this.option = option;
    }

    public ExternalButtonOption getOption() {
        return this.option;
    }

    @Override
    protected int controlContentWidth() {
        return 65;
    }

    @Override
    protected void renderControl(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        Component text = this.buttonText();
        int x = this.rightAlignedControlX(this.font.width((FormattedText)text));
        int y = this.centeredTextY();
        this.drawString(guiGraphics, text, x, y, -1);
        if (this.option.isEnabled()) {
            this.requestPointerCursorIfHovered(guiGraphics);
        }
    }

    @Override
    protected boolean activateControl() {
        if (!this.option.isEnabled()) {
            return false;
        }
        this.option.getCurrentScreenConsumer().accept(this.screen);
        this.playClickSound();
        return true;
    }

    private Component buttonText() {
        if (!this.option.isEnabled()) {
            return BASE_BUTTON_TEXT.copy().withStyle(new ChatFormatting[]{ChatFormatting.STRIKETHROUGH, ChatFormatting.GRAY});
        }
        return Component.empty().append((Component)BASE_BUTTON_TEXT.copy().withStyle(ChatFormatting.UNDERLINE)).append((Component)Component.literal((String)" >").withStyle(Style.EMPTY.withColor(this.theme.theme)));
    }

    @Override
    protected void updateControlNarration(NarrationElementOutput builder) {
        if (!this.option.isEnabled()) {
            builder.add(NarratedElementType.HINT, (Component)Component.translatable((String)"rso.narration.option_unavailable"));
            return;
        }
        this.addUsageNarration(builder, "narration.link.usage.focused", "narration.link.usage.hovered");
    }
}

