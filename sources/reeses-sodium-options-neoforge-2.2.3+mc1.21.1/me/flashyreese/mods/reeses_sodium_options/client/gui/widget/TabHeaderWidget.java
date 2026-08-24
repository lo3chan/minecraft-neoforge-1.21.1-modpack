package me.flashyreese.mods.reeses_sodium_options.client.gui.widget;

import me.flashyreese.mods.reeses_sodium_options.client.config.ReeseSodiumOptionsConfig;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiTheme;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiThemes;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.IconRenderer;
import net.caffeinemc.mods.sodium.client.config.structure.ModOptions;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TabHeaderWidget extends BaseWidget {
   private static final float SCROLL_SPEED_PX_PER_SEC = 10.0F;
   private static final int TEXT_PADDING_RIGHT = 2;
   private static final long DWELL_MS = 1000L;
   private static final int HEADER_BACKGROUND = -1107296256;
   private static final int HEADER_BACKGROUND_HOVERED = -536870912;
   private static final int TEXT_COLOR = -1;
   private static final int SECONDARY_TEXT_COLOR = -5592406;
   private static final int TEXT_X = 5;
   private static final int ICON_SPACING = 4;
   private final ModOptions modOptions;
   @Nullable
   private final Runnable action;
   private boolean selected;

   public TabHeaderWidget(LayoutBounds dim, ModOptions modOptions) {
      this(dim, modOptions, null, false);
   }

   public TabHeaderWidget(LayoutBounds dim, ModOptions modOptions, @Nullable Runnable action, boolean selected) {
      super(dim);
      this.modOptions = modOptions;
      this.action = action;
      this.selected = selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   public void applyScissor(GuiGraphics guiGraphics, int x, int y, int width, int height, Runnable action) {
      guiGraphics.enableScissor(x, y, x + width, y + height);
      action.run();
      guiGraphics.disableScissor();
   }

   private void drawScrollingString(GuiGraphics g, String text, int x, int y, int color, int availableWidth) {
      Font font = Minecraft.getInstance().font;
      if (availableWidth > 0 && text != null && !text.isEmpty()) {
         int textWidth = font.width(text);
         int lineHeight = 9;
         if (textWidth <= availableWidth) {
            g.drawString(font, text, x, y, color, false);
         } else {
            int overflow = textWidth - availableWidth;
            if (ReeseSodiumOptionsConfig.config().isReducedMotion()) {
               this.applyScissor(g, x, y, availableWidth, lineHeight, () -> g.drawString(font, text, x, y, color, false));
            } else {
               long nowMs = Util.getMillis();
               double speedPxPerMs = 0.01;
               long travelMs = Math.max(1L, (long)Math.ceil(overflow / speedPxPerMs));
               long cycleMs = 1000L + travelMs + 1000L + travelMs;
               long t = nowMs % cycleMs;
               double offset;
               if (t < 1000L) {
                  offset = 0.0;
               } else if (t < 1000L + travelMs) {
                  long tt = t - 1000L;
                  offset = Math.min((double)overflow, tt * speedPxPerMs);
               } else if (t < 1000L + travelMs + 1000L) {
                  offset = overflow;
               } else {
                  long tt = t - (1000L + travelMs + 1000L);
                  offset = Math.max(0.0, overflow - tt * speedPxPerMs);
               }

               this.applyScissor(g, x, y, availableWidth, lineHeight, () -> g.drawString(font, text, (int)(x - offset), y, color, false));
            }
         }
      }
   }

   @Override
   public void render(@NotNull GuiGraphics guiGraphics, int i, int j, float f) {
      this.hovered = this.action != null && this.isMouseOver(i, j);
      this.applyScissor(
         guiGraphics,
         this.getX(),
         this.getY(),
         this.getWidth(),
         this.getHeight(),
         () -> {
            int primaryTextColor = this.primaryTextColor();
            int secondaryTextColor = this.secondaryTextColor();
            this.drawRect(guiGraphics, this.getX(), this.getY(), this.getLimitX(), this.getLimitY(), this.hovered ? -536870912 : -1107296256);
            if (this.selected) {
               this.drawRect(guiGraphics, this.getX(), this.getY(), this.getX() + 2, this.getLimitY(), this.accentColor());
            }

            int xOffset;
            if (ReeseSodiumOptionsConfig.config().isTabHeaderIcons() && this.modOptions.icon() != null) {
               xOffset = IconRenderer.renderIconWithSpacing(
                  guiGraphics, this.modOptions.icon(), primaryTextColor, this.modOptions.iconMonochrome(), this.getX(), this.getY(), this.getHeight(), 4
               );
            } else {
               xOffset = 5;
            }

            int textX = this.getX() + xOffset;
            int available = this.getX() + this.getWidth() - textX - 2;
            if (ReeseSodiumOptionsConfig.config().isTabHeaderVersionLabels()) {
               this.drawScrollingString(guiGraphics, this.modOptions.name(), textX, this.getY() + 2, primaryTextColor, available);
               this.drawScrollingString(guiGraphics, this.modOptions.version(), textX, this.getY() + 12, secondaryTextColor, available);
            } else {
               int centeredY = this.getY() + Math.ceilDiv(this.getHeight() - 9, 2);
               this.drawScrollingString(guiGraphics, this.modOptions.name(), textX, centeredY, primaryTextColor, available);
            }

            if (this.shouldRenderFocusBorder()) {
               this.drawBorder(guiGraphics, this.getX(), this.getY(), this.getLimitX(), this.getLimitY(), -1);
            }
         }
      );
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.action != null && button == 0 && this.isMouseOver(mouseX, mouseY)) {
         this.action.run();
         this.playClickSound();
         return true;
      } else {
         return false;
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.action != null && this.isFocused() && isSelectionKey(keyCode)) {
         this.action.run();
         this.playClickSound();
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   @Override
   public ComponentPath nextFocusPath(@NotNull FocusNavigationEvent navigation) {
      return this.action != null && !this.isFocused() ? ComponentPath.leaf(this) : null;
   }

   public boolean isActive() {
      return this.action != null;
   }

   @Override
   public void updateNarration(NarrationElementOutput builder) {
      if (this.action != null) {
         Component label = Component.literal(this.modOptions.name());
         if (ReeseSodiumOptionsConfig.config().isTabHeaderVersionLabels()) {
            label = CommonComponents.optionNameValue(label, Component.literal(this.modOptions.version()));
         }

         this.addButtonNarration(builder, label);
      }
   }

   private boolean useThemedText() {
      return ReeseSodiumOptionsConfig.config().isColorThemes() && ReeseSodiumOptionsConfig.config().isThemedHeadersAndLabels();
   }

   private GuiTheme theme() {
      return GuiThemes.fromSodium(this.modOptions.theme());
   }

   private int primaryTextColor() {
      return this.useThemedText() ? this.theme().themeLighter : -1;
   }

   private int secondaryTextColor() {
      return this.useThemedText() ? this.theme().themeDarker : -5592406;
   }

   private int accentColor() {
      return this.useThemedText() ? this.theme().theme : -1;
   }
}
