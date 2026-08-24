package com.seibel.distanthorizons.common.wrappers.gui.updater;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList.Entry;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

public class ChangelogScreen$ButtonEntry_neoforge extends Entry<ChangelogScreen$ButtonEntry_neoforge> {
   private static final Font textRenderer = Minecraft.getInstance().font;
   private final Component text;
   private final List<AbstractWidget> children = new ArrayList<>();

   private ChangelogScreen$ButtonEntry_neoforge(Component text) {
      this.text = text;
   }

   public static ChangelogScreen$ButtonEntry_neoforge create(Component text) {
      return new ChangelogScreen$ButtonEntry_neoforge(text);
   }

   public void render(GuiGraphics matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
      matrices.drawString(textRenderer, this.text, 12, y + 5, 16777215);
   }

   public List<? extends GuiEventListener> children() {
      return this.children;
   }

   public List<? extends NarratableEntry> narratables() {
      return this.children;
   }
}
