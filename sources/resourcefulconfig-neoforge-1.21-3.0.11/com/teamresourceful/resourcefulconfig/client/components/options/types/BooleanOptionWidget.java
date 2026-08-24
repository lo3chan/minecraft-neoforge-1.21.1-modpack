package com.teamresourceful.resourcefulconfig.client.components.options.types;

import com.teamresourceful.resourcefulconfig.client.components.ModSprites;
import com.teamresourceful.resourcefulconfig.client.components.base.BaseWidget;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.function.BooleanSupplier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class BooleanOptionWidget extends BaseWidget {
   private static final int WIDTH = 80;
   private static final int HALF_WIDTH = 40;
   private static final int SWITCH_WIDTH = 38;
   private static final int HALF_SWITCH_WIDTH = 19;
   private final BooleanSupplier getter;
   private final BooleanConsumer setter;

   public BooleanOptionWidget(BooleanSupplier getter, BooleanConsumer setter) {
      super(80, 16);
      this.getter = getter;
      this.setter = setter;
   }

   @Override
   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      boolean value = this.getter.getAsBoolean();
      int offX = this.getX() + 1;
      int onX = this.getX() + 1 + 40;
      int color = value ? -9276296 : -329226;
      graphics.blitSprite(ModSprites.BUTTON, this.getX(), this.getY(), this.width, this.height);
      graphics.blitSprite(ModSprites.ofSwitch(value), value ? onX : offX, this.getY() + 1, 38, this.height - 2);
      drawCenteredString(graphics, this.font, CommonComponents.OPTION_OFF, offX + 19, this.getY() + 4, color, value && this.isHovered());
      drawCenteredString(graphics, this.font, CommonComponents.OPTION_ON, onX + 19, this.getY() + 4, color, !value && this.isHovered());
   }

   public void onClick(double mouseX, double mouseY) {
      this.setter.accept(!this.getter.getAsBoolean());
   }

   public static void drawCenteredString(GuiGraphics graphics, Font font, Component component, int x, int y, int color, boolean shadowed) {
      int actualX = x - font.width(component) / 2;
      graphics.drawString(font, component, actualX, y, color, shadowed);
   }
}
