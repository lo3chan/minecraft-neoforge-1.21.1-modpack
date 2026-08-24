package com.seibel.distanthorizons.common.wrappers.gui;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class GuiHelper_neoforge {
   public static Button MakeBtn(Component base, int posX, int posZ, int width, int height, OnPress action) {
      return Button.builder(base, action).bounds(posX, posZ, width, height).build();
   }

   public static MutableComponent TextOrLiteral(String text) {
      return Component.literal(text);
   }

   public static MutableComponent TextOrTranslatable(String text) {
      return Component.translatable(text);
   }

   public static MutableComponent Translatable(String text, Object... args) {
      return Component.translatable(text, args);
   }

   public static void SetX(AbstractWidget widget, int x) {
      widget.setX(x);
   }

   public static void SetY(AbstractWidget widget, int y) {
      widget.setY(y);
   }
}
