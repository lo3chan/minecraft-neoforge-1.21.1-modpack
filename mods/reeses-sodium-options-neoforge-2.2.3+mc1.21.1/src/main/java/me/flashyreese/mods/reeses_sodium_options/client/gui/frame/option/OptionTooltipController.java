/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.api.config.option.OptionImpact
 *  net.caffeinemc.mods.sodium.client.config.structure.ModOptions
 *  net.caffeinemc.mods.sodium.client.config.structure.Option
 *  net.caffeinemc.mods.sodium.client.gui.ColorTheme
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.locale.Language
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.FormattedCharSequence
 *  org.jetbrains.annotations.Nullable
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option;

import java.util.ArrayList;
import java.util.List;
import me.flashyreese.mods.reeses_sodium_options.client.config.ReeseSodiumOptionsConfig;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.OptionRow;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.option.OptionExtended;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionStateStore;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiThemes;
import me.flashyreese.mods.reeses_sodium_options.client.gui.widget.BaseWidget;
import net.caffeinemc.mods.sodium.api.config.option.OptionImpact;
import net.caffeinemc.mods.sodium.client.config.structure.ModOptions;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.caffeinemc.mods.sodium.client.gui.ColorTheme;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

final class OptionTooltipController {
    private static final int DEFAULT_TOOLTIP_BORDER_COLOR = -7019309;
    private static final int TEXT_PADDING = 3;
    private static final int BOX_PADDING = 3;
    private static final int LINE_HEIGHT = 12;
    private static final float TOOLTIP_Z_OFFSET = 400.0f;
    private final LayoutBounds viewportBounds;
    private final ModOptions modOptions;
    private final OptionStateStore optionStateStore;
    private final BoxRenderer boxRenderer;
    private long targetStartTime;
    @Nullable
    private OptionRow targetElement;

    OptionTooltipController(LayoutBounds viewportBounds, ModOptions modOptions, OptionStateStore optionStateStore, BoxRenderer boxRenderer) {
        this.viewportBounds = viewportBounds;
        this.modOptions = modOptions;
        this.optionStateStore = optionStateStore;
        this.boxRenderer = boxRenderer;
    }

    void render(GuiGraphics guiGraphics, List<OptionRow> optionRows, int mouseX, int mouseY) {
        OptionRow targetElement = this.findTargetOptionRow(optionRows, mouseX, mouseY);
        if (targetElement != null && this.targetElement == targetElement) {
            if (this.targetStartTime == 0L) {
                this.targetStartTime = System.currentTimeMillis();
            }
            this.renderTooltip(guiGraphics, targetElement);
        } else {
            this.targetStartTime = 0L;
            this.targetElement = targetElement;
        }
    }

    @Nullable
    private OptionRow findTargetOptionRow(List<OptionRow> optionRows, int mouseX, int mouseY) {
        OptionRow hoveredElement = this.findHoveredOptionRow(optionRows, mouseX, mouseY);
        if (hoveredElement != null) {
            return hoveredElement;
        }
        OptionRow focusedElement = this.findFocusedOptionRow(optionRows);
        if (focusedElement != null) {
            return focusedElement;
        }
        return this.findSelectedSearchResultRow(optionRows);
    }

    @Nullable
    private OptionRow findHoveredOptionRow(List<OptionRow> optionRows, int mouseX, int mouseY) {
        if (!this.viewportBounds.contains(mouseX, mouseY)) {
            return null;
        }
        return optionRows.stream().filter(this::isVisibleOptionRow).filter(optionRow -> optionRow.isMouseOver(mouseX, mouseY)).findFirst().orElse(null);
    }

    @Nullable
    private OptionRow findFocusedOptionRow(List<OptionRow> optionRows) {
        if (!BaseWidget.isKeyboardFocusVisible()) {
            return null;
        }
        return optionRows.stream().filter(this::isVisibleOptionRow).filter(GuiEventListener::isFocused).findFirst().orElse(null);
    }

    @Nullable
    private OptionRow findSelectedSearchResultRow(List<OptionRow> optionRows) {
        if (!this.optionStateStore.searchActive()) {
            return null;
        }
        return optionRows.stream().filter(this::isVisibleOptionRow).filter(this::isSelectedSearchResult).findFirst().orElse(null);
    }

    private boolean isSelectedSearchResult(OptionRow optionRow) {
        Option option = optionRow.getOption();
        if (!(option instanceof OptionExtended)) {
            return false;
        }
        OptionExtended optionExtended = (OptionExtended)option;
        return this.optionStateStore.optionUiState(optionExtended.rso$getId()).isSelected();
    }

    private boolean isVisibleOptionRow(OptionRow optionRow) {
        return this.viewportBounds.overlaps(optionRow.getDimensions());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renderTooltip(GuiGraphics guiGraphics, OptionRow element) {
        int boxYCutoff;
        if (this.targetStartTime + (long)ReeseSodiumOptionsConfig.config().getTooltipDelayMs() > System.currentTimeMillis()) {
            return;
        }
        LayoutBounds dim = element.getDimensions();
        int boxWidth = dim.width();
        int boxY = dim.getLimitY();
        int boxX = dim.x();
        List<FormattedCharSequence> tooltip = this.buildTooltip(element.getOption(), boxWidth);
        if (tooltip.isEmpty()) {
            return;
        }
        int boxHeight = tooltip.size() * 12 + 3;
        int boxYLimit = boxY + boxHeight;
        if (boxYLimit > (boxYCutoff = this.viewportBounds.getLimitY())) {
            boxY -= boxHeight + dim.height();
        }
        if (boxY < 0) {
            boxY = dim.getLimitY();
        }
        guiGraphics.flush();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0f, 0.0f, 400.0f);
        try {
            this.boxRenderer.drawRect(guiGraphics, boxX, boxY, boxX + boxWidth, boxY + boxHeight, -536870912);
            int borderColor = ReeseSodiumOptionsConfig.config().isColorThemes() && ReeseSodiumOptionsConfig.config().isThemedTooltipBorders() ? GuiThemes.fromSodium((ColorTheme)this.modOptions.theme()).theme : -7019309;
            this.boxRenderer.drawBorder(guiGraphics, boxX, boxY, boxX + boxWidth, boxY + boxHeight, borderColor);
            for (int i = 0; i < tooltip.size(); ++i) {
                guiGraphics.drawString(Minecraft.getInstance().font, tooltip.get(i), boxX + 3, boxY + 3 + i * 12, -1, true);
            }
        }
        finally {
            guiGraphics.flush();
            guiGraphics.pose().popPose();
        }
    }

    private List<FormattedCharSequence> buildTooltip(Option option, int boxWidth) {
        ArrayList<FormattedCharSequence> tooltip = new ArrayList<FormattedCharSequence>();
        if (ReeseSodiumOptionsConfig.config().isTooltipOptionIds() && option instanceof OptionExtended) {
            OptionExtended optionExtended = (OptionExtended)option;
            tooltip.add(Language.getInstance().getVisualOrder((FormattedText)Component.literal((String)optionExtended.rso$getId().toString()).withStyle(ChatFormatting.GRAY)));
            tooltip.add(Language.getInstance().getVisualOrder((FormattedText)Component.literal((String)"")));
        }
        tooltip.addAll(Minecraft.getInstance().font.split((FormattedText)option.getTooltip(), boxWidth - 6));
        OptionImpact impact = option.getImpact();
        if (impact != null) {
            tooltip.add(Language.getInstance().getVisualOrder((FormattedText)Component.translatable((String)"sodium.options.performance_impact_string", (Object[])new Object[]{impact.getName()}).withStyle(ChatFormatting.GRAY)));
        }
        return tooltip;
    }

    static interface BoxRenderer {
        public void drawRect(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6);

        public void drawBorder(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6);
    }
}

