package com.iafenvoy.jupiter.render.screen.dialog;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.interfaces.ConfigMetaProvider;
import com.iafenvoy.jupiter.render.TitleStack;
import com.iafenvoy.jupiter.render.screen.JupiterScreen;
import com.iafenvoy.jupiter.render.screen.WidgetBuilderManager;
import com.iafenvoy.jupiter.render.screen.scrollbar.VerticalScrollBar;
import com.iafenvoy.jupiter.render.widget.WidgetBuilder;
import com.iafenvoy.jupiter.util.TextUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractListDialog<T, S> extends Dialog<T> {
   protected final VerticalScrollBar itemScrollBar = new VerticalScrollBar();
   protected final List<WidgetBuilder<S>> widgets = new ArrayList<>();
   private int configPerPage;

   protected AbstractListDialog(Screen parent, TitleStack titleStack, ConfigMetaProvider provider, ConfigEntry<T> entry) {
      super(parent, titleStack, provider, entry);
   }

   @Override
   protected void init() {
      super.init();
      this.addRenderableWidget(JupiterScreen.createButton(10, 5, 20, 20, TextUtil.literal("<"), button -> this.onClose()));
      this.addRenderableWidget(JupiterScreen.createButton(this.width - 80, 5, 20, 20, TextUtil.literal("+"), button -> {
         this.addNewValue();
         this.rebuildWidgets();
      }));
      this.calculateMaxItems();
      this.widgets.clear();
      List<S> values = this.getValues().stream().toList();

      for (int i = 0; i < values.size(); i++) {
         WidgetBuilder<S> widget = WidgetBuilderManager.get(this.provider, this.newSingleInstance(values.get(i), i, () -> this.rebuildWidgets()));
         this.widgets.add(widget);
         widget.addDialogElements(new WidgetBuilder.Context(this, x$0 -> {
            AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(x$0);
         }, this.titleStack), i + ":", 40, 0, Math.max(10, this.width - 70), 20);
      }

      this.updateItemPos();
   }

   protected abstract void addNewValue();

   protected abstract Collection<S> getValues();

   protected abstract ConfigEntry<S> newSingleInstance(S var1, int var2, Runnable var3);

   public void calculateMaxItems() {
      this.configPerPage = Math.max(0, (this.height - 30) / 25);
      this.itemScrollBar.setMaxValue(Math.max(0, this.getValues().size() - this.configPerPage));
   }

   public void resize(@NotNull Minecraft minecraft, int width, int height) {
      super.resize(minecraft, width, height);
      this.calculateMaxItems();
      this.updateItemPos();
   }

   public void updateItemPos() {
      int top = this.itemScrollBar.getValue();
      Collection<S> entries = this.getValues();

      for (int i = 0; i < top && i < entries.size(); i++) {
         this.widgets.get(i).update(false, 0);
      }

      for (int i = top; i < top + this.configPerPage && i < entries.size(); i++) {
         this.widgets.get(i).update(true, 30 + (i - top) * 25);
      }

      for (int i = top + this.configPerPage; i < entries.size(); i++) {
         this.widgets.get(i).update(false, 0);
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      if (super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
         return true;
      } else {
         this.itemScrollBar.setValue(this.itemScrollBar.getValue() + (scrollY > 0.0 ? -1 : 1) * 2);
         this.updateItemPos();
         return true;
      }
   }

   @Override
   public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.render(graphics, mouseX, mouseY, partialTicks);
      this.itemScrollBar
         .render(graphics, mouseX, mouseY, partialTicks, this.width - 18, 30, 8, this.height - 50, (this.configPerPage + this.itemScrollBar.getMaxValue()) * 25);
      if (this.itemScrollBar.isDragging()) {
         this.updateItemPos();
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0 && this.itemScrollBar.wasMouseOver()) {
         this.itemScrollBar.setIsDragging(true);
         this.updateItemPos();
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
         this.itemScrollBar.setIsDragging(false);
      }

      return super.mouseReleased(mouseX, mouseY, button);
   }
}
