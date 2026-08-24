package com.iafenvoy.jupiter.config.interfaces;

@FunctionalInterface
public interface ValueChangeCallback<T> {
   void onValueChange(T var1, boolean var2, boolean var3);
}
