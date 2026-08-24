package com.seibel.distanthorizons.common.wrappers.gui;

import net.minecraft.class_2561;
import net.minecraft.class_339;
import net.minecraft.class_4185;
import net.minecraft.class_5250;
import net.minecraft.class_4185.class_4241;

public class GuiHelper_fabric {
   public static class_4185 MakeBtn(class_2561 base, int posX, int posZ, int width, int height, class_4241 action) {
      return class_4185.method_46430(base, action).method_46434(posX, posZ, width, height).method_46431();
   }

   public static class_5250 TextOrLiteral(String text) {
      return class_2561.method_43470(text);
   }

   public static class_5250 TextOrTranslatable(String text) {
      return class_2561.method_43471(text);
   }

   public static class_5250 Translatable(String text, Object... args) {
      return class_2561.method_43469(text, args);
   }

   public static void SetX(class_339 widget, int x) {
      widget.method_46421(x);
   }

   public static void SetY(class_339 widget, int y) {
      widget.method_46419(y);
   }
}
