package com.teamresourceful.resourcefulconfig.client.components.header;

import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig;
import com.teamresourceful.resourcefulconfig.client.components.base.ContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LinearLayout;

public class HeaderWidget extends ContainerWidget {
   public HeaderWidget(int width, ResourcefulConfig config, Runnable onSearchUpdate) {
      super(0, 0, width, 0);
      LinearLayout layout = LinearLayout.horizontal().spacing(10);
      int controlsWidth = width / 4;
      int contentWidth = width - controlsWidth - 10;
      HeaderControlsWidget controls = (HeaderControlsWidget)layout.addChild(new HeaderControlsWidget(controlsWidth, config, onSearchUpdate));
      HeaderContentWidget content = (HeaderContentWidget)layout.addChild(new HeaderContentWidget(contentWidth, config));
      layout.arrangeElements();
      layout.setPosition(this.getX() + 10, this.getY() + 10);
      layout.visitWidgets(x$0 -> {
         AbstractWidget var10000 = this.addRenderableWidget(x$0);
      });
      this.height = layout.getHeight();
      controls.setHeight(this.height);
      content.setHeight(this.height);
   }
}
