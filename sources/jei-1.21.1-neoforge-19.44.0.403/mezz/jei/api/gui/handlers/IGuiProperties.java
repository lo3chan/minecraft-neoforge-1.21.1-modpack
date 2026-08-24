package mezz.jei.api.gui.handlers;

import net.minecraft.client.gui.screens.Screen;

public interface IGuiProperties {
   Class<? extends Screen> screenClass();

   int guiLeft();

   int guiTop();

   int guiXSize();

   int guiYSize();

   int screenWidth();

   int screenHeight();

   default int guiRight() {
      return this.guiLeft() + this.guiXSize();
   }

   default int guiBottom() {
      return this.guiTop() + this.guiYSize();
   }
}
