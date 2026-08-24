package dev.isxander.yacl3.api.utils;

public interface MutableDimension<T extends Number> extends Dimension<T> {
   MutableDimension<T> setX(T var1);

   MutableDimension<T> setY(T var1);

   MutableDimension<T> setWidth(T var1);

   MutableDimension<T> setHeight(T var1);

   MutableDimension<T> move(T var1, T var2);

   MutableDimension<T> expand(T var1, T var2);
}
