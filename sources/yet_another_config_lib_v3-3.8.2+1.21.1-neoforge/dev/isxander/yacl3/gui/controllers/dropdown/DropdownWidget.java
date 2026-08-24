package dev.isxander.yacl3.gui.controllers.dropdown;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.api.utils.MutableDimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerPopupWidget;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DropdownWidget<T> extends ControllerPopupWidget<AbstractDropdownController<T>> {
   public static final int MAX_SHOWN_NUMBER_OF_ITEMS = 7;
   public static final int DROPDOWN_PADDING = 2;
   private final AbstractDropdownControllerElement<T, ?> dropdownElement;
   protected Dimension<Integer> dropdownDim;
   protected int firstVisibleIndex = 0;
   protected int selectedIndex = 0;

   public DropdownWidget(
      AbstractDropdownController<T> control, YACLScreen screen, Dimension<Integer> dim, AbstractDropdownControllerElement<T, ?> dropdownElement
   ) {
      super(control, screen, dim, dropdownElement);
      this.dropdownElement = dropdownElement;
      this.setDimension(dim);
   }

   @Override
   public void setDimension(Dimension<Integer> dim) {
      super.setDimension(dim);
      int dropdownHeight = dim.height() * this.numberOfVisibleItems();
      int dropdownY = dim.y() - dropdownHeight - 2;
      if (dropdownY < this.screen.tabArea.top()) {
         dropdownY = dim.yLimit() + 2;
      }

      this.dropdownDim = Dimension.ofInt(dim.x(), dropdownY, dim.width(), dropdownHeight);
   }

   public int entryHeight() {
      return this.dropdownElement.getDimension().height();
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      if (this.dropdownLength() != 0) {
         GuiUtils.pushPose(graphics);
         GuiUtils.translateZ(graphics, 200.0F);
         GuiUtils.blitGuiTexColor(
            graphics,
            Screen.MENU_BACKGROUND,
            this.dropdownDim.x(),
            this.dropdownDim.y(),
            0.0F,
            0.0F,
            this.dropdownDim.width(),
            this.dropdownDim.height(),
            32,
            32,
            -12632257
         );
         graphics.renderOutline(this.dropdownDim.x(), this.dropdownDim.y(), this.dropdownDim.width(), this.dropdownDim.height(), -1);
         int y = this.dropdownDim.y() + 2 + this.entryHeight() * this.selectedVisibleIndex();
         graphics.fill(this.dropdownDim.x(), y, this.dropdownDim.xLimit(), y + this.entryHeight(), 2130706432);
         graphics.renderOutline(this.dropdownDim.x(), y, this.dropdownDim.width(), this.entryHeight(), -1);
         MutableDimension<Integer> entryDimension = Dimension.ofInt(
            this.dropdownDim.x() - this.dropdownElement.getDecorationPadding(), this.dropdownDim.y() + 2, this.dropdownDim.width(), this.entryHeight()
         );

         for (int i = this.firstVisibleIndex; i < this.lastVisibleIndex(); i++) {
            this.dropdownElement.renderDropdownEntry(graphics, entryDimension, i);
            entryDimension.move(0, this.entryHeight());
         }

         GuiUtils.popPose(graphics);
      }
   }

   @Override
   public boolean onMouseClicked(double mouseX, double mouseY, int button) {
      if (this.isMouseOver(mouseX, mouseY)) {
         this.dropdownElement.unfocus();
         return true;
      } else if (this.dropdownElement.isMouseOver(mouseX, mouseY)) {
         return this.dropdownElement.onMouseClicked(mouseX, mouseY, button);
      } else {
         this.close();
         return false;
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
      if (this.isMouseOver(mouseX, mouseY)) {
         if (vertical < 0.0) {
            this.scrollDown();
         } else {
            this.scrollUp();
         }

         return true;
      } else {
         return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
      }
   }

   public void mouseMoved(double mouseX, double mouseY) {
      if (this.isMouseOver(mouseX, mouseY)) {
         int index = (int)((mouseY - this.dropdownDim.y().intValue()) / this.entryHeight());
         this.selectVisibleItem(index);
      }
   }

   @Override
   public boolean isMouseOver(double mouseX, double mouseY) {
      return this.dropdownDim.isPointInside((int)mouseX, (int)mouseY);
   }

   @Override
   public boolean onCharTyped(char chr, String cpStr, int modifiers) {
      return this.dropdownElement.onCharTyped(chr, cpStr, modifiers);
   }

   public int dropdownLength() {
      return this.dropdownElement.matchingValues.size();
   }

   public int numberOfVisibleItems() {
      return Math.min(7, this.dropdownLength());
   }

   public int lastVisibleIndex() {
      return Math.min(this.firstVisibleIndex + 7, this.dropdownLength());
   }

   public int selectedIndex() {
      return this.selectedIndex;
   }

   public void resetSelectedIndex() {
      this.selectedIndex = 0;
   }

   public int selectedVisibleIndex() {
      return this.selectedIndex - this.firstVisibleIndex;
   }

   public void selectVisibleItem(int visibleIndex) {
      this.selectedIndex = Math.min(this.firstVisibleIndex + visibleIndex, this.dropdownLength() - 1);
   }

   public void selectNextEntry() {
      if (this.selectedIndex == this.dropdownLength() - 1) {
         this.selectedIndex = 0;
      } else {
         this.selectedIndex++;
      }

      if (this.selectedIndex - this.firstVisibleIndex >= 3) {
         this.centerOnSelectedItem();
      }
   }

   public void selectPreviousEntry() {
      if (this.selectedIndex == 0) {
         this.selectedIndex = this.dropdownLength() - 1;
      } else {
         this.selectedIndex--;
      }

      if (this.selectedIndex - this.firstVisibleIndex <= 3) {
         this.centerOnSelectedItem();
      }
   }

   private void centerOnSelectedItem() {
      int begin = Math.max(0, this.selectedIndex - 3);
      int end = begin + 7;
      if (end >= this.dropdownLength()) {
         end = this.dropdownLength();
         begin = Math.max(0, end - 7);
      }

      this.firstVisibleIndex = begin;
   }

   public void scrollDown() {
      if (this.firstVisibleIndex + 1 + 7 <= this.dropdownLength()) {
         this.firstVisibleIndex++;
      }

      if (this.selectedIndex < this.firstVisibleIndex) {
         this.selectedIndex = this.firstVisibleIndex;
      }
   }

   public void scrollUp() {
      if (this.firstVisibleIndex > 0) {
         this.firstVisibleIndex--;
      }

      if (this.selectedIndex > this.firstVisibleIndex + 7 - 1) {
         this.selectedIndex = this.firstVisibleIndex + 7 - 1;
      }
   }

   @Override
   public void close() {
      this.dropdownElement.removeDropdownWidget();
   }

   @Override
   public Component popupTitle() {
      return Component.translatable("yacl.control.dropdown.dropdown_widget_title");
   }
}
