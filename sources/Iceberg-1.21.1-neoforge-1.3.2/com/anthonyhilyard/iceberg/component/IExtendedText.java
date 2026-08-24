package com.anthonyhilyard.iceberg.component;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public interface IExtendedText {
   void setAlignment(IExtendedText.TextAlignment var1);

   IExtendedText.TextAlignment getAlignment();

   default void setPadding(int padding) {
      this.setPadding(padding, padding, padding, padding);
   }

   default void setPadding(int left, int right) {
      this.setPadding(left, right, 0, 0);
   }

   void setPadding(int var1, int var2, int var3, int var4);

   int getLeftPadding();

   int getRightPadding();

   int getTopPadding();

   int getBottomPadding();

   public static class ExtendedTextDataStore {
      private static final Map<Object, IExtendedText.TextAlignment> alignmentMap = Collections.synchronizedMap(new WeakHashMap<>());
      private static final Map<Object, Integer> leftPaddingMap = Collections.synchronizedMap(new WeakHashMap<>());
      private static final Map<Object, Integer> rightPaddingMap = Collections.synchronizedMap(new WeakHashMap<>());

      public static void setAlignment(Object key, IExtendedText.TextAlignment alignment) {
         alignmentMap.put(key, alignment);
      }

      public static IExtendedText.TextAlignment get(Object key) {
         return alignmentMap.getOrDefault(key, IExtendedText.TextAlignment.LEFT);
      }

      public static void setPadding(Object key, int left, int right) {
         leftPaddingMap.put(key, left);
         rightPaddingMap.put(key, right);
      }

      public static int getLeftPadding(Object key) {
         return leftPaddingMap.getOrDefault(key, 0);
      }

      public static int getRightPadding(Object key) {
         return rightPaddingMap.getOrDefault(key, 0);
      }
   }

   public static enum TextAlignment {
      LEFT,
      CENTER,
      RIGHT;
   }
}
