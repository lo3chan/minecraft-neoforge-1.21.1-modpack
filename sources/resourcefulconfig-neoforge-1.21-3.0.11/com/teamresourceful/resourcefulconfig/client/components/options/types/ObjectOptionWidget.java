package com.teamresourceful.resourcefulconfig.client.components.options.types;

import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigObjectEntry;
import com.teamresourceful.resourcefulconfig.api.types.info.Translatable;
import com.teamresourceful.resourcefulconfig.client.UIConstants;
import com.teamresourceful.resourcefulconfig.client.components.ModSprites;
import com.teamresourceful.resourcefulconfig.client.components.base.BaseWidget;
import com.teamresourceful.resourcefulconfig.client.components.options.Options;
import com.teamresourceful.resourcefulconfig.client.components.options.OptionsListWidget;
import com.teamresourceful.resourcefulconfig.client.screens.base.ModalOverlay;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;

public class ObjectOptionWidget extends BaseWidget {
   private static final int WIDTH = 100;
   private static final int SIZE = 12;
   private static final int SPACING = 4;
   private static final int PADDING = 2;
   private final ResourcefulConfigObjectEntry entry;

   public ObjectOptionWidget(ResourcefulConfigObjectEntry entry) {
      super(100, 16);
      this.entry = entry;
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
      new ObjectOptionWidget.ObjectEditOverlay(this.entry).open();
   }

   private static class ObjectEditOverlay extends ModalOverlay {
      private final ResourcefulConfigObjectEntry entry;

      protected ObjectEditOverlay(ResourcefulConfigObjectEntry entry) {
         this.entry = entry;
         this.title = Translatable.toSpeifiedComponent(entry.instance(), UIConstants.EDIT_OBJECT);
      }

      @Override
      protected void init() {
         super.init();
         OptionsListWidget list = (OptionsListWidget)this.addRenderableWidget(new OptionsListWidget(this.contentWidth, this.contentHeight));
         list.setPosition(this.left, this.top);
         Options.populateOptions(list, this.entry.entries(), List.of());
      }

      @Override
      public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
         super.renderBackground(graphics, mouseX, mouseY, partialTicks);
      }
   }
}
