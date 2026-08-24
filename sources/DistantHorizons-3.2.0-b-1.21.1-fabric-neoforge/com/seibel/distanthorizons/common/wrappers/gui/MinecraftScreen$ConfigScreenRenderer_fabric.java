package com.seibel.distanthorizons.common.wrappers.gui;

import com.seibel.distanthorizons.core.config.gui.AbstractScreen;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.class_1041;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_5250;
import org.jetbrains.annotations.NotNull;

class MinecraftScreen$ConfigScreenRenderer_fabric extends DhScreen_fabric {
   private final class_437 parent;
   private MinecraftScreen$ConfigListWidget_fabric configListWidget;
   private AbstractScreen screen;

   public static class_5250 translate(String str, Object... args) {
      return class_2561.method_43469(str, args);
   }

   protected MinecraftScreen$ConfigScreenRenderer_fabric(class_437 parent, AbstractScreen screen, String translationName) {
      super(translate(translationName));
      screen.minecraftWindow = class_310.method_1551().method_22683().method_4490();
      this.parent = parent;
      this.screen = screen;
   }

   protected void method_25426() {
      super.method_25426();
      class_1041 mcWindow = this.field_22787.method_22683();
      this.screen.width = mcWindow.method_4489();
      this.screen.height = mcWindow.method_4506();
      this.screen.scaledWidth = this.field_22789;
      this.screen.scaledHeight = this.field_22790;
      this.screen.init();
      this.configListWidget = new MinecraftScreen$ConfigListWidget_fabric(this.field_22787, this.field_22789, this.field_22790, 0, 0, 25);
      this.method_25429(this.configListWidget);
   }

   public void method_25394(class_332 matrices, int mouseX, int mouseY, float delta) {
      this.method_25420(matrices, mouseX, mouseY, delta);
      this.configListWidget.method_25394(matrices, mouseX, mouseY, delta);
      this.screen.mouseX = mouseX;
      this.screen.mouseY = mouseY;
      this.screen.render(delta);
      super.method_25394(matrices, mouseX, mouseY, delta);
   }

   public void method_25410(class_310 mc, int width, int height) {
      super.method_25410(mc, width, height);
      class_1041 mcWindow = this.field_22787.method_22683();
      this.screen.width = mcWindow.method_4489();
      this.screen.height = mcWindow.method_4506();
      this.screen.scaledWidth = this.field_22789;
      this.screen.scaledHeight = this.field_22790;
      this.screen.onResize();
   }

   public void method_25393() {
      super.method_25393();
      this.screen.tick();
      if (this.screen.close) {
         this.method_25419();
      }
   }

   public void method_25419() {
      this.screen.onClose();
      DhScreenUtil_fabric.setScreen(this.parent);
   }

   public void method_29638(@NotNull List<Path> files) {
      this.screen.onFilesDrop(files);
   }

   public boolean method_25422() {
      return this.screen.shouldCloseOnEsc;
   }
}
