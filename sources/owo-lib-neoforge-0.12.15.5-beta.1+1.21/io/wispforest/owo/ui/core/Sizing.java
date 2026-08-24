package io.wispforest.owo.ui.core;

import io.wispforest.owo.ui.parsing.UIModelParsingException;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.Mth;
import org.w3c.dom.Element;

public class Sizing implements Animatable<Sizing> {
   private static final Sizing CONTENT_SIZING = new Sizing(0, Sizing.Method.CONTENT);
   public final Sizing.Method method;
   public final int value;

   private Sizing(int value, Sizing.Method method) {
      this.method = method;
      this.value = value;
   }

   public int inflate(int space, Function<Sizing, Integer> contentSizeFunction) {
      return switch (this.method) {
         case FIXED -> this.value;
         case CONTENT -> contentSizeFunction.apply(this) + this.value * 2;
         case FILL, EXPAND -> Math.round(this.value / 100.0F * space);
      };
   }

   public static Sizing fixed(int value) {
      return new Sizing(value, Sizing.Method.FIXED);
   }

   public static Sizing content() {
      return CONTENT_SIZING;
   }

   public static Sizing content(int padding) {
      return new Sizing(padding, Sizing.Method.CONTENT);
   }

   public static Sizing fill() {
      return fill(100);
   }

   public static Sizing fill(int percent) {
      return new Sizing(percent, Sizing.Method.FILL);
   }

   public static Sizing expand() {
      return expand(100);
   }

   public static Sizing expand(int percent) {
      return new Sizing(percent, Sizing.Method.EXPAND);
   }

   public boolean isContent() {
      return this.method == Sizing.Method.CONTENT;
   }

   public boolean isExpand() {
      return this.method == Sizing.Method.EXPAND;
   }

   public float contentFactor() {
      return this.isContent() ? 1.0F : 0.0F;
   }

   public Sizing interpolate(Sizing next, float delta) {
      return (Sizing)(next.method != this.method
         ? new Sizing.MergedSizing(this, next, delta)
         : new Sizing(Mth.lerpInt(delta, this.value, next.value), this.method));
   }

   public static Sizing parse(Element sizingElement) {
      String methodString = sizingElement.getAttribute("method");
      if (methodString.isBlank()) {
         throw new UIModelParsingException("Missing 'method' attribute on sizing declaration. Must be one of: fixed, content, fill");
      } else {
         Sizing.Method method = Sizing.Method.valueOf(methodString.toUpperCase(Locale.ROOT));
         String value = sizingElement.getTextContent().strip();
         if (method == Sizing.Method.CONTENT) {
            if (!value.matches("(-?\\d+)?")) {
               throw new UIModelParsingException("Invalid value in sizing declaration");
            } else {
               return new Sizing(value.isEmpty() ? 0 : Integer.parseInt(value), method);
            }
         } else if (!value.matches("-?\\d+")) {
            throw new UIModelParsingException("Invalid value in sizing declaration");
         } else {
            return new Sizing(Integer.parseInt(value), method);
         }
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Sizing sizing = (Sizing)o;
         return this.value == sizing.value && this.method == sizing.method;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.method, this.value);
   }

   private static final class MergedSizing extends Sizing {
      private final Sizing first;
      private final Sizing second;
      private final float delta;

      private MergedSizing(Sizing first, Sizing second, float delta) {
         super(first.value, first.method);
         this.first = first;
         this.second = second;
         this.delta = delta;
      }

      @Override
      public int inflate(int space, Function<Sizing, Integer> contentSizeFunction) {
         return Mth.lerpInt(this.delta, this.first.inflate(space, contentSizeFunction), this.second.inflate(space, contentSizeFunction));
      }

      @Override
      public Sizing interpolate(Sizing next, float delta) {
         return this.first.interpolate(next, delta);
      }

      @Override
      public boolean isContent() {
         return this.first.isContent() || this.second.isContent();
      }

      @Override
      public float contentFactor() {
         if (this.first.isContent() && this.second.isContent()) {
            return super.contentFactor();
         } else if (this.first.isContent()) {
            return 1.0F - this.delta;
         } else {
            return this.second.isContent() ? this.delta : 0.0F;
         }
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o == null || this.getClass() != o.getClass()) {
            return false;
         } else if (!super.equals(o)) {
            return false;
         } else {
            Sizing.MergedSizing that = (Sizing.MergedSizing)o;
            return Float.compare(this.delta, that.delta) == 0 && Objects.equals(this.first, that.first) && Objects.equals(this.second, that.second);
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(super.hashCode(), this.first, this.second, this.delta);
      }
   }

   public static enum Method {
      FIXED,
      CONTENT,
      FILL,
      EXPAND;
   }

   public static class Random {
      private static final java.util.Random SIZING_RANDOM = new java.util.Random();

      public static Sizing fill(int min, int max) {
         return Sizing.fill(SIZING_RANDOM.nextInt(min, max));
      }

      public static Sizing fill(int max) {
         return Sizing.fill(SIZING_RANDOM.nextInt(0, max));
      }

      public static Sizing fill() {
         return Sizing.fill(SIZING_RANDOM.nextInt(0, 100));
      }

      public static Sizing expand(int min, int max) {
         return Sizing.expand(SIZING_RANDOM.nextInt(min, max));
      }

      public static Sizing expand(int max) {
         return Sizing.expand(SIZING_RANDOM.nextInt(0, max));
      }

      public static Sizing expand() {
         return Sizing.expand(SIZING_RANDOM.nextInt(0, 100));
      }

      public static Sizing fixed(int min, int max) {
         return Sizing.fixed(SIZING_RANDOM.nextInt(min, max));
      }

      public static Sizing fixed(int max) {
         return Sizing.fixed(SIZING_RANDOM.nextInt(0, max));
      }

      public static Sizing fixed() {
         return Sizing.fixed(SIZING_RANDOM.nextInt(0, 100));
      }

      public static Sizing content(int min, int max) {
         return Sizing.content(SIZING_RANDOM.nextInt(min, max));
      }

      public static Sizing content(int max) {
         return Sizing.content(SIZING_RANDOM.nextInt(0, max));
      }

      public static Sizing content() {
         return Sizing.content(SIZING_RANDOM.nextInt(0, 100));
      }

      public static Sizing random(int min, int max) {
         return switch (SIZING_RANDOM.nextInt(0, 4)) {
            case 0 -> fill(min, max);
            case 1 -> expand(min, max);
            case 2 -> fixed(min, max);
            case 3 -> content(min, max);
            default -> throw new IllegalStateException("Unexpected value: " + SIZING_RANDOM.nextInt(0, 4));
         };
      }

      public static Sizing random(int max) {
         return random(0, max);
      }

      public static Sizing random() {
         return random(0, 100);
      }

      public static Sizing noContent(int min, int max) {
         return switch (SIZING_RANDOM.nextInt(0, 3)) {
            case 0 -> fill(min, max);
            case 1 -> expand(min, max);
            case 2 -> fixed(min, max);
            default -> throw new IllegalStateException("Unexpected value: " + SIZING_RANDOM.nextInt(0, 3));
         };
      }

      public static Sizing noContent(int max) {
         return noContent(0, max);
      }

      public static Sizing noContent() {
         return noContent(0, 100);
      }
   }
}
