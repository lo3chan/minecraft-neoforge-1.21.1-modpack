package com.seibel.distanthorizons.common.wrappers.gui;

import com.mojang.blaze3d.platform.Window;
import com.seibel.distanthorizons.core.config.gui.AbstractScreen;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

class MinecraftScreen$ConfigScreenRenderer_neoforge extends DhScreen_neoforge {
   private final Screen parent;
   private MinecraftScreen$ConfigListWidget_neoforge configListWidget;
   private AbstractScreen screen;

   public static MutableComponent translate(String str, Object... args) {
      return Component.translatable(str, args);
   }

   protected MinecraftScreen$ConfigScreenRenderer_neoforge(Screen parent, AbstractScreen screen, String translationName) {
      super(translate(translationName));
      screen.minecraftWindow = Minecraft.getInstance().getWindow().getWindow();
      this.parent = parent;
      this.screen = screen;
   }

   protected void init() {
      super.init();
      Window mcWindow = this.minecraft.getWindow();
      this.screen.width = mcWindow.getWidth();
      this.screen.height = mcWindow.getHeight();
      this.screen.scaledWidth = this.width;
      this.screen.scaledHeight = this.height;
      this.screen.init();
      this.configListWidget = new MinecraftScreen$ConfigListWidget_neoforge(this.minecraft, this.width, this.height, 0, 0, 25);
      this.addWidget(this.configListWidget);
   }

   public void render(GuiGraphics matrices, int mouseX, int mouseY, float delta) {
      this.renderBackground(matrices, mouseX, mouseY, delta);
      this.configListWidget.render(matrices, mouseX, mouseY, delta);
      this.screen.mouseX = mouseX;
      this.screen.mouseY = mouseY;
      this.screen.render(delta);
      super.render(matrices, mouseX, mouseY, delta);
   }

   public void resize(Minecraft mc, int width, int height) {
      super.resize(mc, width, height);
      Window mcWindow = this.minecraft.getWindow();
      this.screen.width = mcWindow.getWidth();
      this.screen.height = mcWindow.getHeight();
      this.screen.scaledWidth = this.width;
      this.screen.scaledHeight = this.height;
      this.screen.onResize();
   }

   public void tick() {
      super.tick();
      this.screen.tick();
      if (this.screen.close) {
         this.onClose();
      }
   }

   public void onClose() {
      this.screen.onClose();
      DhScreenUtil_neoforge.setScreen(this.parent);
   }

   public void onFilesDrop(@NotNull List<Path> files) {
      this.screen.onFilesDrop(files);
   }

   public boolean shouldCloseOnEsc() {
      return this.screen.shouldCloseOnEsc;
   }
}
