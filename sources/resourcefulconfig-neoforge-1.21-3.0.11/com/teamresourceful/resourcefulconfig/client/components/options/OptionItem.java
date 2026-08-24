package com.teamresourceful.resourcefulconfig.client.components.options;

import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigEntry;
import com.teamresourceful.resourcefulconfig.client.components.base.ContainerWidget;
import com.teamresourceful.resourcefulconfig.client.components.base.ListWidget;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.EqualSpacingLayout.Orientation;
import net.minecraft.network.chat.Component;

public class OptionItem extends ContainerWidget implements ListWidget.Item {
   private static final int PADDING = 10;
   private final Component title;
   private final Component description;
   private final List<AbstractWidget> widgets;

   public OptionItem(ResourcefulConfigEntry entry, List<AbstractWidget> widgets) {
      this(entry.options().title().toComponent(), entry.options().comment().toComponent(), widgets);
   }

   public OptionItem(Component title, Component description, List<AbstractWidget> widgets) {
      super(0, 0, 0, 0);
      this.title = title.copy().withColor(-329226);
      this.description = description.copy().withColor(-9276296);
      this.widgets = widgets;
      this.init();
   }

   public void init() {
      this.clear();
      Font font = Minecraft.getInstance().font;
      int half = (int)(this.width * 0.5F);
      EqualSpacingLayout layout = new EqualSpacingLayout(this.width - 20, 0, Orientation.HORIZONTAL);
      LinearLayout titleDesc = LinearLayout.vertical().spacing(4);
      titleDesc.addChild(new StringWidget(half, 9, this.title, font).alignLeft());
      titleDesc.addChild(new MultiLineTextWidget(this.description, font).setCentered(false).setMaxWidth(half));
      LinearLayout options = LinearLayout.horizontal().spacing(4);

      for (AbstractWidget widget : this.widgets) {
         options.addChild(widget);
      }

      layout.addChild(titleDesc);
      layout.addChild(options, settings -> settings.alignVerticallyMiddle().alignHorizontallyRight());
      layout.arrangeElements();
      layout.setPosition(this.getX() + 10, this.getY() + 10);
      layout.visitWidgets(x$0 -> {
         AbstractWidget var10000 = this.addRenderableWidget(x$0);
      });
      this.height = layout.getHeight() + 20;
   }

   @Override
   protected void positionUpdated() {
      this.init();
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.getChildAt(mouseX, mouseY).isEmpty()) {
         this.setFocused(null);
         return false;
      } else {
         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   @Override
   public void setItemWidth(int width) {
      boolean changed = this.width != width;
      this.setWidth(width);
      if (changed) {
         this.init();
      }
   }
}
