package com.teamresourceful.resourcefulconfig.api.client;

import com.teamresourceful.resourcefulconfig.client.screens.base.ModalOverlay;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class GenericModalOverlay extends ModalOverlay {
   private final ModalWidgetConstructor constructor;

   public GenericModalOverlay(Component title, ModalWidgetConstructor constructor) {
      this.title = title;
      this.constructor = constructor;
   }

   @Override
   protected void init() {
      super.init();
      this.addRenderableWidget(this.constructor.construct(this.left, this.top, this.contentWidth, this.contentHeight));
   }
}
