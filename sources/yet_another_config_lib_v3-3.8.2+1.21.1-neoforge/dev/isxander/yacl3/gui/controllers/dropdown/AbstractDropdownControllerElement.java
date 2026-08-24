package dev.isxander.yacl3.gui.controllers.dropdown;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.string.StringControllerElement;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import dev.isxander.yacl3.gui.utils.KeyUtils;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public abstract class AbstractDropdownControllerElement<T, U> extends StringControllerElement {
   private final AbstractDropdownController<T> dropdownController;
   protected DropdownWidget<T> dropdownWidget;
   protected boolean dropdownVisible = false;
   protected List<U> matchingValues = null;

   public AbstractDropdownControllerElement(AbstractDropdownController<T> control, YACLScreen screen, Dimension<Integer> dim) {
      super(control, screen, dim, false);
      this.dropdownController = control;
      this.dropdownController.option.addListener((opt, val) -> this.matchingValues = this.computeMatchingValues());
   }

   public void ensureValidValue() {
      if (!this.dropdownController.isValueValid(this.inputField)) {
         if (this.dropdownWidget == null) {
            this.inputField = this.dropdownController.getValidValue(this.inputField);
         } else {
            this.inputField = this.dropdownController.getValidValue(this.inputField, this.dropdownWidget.selectedIndex());
            this.dropdownWidget.resetSelectedIndex();
         }

         this.caretPos = this.getDefaultCaretPos();
         this.matchingValues = this.computeMatchingValues();
      }
   }

   @Override
   public boolean onMouseClicked(double mouseX, double mouseY, int button) {
      if (super.onMouseClicked(mouseX, mouseY, button)) {
         if (!this.dropdownVisible) {
            this.createDropdownWidget();
            this.doSelectAll();
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void setFocused(boolean focused) {
      if (focused) {
         this.doSelectAll();
         super.setFocused(true);
      } else {
         this.unfocus();
      }
   }

   @Override
   public void unfocus() {
      if (this.dropdownVisible) {
         this.removeDropdownWidget();
      }

      super.unfocus();
   }

   @Override
   public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
      if (!this.inputFieldFocused) {
         return false;
      } else {
         if (this.dropdownVisible) {
            switch (keyCode) {
               case 258:
                  if (KeyUtils.hasShiftDown(modifiers)) {
                     this.dropdownWidget.selectPreviousEntry();
                  } else {
                     this.dropdownWidget.selectNextEntry();
                  }

                  return true;
               case 264:
                  this.dropdownWidget.selectNextEntry();
                  return true;
               case 265:
                  this.dropdownWidget.selectPreviousEntry();
                  return true;
            }
         } else if (keyCode == 257 || keyCode == 335) {
            this.createDropdownWidget();
            return true;
         }

         return super.onKeyPressed(keyCode, scanCode, modifiers);
      }
   }

   @Override
   public boolean onCharTyped(char chr, String cpStr, int modifiers) {
      if (!this.inputFieldFocused) {
         return false;
      } else {
         if (!this.dropdownVisible) {
            this.createDropdownWidget();
         }

         return super.onCharTyped(chr, cpStr, modifiers);
      }
   }

   @Override
   protected int getValueColor() {
      return this.inputFieldFocused && !this.dropdownController.isValueValid(this.inputField) ? -1023872 : super.getValueColor();
   }

   @Override
   public boolean modifyInput(Consumer<StringBuilder> builder) {
      boolean success = super.modifyInput(builder);
      if (success) {
         this.matchingValues = this.computeMatchingValues();
      }

      return success;
   }

   public abstract List<U> computeMatchingValues();

   public boolean matchingValue(String value) {
      return value.toLowerCase().contains(this.inputField.toLowerCase());
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      if (this.matchingValues == null) {
         this.matchingValues = this.computeMatchingValues();
      }

      super.render(graphics, mouseX, mouseY, delta);
   }

   void renderDropdownEntry(GuiGraphics graphics, Dimension<Integer> entryDimension, int index) {
      this.renderDropdownEntry(graphics, entryDimension, this.matchingValues.get(index));
   }

   protected void renderDropdownEntry(GuiGraphics graphics, Dimension<Integer> entryDimension, U value) {
      String entry = this.getString(value);
      Component text;
      if (entry.isBlank()) {
         text = Component.translatable("yacl.control.text.blank").withStyle(ChatFormatting.GRAY);
      } else {
         text = this.shortenString(entry);
      }

      graphics.drawString(
         this.textRenderer,
         text,
         entryDimension.xLimit() - this.textRenderer.width(text) - this.getDropdownEntryPadding(),
         this.getTextY(entryDimension),
         -1,
         true
      );
   }

   protected int getTextY(Dimension<Integer> dim) {
      return (int)(dim.y().intValue() + dim.height().intValue() / 2.0F - 9.0F / 2.0F);
   }

   @Override
   public void setDimension(Dimension<Integer> dim) {
      super.setDimension(dim);
      if (this.dropdownWidget != null) {
         this.dropdownWidget.setDimension(this.dropdownWidget.getDimension().withY(this.getDimension().y()));
         if (this.getDimension().y() < this.screen.tabArea.top() || this.getDimension().yLimit() > this.screen.tabArea.bottom()) {
            this.removeDropdownWidget();
         }
      }
   }

   public abstract String getString(U var1);

   public Component shortenString(String value) {
      return Component.literal(GuiUtils.shortenString(value, this.textRenderer, this.getDimension().width() - 20, "..."));
   }

   protected int getDecorationPadding() {
      return super.getXPadding();
   }

   protected int getDropdownEntryPadding() {
      return 0;
   }

   public void createDropdownWidget() {
      this.dropdownVisible = true;
      this.dropdownWidget = new DropdownWidget<>(this.dropdownController, this.screen, this.getDimension(), this);
      this.screen.addPopupControllerWidget(this.dropdownWidget);
   }

   public DropdownWidget<T> dropdownWidget() {
      return this.dropdownWidget;
   }

   public boolean isDropdownVisible() {
      return this.dropdownVisible;
   }

   public void removeDropdownWidget() {
      this.ensureValidValue();
      this.screen.clearPopupControllerWidget();
      this.dropdownVisible = false;
      this.dropdownWidget = null;
   }
}
