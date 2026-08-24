package com.teamresourceful.resourcefulconfig.client.components.configs;

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen;
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig;
import com.teamresourceful.resourcefulconfig.client.components.ModSprites;
import com.teamresourceful.resourcefulconfig.client.components.base.ContainerWidget;
import com.teamresourceful.resourcefulconfig.client.components.base.ListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

public class ConfigItem extends ContainerWidget implements ListWidget.Item {
   private final Component title;
   private final Component description;
   private final ResourcefulConfig config;

   public ConfigItem(ResourcefulConfig config) {
      super(0, 0, 0, 0);
      this.title = config.info().title().toComponent().withColor(-329226);
      this.description = config.info().description().toComponent().withColor(-9276296);
      this.config = config;
   }

   public void init() {
      this.clear();
      Font font = Minecraft.getInstance().font;
      int width = this.width - 40;
      LinearLayout titleDesc = LinearLayout.vertical().spacing(4);
      titleDesc.addChild(new StringWidget(width, 9, this.title, font).alignLeft());
      titleDesc.addChild(new StringWidget(width, 9, this.description, font).alignLeft());
      titleDesc.arrangeElements();
      titleDesc.setPosition(this.getX() + 20, this.getY() + 20);
      titleDesc.visitWidgets(x$0 -> {
         AbstractWidget var10000 = this.addRenderableWidget(x$0);
      });
      this.height = titleDesc.getHeight() + 40;
   }

   @Override
   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      graphics.blitSprite(ModSprites.ofButton(this.isHovered()), this.getX() + 10, this.getY() + 10, this.width - 20, this.height - 20);
      super.renderWidget(graphics, mouseX, mouseY, partialTicks);
   }

   @Override
   protected void positionUpdated() {
      this.init();
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.isHoveredOrFocused()) {
         Minecraft.getInstance().setScreen(ResourcefulConfigScreen.get(Minecraft.getInstance().screen, this.config));
      }

      return false;
   }

   @Override
   public void setItemWidth(int width) {
      this.setWidth(width);
   }
}
