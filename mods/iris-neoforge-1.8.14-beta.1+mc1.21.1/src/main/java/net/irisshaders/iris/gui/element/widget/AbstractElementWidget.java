/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.ComponentPath
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.narration.NarratableEntry
 *  net.minecraft.client.gui.narration.NarratableEntry$NarrationPriority
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.gui.navigation.FocusNavigationEvent
 *  net.minecraft.client.gui.navigation.ScreenRectangle
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.gui.element.widget;

import net.irisshaders.iris.gui.NavigationController;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuElement;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractElementWidget<T extends OptionMenuElement>
implements GuiEventListener,
NarratableEntry {
    public static final AbstractElementWidget<OptionMenuElement> EMPTY = new AbstractElementWidget<OptionMenuElement>(null){

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta, boolean hovered) {
        }

        @Override
        @Nullable
        public ComponentPath nextFocusPath(FocusNavigationEvent pGuiEventListener0) {
            return null;
        }

        @Override
        @NotNull
        public ScreenRectangle getRectangle() {
            return ScreenRectangle.empty();
        }
    };
    protected final T element;
    public ScreenRectangle bounds = ScreenRectangle.empty();
    private boolean focused;

    public AbstractElementWidget(T element) {
        this.element = element;
    }

    public void init(ShaderPackScreen screen, NavigationController navigation) {
    }

    public abstract void render(GuiGraphics var1, int var2, int var3, float var4, boolean var5);

    public boolean mouseClicked(double mx, double my, int button) {
        return false;
    }

    public boolean mouseReleased(double mx, double my, int button) {
        return false;
    }

    public boolean keyPressed(int keycode, int scancode, int modifiers) {
        return false;
    }

    public boolean isFocused() {
        return this.focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent pGuiEventListener0) {
        return !this.isFocused() ? ComponentPath.leaf((GuiEventListener)this) : null;
    }

    public ScreenRectangle getRectangle() {
        return this.bounds;
    }

    public NarratableEntry.NarrationPriority narrationPriority() {
        return NarratableEntry.NarrationPriority.NONE;
    }

    public void updateNarration(NarrationElementOutput p0) {
    }
}

