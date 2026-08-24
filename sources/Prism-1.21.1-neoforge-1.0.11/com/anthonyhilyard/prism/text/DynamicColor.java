package com.anthonyhilyard.prism.text;

import com.anthonyhilyard.prism.events.client.RenderTickEvent;
import com.anthonyhilyard.prism.util.ColorUtil;
import com.anthonyhilyard.prism.util.IColor;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.DeltaTracker;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;

public final class DynamicColor extends TextColor implements IColor {
   private final List<IColor> values = Lists.newArrayList();
   private float duration;
   private int currentIndex;
   private float timer;

   public DynamicColor(IColor color) {
      this(color, color.getName());
   }

   public DynamicColor(IColor color, String name) {
      this(List.of(color), 0.0F, name);
   }

   public DynamicColor(List<IColor> values, float duration) {
      this(values, duration, null);
   }

   public DynamicColor(List<IColor> values, float duration, String name) {
      super(values.get(0).getIntValue(), name);
      this.values.addAll(values);
      this.duration = values.size() > 0 ? duration / values.size() : duration;
      if (this.isAnimated()) {
         RenderTickEvent.START.register(this::onRenderTick);
      }
   }

   public static DynamicColor fromRgb(int value) {
      return Integer.compareUnsigned(value, 16777215) >= 0
         ? fromARGB(value >> 24 & 0xFF, value >> 16 & 0xFF, value >> 8 & 0xFF, value >> 0 & 0xFF)
         : fromRGB(value >> 16 & 0xFF, value >> 8 & 0xFF, value >> 0 & 0xFF);
   }

   public static DynamicColor fromRGB(float red, float green, float blue) {
      return fromARGB(1.0F, red, green, blue);
   }

   public static DynamicColor fromRGB(int red, int green, int blue) {
      return fromARGB(255, red, green, blue);
   }

   public static DynamicColor fromARGB(float alpha, float red, float green, float blue) {
      return fromARGB((int)(alpha * 255.0F), (int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F));
   }

   public static DynamicColor fromARGB(int alpha, int red, int green, int blue) {
      return new DynamicColor(new IColor() {
         @Override
         public String getName() {
            return null;
         }

         @Override
         public int getIntValue() {
            return ColorUtil.combineARGB(alpha, red, green, blue);
         }

         @Override
         public boolean isAnimated() {
            return false;
         }
      }, null);
   }

   public static DynamicColor fromHSV(float hue, float saturation, float value) {
      return fromAHSV(1.0F, hue, saturation, value);
   }

   public static DynamicColor fromHSV(int hue, int saturation, int value) {
      return fromAHSV(255, hue, saturation, value);
   }

   public static DynamicColor fromAHSV(float alpha, float hue, float saturation, float value) {
      return fromAHSV((int)(alpha * 255.0F + 0.5F), (int)(hue * 360.0F + 0.5F), (int)(saturation * 255.0F + 0.5F), (int)(value * 255.0F + 0.5F));
   }

   public static DynamicColor fromAHSV(int alpha, int hue, int saturation, int value) {
      return new DynamicColor(new IColor() {
         @Override
         public String getName() {
            return null;
         }

         @Override
         public int getIntValue() {
            return ColorUtil.AHSVtoARGB(alpha, hue, saturation, value);
         }

         @Override
         public boolean isAnimated() {
            return false;
         }
      }, null);
   }

   public static DynamicColor fromColor(IColor color) {
      return new DynamicColor(color);
   }

   public int alpha() {
      return this.getIntValue() >> 24 & 0xFF;
   }

   public int red() {
      return this.getIntValue() >> 16 & 0xFF;
   }

   public int green() {
      return this.getIntValue() >> 8 & 0xFF;
   }

   public int blue() {
      return this.getIntValue() >> 0 & 0xFF;
   }

   public int hue() {
      return (int)(ColorUtil.RGBtoHSV(this.red(), this.green(), this.blue())[0] * 360.0F);
   }

   public int saturation() {
      return (int)(ColorUtil.RGBtoHSV(this.red(), this.green(), this.blue())[1] * 255.0F);
   }

   public int value() {
      return (int)(ColorUtil.RGBtoHSV(this.red(), this.green(), this.blue())[2] * 255.0F);
   }

   public void addColor(IColor color) {
      this.values.add(color);
   }

   public void clearColors() {
      this.values.clear();
      this.currentIndex = 0;
   }

   public void setDuration(float duration) {
      this.duration = Math.max(duration, 0.0F);
      if (this.isAnimated()) {
         RenderTickEvent.START.register(this::onRenderTick);
      }
   }

   @Override
   public boolean isAnimated() {
      return this.values.size() > 1 && this.duration > 0.0F;
   }

   public int getValue() {
      return this.getIntValue();
   }

   @Override
   public int getIntValue() {
      if (this.values.isEmpty()) {
         return 0;
      } else if (this.values.size() > 1) {
         int nextIndex = (this.currentIndex + 1) % this.values.size();
         int currentValue = this.values.get(this.currentIndex).getIntValue();
         int nextValue = this.values.get(nextIndex).getIntValue();
         int alpha = (int)Mth.lerp(this.timer / this.duration, currentValue >> 24 & 0xFF, nextValue >> 24 & 0xFF);
         int red = (int)Mth.lerp(this.timer / this.duration, currentValue >> 16 & 0xFF, nextValue >> 16 & 0xFF);
         int green = (int)Mth.lerp(this.timer / this.duration, currentValue >> 8 & 0xFF, nextValue >> 8 & 0xFF);
         int blue = (int)Mth.lerp(this.timer / this.duration, currentValue >> 0 & 0xFF, nextValue >> 0 & 0xFF);
         return ColorUtil.combineARGB(alpha, red, green, blue);
      } else {
         return this.values.get(0).getIntValue();
      }
   }

   @Override
   public String toString() {
      if (this.values.size() == 1) {
         return this.name != null ? this.name : String.format("#%08X", this.getValue());
      } else {
         StringBuilder result = new StringBuilder();
         result.append("([");

         for (IColor value : this.values) {
            result.append(value.getName() != null ? value.getName() : String.format("#%08X", value.getIntValue()));
            result.append(", ");
         }

         result.delete(result.length() - 2, result.length() - 1);
         result.append("], ");
         result.append(this.duration);
         result.append("s)");
         return result.toString();
      }
   }

   public void onRenderTick(DeltaTracker tracker) {
      this.timer = this.timer + tracker.getRealtimeDeltaTicks() / 50.0F;
      if (this.timer >= this.duration) {
         this.currentIndex = (this.currentIndex + 1) % this.values.size();
         this.timer = this.timer - this.duration;
      }
   }

   @Override
   public String getName() {
      return this.name;
   }
}
