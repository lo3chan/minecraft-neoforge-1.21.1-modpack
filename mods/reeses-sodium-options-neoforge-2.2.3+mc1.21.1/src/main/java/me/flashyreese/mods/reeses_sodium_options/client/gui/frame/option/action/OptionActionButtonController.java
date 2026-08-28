/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  net.caffeinemc.mods.sodium.client.config.structure.StatefulOption
 *  net.minecraft.client.gui.ComponentPath
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.ContainerEventHandler
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.navigation.FocusNavigationEvent
 *  net.minecraft.client.gui.navigation.FocusNavigationEvent$ArrowNavigation
 *  net.minecraft.client.gui.navigation.FocusNavigationEvent$TabNavigation
 *  net.minecraft.client.gui.navigation.ScreenDirection
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.action;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.action.OptionActionButtonElement;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.action.OptionResetAction;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.action.OptionUndoAction;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import net.caffeinemc.mods.sodium.client.config.structure.StatefulOption;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class OptionActionButtonController {
    private final Supplier<LayoutBounds> rowBoundsSupplier;
    private final Supplier<@Nullable StatefulOption<?>> optionSupplier;
    private final List<ActionButton> buttons;
    private final ActionButton undoButton;
    @Nullable
    private GuiEventListener focusedChild;
    private int heldActionButtonWidth = -1;
    private boolean hideNewActionButtons;

    public OptionActionButtonController(Supplier<LayoutBounds> rowBoundsSupplier, Supplier<@Nullable StatefulOption<?>> optionSupplier, Runnable clickSound) {
        this.rowBoundsSupplier = rowBoundsSupplier;
        this.optionSupplier = optionSupplier;
        this.buttons = new ArrayList<ActionButton>(2);
        this.buttons.add(new ActionButton(0, OptionResetAction.ICON, (Component)Component.translatable((String)"rso.controller.guide.reset"), option -> Component.translatable((String)"rso.narration.reset_to_default", (Object[])new Object[]{option.getName()}), OptionResetAction::isVisible, OptionResetAction::isActive, OptionResetAction::resetToDefault, clickSound));
        this.undoButton = new ActionButton(1, OptionUndoAction.ICON, (Component)Component.translatable((String)"rso.controller.guide.undo"), option -> Component.translatable((String)"rso.narration.undo_changes", (Object[])new Object[]{option.getName()}), OptionUndoAction::isVisible, OptionUndoAction::isActive, OptionUndoAction::undoChanges, clickSound);
        this.buttons.add(this.undoButton);
    }

    public int actionButtonWidth() {
        if (this.isLayoutHeld()) {
            return this.heldActionButtonWidth;
        }
        return this.naturalReservedWidth();
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        if (this.optionSupplier.get() == null) {
            return false;
        }
        for (ActionButton button : this.buttons) {
            if (!button.visible() || !button.element.isMouseOver(mouseX, mouseY)) continue;
            return true;
        }
        return false;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.pruneInvisibleFocus();
        if (this.optionSupplier.get() == null) {
            return;
        }
        for (ActionButton button : this.buttons) {
            if (!button.visible()) continue;
            button.element.render(guiGraphics, mouseX, mouseY, this.getFocused() == button.element);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int buttonCode) {
        if (buttonCode != 0 || this.optionSupplier.get() == null) {
            return false;
        }
        for (ActionButton button : this.buttons) {
            if (!button.visible() || !button.element.isMouseOver(mouseX, mouseY)) continue;
            this.setFocused(button.element);
            button.element.mouseClicked(mouseX, mouseY, buttonCode);
            return true;
        }
        return false;
    }

    public boolean keyPressedFocusedChild(int keyCode, int scanCode, int modifiers) {
        GuiEventListener focusedChild = this.getFocused();
        return focusedChild != null && focusedChild.keyPressed(keyCode, scanCode, modifiers);
    }

    public FocusPathResult nextFocusPath(ContainerEventHandler parent, GuiEventListener owner, boolean ownerFocused, FocusNavigationEvent navigation) {
        GuiEventListener focusedChild = this.getFocused();
        if (focusedChild != null) {
            return this.nextFocusPathFromChild(parent, owner, focusedChild, navigation);
        }
        GuiEventListener firstActionButton = this.firstVisibleActionButton();
        if (ownerFocused && firstActionButton != null && this.shouldEnterActionButton(navigation)) {
            return FocusPathResult.handled(this.childFocusPath(parent, firstActionButton));
        }
        return FocusPathResult.unhandled();
    }

    private FocusPathResult nextFocusPathFromChild(ContainerEventHandler parent, GuiEventListener owner, GuiEventListener focusedChild, FocusNavigationEvent navigation) {
        ComponentPath childPath = focusedChild.nextFocusPath(navigation);
        if (childPath != null) {
            return FocusPathResult.handled(ComponentPath.path((ContainerEventHandler)parent, (ComponentPath)childPath));
        }
        List<GuiEventListener> active = this.activeButtonElements();
        int index = active.indexOf(focusedChild);
        if (this.shouldEnterActionButton(navigation)) {
            if (index >= 0 && index + 1 < active.size()) {
                return FocusPathResult.handled(this.childFocusPath(parent, active.get(index + 1)));
            }
            return FocusPathResult.handled(null);
        }
        if (this.shouldReturnToControl(navigation)) {
            if (index > 0) {
                return FocusPathResult.handled(this.childFocusPath(parent, active.get(index - 1)));
            }
            return FocusPathResult.handled(ComponentPath.leaf((GuiEventListener)owner));
        }
        return FocusPathResult.handled(null);
    }

    @Nullable
    public ComponentPath currentFocusPath(ContainerEventHandler parent, GuiEventListener owner, boolean ownerFocused) {
        GuiEventListener focusedChild = this.getFocused();
        if (focusedChild != null) {
            return ComponentPath.path((ContainerEventHandler)parent, (ComponentPath)focusedChild.getCurrentFocusPath());
        }
        return ownerFocused ? ComponentPath.leaf((GuiEventListener)owner) : null;
    }

    public List<GuiEventListener> children() {
        return this.activeButtonElements();
    }

    @Nullable
    public GuiEventListener getFocused() {
        return this.focusedChild;
    }

    private void pruneInvisibleFocus() {
        if (this.focusedChild != null && !this.isFocusedChildVisible()) {
            this.clearFocus();
        }
    }

    public void setFocused(@Nullable GuiEventListener focused) {
        if (this.focusedChild == focused) {
            return;
        }
        if (this.focusedChild != null) {
            this.focusedChild.setFocused(false);
        }
        this.focusedChild = focused;
        if (focused != null) {
            focused.setFocused(true);
        }
    }

    public void holdLayout(boolean hideButton) {
        if (!this.isLayoutHeld()) {
            for (ActionButton button : this.buttons) {
                button.heldVisible = button.naturallyVisible();
            }
            this.heldActionButtonWidth = this.naturalReservedWidth();
        }
        this.hideNewActionButtons = hideButton;
        if (this.focusedChild != null && !this.isFocusedChildVisible()) {
            this.clearFocus();
        }
    }

    public void releaseLayoutHold() {
        this.heldActionButtonWidth = -1;
        this.hideNewActionButtons = false;
        for (ActionButton button : this.buttons) {
            button.heldVisible = false;
        }
    }

    public void clearFocus() {
        if (this.focusedChild != null) {
            this.focusedChild.setFocused(false);
        }
        this.focusedChild = null;
    }

    public boolean undoFocusedButton() {
        return this.getFocused() == this.undoButton.element && this.undoButton.element.performAction();
    }

    private int naturalReservedWidth() {
        if (this.optionSupplier.get() == null) {
            return 0;
        }
        int height = this.rowBounds().height();
        int reserved = 0;
        for (ActionButton button : this.buttons) {
            if (!button.naturallyVisible()) continue;
            reserved += height;
        }
        return reserved;
    }

    private boolean isLayoutHeld() {
        return this.heldActionButtonWidth >= 0;
    }

    private boolean isFocusedChildVisible() {
        for (ActionButton button : this.buttons) {
            if (button.element != this.focusedChild) continue;
            return button.visible() && button.element.isActive();
        }
        return true;
    }

    private List<GuiEventListener> activeButtonElements() {
        ArrayList<GuiEventListener> active = new ArrayList<GuiEventListener>(this.buttons.size());
        for (ActionButton button : this.buttons) {
            if (!button.visible() || !button.element.isActive()) continue;
            active.add(button.element);
        }
        return active;
    }

    @Nullable
    private GuiEventListener firstVisibleActionButton() {
        for (ActionButton button : this.buttons) {
            if (!button.visible() || !button.element.isActive()) continue;
            return button.element;
        }
        return null;
    }

    private LayoutBounds rowBounds() {
        return this.rowBoundsSupplier.get();
    }

    private ComponentPath childFocusPath(ContainerEventHandler parent, GuiEventListener child) {
        return ComponentPath.path((ContainerEventHandler)parent, (ComponentPath)ComponentPath.leaf((GuiEventListener)child));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean shouldEnterActionButton(FocusNavigationEvent navigation) {
        if (navigation instanceof FocusNavigationEvent.ArrowNavigation) {
            FocusNavigationEvent.ArrowNavigation arrowNavigation = (FocusNavigationEvent.ArrowNavigation)navigation;
            if (arrowNavigation.direction() != ScreenDirection.RIGHT) return false;
            return true;
        }
        if (!(navigation instanceof FocusNavigationEvent.TabNavigation)) return false;
        FocusNavigationEvent.TabNavigation tabNavigation = (FocusNavigationEvent.TabNavigation)navigation;
        try {
            boolean bl2;
            boolean forward = bl2 = tabNavigation.forward();
            if (!forward) return false;
            return true;
        }
        catch (Throwable throwable) {
            throw new MatchException(throwable.toString(), throwable);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean shouldReturnToControl(FocusNavigationEvent navigation) {
        if (navigation instanceof FocusNavigationEvent.ArrowNavigation) {
            FocusNavigationEvent.ArrowNavigation arrowNavigation = (FocusNavigationEvent.ArrowNavigation)navigation;
            if (arrowNavigation.direction() != ScreenDirection.LEFT) return false;
            return true;
        }
        if (!(navigation instanceof FocusNavigationEvent.TabNavigation)) return false;
        FocusNavigationEvent.TabNavigation tabNavigation = (FocusNavigationEvent.TabNavigation)navigation;
        try {
            boolean bl2;
            boolean forward = bl2 = tabNavigation.forward();
            if (forward) return false;
            return true;
        }
        catch (Throwable throwable) {
            throw new MatchException(throwable.toString(), throwable);
        }
    }

    private final class ActionButton {
        private final int index;
        private final OptionActionButtonElement element;
        private boolean heldVisible;

        private ActionButton(int index, ResourceLocation icon, Component guideLabel, Function<StatefulOption<?>, Component> narrationLabelProvider, Predicate<StatefulOption<?>> visiblePredicate, Predicate<StatefulOption<?>> activePredicate, Consumer<StatefulOption<?>> action, Runnable clickSound) {
            this.index = index;
            this.element = new OptionActionButtonElement(OptionActionButtonController.this.rowBoundsSupplier, OptionActionButtonController.this.optionSupplier, this::buttonsFromRight, icon, guideLabel, narrationLabelProvider, visiblePredicate, activePredicate, action, clickSound, OptionActionButtonController.this::clearFocus);
        }

        private boolean naturallyVisible() {
            return this.element.isVisible();
        }

        private boolean visible() {
            return this.naturallyVisible() && (!OptionActionButtonController.this.isLayoutHeld() || !OptionActionButtonController.this.hideNewActionButtons || this.heldVisible);
        }

        private int buttonsFromRight() {
            int slot = 1;
            for (int i = this.index + 1; i < OptionActionButtonController.this.buttons.size(); ++i) {
                if (!OptionActionButtonController.this.buttons.get(i).visible()) continue;
                ++slot;
            }
            return slot;
        }
    }

    public record FocusPathResult(boolean handled, @Nullable ComponentPath path) {
        private static FocusPathResult handled(@Nullable ComponentPath path) {
            return new FocusPathResult(true, path);
        }

        private static FocusPathResult unhandled() {
            return new FocusPathResult(false, null);
        }
    }
}

