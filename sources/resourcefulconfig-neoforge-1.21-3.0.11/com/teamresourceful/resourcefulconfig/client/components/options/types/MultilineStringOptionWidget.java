package com.teamresourceful.resourcefulconfig.client.components.options.types;

import com.teamresourceful.resourcefulconfig.client.UIConstants;
import com.teamresourceful.resourcefulconfig.client.components.ModSprites;
import com.teamresourceful.resourcefulconfig.client.components.base.BaseWidget;
import com.teamresourceful.resourcefulconfig.client.screens.base.ModalOverlay;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.CommonComponents;

public class MultilineStringOptionWidget extends BaseWidget {
   private static final int WIDTH = 80;
   private static final int SIZE = 12;
   private static final int SPACING = 4;
   private static final int PADDING = 2;
   private final Supplier<String> getter;
   private final Consumer<String> setter;

   public MultilineStringOptionWidget(Supplier<String> getter, Consumer<String> setter) {
      super(80, 16);
      this.getter = getter;
      this.setter = setter;
   }

   @Override
   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      graphics.blitSprite(ModSprites.ofButton(this.isHovered()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
      int contentWidth = this.font.width(UIConstants.EDIT) + 4 + 12;
      graphics.blitSprite(ModSprites.EDIT, this.getX() + (this.getWidth() - contentWidth) / 2, this.getY() + 2, 12, 12);
      graphics.drawString(
         this.font, UIConstants.EDIT, this.getX() + (this.getWidth() - contentWidth) / 2 + 12 + 4, this.getY() + (this.getHeight() - 9) / 2 + 1, -329226
      );
   }

   public void onClick(double mouseX, double mouseY) {
      new MultilineStringOptionWidget.MutlilineStringOverlay(this.getter, this.setter).open();
   }

   private static class MutlilineStringOverlay extends ModalOverlay {
      private final Supplier<String> getter;
      private final Consumer<String> setter;

      protected MutlilineStringOverlay(Supplier<String> getter, Consumer<String> setter) {
         this.title = UIConstants.EDIT_STRING;
         this.getter = getter;
         this.setter = setter;
      }

      @Override
      protected void init() {
         super.init();
         MultiLineEditBox box = (MultiLineEditBox)this.addRenderableWidget(
            new MultiLineEditBox(this.font, this.left, this.top, this.contentWidth, this.contentHeight, CommonComponents.EMPTY, CommonComponents.EMPTY) {
               protected void renderBackground(GuiGraphics guiGraphics) {
               }
            }
         );
         box.setValue(this.getter.get());
         box.setValueListener(this.setter);
      }

      @Override
      public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
         super.renderBackground(graphics, mouseX, mouseY, partialTicks);
         graphics.blitSprite(ModSprites.BUTTON, this.left, this.top, this.contentWidth, this.contentHeight);
      }
   }
}
