package net.mehvahdjukaar.moonlight.api.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.ConfigGuiColors;
import net.mehvahdjukaar.moonlight.api.util.TextHelper;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import org.jetbrains.annotations.Nullable;

public final class GuiHelper {
   private static final ResourceLocation MENU_LIST_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/menu_list_background.png");
   private static final ResourceLocation INWORLD_MENU_LIST_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/inworld_menu_list_background.png");
   private static final ResourceLocation INWORLD_MENU_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/inworld_menu_background.png");

   public static void renderHeaderBar(GuiGraphics graphics, int width, int headerHeight) {
      boolean inWorld = Minecraft.getInstance().level != null;
      Screen.renderMenuBackgroundTexture(graphics, inWorld ? INWORLD_MENU_BACKGROUND : Screen.MENU_BACKGROUND, 0, 0, 0.0F, 0.0F, width, headerHeight - 2);
      ResourceLocation separator = inWorld ? Screen.INWORLD_HEADER_SEPARATOR : Screen.HEADER_SEPARATOR;
      RenderSystem.enableBlend();
      graphics.blit(separator, 0, headerHeight - 2, 0.0F, 0.0F, width, 2, 32, 2);
      RenderSystem.disableBlend();
   }

   public static void renderHeaderBar(GuiGraphics graphics, Font font, Component title, int width, int headerHeight) {
      renderHeaderBar(graphics, width, headerHeight);
      graphics.drawCenteredString(font, title, width / 2, (headerHeight - 9) / 2, ConfigGuiColors.TITLE);
   }

   public static void renderHeaderBar(GuiGraphics graphics, Font font, Component title, @Nullable Component subtitle, int width, int headerHeight) {
      if (subtitle == null) {
         renderHeaderBar(graphics, font, title, width, headerHeight);
      } else {
         renderHeaderBar(graphics, width, headerHeight);
         int gap = 2;
         int top = (headerHeight - 2 - (2 * 9 + gap)) / 2;
         graphics.drawCenteredString(font, title, width / 2, top, ConfigGuiColors.TITLE);
         graphics.drawCenteredString(font, subtitle, width / 2, top + 9 + gap, ConfigGuiColors.DESCRIPTION);
      }
   }

   public static void fillGradientHorizontal(GuiGraphics graphics, int minX, int minY, int maxX, int maxY, int colorFrom, int colorTo) {
      fillGradientHorizontal(graphics, RenderType.gui(), minX, minY, maxX, maxY, colorFrom, colorTo);
   }

   public static void fillGradientHorizontal(GuiGraphics graphics, RenderType renderType, int minX, int minY, int maxX, int maxY, int colorFrom, int colorTo) {
      int steps = maxX - minX;
      if (steps > 0) {
         for (int i = 0; i < steps; i++) {
            int color = ARGB32.lerp(steps == 1 ? 0.0F : (float)i / (steps - 1), colorFrom, colorTo);
            graphics.fill(renderType, minX + i, minY, minX + i + 1, maxY, color);
         }
      }
   }

   public static void renderMenuBand(GuiGraphics graphics, int x, int y, int width, int height) {
      ResourceLocation bg = Minecraft.getInstance().level != null ? INWORLD_MENU_BACKGROUND : Screen.MENU_BACKGROUND;
      Screen.renderMenuBackgroundTexture(graphics, bg, x, y, x, y, width, height);
   }

   public static void renderSeparator(GuiGraphics graphics, int x, int y, int width) {
      ResourceLocation sprite = Minecraft.getInstance().level != null ? Screen.INWORLD_HEADER_SEPARATOR : Screen.HEADER_SEPARATOR;
      RenderSystem.enableBlend();
      graphics.blit(sprite, x, y, 0.0F, 0.0F, width, 2, 32, 2);
      RenderSystem.disableBlend();
   }

   public static void renderVerticalSeparator(GuiGraphics graphics, int x, int top, int bottom) {
      graphics.fill(x, top, x + 1, bottom, -15724526);
      graphics.fill(x + 1, top, x + 2, bottom, 419430399);
   }

   public static void renderModIcon(GuiGraphics graphics, ModIcons.Icon icon, int x, int y, int maxWidth, int maxHeight) {
      int h = maxHeight;
      int w = Math.round(maxHeight * ((float)icon.width() / icon.height()));
      if (w > maxWidth) {
         w = maxWidth;
         h = Math.round(maxWidth * ((float)icon.height() / icon.width()));
      }

      graphics.blit(icon.texture(), x + (maxWidth - w) / 2, y + (maxHeight - h) / 2, w, h, 0.0F, 0.0F, icon.width(), icon.height(), icon.width(), icon.height());
   }

   public static void renderListBackground(GuiGraphics graphics, int top, int bottom, int width, double scroll) {
      ResourceLocation bg = Minecraft.getInstance().level != null ? INWORLD_MENU_LIST_BACKGROUND : MENU_LIST_BACKGROUND;
      RenderSystem.enableBlend();
      graphics.blit(bg, 0, top, width, bottom + (int)scroll, width, bottom - top, 32, 32);
      RenderSystem.disableBlend();
   }

   public static void renderFooterSeparator(GuiGraphics graphics, int bottom, int width) {
      ResourceLocation footer = Minecraft.getInstance().level != null ? Screen.INWORLD_FOOTER_SEPARATOR : Screen.FOOTER_SEPARATOR;
      RenderSystem.enableBlend();
      graphics.blit(footer, 0, bottom, 0.0F, 0.0F, width, 2, 32, 2);
      RenderSystem.disableBlend();
   }

   public static void renderScrollbar(GuiGraphics graphics, int top, int bottom, int width, double scroll, int maxScroll) {
      if (maxScroll > 0) {
         int trackX = width - 6;
         int trackH = bottom - top;
         int thumbH = Math.max(16, trackH * trackH / (trackH + maxScroll));
         int thumbY = top + (int)((trackH - thumbH) * (scroll / maxScroll));
         graphics.fill(trackX, top, trackX + 3, top + trackH, 1073741824);
         graphics.fill(trackX, thumbY, trackX + 3, thumbY + thumbH, -5197648);
      }
   }

   public static void renderInitialTile(
      GuiGraphics graphics, Font font, String name, int x, int y, int size, int tileColor, int letterColor, ResourceLocation gearIcon
   ) {
      graphics.fill(x, y, x + size, y + size, tileColor);
      graphics.renderOutline(x, y, size, size, -16777216);
      String trimmed = name.trim();
      if (trimmed.isEmpty()) {
         int g = size >= 26 ? 16 : 8;
         graphics.blitSprite(gearIcon, x + (size - g) / 2, y + (size - g) / 2, g, g);
      } else {
         String initial = trimmed.substring(0, 1).toUpperCase();
         int tx = x + (size - font.width(initial)) / 2;
         int ty = y + (size - 9) / 2;
         graphics.drawString(font, initial, tx, ty, letterColor, false);
      }
   }

   public static void playClickSound() {
      Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }

   @Deprecated(
      forRemoval = true
   )
   public static String formatNumber(double v) {
      return TextHelper.formatNumber(v);
   }

   public static void renderScrollingText(GuiGraphics graphics, Font font, Component text, int minX, int maxX, int rowTop, int rowHeight, int color) {
      int textY = rowTop + (rowHeight - 9) / 2 + 1;
      if (!scrollIfOverflow(graphics, font, text, minX, maxX, rowTop, rowHeight, textY, color)) {
         graphics.drawString(font, text, minX, textY, color);
      }
   }

   public static void renderScrollingTextCentered(GuiGraphics graphics, Font font, Component text, int minX, int maxX, int rowTop, int rowHeight, int color) {
      int textY = rowTop + (rowHeight - 9) / 2 + 1;
      if (!scrollIfOverflow(graphics, font, text, minX, maxX, rowTop, rowHeight, textY, color)) {
         int cx = minX + (maxX - minX - font.width(text)) / 2;
         graphics.drawString(font, text, cx, textY, color);
      }
   }

   private static boolean scrollIfOverflow(GuiGraphics graphics, Font font, Component text, int minX, int maxX, int rowTop, int rowHeight, int textY, int color) {
      int overflow = font.width(text) - (maxX - minX);
      if (overflow <= 0) {
         return false;
      } else {
         double seconds = Util.getMillis() / 1000.0;
         double period = Math.max(overflow * 0.5, 3.0);
         double phase = Math.sin(1.5707963267948966 * Math.cos(6.283185307179586 * seconds / period)) / 2.0 + 0.5;
         double offset = Mth.lerp(phase, 0.0, overflow);
         graphics.enableScissor(minX, rowTop, maxX, rowTop + rowHeight);
         graphics.drawString(font, text, minX - (int)offset, textY, color);
         graphics.disableScissor();
         return true;
      }
   }
}
