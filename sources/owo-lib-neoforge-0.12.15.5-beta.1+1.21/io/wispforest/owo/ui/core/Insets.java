package io.wispforest.owo.ui.core;

import io.wispforest.owo.ui.parsing.UIModelParsingException;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.w3c.dom.Element;

public record Insets(int top, int bottom, int left, int right) implements Animatable<Insets> {
   private static final Insets NONE = new Insets(0, 0, 0, 0);

   @Deprecated(
      forRemoval = true
   )
   @Internal
   public Insets(int top, int bottom, int left, int right) {
      this.top = top;
      this.bottom = bottom;
      this.left = left;
      this.right = right;
   }

   public Insets inverted() {
      return new Insets(-this.top, -this.bottom, -this.left, -this.right);
   }

   public Insets add(int top, int bottom, int left, int right) {
      return new Insets(this.top + top, this.bottom + bottom, this.left + left, this.right + right);
   }

   public Insets withTop(int top) {
      return new Insets(top, this.bottom, this.left, this.right);
   }

   public Insets withBottom(int bottom) {
      return new Insets(this.top, bottom, this.left, this.right);
   }

   public Insets withLeft(int left) {
      return new Insets(this.top, this.bottom, left, this.right);
   }

   public Insets withRight(int right) {
      return new Insets(this.top, this.bottom, this.left, right);
   }

   public int horizontal() {
      return this.left + this.right;
   }

   public int vertical() {
      return this.top + this.bottom;
   }

   public Insets interpolate(Insets next, float delta) {
      return new Insets(
         Mth.lerpInt(delta, this.top, next.top),
         Mth.lerpInt(delta, this.bottom, next.bottom),
         Mth.lerpInt(delta, this.left, next.left),
         Mth.lerpInt(delta, this.right, next.right)
      );
   }

   public static Insets both(int horizontal, int vertical) {
      return new Insets(vertical, vertical, horizontal, horizontal);
   }

   public static Insets top(int top) {
      return new Insets(top, 0, 0, 0);
   }

   public static Insets bottom(int bottom) {
      return new Insets(0, bottom, 0, 0);
   }

   public static Insets left(int left) {
      return new Insets(0, 0, left, 0);
   }

   public static Insets right(int right) {
      return new Insets(0, 0, 0, right);
   }

   public static Insets of(int top, int bottom, int left, int right) {
      return new Insets(top, bottom, left, right);
   }

   public static Insets of(int inset) {
      return new Insets(inset, inset, inset, inset);
   }

   public static Insets vertical(int inset) {
      return new Insets(inset, inset, 0, 0);
   }

   public static Insets horizontal(int inset) {
      return new Insets(0, 0, inset, inset);
   }

   public static Insets none() {
      return NONE;
   }

   public static Insets parse(Element insetsElement) {
      int top = 0;
      int bottom = 0;
      int left = 0;
      int right = 0;

      for (Element node : UIParsing.allChildrenOfType(insetsElement, (short)1)) {
         try {
            int value = Integer.parseInt(node.getTextContent().strip());
            String var8 = node.getNodeName();
            switch (var8) {
               case "top":
                  top = value;
                  break;
               case "bottom":
                  bottom = value;
                  break;
               case "left":
                  left = value;
                  break;
               case "right":
                  right = value;
                  break;
               case "all":
                  bottom = value;
                  top = value;
                  left = value;
                  right = value;
                  break;
               case "vertical":
                  bottom = value;
                  top = value;
                  break;
               case "horizontal":
                  right = value;
                  left = value;
            }
         } catch (NumberFormatException var10) {
            throw new UIModelParsingException("Non-int value in inset declaration");
         }
      }

      return of(top, bottom, left, right);
   }
}
