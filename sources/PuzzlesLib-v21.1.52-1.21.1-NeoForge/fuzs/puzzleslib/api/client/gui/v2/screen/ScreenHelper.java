package fuzs.puzzleslib.api.client.gui.v2.screen;

@Deprecated
public final class ScreenHelper {
   private ScreenHelper() {
   }

   public static int getMouseX() {
      return fuzs.puzzleslib.api.client.gui.v2.ScreenHelper.getMouseX();
   }

   public static int getMouseY() {
      return fuzs.puzzleslib.api.client.gui.v2.ScreenHelper.getMouseY();
   }

   public static boolean isHovering(int posX, int posY, int width, int height, double mouseX, double mouseY) {
      return fuzs.puzzleslib.api.client.gui.v2.ScreenHelper.isHovering(posX, posY, width, height, mouseX, mouseY);
   }
}
