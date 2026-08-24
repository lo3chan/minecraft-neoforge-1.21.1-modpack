package net.mehvahdjukaar.moonlight.core.client.config;

import java.util.ArrayList;
import java.util.List;
import net.mehvahdjukaar.moonlight.api.client.gui.MoonlightIcons;
import net.mehvahdjukaar.moonlight.api.client.gui.widget.IconButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

class ListEntryRow extends ConfigListRow {
   private final ConfigListRow inner;
   private final IconButton remove;
   private final List<GuiEventListener> children;
   private final List<NarratableEntry> narratables;

   ListEntryRow(ConfigListRow inner, boolean canRemove, Runnable onRemove) {
      this.inner = inner;
      this.remove = new IconButton(0, 0, 20, 20, Component.empty(), MoonlightIcons.DELETE, 12, 12, b -> onRemove.run());
      this.remove.active = canRemove;
      this.remove.setTooltip(Tooltip.create(Component.translatable("gui.moonlight.config.list_remove")));
      List<GuiEventListener> all = new ArrayList<>(inner.children());
      all.add(this.remove);
      this.children = List.copyOf(all);
      List<NarratableEntry> narrated = new ArrayList<>(inner.narratables());
      narrated.add(this.remove);
      this.narratables = List.copyOf(narrated);
   }

   public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
      this.inner.render(graphics, index, top, left, width - 20 - 4, height, mouseX, mouseY, hovering, partialTick);
      this.remove.setX(left + width - 20);
      this.remove.setY(top + (height - 20) / 2);
      this.remove.render(graphics, mouseX, mouseY, partialTick);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return this.inner.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
   }

   public List<? extends GuiEventListener> children() {
      return this.children;
   }

   public List<? extends NarratableEntry> narratables() {
      return this.narratables;
   }

   @Nullable
   @Override
   Component getTooltip(int mouseX, int mouseY) {
      return this.inner.getTooltip(mouseX, mouseY);
   }

   @Nullable
   @Override
   Component getGutterTooltip(int mouseX, int mouseY) {
      return this.inner.getGutterTooltip(mouseX, mouseY);
   }
}
