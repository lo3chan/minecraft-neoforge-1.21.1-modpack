package com.iafenvoy.jupiter.render.screen;

import com.iafenvoy.jupiter.config.container.wrapper.RemoteConfigWrapper;
import com.iafenvoy.jupiter.config.entry.BaseEntry;
import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.interfaces.ConfigMetaProvider;
import com.iafenvoy.jupiter.render.TitleStack;
import com.iafenvoy.jupiter.render.screen.scrollbar.VerticalScrollBar;
import com.iafenvoy.jupiter.render.widget.WidgetBuilder;
import com.iafenvoy.jupiter.util.TextUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigListScreen extends Screen implements JupiterScreen {
   private final Screen parent;
   private final TitleStack titleStack;
   private final ResourceLocation id;
   private final boolean client;
   protected final List<WidgetBuilder<?>> configWidgets = new ArrayList<>();
   protected final VerticalScrollBar entryScrollBar = new VerticalScrollBar();
   protected List<ConfigEntry<?>> entries = List.of();
   protected int topBorder = 30;
   private int configPerPage;
   private int textMaxLength;

   public ConfigListScreen(Screen parent, TitleStack titleStack, ResourceLocation id, List<ConfigEntry<?>> entries, boolean client) {
      this(parent, titleStack, id, client);
      this.entries = entries;
   }

   public ConfigListScreen(Screen parent, TitleStack titleStack, ResourceLocation id, boolean client) {
      super(TextUtil.empty());
      this.parent = parent;
      this.titleStack = titleStack;
      this.id = id;
      this.client = client;
   }

   protected void init() {
      super.init();
      this.titleStack.cacheTitle(this.width - this.font.width(this.getCurrentEditText()) - 70);
      this.addRenderableWidget(JupiterScreen.createButton(10, 5, 20, 20, TextUtil.literal("<"), button -> this.onClose()));
      this.calculateMaxEntries();
      this.textMaxLength = Mth.clamp(
         this.entries
               .stream()
               .filter(x -> x instanceof BaseEntry)
               .map(ConfigEntry::getName)
               .filter(Objects::nonNull)
               .map(t -> this.font.width(t))
               .max(Comparator.naturalOrder())
               .orElse(0)
            + 30,
         this.width / 2,
         this.width - 150
      );
      this.configWidgets.clear();
      this.configWidgets
         .addAll(
            this.entries
               .stream()
               .map(c -> WidgetBuilderManager.get(new ConfigMetaProvider.SimpleProvider(this.id, "%ERROR%", this.client), (ConfigEntry<?>)c))
               .toList()
         );
      this.configWidgets.forEach(b -> b.addElements(new WidgetBuilder.Context(this, x$0 -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(x$0);
      }, this.titleStack), this.textMaxLength, 0, Math.max(10, this.width - this.textMaxLength - 30), 20));
      this.updateEntryPos();
   }

   public void resize(@NotNull Minecraft minecraft, int width, int height) {
      super.resize(minecraft, width, height);
      this.calculateMaxEntries();
      this.updateEntryPos();
   }

   @NotNull
   public Component getTitle() {
      return this.titleStack.getTitle();
   }

   public void calculateMaxEntries() {
      this.configPerPage = Math.max(0, (this.height - this.topBorder - 10) / 25);
      this.entryScrollBar.setMaxValue(Math.max(0, this.entries.size() - this.configPerPage));
   }

   public void updateEntryPos() {
      int top = this.entryScrollBar.getValue();

      for (int i = 0; i < top && i < this.entries.size(); i++) {
         this.configWidgets.get(i).update(false, 0);
      }

      for (int i = top; i < top + this.configPerPage && i < this.entries.size(); i++) {
         this.configWidgets.get(i).update(true, this.topBorder + 5 + (i - top) * 25);
      }

      for (int i = top + this.configPerPage; i < this.entries.size(); i++) {
         this.configWidgets.get(i).update(false, 0);
      }
   }

   @Nullable
   public ConfigEntry<?> getMouseOverEntry(int mouseX, int mouseY) {
      return this.configWidgets.stream().filter(widget -> widget.isMouseOver(mouseX, mouseY)).findFirst().map(WidgetBuilder::getConfig).orElse(null);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 256) {
         this.onClose();
         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      if (super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
         return true;
      } else if (mouseY >= this.topBorder) {
         this.entryScrollBar.setValue(this.entryScrollBar.getValue() + (scrollY > 0.0 ? -1 : 1) * 2);
         this.updateEntryPos();
         return true;
      } else {
         return false;
      }
   }

   public void onClose() {
      assert this.minecraft != null;

      this.minecraft.setScreen(this.parent);
   }

   @Nullable
   protected ResourceLocation getBackgroundTexture(boolean ingame) {
      return null;
   }

   protected void renderMenuBackground(@NotNull GuiGraphics guiGraphics, int x, int y, int width, int height) {
      assert this.minecraft != null;

      ResourceLocation texture = this.getBackgroundTexture(this.minecraft.level != null);
      if (texture == null) {
         super.renderMenuBackground(guiGraphics, x, y, width, height);
      } else {
         renderMenuBackgroundTexture(guiGraphics, texture, x, y, 0.0F, 0.0F, width, height);
      }
   }

   public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.render(graphics, mouseX, mouseY, partialTicks);
      String currentText = this.getCurrentEditText();
      int textWidth = this.font.width(currentText);
      graphics.drawString(this.font, this.getTitle(), 40, 10, -1, true);
      graphics.drawString(this.font, currentText, this.width - textWidth - 10, 10, -1);
      this.entryScrollBar
         .render(
            graphics,
            mouseX,
            mouseY,
            partialTicks,
            this.width - 18,
            this.topBorder,
            8,
            this.height - this.topBorder - 10,
            (this.configPerPage + this.entryScrollBar.getMaxValue()) * 25
         );
      if (this.entryScrollBar.isDragging()) {
         this.updateEntryPos();
      }

      ConfigEntry<?> entry = this.getMouseOverEntry(mouseX, mouseY);
      if (entry != null && entry.getTooltip() != null) {
         this.setTooltipForNextRenderPass(entry.getTooltip());
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0 && this.entryScrollBar.wasMouseOver()) {
         this.entryScrollBar.setIsDragging(true);
         this.updateEntryPos();
         return true;
      } else {
         boolean b = super.mouseClicked(mouseX, mouseY, button);
         if (!b) {
            this.setFocused(null);
         }

         return b;
      }
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (button == 0) {
         this.entryScrollBar.setIsDragging(false);
      }

      return super.mouseReleased(mouseX, mouseY, button);
   }

   protected String getCurrentEditText() {
      if (this.client) {
         return I18n.get("jupiter.screen.current_modifying_client", new Object[0]);
      } else {
         return this.entries instanceof RemoteConfigWrapper
            ? I18n.get("jupiter.screen.current_modifying_dedicate_server", new Object[0])
            : I18n.get("jupiter.screen.current_modifying_local_server", new Object[0]);
      }
   }
}
