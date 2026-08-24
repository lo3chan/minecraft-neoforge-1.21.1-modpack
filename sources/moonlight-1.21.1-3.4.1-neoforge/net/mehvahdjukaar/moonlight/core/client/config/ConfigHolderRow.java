package net.mehvahdjukaar.moonlight.core.client.config;

import java.util.List;
import net.mehvahdjukaar.moonlight.api.client.gui.GuiHelper;
import net.mehvahdjukaar.moonlight.api.client.gui.MoonlightIcons;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.ConfigGuiColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

class ConfigHolderRow extends ConfigListRow {
   private final Button button;
   private final Component label;
   @Nullable
   private final Component subtitle;
   private final ResourceLocation icon;
   private final List<AbstractWidget> children;

   ConfigHolderRow(Component label, @Nullable Component subtitle, ResourceLocation icon, Runnable onClick) {
      this.label = label;
      this.subtitle = subtitle;
      this.icon = icon;
      this.button = Button.builder(Component.empty(), b -> onClick.run()).bounds(0, 0, 280, 24).build();
      this.children = List.of(this.button);
   }

   public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
      Font font = Minecraft.getInstance().font;
      this.button.setMessage(Component.empty());
      this.button.setX(left);
      this.button.setWidth(width);
      this.button.setY(top);
      this.button.setHeight(height);
      this.button.render(graphics, mouseX, mouseY, partialTick);
      int iconX = left + 8;
      int textLeft = iconX + 16 + 6;
      int editX = left + width - 8 - 16;
      int textRight = editX - 4;
      graphics.blitSprite(this.icon, iconX, this.subtitle != null ? top + 5 : top + (height - 16) / 2, 16, 16);
      if (this.subtitle != null) {
         GuiHelper.renderScrollingText(graphics, font, this.label, textLeft, textRight, top + 3, 9 + 2, ConfigGuiColors.CATEGORY);
         ConfigScreenLayout.drawClipped(graphics, font, this.subtitle, textLeft, top + 5 + 9, textRight, ConfigGuiColors.TEXT);
      } else {
         GuiHelper.renderScrollingText(graphics, font, this.label, textLeft, textRight, top, height, ConfigGuiColors.CATEGORY);
      }

      graphics.blitSprite(MoonlightIcons.EDIT, editX, top + (height - 16) / 2, 16, 16);
   }

   public List<? extends GuiEventListener> children() {
      return this.children;
   }

   public List<? extends NarratableEntry> narratables() {
      return this.children;
   }

   @Nullable
   @Override
   Component getTooltip(int mouseX, int mouseY) {
      return null;
   }
}
