package dev.isxander.yacl3.gui.utils;

import net.minecraft.client.gui.components.events.GuiEventListener;

public final class WidgetUtils {
   public static boolean mouseClicked(GuiEventListener l, double mouseX, double mouseY, int button) {
      return l.mouseClicked(mouseX, mouseY, button);
   }

   public static boolean keyPressed(GuiEventListener l, int keyCode, int scanCode, int modifiers) {
      return l.keyPressed(keyCode, scanCode, modifiers);
   }

   public static boolean charTyped(GuiEventListener l, char codePoint, int modifiers) {
      return l.charTyped(codePoint, modifiers);
   }

   public static boolean mouseDragged(GuiEventListener l, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      return l.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
   }

   private WidgetUtils() {
   }
}
