package dev.isxander.yacl3.gui.controllers;

import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class PopupControllerScreen extends Screen {
   private final YACLScreen backgroundYaclScreen;
   private final ControllerPopupWidget<?> controllerPopup;

   public PopupControllerScreen(YACLScreen backgroundYaclScreen, ControllerPopupWidget<?> controllerPopup) {
      super(controllerPopup.popupTitle());
      this.backgroundYaclScreen = backgroundYaclScreen;
      this.controllerPopup = controllerPopup;
   }

   protected void init() {
      this.addRenderableWidget(this.controllerPopup);
   }

   protected void repositionElements() {
      super.repositionElements();
      this.onClose();
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      this.controllerPopup.renderBackground(graphics, mouseX, mouseY, delta);
      this.backgroundYaclScreen.render(graphics, -1, -1, delta);
      super.render(graphics, mouseX, mouseY, delta);
   }

   public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (!super.mouseClicked(mouseX, mouseY, button)) {
         this.onClose();
         return false;
      } else {
         return true;
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
      if (this.controllerPopup.mouseScrolled(mouseX, mouseY, horizontal, vertical)) {
         return true;
      } else {
         this.backgroundYaclScreen.mouseScrolled(mouseX, mouseY, horizontal, vertical);
         return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
      }
   }

   public void mouseMoved(double mouseX, double mouseY) {
      this.controllerPopup.mouseMoved(mouseX, mouseY);
   }

   public boolean charTyped(char codePoint, int modifiers) {
      return this.controllerPopup.charTyped(codePoint, modifiers);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return this.controllerPopup.keyPressed(keyCode, scanCode, modifiers);
   }

   public void tick() {
      super.tick();
      this.backgroundYaclScreen.tick();
   }

   public void onClose() {
      this.minecraft.screen = this.backgroundYaclScreen;
      this.controllerPopup.close();
   }
}
