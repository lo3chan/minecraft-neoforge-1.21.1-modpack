package com.iafenvoy.jupiter.render.screen.dialog;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.interfaces.ConfigMetaProvider;
import com.iafenvoy.jupiter.render.TitleStack;
import com.iafenvoy.jupiter.render.screen.JupiterScreen;
import com.iafenvoy.jupiter.util.TextUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class Dialog<T> extends Screen implements JupiterScreen {
   private final Screen parent;
   protected final TitleStack titleStack;
   protected final ConfigMetaProvider provider;
   protected final ConfigEntry<T> entry;

   protected Dialog(Screen parent, TitleStack titleStack, ConfigMetaProvider provider, ConfigEntry<T> entry) {
      super(TextUtil.empty());
      this.parent = parent;
      this.titleStack = titleStack;
      this.provider = provider;
      this.entry = entry;
   }

   protected void init() {
      super.init();
      this.titleStack.cacheTitle(this.width - 130);
   }

   @NotNull
   public Component getTitle() {
      return this.titleStack.getTitle();
   }

   public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.render(graphics, mouseX, mouseY, partialTicks);
      graphics.drawString(this.font, this.getTitle(), 40, 10, -1, true);
   }

   public void onClose() {
      assert this.minecraft != null;

      this.minecraft.setScreen(this.parent);
   }
}
