package com.seibel.distanthorizons.common.wrappers.gui;

import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4185;
import net.minecraft.class_437;

public class DhScreen_fabric extends class_437 {
   protected DhScreen_fabric(class_2561 title) {
      super(title);
   }

   protected class_4185 addBtn(class_4185 button) {
      return (class_4185)this.method_37063(button);
   }

   protected void DhDrawCenteredString(class_332 guiStack, class_327 font, class_2561 text, int x, int y, int color) {
      guiStack.method_27534(font, text, x, y, color);
   }

   protected void DhDrawString(class_332 guiStack, class_327 font, class_2561 text, int x, int y, int color) {
      guiStack.method_27535(font, text, x, y, color);
   }

   protected void DhRenderComponentTooltip(class_332 guiStack, class_327 font, List<class_2561> comp, int x, int y) {
      guiStack.method_51434(font, comp, x, y);
   }

   protected void DhRenderTooltip(class_332 guiStack, class_327 font, class_2561 text, int x, int y) {
      guiStack.method_51438(font, text, x, y);
   }
}
