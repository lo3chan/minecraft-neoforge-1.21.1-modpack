package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.action;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import net.caffeinemc.mods.sodium.client.config.structure.StatefulOption;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.FocusNavigationEvent.ArrowNavigation;
import net.minecraft.client.gui.navigation.FocusNavigationEvent.TabNavigation;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class OptionActionButtonController {
   private final Supplier<LayoutBounds> rowBoundsSupplier;
   private final Supplier<StatefulOption<?>> optionSupplier;
   private final List<OptionActionButtonController.ActionButton> buttons;
   private final OptionActionButtonController.ActionButton undoButton;
   @Nullable
   private GuiEventListener focusedChild;
   private int heldActionButtonWidth = -1;
   private boolean hideNewActionButtons;

   public OptionActionButtonController(Supplier<LayoutBounds> rowBoundsSupplier, Supplier<StatefulOption<?>> optionSupplier, Runnable clickSound) {
      this.rowBoundsSupplier = rowBoundsSupplier;
      this.optionSupplier = optionSupplier;
      this.buttons = new ArrayList<>(2);
      this.buttons
         .add(
            new OptionActionButtonController.ActionButton(
               0,
               OptionResetAction.ICON,
               Component.translatable("rso.controller.guide.reset"),
               option -> Component.translatable("rso.narration.reset_to_default", new Object[]{option.getName()}),
               OptionResetAction::isVisible,
               OptionResetAction::isActive,
               OptionResetAction::resetToDefault,
               clickSound
            )
         );
      this.undoButton = new OptionActionButtonController.ActionButton(
         1,
         OptionUndoAction.ICON,
         Component.translatable("rso.controller.guide.undo"),
         option -> Component.translatable("rso.narration.undo_changes", new Object[]{option.getName()}),
         OptionUndoAction::isVisible,
         OptionUndoAction::isActive,
         OptionUndoAction::undoChanges,
         clickSound
      );
      this.buttons.add(this.undoButton);
   }

   public int actionButtonWidth() {
      return this.isLayoutHeld() ? this.heldActionButtonWidth : this.naturalReservedWidth();
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      if (this.optionSupplier.get() == null) {
         return false;
      } else {
         for (OptionActionButtonController.ActionButton button : this.buttons) {
            if (button.visible() && button.element.isMouseOver(mouseX, mouseY)) {
               return true;
            }
         }

         return false;
      }
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      this.pruneInvisibleFocus();
      if (this.optionSupplier.get() != null) {
         for (OptionActionButtonController.ActionButton button : this.buttons) {
            if (button.visible()) {
               button.element.render(guiGraphics, mouseX, mouseY, this.getFocused() == button.element);
            }
         }
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int buttonCode) {
      if (buttonCode == 0 && this.optionSupplier.get() != null) {
         for (OptionActionButtonController.ActionButton button : this.buttons) {
            if (button.visible() && button.element.isMouseOver(mouseX, mouseY)) {
               this.setFocused(button.element);
               button.element.mouseClicked(mouseX, mouseY, buttonCode);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean keyPressedFocusedChild(int keyCode, int scanCode, int modifiers) {
      GuiEventListener focusedChild = this.getFocused();
      return focusedChild != null && focusedChild.keyPressed(keyCode, scanCode, modifiers);
   }

   public OptionActionButtonController.FocusPathResult nextFocusPath(
      ContainerEventHandler parent, GuiEventListener owner, boolean ownerFocused, FocusNavigationEvent navigation
   ) {
      GuiEventListener focusedChild = this.getFocused();
      if (focusedChild != null) {
         return this.nextFocusPathFromChild(parent, owner, focusedChild, navigation);
      } else {
         GuiEventListener firstActionButton = this.firstVisibleActionButton();
         return ownerFocused && firstActionButton != null && this.shouldEnterActionButton(navigation)
            ? OptionActionButtonController.FocusPathResult.handled(this.childFocusPath(parent, firstActionButton))
            : OptionActionButtonController.FocusPathResult.unhandled();
      }
   }

   private OptionActionButtonController.FocusPathResult nextFocusPathFromChild(
      ContainerEventHandler parent, GuiEventListener owner, GuiEventListener focusedChild, FocusNavigationEvent navigation
   ) {
      ComponentPath childPath = focusedChild.nextFocusPath(navigation);
      if (childPath != null) {
         return OptionActionButtonController.FocusPathResult.handled(ComponentPath.path(parent, childPath));
      } else {
         List<GuiEventListener> active = this.activeButtonElements();
         int index = active.indexOf(focusedChild);
         if (this.shouldEnterActionButton(navigation)) {
            return index >= 0 && index + 1 < active.size()
               ? OptionActionButtonController.FocusPathResult.handled(this.childFocusPath(parent, active.get(index + 1)))
               : OptionActionButtonController.FocusPathResult.handled(null);
         } else if (this.shouldReturnToControl(navigation)) {
            return index > 0
               ? OptionActionButtonController.FocusPathResult.handled(this.childFocusPath(parent, active.get(index - 1)))
               : OptionActionButtonController.FocusPathResult.handled(ComponentPath.leaf(owner));
         } else {
            return OptionActionButtonController.FocusPathResult.handled(null);
         }
      }
   }

   @Nullable
   public ComponentPath currentFocusPath(ContainerEventHandler parent, GuiEventListener owner, boolean ownerFocused) {
      GuiEventListener focusedChild = this.getFocused();
      if (focusedChild != null) {
         return ComponentPath.path(parent, focusedChild.getCurrentFocusPath());
      } else {
         return ownerFocused ? ComponentPath.leaf(owner) : null;
      }
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
      if (this.focusedChild != focused) {
         if (this.focusedChild != null) {
            this.focusedChild.setFocused(false);
         }

         this.focusedChild = focused;
         if (focused != null) {
            focused.setFocused(true);
         }
      }
   }

   public void holdLayout(boolean hideButton) {
      if (!this.isLayoutHeld()) {
         for (OptionActionButtonController.ActionButton button : this.buttons) {
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

      for (OptionActionButtonController.ActionButton button : this.buttons) {
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
      } else {
         int height = this.rowBounds().height();
         int reserved = 0;

         for (OptionActionButtonController.ActionButton button : this.buttons) {
            if (button.naturallyVisible()) {
               reserved += height;
            }
         }

         return reserved;
      }
   }

   private boolean isLayoutHeld() {
      return this.heldActionButtonWidth >= 0;
   }

   private boolean isFocusedChildVisible() {
      for (OptionActionButtonController.ActionButton button : this.buttons) {
         if (button.element == this.focusedChild) {
            return button.visible() && button.element.isActive();
         }
      }

      return true;
   }

   private List<GuiEventListener> activeButtonElements() {
      List<GuiEventListener> active = new ArrayList<>(this.buttons.size());

      for (OptionActionButtonController.ActionButton button : this.buttons) {
         if (button.visible() && button.element.isActive()) {
            active.add(button.element);
         }
      }

      return active;
   }

   @Nullable
   private GuiEventListener firstVisibleActionButton() {
      for (OptionActionButtonController.ActionButton button : this.buttons) {
         if (button.visible() && button.element.isActive()) {
            return button.element;
         }
      }

      return null;
   }

   private LayoutBounds rowBounds() {
      return this.rowBoundsSupplier.get();
   }

   private ComponentPath childFocusPath(ContainerEventHandler parent, GuiEventListener child) {
      return ComponentPath.path(parent, ComponentPath.leaf(child));
   }

   private boolean shouldEnterActionButton(FocusNavigationEvent navigation) {
      if (navigation instanceof ArrowNavigation arrowNavigation) {
         return arrowNavigation.direction() == ScreenDirection.RIGHT;
      } else {
         if (navigation instanceof TabNavigation var3) {
            TabNavigation var10000 = var3;

            try {
               var6 = var10000.forward();
            } catch (Throwable var5) {
               throw new MatchException(var5.toString(), var5);
            }

            boolean var4 = var6;
            if (var4) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean shouldReturnToControl(FocusNavigationEvent navigation) {
      if (navigation instanceof ArrowNavigation arrowNavigation) {
         return arrowNavigation.direction() == ScreenDirection.LEFT;
      } else {
         if (navigation instanceof TabNavigation var3) {
            TabNavigation var10000 = var3;

            try {
               var6 = var10000.forward();
            } catch (Throwable var5) {
               throw new MatchException(var5.toString(), var5);
            }

            boolean var4 = var6;
            if (!var4) {
               return true;
            }
         }

         return false;
      }
   }

   private final class ActionButton {
      private final int index;
      private final OptionActionButtonElement element;
      private boolean heldVisible;

      private ActionButton(
         int index,
         ResourceLocation icon,
         Component guideLabel,
         Function<StatefulOption<?>, Component> narrationLabelProvider,
         Predicate<StatefulOption<?>> visiblePredicate,
         Predicate<StatefulOption<?>> activePredicate,
         Consumer<StatefulOption<?>> action,
         Runnable clickSound
      ) {
         this.index = index;
         this.element = new OptionActionButtonElement(
            OptionActionButtonController.this.rowBoundsSupplier,
            OptionActionButtonController.this.optionSupplier,
            this::buttonsFromRight,
            icon,
            guideLabel,
            narrationLabelProvider,
            visiblePredicate,
            activePredicate,
            action,
            clickSound,
            OptionActionButtonController.this::clearFocus
         );
      }

      private boolean naturallyVisible() {
         return this.element.isVisible();
      }

      private boolean visible() {
         return this.naturallyVisible()
            && (!OptionActionButtonController.this.isLayoutHeld() || !OptionActionButtonController.this.hideNewActionButtons || this.heldVisible);
      }

      private int buttonsFromRight() {
         int slot = 1;

         for (int i = this.index + 1; i < OptionActionButtonController.this.buttons.size(); i++) {
            if (OptionActionButtonController.this.buttons.get(i).visible()) {
               slot++;
            }
         }

         return slot;
      }
   }

   public record FocusPathResult(boolean handled, @Nullable ComponentPath path) {
      private static OptionActionButtonController.FocusPathResult handled(@Nullable ComponentPath path) {
         return new OptionActionButtonController.FocusPathResult(true, path);
      }

      private static OptionActionButtonController.FocusPathResult unhandled() {
         return new OptionActionButtonController.FocusPathResult(false, null);
      }
   }
}
