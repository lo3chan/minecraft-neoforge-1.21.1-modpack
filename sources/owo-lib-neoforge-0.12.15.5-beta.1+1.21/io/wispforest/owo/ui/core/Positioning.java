package io.wispforest.owo.ui.core;

import io.wispforest.owo.Owo;
import io.wispforest.owo.ui.parsing.UIModelParsingException;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.util.Mth;
import org.w3c.dom.Element;

public class Positioning implements Animatable<Positioning> {
   private static final Positioning LAYOUT_POSITIONING = new Positioning(0, 0, Positioning.Type.LAYOUT);
   public final Positioning.Type type;
   public final int x;
   public final int y;

   private Positioning(int x, int y, Positioning.Type type) {
      this.type = type;
      this.x = x;
      this.y = y;
   }

   public Positioning withX(int x) {
      return new Positioning(x, this.y, this.type);
   }

   public Positioning withY(int y) {
      return new Positioning(this.x, y, this.type);
   }

   public boolean isRelative() {
      return this.type == Positioning.Type.RELATIVE || this.type == Positioning.Type.ACROSS;
   }

   public Positioning interpolate(Positioning next, float delta) {
      if (next.type != this.type) {
         Owo.LOGGER.warn("Cannot interpolate between positioning of type " + this.type + " and " + next.type);
         return this;
      } else {
         return new Positioning(Mth.lerpInt(delta, this.x, next.x), Mth.lerpInt(delta, this.y, next.y), this.type);
      }
   }

   public static Positioning absolute(int xPixels, int yPixels) {
      return new Positioning(xPixels, yPixels, Positioning.Type.ABSOLUTE);
   }

   public static Positioning relative(int xPercent, int yPercent) {
      return new Positioning(xPercent, yPercent, Positioning.Type.RELATIVE);
   }

   public static Positioning across(int xPercent, int yPercent) {
      return new Positioning(xPercent, yPercent, Positioning.Type.ACROSS);
   }

   public static Positioning layout() {
      return LAYOUT_POSITIONING;
   }

   public static Positioning parse(Element positioningElement) {
      String typeString = positioningElement.getAttribute("type");
      if (typeString.isBlank()) {
         throw new UIModelParsingException("Missing 'type' attribute on positioning declaration. Must be one of: relative, absolute, layout");
      } else {
         Positioning.Type type = Positioning.Type.valueOf(typeString.toUpperCase(Locale.ROOT));
         String values = positioningElement.getTextContent().strip();
         if (!values.matches("-?\\d+,-?\\d+")) {
            throw new UIModelParsingException("Invalid value in positioning declaration");
         } else {
            int x = Integer.parseInt(values.split(",")[0]);
            int y = Integer.parseInt(values.split(",")[1]);
            return new Positioning(x, y, type);
         }
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Positioning that = (Positioning)o;
         return this.x == that.x && this.y == that.y && this.type == that.type;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.type, this.x, this.y);
   }

   public static enum Type {
      RELATIVE,
      ACROSS,
      ABSOLUTE,
      LAYOUT;
   }
}
