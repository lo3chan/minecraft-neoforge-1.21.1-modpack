package com.seibel.distanthorizons.common.wrappers.gui;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DhScreen_neoforge extends Screen {
   protected DhScreen_neoforge(Component title) {
      super(title);
   }

   protected Button addBtn(Button button) {
      return (Button)this.addRenderableWidget(button);
   }

   protected void DhDrawCenteredString(GuiGraphics guiStack, Font font, Component text, int x, int y, int color) {
      guiStack.drawCenteredString(font, text, x, y, color);
   }

   protected void DhDrawString(GuiGraphics guiStack, Font font, Component text, int x, int y, int color) {
      guiStack.drawString(font, text, x, y, color);
   }

   protected void DhRenderComponentTooltip(GuiGraphics guiStack, Font font, List<Component> comp, int x, int y) {
      guiStack.renderComponentTooltip(font, comp, x, y);
   }

   protected void DhRenderTooltip(GuiGraphics guiStack, Font font, Component text, int x, int y) {
      guiStack.renderTooltip(font, text, x, y);
   }
}
