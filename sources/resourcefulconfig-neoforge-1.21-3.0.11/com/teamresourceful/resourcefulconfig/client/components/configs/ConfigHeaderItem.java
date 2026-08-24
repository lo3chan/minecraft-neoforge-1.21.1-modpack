package com.teamresourceful.resourcefulconfig.client.components.configs;

import com.teamresourceful.resourcefulconfig.client.UIConstants;
import com.teamresourceful.resourcefulconfig.client.components.ModSprites;
import com.teamresourceful.resourcefulconfig.client.components.base.ContainerWidget;
import com.teamresourceful.resourcefulconfig.client.components.base.ListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;

public class ConfigHeaderItem extends ContainerWidget implements ListWidget.Item {
   public ConfigHeaderItem() {
      super(0, 0, 0, 0);
   }

   public void init() {
      this.clear();
      Font font = Minecraft.getInstance().font;
      int width = this.width - 40;
      LinearLayout titleDesc = LinearLayout.vertical().spacing(4);
      titleDesc.addChild(new StringWidget(width, 9, UIConstants.MOD_CONFIGS, font).alignLeft());
      titleDesc.addChild(new StringWidget(width, 9, UIConstants.MOD_CONFIGS_DESCRIPTION, font).alignLeft());
      titleDesc.arrangeElements();
      titleDesc.setPosition(this.getX() + 20, this.getY() + 20);
      titleDesc.visitWidgets(x$0 -> {
         AbstractWidget var10000 = this.addRenderableWidget(x$0);
      });
      this.height = titleDesc.getHeight() + 30;
   }

   @Override
   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      graphics.blitSprite(ModSprites.ACCENT, this.getX() + 10, this.getY() + 10, this.width - 20, this.height - 10);
      super.renderWidget(graphics, mouseX, mouseY, partialTicks);
   }

   @Override
   protected void positionUpdated() {
      this.init();
   }

   @Override
   public void setItemWidth(int width) {
      this.setWidth(width);
   }
}
