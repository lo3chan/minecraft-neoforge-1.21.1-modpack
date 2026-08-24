package net.diebuddies.physics.settings.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;

public class TitleWidget extends AbstractWidget {
   private Screen screen;

   public TitleWidget(Screen screen) {
      super(0, 0, screen.width, screen.height, screen.getTitle());
      this.active = false;
      this.screen = screen;
   }

   public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      guiGraphics.drawCenteredString(Minecraft.getInstance().font, this.screen.getTitle(), this.width / 2, 15, 16777215);
   }

   public void updateWidgetNarration(NarrationElementOutput narration) {
   }
}
