package com.seibel.distanthorizons.core.util;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import java.util.ArrayList;

public class ListUtil {
   public static <T> ArrayList<T> createEmptyList(int size) {
      ArrayList<T> list = new ArrayList<>();

      for (int i = 0; i < size; i++) {
         list.add(null);
      }

      return list;
   }

   public static void clearAndSetSize(LongArrayList arrayList, int size) {
      arrayList.clear();
      arrayList.size(size);
   }

   public static void clearAndSetSize(ShortArrayList arrayList, int size) {
      arrayList.clear();
      arrayList.size(size);
   }

   public static void clearAndSetSize(ByteArrayList arrayList, int size) {
      arrayList.clear();
      arrayList.size(size);
   }

   public static void clearAndSetSize(CharArrayList arrayList, int size) {
      arrayList.clear();
      arrayList.size(size);
   }
}
