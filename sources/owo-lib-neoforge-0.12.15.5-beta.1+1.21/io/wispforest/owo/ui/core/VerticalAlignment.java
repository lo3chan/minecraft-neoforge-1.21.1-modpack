package io.wispforest.owo.ui.core;

import java.util.Locale;
import org.w3c.dom.Element;

public enum VerticalAlignment {
   TOP,
   CENTER,
   BOTTOM;

   public int align(int componentWidth, int span) {
      return switch (this) {
         case TOP -> 0;
         case CENTER -> span / 2 - componentWidth / 2;
         case BOTTOM -> span - componentWidth;
      };
   }

   public static VerticalAlignment parse(Element element) {
      return valueOf(element.getTextContent().strip().toUpperCase(Locale.ROOT));
   }
}
