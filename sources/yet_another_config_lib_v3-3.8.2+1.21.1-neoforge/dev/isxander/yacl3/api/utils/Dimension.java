package dev.isxander.yacl3.api.utils;

import dev.isxander.yacl3.impl.utils.DimensionIntegerImpl;

public interface Dimension<T extends Number> {
   T x();

   T y();

   T width();

   T height();

   T xLimit();

   T yLimit();

   T centerX();

   T centerY();

   boolean isPointInside(T var1, T var2);

   MutableDimension<T> clone();

   Dimension<T> withX(T var1);

   Dimension<T> withY(T var1);

   Dimension<T> withWidth(T var1);

   Dimension<T> withHeight(T var1);

   Dimension<T> moved(T var1, T var2);

   Dimension<T> expanded(T var1, T var2);

   static MutableDimension<Integer> ofInt(int x, int y, int width, int height) {
      return new DimensionIntegerImpl(x, y, width, height);
   }
}
