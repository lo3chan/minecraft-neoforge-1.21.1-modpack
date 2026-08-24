package com.iafenvoy.jupiter.render.internal;

import com.iafenvoy.jupiter.ConfigManager;
import com.iafenvoy.jupiter.config.ConfigSide;
import com.iafenvoy.jupiter.config.ConfigSource;
import com.iafenvoy.jupiter.config.container.AbstractConfigContainer;
import com.iafenvoy.jupiter.render.BadgeRenderer;
import com.iafenvoy.jupiter.util.TextUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class JupiterConfigListWidget extends ObjectSelectionList<JupiterConfigListWidget.Entry> {
   private final JupiterConfigListScreen screen;
   private final List<JupiterConfigListWidget.Entry> entries = new ArrayList<>();
   private String filter = "";

   public JupiterConfigListWidget(JupiterConfigListScreen screen, Minecraft client, int width, int height, int y) {
      super(client, width, height, y, 32);
      this.screen = screen;
   }

   public void update() {
      this.entries.clear();

      for (AbstractConfigContainer x : ConfigManager.getInstance().getConfigs()) {
         this.entries.add(new JupiterConfigListWidget.Entry(this, x));
      }

      this.updateEntries();
   }

   private void updateEntries() {
      this.clearEntries();
      this.entries.stream().filter(x -> x.match(this.filter)).forEach(x$0 -> this.addEntry(x$0));
      this.setScrollAmount(0.0);
   }

   public void setFilter(String filter) {
      this.filter = filter.toLowerCase(Locale.ROOT);
      this.updateEntries();
   }

   protected int getScrollbarPosition() {
      return this.getRight() - 8;
   }

   public int getRowWidth() {
      return this.width - 4;
   }

   public void setSelected(@Nullable JupiterConfigListWidget.Entry selected) {
      super.setSelected(selected);
      this.screen.updateButtonState();
   }

   public static class Entry extends net.minecraft.client.gui.components.ObjectSelectionList.Entry<JupiterConfigListWidget.Entry> {
      private final Minecraft client = Minecraft.getInstance();
      private final JupiterConfigListWidget widget;
      private final AbstractConfigContainer handler;

      public Entry(JupiterConfigListWidget widget, AbstractConfigContainer handler) {
         this.widget = widget;
         this.handler = handler;
      }

      public void render(
         @NotNull GuiGraphics graphics, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick
      ) {
         graphics.drawString(this.client.font, this.handler.getTitle(), x + 65, y + 1, -1);
         graphics.drawString(this.client.font, this.handler.getConfigId().toString(), x + 65, y + 1 + 9, -8421505);
         graphics.drawString(this.client.font, this.handler.getPath(), x + 65, y + 1 + 18, -8421505);
         ConfigSource source = this.handler.getSource();
         ConfigSide side = this.handler.getSide();
         BadgeRenderer.draw(graphics, this.client.font, x + 1, y + 1, source.name(), source.color());
         BadgeRenderer.draw(graphics, this.client.font, x + 1, y + 16, TextUtil.literal(side.getDisplayText()), side.getColor());
      }

      public boolean mouseClicked(double x, double y, int button) {
         this.widget.setSelected(this);
         return false;
      }

      public AbstractConfigContainer getConfigContainer() {
         return this.handler;
      }

      @NotNull
      public Component getNarration() {
         return TextUtil.empty();
      }

      public boolean match(String filter) {
         return this.handler.getTitle().getString().toLowerCase(Locale.ROOT).contains(filter)
            || this.handler.getConfigId().toString().contains(filter)
            || this.handler.getPath().contains(filter)
            || this.handler.getSource().name().getString().toLowerCase(Locale.ROOT).contains(filter)
            || this.handler.getSide().name().toLowerCase(Locale.ROOT).contains(filter);
      }
   }
}
