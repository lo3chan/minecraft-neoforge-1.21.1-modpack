package com.finndog.moogs_structures.client;

import com.finndog.moogs_structures.config.MslConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SupportButton extends AbstractWidget {
   private static final int CLOSE = 9;
   private final ResourceLocation icon;
   private final String url;
   private final String configId;

   public SupportButton(int x, int y, int w, int h, ResourceLocation icon, String url, Component tooltip, String configId) {
      super(x, y, w, h, tooltip);
      this.icon = icon;
      this.url = url;
      this.configId = configId;
      this.setTooltip(Tooltip.create(tooltip));
   }

   private boolean inClose(double mouseX, double mouseY) {
      int cx = this.getX() + this.width - 9;
      int cy = this.getY();
      return mouseX >= cx && mouseX < cx + 9 && mouseY >= cy && mouseY < cy + 9;
   }

   public void onClick(double mouseX, double mouseY) {
      if (this.inClose(mouseX, mouseY)) {
         MslConfig.get().setButtonHiddenAndSave(this.configId, true);
         this.visible = false;
         this.active = false;
      } else {
         ConfigButtons.openLink(this.url);
      }
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      graphics.blitSprite(this.icon, this.getX(), this.getY(), this.width, this.height);
      if (this.isHoveredOrFocused()) {
         graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 872415231);
      }

      int cx = this.getX() + this.width - 9;
      int cy = this.getY();
      boolean closeHover = this.inClose(mouseX, mouseY);
      graphics.fill(cx, cy, cx + 9, cy + 9, closeHover ? -805306368 : -2147483648);
      graphics.drawCenteredString(Minecraft.getInstance().font, "×", cx + 4, cy + 1, closeHover ? -43691 : -1);
   }

   protected void updateWidgetNarration(NarrationElementOutput output) {
      this.defaultButtonNarrationText(output);
   }
}
