package net.mehvahdjukaar.moonlight.api.client.gui.widget;

import net.mehvahdjukaar.moonlight.api.client.gui.MoonlightIcons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class ChatBubbleWidget extends AbstractWidget {
   public static final int HEIGHT = 12;
   private static final int TAIL_WIDTH = 7;
   private static final int TAIL_HEIGHT = 5;
   private static final int TAIL_TIP = 3;
   private static final int PADDING = 6;
   private static final int CAP_INSET = 3;
   private static final int SCREEN_MARGIN = 3;
   private static final int TIP_GAP = 1;
   private static final long BOB_PERIOD_MS = 2200L;
   private final Font font;
   private int textColor = -16777216;
   private boolean animated = false;

   public ChatBubbleWidget(int x, int y, Component message) {
      super(x, y, measureWidth(message), 12, message);
      this.font = Minecraft.getInstance().font;
   }

   private static int measureWidth(Component message) {
      return Minecraft.getInstance().font.width(message) + 12;
   }

   public void setText(Component message) {
      this.setMessage(message);
      this.setWidth(measureWidth(message));
   }

   public ChatBubbleWidget setAnimated(boolean animated) {
      this.animated = animated;
      return this;
   }

   public ChatBubbleWidget setTextColor(int textColor) {
      this.textColor = textColor;
      return this;
   }

   private int bobOffset() {
      if (!this.animated) {
         return 0;
      } else {
         double phase = System.currentTimeMillis() % 2200L / 2200.0;
         return -((int)Math.round((1.0 - Math.cos(phase * 2.0 * 3.141592653589793)) / 2.0));
      }
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      graphics.blitSprite(MoonlightIcons.CHAT_BUBBLE_BODY, this.getX(), this.getY(), this.getWidth(), this.getHeight());
      int textX = this.getX() + 6;
      int textY = this.getY() + (this.getHeight() - 9) / 2 + 1;
      graphics.drawString(this.font, this.getMessage(), textX, textY, this.textColor, false);
   }

   public void renderPointingAt(GuiGraphics graphics, AbstractWidget target, int screenWidth, int mouseX, int mouseY, float partialTick) {
      int bubbleW = this.getWidth();
      int targetCenterX = target.getX() + target.getWidth() / 2;
      int bob = this.bobOffset();
      int tailY = target.getY() - 1 - 5 + 2 + bob;
      int bubbleY = tailY - 12 + 1;
      int bubbleX = targetCenterX - bubbleW / 2;
      int maxX = screenWidth - bubbleW - 3;
      bubbleX = maxX < 3 ? 3 : Math.clamp(bubbleX, 3, maxX);
      int tailX = targetCenterX - 3;
      int tailMin = bubbleX + 3;
      int tailMax = bubbleX + bubbleW - 7 - 3;
      tailX = tailMax < tailMin ? tailMin : Math.clamp(tailX, tailMin, tailMax);
      this.setX(bubbleX);
      this.setY(bubbleY);
      this.renderWidget(graphics, mouseX, mouseY, partialTick);
      graphics.blitSprite(MoonlightIcons.CHAT_BUBBLE_TAIL, tailX, tailY, 7, 5);
   }

   protected void updateWidgetNarration(NarrationElementOutput output) {
      this.defaultButtonNarrationText(output);
   }
}
