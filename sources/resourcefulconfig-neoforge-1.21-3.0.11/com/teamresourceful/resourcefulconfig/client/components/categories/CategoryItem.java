package com.teamresourceful.resourcefulconfig.client.components.categories;

import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig;
import com.teamresourceful.resourcefulconfig.client.ConfigScreen;
import com.teamresourceful.resourcefulconfig.client.components.ModSprites;
import com.teamresourceful.resourcefulconfig.client.components.base.BaseWidget;
import com.teamresourceful.resourcefulconfig.client.components.base.ListWidget;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class CategoryItem extends BaseWidget implements ListWidget.Item {
   private static final int PADDING = 4;
   private final ConfigScreen screen;
   private final ResourcefulConfig config;
   private final Function<String, List<String>> termCollector;

   public CategoryItem(ConfigScreen screen, ResourcefulConfig config, Function<String, List<String>> termCollector) {
      super(0, 8 + 9);
      this.screen = screen;
      this.config = config;
      this.termCollector = termCollector;
   }

   @Override
   public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      if (this.isHovered()) {
         graphics.blitSprite(ModSprites.BUTTON_HOVER, this.getX() + 1, this.getY(), this.getWidth() - 2, this.getHeight());
      }

      int color = this.isHovered() ? -329226 : -9276296;
      renderScrollingString(
         graphics,
         Minecraft.getInstance().font,
         this.config.info().title().toComponent(),
         this.getX() + 8,
         this.getY() + 4,
         this.getX() + 8 + this.getWidth() - 16,
         this.getY() + this.getHeight() - 4,
         color
      );
   }

   public void onClick(double d, double e) {
      Minecraft.getInstance().setScreen(new ConfigScreen(this.screen, this.config, this.termCollector));
   }

   @Override
   public void setItemWidth(int width) {
      this.setWidth(width);
   }
}
