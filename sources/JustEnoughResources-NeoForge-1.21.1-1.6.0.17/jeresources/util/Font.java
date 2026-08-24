package jeresources.util;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class Font {
   public static final Font small = new Font(true);
   public static final Font normal = new Font(false);
   private final boolean isSmall;
   private static final float SCALING = 0.75F;

   private Font(boolean small) {
      this.isSmall = small;
   }

   public void print(GuiGraphics guiGraphics, String line, int x, int y) {
      this.doTransform(guiGraphics, x, y);
      guiGraphics.drawString(getMCFont(), line, 0, 0, 8, false);
      guiGraphics.pose().popPose();
   }

   public void print(GuiGraphics guiGraphics, FormattedCharSequence line, int x, int y) {
      this.doTransform(guiGraphics, x, y);
      guiGraphics.drawString(getMCFont(), line, 0, 0, 8, false);
      guiGraphics.pose().popPose();
   }

   public void print(GuiGraphics guiGraphics, int number, int x, int y) {
      this.print(guiGraphics, String.valueOf(number), x, y);
   }

   public void splitPrint(GuiGraphics guiGraphics, String line, int x, int y, int maxWidth) {
      this.doTransform(guiGraphics, x, y);
      int scaledWidth = (int)(maxWidth * (this.isSmall ? 0.75F : 1.0F));
      List<FormattedCharSequence> lines = Minecraft.getInstance().font.split(Component.literal(line), scaledWidth);
      int scaledLineHeight = (int)(9.0F * (this.isSmall ? 0.75F : 1.0F));

      for (int i = 0; i < lines.size(); i++) {
         guiGraphics.drawString(getMCFont(), lines.get(i), 0, i * scaledLineHeight, 8, false);
      }

      guiGraphics.pose().popPose();
   }

   public void splitPrint(GuiGraphics guiGraphics, FormattedCharSequence line, int x, int y, int maxWidth) {
      this.splitPrint(guiGraphics, line.toString(), x, y, maxWidth);
   }

   public void print(GuiGraphics guiGraphics, FormattedCharSequence line, int x, int y, int color) {
      this.print(guiGraphics, line, x, y, color, false);
   }

   public void print(GuiGraphics guiGraphics, FormattedCharSequence line, int x, int y, int color, boolean shadow) {
      this.doTransform(guiGraphics, x, y);
      guiGraphics.drawString(getMCFont(), line, 0, 0, color, shadow);
      guiGraphics.pose().popPose();
   }

   public int getStringWidth(FormattedCharSequence line) {
      int width = Minecraft.getInstance().font.width(line);
      return (int)(this.isSmall ? width * 0.75F : width);
   }

   public int getStringWidth(String line) {
      int width = Minecraft.getInstance().font.width(line);
      return (int)(this.isSmall ? width * 0.75F : width);
   }

   private void doTransform(GuiGraphics guiGraphics, int x, int y) {
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(x, y, 0.0F);
      if (this.isSmall) {
         guiGraphics.pose().scale(0.75F, 0.75F, 1.0F);
      }
   }

   public static net.minecraft.client.gui.Font getMCFont() {
      return Minecraft.getInstance().font;
   }
}
