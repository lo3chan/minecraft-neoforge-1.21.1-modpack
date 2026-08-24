package dev.isxander.yacl3.gui.controllers;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import java.util.function.BiConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ActionController implements Controller<BiConsumer<YACLScreen, ButtonOption>> {
   public static final Component DEFAULT_TEXT = Component.translatable("yacl.control.action.execute");
   private final ButtonOption option;
   private final Component text;

   public ActionController(ButtonOption option) {
      this(option, DEFAULT_TEXT);
   }

   public ActionController(ButtonOption option, Component text) {
      this.option = option;
      this.text = text;
   }

   public ButtonOption option() {
      return this.option;
   }

   @Override
   public Component formatValue() {
      return this.text;
   }

   @Override
   public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
      return new ActionController.ActionControllerElement(this, screen, widgetDimension);
   }

   public static class ActionControllerElement extends ControllerWidget<ActionController> {
      private final String buttonString;

      public ActionControllerElement(ActionController control, YACLScreen screen, Dimension<Integer> dim) {
         super(control, screen, dim);
         this.buttonString = control.formatValue().getString().toLowerCase();
      }

      public void executeAction() {
         this.playDownSound();
         this.control.option().action().accept(this.screen, this.control.option());
      }

      @Override
      protected void drawValueText(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
         super.drawValueText(graphics, mouseX, mouseY, delta);
         if (this.hovered) {
         }
      }

      @Override
      public boolean onMouseClicked(double mouseX, double mouseY, int button) {
         if (this.isMouseOver(mouseX, mouseY) && this.isAvailable()) {
            this.executeAction();
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
         if (!this.focused) {
            return false;
         } else if (keyCode != 257 && keyCode != 32 && keyCode != 335) {
            return false;
         } else {
            this.executeAction();
            return true;
         }
      }

      @Override
      protected int getHoveredControlWidth() {
         return this.getUnhoveredControlWidth();
      }

      @Override
      public boolean canReset() {
         return false;
      }

      @Override
      public boolean matchesSearch(String query) {
         return super.matchesSearch(query) || this.buttonString.contains(query);
      }
   }
}
