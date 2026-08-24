package com.seibel.distanthorizons.common.wrappers.gui;

import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_4185;
import net.minecraft.class_4185.class_4241;

public class TexturedButtonWidget_fabric extends class_4185 {
   public final boolean renderBackground;
   private final int u;
   private final int v;
   private final int hoveredVOffset;
   private final class_2960 textureResourceLocation;
   private final int textureWidth;
   private final int textureHeight;

   public TexturedButtonWidget_fabric(
      int x,
      int y,
      int width,
      int height,
      int u,
      int v,
      int hoveredVOffset,
      class_2960 textureResourceLocation,
      int textureWidth,
      int textureHeight,
      class_4241 pressAction,
      class_2561 text
   ) {
      this(x, y, width, height, u, v, hoveredVOffset, textureResourceLocation, textureWidth, textureHeight, pressAction, text, true);
   }

   public TexturedButtonWidget_fabric(
      int x,
      int y,
      int width,
      int height,
      int u,
      int v,
      int hoveredVOffset,
      class_2960 textureResourceLocation,
      int textureWidth,
      int textureHeight,
      class_4241 pressAction,
      class_2561 text,
      boolean renderBackground
   ) {
      super(x, y, width, height, class_2561.method_43473(), pressAction, field_40754);
      this.u = u;
      this.v = v;
      this.hoveredVOffset = hoveredVOffset;
      this.textureResourceLocation = textureResourceLocation;
      this.textureWidth = textureWidth;
      this.textureHeight = textureHeight;
      this.renderBackground = renderBackground;
   }

   public void method_48579(class_332 matrices, int mouseX, int mouseY, float delta) {
      if (this.renderBackground) {
         matrices.method_52706(
            field_45339.method_52729(this.field_22763, this.method_25367()), this.method_46426(), this.method_46427(), this.method_25368(), this.method_25364()
         );
      }

      int i = 0;
      if (!this.field_22763) {
         i = 2;
      } else if (this.field_22762) {
         i = 1;
      }

      matrices.method_25290(
         this.textureResourceLocation,
         this.method_46426(),
         this.method_46427(),
         this.u,
         this.v + this.hoveredVOffset * i,
         this.field_22758,
         this.field_22759,
         this.textureWidth,
         this.textureHeight
      );
   }
}
