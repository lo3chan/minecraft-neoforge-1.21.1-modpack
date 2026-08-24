package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.action;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import me.flashyreese.mods.reeses_sodium_options.client.gui.control.ControlGuide;
import me.flashyreese.mods.reeses_sodium_options.client.gui.control.ControlGuideProvider;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import net.caffeinemc.mods.sodium.client.config.structure.StatefulOption;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

final class OptionActionButtonElement implements GuiEventListener, NarratableEntry, ControlGuideProvider {
   private final Supplier<LayoutBounds> rowBoundsSupplier;
   private final Supplier<StatefulOption<?>> optionSupplier;
   private final IntSupplier buttonsFromRight;
   private final ResourceLocation icon;
   private final Component guideLabel;
   private final Function<StatefulOption<?>, Component> narrationLabelProvider;
   private final Predicate<StatefulOption<?>> visiblePredicate;
   private final Predicate<StatefulOption<?>> activePredicate;
   private final Consumer<StatefulOption<?>> action;
   private final Runnable clickSound;
   private final Runnable afterAction;
   private boolean focused;
   private boolean hovered;

   OptionActionButtonElement(
      Supplier<LayoutBounds> rowBoundsSupplier,
      Supplier<StatefulOption<?>> optionSupplier,
      IntSupplier buttonsFromRight,
      ResourceLocation icon,
      Component guideLabel,
      Function<StatefulOption<?>, Component> narrationLabelProvider,
      Predicate<StatefulOption<?>> visiblePredicate,
      Predicate<StatefulOption<?>> activePredicate,
      Consumer<StatefulOption<?>> action,
      Runnable clickSound,
      Runnable afterAction
   ) {
      this.rowBoundsSupplier = rowBoundsSupplier;
      this.optionSupplier = optionSupplier;
      this.buttonsFromRight = buttonsFromRight;
      this.icon = icon;
      this.guideLabel = guideLabel;
      this.narrationLabelProvider = narrationLabelProvider;
      this.visiblePredicate = visiblePredicate;
      this.activePredicate = activePredicate;
      this.action = action;
      this.clickSound = clickSound;
      this.afterAction = afterAction;
   }

   public boolean isActive() {
      StatefulOption<?> option = this.optionSupplier.get();
      return option != null && this.visiblePredicate.test(option) && this.activePredicate.test(option);
   }

   public boolean isVisible() {
      StatefulOption<?> option = this.optionSupplier.get();
      return option != null && this.visiblePredicate.test(option);
   }

   @Override
   public List<ControlGuide> controlGuides() {
      return this.isFocused() && this.isActive() ? List.of(new ControlGuide(ControlGuide.Input.PRESS, this.guideLabel)) : List.of();
   }

   public LayoutBounds getDimensions() {
      return this.getButtonDim(this.rowBoundsSupplier.get());
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean focused) {
      StatefulOption<?> option = this.optionSupplier.get();
      if (option != null && this.visiblePredicate.test(option)) {
         boolean active = this.activePredicate.test(option);
         this.hovered = active && this.isMouseOver(mouseX, mouseY);
         OptionActionButtonRenderer.render(guiGraphics, this.icon, this.getButtonDim(this.rowBoundsSupplier.get()), mouseX, mouseY, focused, active);
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return button == 0 && this.isMouseOver(mouseX, mouseY) ? this.performAction() : false;
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return this.focused && isSelectionKey(keyCode) ? this.performAction() : false;
   }

   public boolean performAction() {
      StatefulOption<?> option = this.optionSupplier.get();
      if (option != null && this.visiblePredicate.test(option) && this.activePredicate.test(option)) {
         this.action.accept(option);
         this.clickSound.run();
         this.afterAction.run();
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public ComponentPath nextFocusPath(FocusNavigationEvent navigation) {
      return this.isActive() && !this.focused ? ComponentPath.leaf(this) : null;
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      StatefulOption<?> option = this.optionSupplier.get();
      return option != null
         && this.visiblePredicate.test(option)
         && this.activePredicate.test(option)
         && this.getButtonDim(this.rowBoundsSupplier.get()).contains(mouseX, mouseY);
   }

   public ScreenRectangle getRectangle() {
      if (!this.isActive()) {
         return ScreenRectangle.empty();
      } else {
         LayoutBounds dim = this.getDimensions();
         return new ScreenRectangle(dim.x(), dim.y(), dim.width(), dim.height());
      }
   }

   public void setFocused(boolean focused) {
      this.focused = focused && this.isActive();
   }

   public boolean isFocused() {
      return this.focused;
   }

   public NarrationPriority narrationPriority() {
      if (this.focused) {
         return NarrationPriority.FOCUSED;
      } else {
         return this.hovered ? NarrationPriority.HOVERED : NarrationPriority.NONE;
      }
   }

   public void updateNarration(NarrationElementOutput builder) {
      StatefulOption<?> option = this.optionSupplier.get();
      if (option != null && this.visiblePredicate.test(option) && this.activePredicate.test(option)) {
         builder.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.button", new Object[]{this.narrationLabelProvider.apply(option)}));
         if (this.focused) {
            builder.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.focused"));
         } else if (this.hovered) {
            builder.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
         }
      }
   }

   private LayoutBounds getButtonDim(LayoutBounds rowBounds) {
      return OptionActionButtonRenderer.buttonBounds(rowBounds, this.buttonsFromRight.getAsInt());
   }

   private static boolean isSelectionKey(int keyCode) {
      return keyCode == 257 || keyCode == 335 || keyCode == 32;
   }
}
