package com.teamresourceful.resourcefulconfig.api.client;

import com.teamresourceful.resourcefulconfig.client.components.base.ContainerWidget;
import com.teamresourceful.resourcefulconfig.client.components.base.CustomButton;
import com.teamresourceful.resourcefulconfig.client.components.base.SpriteButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ResourcefulConfigUI {
   public static void openModal(Component title, ModalWidgetConstructor constructor) {
      new GenericModalOverlay(title, constructor).open();
   }

   public static AbstractWidget container(int x, int y, int width, int height, Layout layout) {
      return new ContainerWidget(x, y, width, height) {
         {
            this.positionUpdated();
         }

         @Override
         protected void positionUpdated() {
            this.clear();
            layout.setPosition(this.getX(), this.getY());
            layout.arrangeElements();
            layout.visitWidgets(x$0 -> {
               AbstractWidget var10000 = this.addRenderableWidget(x$0);
            });
         }
      };
   }

   public static AbstractWidget button(int x, int y, int width, int height, Component text, Runnable onClick) {
      CustomButton button = new CustomButton(width, height, text, onClick);
      button.setPosition(x, y);
      return button;
   }

   public static AbstractWidget button(int x, int y, int width, int height, ResourceLocation sprite, @Nullable Component tooltip, Runnable onClick) {
      SpriteButton button = SpriteButton.builder(width, height).padding(2).sprite(sprite).tooltip(tooltip).onPress(onClick).build();
      button.setPosition(x, y);
      return button;
   }
}
