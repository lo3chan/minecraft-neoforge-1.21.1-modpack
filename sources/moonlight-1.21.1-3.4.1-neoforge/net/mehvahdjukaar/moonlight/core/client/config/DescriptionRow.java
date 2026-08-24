package net.mehvahdjukaar.moonlight.core.client.config;

import java.util.List;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.ConfigGuiColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

class DescriptionRow extends ConfigListRow {
   private final Font font;
   private final List<FormattedCharSequence> lines;

   DescriptionRow(Font font, List<FormattedCharSequence> lines) {
      this.font = font;
      this.lines = List.copyOf(lines);
   }

   public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
      int y = top + 1;

      for (FormattedCharSequence line : this.lines) {
         graphics.drawString(this.font, line, left + 12 + 2, y, ConfigGuiColors.DESCRIPTION);
         y += 9;
      }
   }

   public List<? extends GuiEventListener> children() {
      return List.of();
   }

   public List<? extends NarratableEntry> narratables() {
      return List.of();
   }

   @Nullable
   @Override
   Component getTooltip(int mouseX, int mouseY) {
      return null;
   }
}
