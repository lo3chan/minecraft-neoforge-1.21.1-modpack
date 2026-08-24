package dev.isxander.yacl3.gui;

import dev.isxander.yacl3.gui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TextScaledButtonWidget extends TooltipButtonWidget {
   public float textScale;

   public TextScaledButtonWidget(Screen screen, int x, int y, int width, int height, float textScale, Component message, Component tooltip, OnPress onPress) {
      super(screen, x, y, width, height, message, tooltip, onPress);
      this.textScale = textScale;
   }

   public TextScaledButtonWidget(Screen screen, int x, int y, int width, int height, float textScale, Component message, OnPress onPress) {
      this(screen, x, y, width, height, textScale, message, null, onPress);
   }

   public void renderString(GuiGraphics graphics, Font textRenderer, int color) {
      Font font = Minecraft.getInstance().font;
      GuiUtils.pushPose(graphics);
      GuiUtils.translate2D(
         graphics,
         this.getX() + this.width / 2.0F - font.width(this.getMessage()) * this.textScale / 2.0F,
         this.getY() + (this.height - 8.0F * this.textScale) / 2.0F / this.textScale
      );
      GuiUtils.scale2D(graphics, this.textScale, this.textScale);
      graphics.drawString(font, this.getMessage(), 0, 0, color | Mth.ceil(this.alpha * 255.0F) << 24, true);
      GuiUtils.popPose(graphics);
   }
}
