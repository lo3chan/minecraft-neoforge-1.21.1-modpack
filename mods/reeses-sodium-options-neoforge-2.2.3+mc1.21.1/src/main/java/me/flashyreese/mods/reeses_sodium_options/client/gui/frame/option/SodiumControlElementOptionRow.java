/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.config.structure.Option
 *  net.caffeinemc.mods.sodium.client.gui.ColorTheme
 *  net.caffeinemc.mods.sodium.client.gui.options.control.AbstractOptionList
 *  net.caffeinemc.mods.sodium.client.gui.options.control.ControlElement
 *  net.caffeinemc.mods.sodium.client.util.Dim2i
 *  net.minecraft.client.gui.ComponentPath
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.narration.NarratableEntry
 *  net.minecraft.client.gui.narration.NarratableEntry$NarrationPriority
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.gui.navigation.FocusNavigationEvent
 *  net.minecraft.client.gui.screens.Screen
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option;

import java.util.List;
import me.flashyreese.mods.reeses_sodium_options.client.gui.SodiumWidgetDimensions;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.OptionRow;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.widget.BaseWidget;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.caffeinemc.mods.sodium.client.gui.ColorTheme;
import net.caffeinemc.mods.sodium.client.gui.options.control.AbstractOptionList;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlElement;
import net.caffeinemc.mods.sodium.client.util.Dim2i;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class SodiumControlElementOptionRow
extends BaseWidget
implements OptionRow {
    private final Screen screen;
    private final ColorTheme theme;
    private final Option option;
    private final StubOptionList list;
    private ControlElement element;

    SodiumControlElementOptionRow(Screen screen, LayoutBounds dim, ColorTheme theme, Option option) {
        super(dim);
        this.screen = screen;
        this.theme = theme;
        this.option = option;
        this.list = new StubOptionList(this.toSodiumDim(dim));
        this.element = this.createElement(dim);
        this.list.getControls().add(this.element);
    }

    @Override
    public Option getOption() {
        return this.option;
    }

    @Override
    public void setDim(LayoutBounds dim) {
        super.setDim(dim);
        this.relayoutElement(dim);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.element.render(guiGraphics, mouseX, mouseY, delta);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.element.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.element.mouseReleased(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.element.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return this.element.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.element.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.element.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean isFocused() {
        return this.element.isFocused();
    }

    @Override
    public void setFocused(boolean focused) {
        this.element.setFocused(focused);
    }

    @Override
    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent navigation) {
        return this.isFocused() || !this.option.isEnabled() ? null : ComponentPath.leaf((GuiEventListener)this);
    }

    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        return this.element.narrationPriority();
    }

    @Override
    public void updateNarration(NarrationElementOutput builder) {
        this.element.updateNarration(builder);
    }

    @Override
    public List<NarratableEntry> collectNarratables() {
        return List.of(this.element);
    }

    @Override
    public void releaseActionButtonLayoutHold() {
    }

    @Override
    public boolean handleBackNavigation() {
        return false;
    }

    @Override
    public boolean undoFocusedActionButton() {
        return false;
    }

    @Override
    public void clearActionButtonFocus() {
    }

    private ControlElement createElement(LayoutBounds dim) {
        return this.option.getControl().createElement(this.screen, (AbstractOptionList)this.list, this.toSodiumDim(dim), this.theme);
    }

    private void relayoutElement(LayoutBounds dim) {
        Dim2i sodiumDim = this.toSodiumDim(dim);
        ((SodiumWidgetDimensions)((Object)this.list)).rso$setDimensions(sodiumDim);
        ((SodiumWidgetDimensions)this.element).rso$setDimensions(sodiumDim);
    }

    private Dim2i toSodiumDim(LayoutBounds dim) {
        return new Dim2i(dim.x(), dim.y(), dim.width(), dim.height());
    }

    private static final class StubOptionList
    extends AbstractOptionList {
        private StubOptionList(Dim2i dim) {
            super(dim);
        }

        public int getScrollAmount() {
            return 0;
        }

        public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
            return false;
        }

        @NotNull
        public List<? extends GuiEventListener> children() {
            return List.of();
        }
    }
}

