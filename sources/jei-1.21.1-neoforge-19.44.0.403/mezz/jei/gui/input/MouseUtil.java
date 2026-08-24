package mezz.jei.gui.input;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

public final class MouseUtil {
   public static double getX() {
      Minecraft minecraft = Minecraft.getInstance();
      MouseHandler mouseHelper = minecraft.mouseHandler;
      double scale = (double)minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow().getScreenWidth();
      return mouseHelper.xpos() * scale;
   }

   public static double getY() {
      Minecraft minecraft = Minecraft.getInstance();
      MouseHandler mouseHelper = minecraft.mouseHandler;
      double scale = (double)minecraft.getWindow().getGuiScaledHeight() / minecraft.getWindow().getScreenHeight();
      return mouseHelper.ypos() * scale;
   }
}
