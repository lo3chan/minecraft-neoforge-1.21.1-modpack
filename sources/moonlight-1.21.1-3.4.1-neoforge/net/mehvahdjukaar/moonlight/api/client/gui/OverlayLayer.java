package net.mehvahdjukaar.moonlight.api.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

public class OverlayLayer {
   @Nullable
   private Popup open;

   public void open(Popup popup) {
      if (this.open != null && this.open != popup) {
         Popup previous = this.open;
         this.open = null;
         previous.onPopupClosed();
      }

      this.open = popup;
   }

   public void close(Popup popup) {
      if (this.open == popup) {
         this.open = null;
         popup.onPopupClosed();
      }
   }

   public void clear() {
      if (this.open != null) {
         Popup previous = this.open;
         this.open = null;
         previous.onPopupClosed();
      }
   }

   public boolean isOpen() {
      return this.open != null;
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return this.open != null && this.open.popupMouseClicked(mouseX, mouseY, button);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
      if (this.open == null) {
         return false;
      } else {
         this.open.popupMouseScrolled(mouseX, mouseY, amount);
         return true;
      }
   }

   public boolean keyPressed(int key, int scanCode, int modifiers) {
      return this.open != null && this.open.popupKeyPressed(key, scanCode, modifiers);
   }

   public boolean charTyped(char c, int modifiers) {
      return this.open != null && this.open.popupCharTyped(c, modifiers);
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY) {
      if (this.open != null) {
         this.open.renderPopup(graphics, mouseX, mouseY);
      }
   }
}
