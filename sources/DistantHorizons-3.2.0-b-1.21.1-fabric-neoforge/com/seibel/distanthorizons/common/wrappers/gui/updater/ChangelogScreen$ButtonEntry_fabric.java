package com.seibel.distanthorizons.common.wrappers.gui.updater;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_364;
import net.minecraft.class_6379;
import net.minecraft.class_4265.class_4266;

public class ChangelogScreen$ButtonEntry_fabric extends class_4266<ChangelogScreen$ButtonEntry_fabric> {
   private static final class_327 textRenderer = class_310.method_1551().field_1772;
   private final class_2561 text;
   private final List<class_339> children = new ArrayList<>();

   private ChangelogScreen$ButtonEntry_fabric(class_2561 text) {
      this.text = text;
   }

   public static ChangelogScreen$ButtonEntry_fabric create(class_2561 text) {
      return new ChangelogScreen$ButtonEntry_fabric(text);
   }

   public void method_25343(
      class_332 matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta
   ) {
      matrices.method_27535(textRenderer, this.text, 12, y + 5, 16777215);
   }

   public List<? extends class_364> method_25396() {
      return this.children;
   }

   public List<? extends class_6379> method_37025() {
      return this.children;
   }
}
