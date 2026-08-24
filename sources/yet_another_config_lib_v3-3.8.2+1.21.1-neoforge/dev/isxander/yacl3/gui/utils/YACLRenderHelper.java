package dev.isxander.yacl3.gui.utils;

import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;

public class YACLRenderHelper {
   private static final WidgetSprites SPRITES = new WidgetSprites(
      YACLPlatform.mcRl("widget/button"),
      YACLPlatform.mcRl("widget/button_disabled"),
      YACLPlatform.mcRl("widget/button_highlighted"),
      YACLPlatform.mcRl("widget/slider_highlighted")
   );

   public static void renderButtonTexture(GuiGraphics graphics, int x, int y, int width, int height, boolean enabled, boolean focused) {
      GuiUtils.blitSprite(graphics, SPRITES.get(enabled, focused), x, y, width, height);
   }
}
