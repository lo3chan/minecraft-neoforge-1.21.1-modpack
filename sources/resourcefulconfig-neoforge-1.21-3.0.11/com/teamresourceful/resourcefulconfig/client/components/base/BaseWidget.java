package com.teamresourceful.resourcefulconfig.client.components.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;

public abstract class BaseWidget extends AbstractWidget {
   protected final Font font;
   protected final Minecraft minecraft = Minecraft.getInstance();

   public BaseWidget(int width, int height) {
      super(0, 0, width, height, CommonComponents.EMPTY);
      this.font = this.minecraft.font;
   }

   protected abstract void renderWidget(GuiGraphics var1, int var2, int var3, float var4);

   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
   }
}
