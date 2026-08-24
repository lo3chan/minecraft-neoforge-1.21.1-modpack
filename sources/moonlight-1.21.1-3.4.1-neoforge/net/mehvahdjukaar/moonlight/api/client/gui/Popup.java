package net.mehvahdjukaar.moonlight.api.client.gui;

import net.minecraft.client.gui.GuiGraphics;

public interface Popup {
   void renderPopup(GuiGraphics var1, int var2, int var3);

   default boolean popupMouseClicked(double mouseX, double mouseY, int button) {
      return false;
   }

   default boolean popupMouseScrolled(double mouseX, double mouseY, double amount) {
      return false;
   }

   default boolean popupKeyPressed(int key, int scanCode, int modifiers) {
      return false;
   }

   default boolean popupCharTyped(char c, int modifiers) {
      return false;
   }

   default void onPopupClosed() {
   }
}
