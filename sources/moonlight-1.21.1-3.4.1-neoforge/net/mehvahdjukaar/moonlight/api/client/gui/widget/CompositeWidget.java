package net.mehvahdjukaar.moonlight.api.client.gui.widget;

import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

public abstract class CompositeWidget extends AbstractContainerWidget {
   protected CompositeWidget(int x, int y, int width, int height, Component message) {
      super(x, y, width, height, message);
   }

   public void setFocused(boolean focused) {
      super.setFocused(focused);
      GuiEventListener child = this.getFocused();
      if (child != null) {
         child.setFocused(focused);
      }
   }
}
