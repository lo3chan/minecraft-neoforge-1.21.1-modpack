package com.iafenvoy.jupiter.render.screen.dialog;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.interfaces.ConfigMetaProvider;
import com.iafenvoy.jupiter.render.TitleStack;
import com.iafenvoy.jupiter.render.screen.JupiterScreen;
import com.iafenvoy.jupiter.util.TextUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

public class EnumSelectDialog<T extends Enum<T>> extends Dialog<T> {
   private EnumSelectWidget<T> widget;
   private boolean initialized = false;

   public EnumSelectDialog(Screen parent, TitleStack titleStack, ConfigMetaProvider provider, ConfigEntry<T> entry) {
      super(parent, titleStack, provider, entry);
   }

   @Override
   protected void init() {
      super.init();
      if (!this.initialized) {
         this.initialized = true;
         this.widget = new EnumSelectWidget<>(this, this.minecraft, this.width - 80, this.height - 80, 60);
      }

      this.widget.updateSize(this.width - 80, new HeaderAndFooterLayout(this, 50, 20));
      this.widget.setX(40);
      this.widget.update();
      this.addRenderableWidget(this.widget);
      this.addRenderableWidget(JupiterScreen.createButton(40, 25, 60, 20, TextUtil.translatable("jupiter.screen.back"), button -> this.onClose()));
   }

   @Override
   public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      super.render(graphics, mouseX, mouseY, delta);
      this.widget.render(graphics, mouseX, mouseY, delta);
      graphics.drawCenteredString(this.font, this.title, this.width / 2, 10, -1);
   }

   ConfigEntry<T> getEntry() {
      return this.entry;
   }
}
