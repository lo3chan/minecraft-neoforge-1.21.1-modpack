package me.shedaniel.clothconfig2.gui.entries;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus.Internal;

@OnlyIn(Dist.CLIENT)
public abstract class TextFieldListEntry<T> extends TooltipListEntry<T> {
   protected EditBox textFieldWidget;
   protected Button resetButton;
   protected Supplier<T> defaultValue;
   protected T original;
   protected List<AbstractWidget> widgets;
   private boolean isSelected = false;

   @Deprecated
   @Internal
   protected TextFieldListEntry(Component fieldName, T original, Component resetButtonKey, Supplier<T> defaultValue) {
      this(fieldName, original, resetButtonKey, defaultValue, null);
   }

   @Deprecated
   @Internal
   protected TextFieldListEntry(
      Component fieldName, T original, Component resetButtonKey, Supplier<T> defaultValue, Supplier<Optional<Component[]>> tooltipSupplier
   ) {
      this(fieldName, original, resetButtonKey, defaultValue, tooltipSupplier, false);
   }

   @Deprecated
   @Internal
   protected TextFieldListEntry(
      Component fieldName,
      T original,
      Component resetButtonKey,
      Supplier<T> defaultValue,
      Supplier<Optional<Component[]>> tooltipSupplier,
      boolean requiresRestart
   ) {
      super(fieldName, tooltipSupplier, requiresRestart);
      this.defaultValue = defaultValue;
      this.original = original;
      this.textFieldWidget = new EditBox(Minecraft.getInstance().font, 0, 0, 148, 18, Component.empty()) {
         public void renderWidget(GuiGraphics graphics, int int_1, int int_2, float float_1) {
            this.setFocused(TextFieldListEntry.this.isSelected && TextFieldListEntry.this.getFocused() == this);
            TextFieldListEntry.this.textFieldPreRender(this);
            super.renderWidget(graphics, int_1, int_2, float_1);
         }

         public void insertText(String string_1) {
            super.insertText(TextFieldListEntry.this.stripAddText(string_1));
         }
      };
      this.textFieldWidget.setMaxLength(999999);
      this.textFieldWidget.setValue(String.valueOf(original));
      this.resetButton = Button.builder(resetButtonKey, widget -> this.textFieldWidget.setValue(String.valueOf(defaultValue.get())))
         .bounds(0, 0, Minecraft.getInstance().font.width(resetButtonKey) + 6, 20)
         .build();
      this.widgets = Lists.newArrayList(new AbstractWidget[]{this.textFieldWidget, this.resetButton});
   }

   @Override
   public boolean isEdited() {
      return this.isChanged(this.original, this.textFieldWidget.getValue());
   }

   protected boolean isChanged(T original, String s) {
      return !String.valueOf(original).equals(s);
   }

   protected static void setTextFieldWidth(EditBox widget, int width) {
      widget.setWidth(width);
   }

   @Deprecated
   public void setValue(String s) {
      this.textFieldWidget.setValue(String.valueOf(s));
   }

   protected String stripAddText(String s) {
      return s;
   }

   protected void textFieldPreRender(EditBox widget) {
      widget.setTextColor(this.getConfigError().isPresent() ? 16733525 : 14737632);
   }

   @Override
   public void updateSelected(boolean isSelected) {
      this.isSelected = isSelected;
   }

   @Override
   public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
      super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
      Window window = Minecraft.getInstance().getWindow();
      this.resetButton.active = this.isEditable() && this.getDefaultValue().isPresent() && !this.isMatchDefault(this.textFieldWidget.getValue());
      this.resetButton.setY(y);
      this.textFieldWidget.setEditable(this.isEditable());
      this.textFieldWidget.setY(y + 1);
      Component displayedFieldName = this.getDisplayedFieldName();
      if (Minecraft.getInstance().font.isBidirectional()) {
         graphics.drawString(
            Minecraft.getInstance().font,
            displayedFieldName.getVisualOrderText(),
            window.getGuiScaledWidth() - x - Minecraft.getInstance().font.width(displayedFieldName),
            y + 6,
            this.getPreferredTextColor()
         );
         this.resetButton.setX(x);
         this.textFieldWidget.setX(x + this.resetButton.getWidth());
      } else {
         graphics.drawString(Minecraft.getInstance().font, displayedFieldName.getVisualOrderText(), x, y + 6, this.getPreferredTextColor());
         this.resetButton.setX(x + entryWidth - this.resetButton.getWidth());
         this.textFieldWidget.setX(x + entryWidth - 148);
      }

      setTextFieldWidth(this.textFieldWidget, 148 - this.resetButton.getWidth() - 4);
      this.resetButton.render(graphics, mouseX, mouseY, delta);
      this.textFieldWidget.render(graphics, mouseX, mouseY, delta);
   }

   protected boolean isMatchDefault(String text) {
      Optional<T> defaultValue = this.getDefaultValue();
      return defaultValue.isPresent() && text.equals(defaultValue.get().toString());
   }

   @Override
   public Optional<T> getDefaultValue() {
      return this.defaultValue == null ? Optional.empty() : Optional.ofNullable(this.defaultValue.get());
   }

   public List<? extends GuiEventListener> children() {
      return this.widgets;
   }

   @Override
   public List<? extends NarratableEntry> narratables() {
      return this.widgets;
   }
}
