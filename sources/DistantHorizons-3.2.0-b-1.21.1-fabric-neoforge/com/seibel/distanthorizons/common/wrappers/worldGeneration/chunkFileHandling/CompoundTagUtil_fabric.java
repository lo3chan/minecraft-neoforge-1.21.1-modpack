package com.seibel.distanthorizons.common.wrappers.worldGeneration.chunkFileHandling;

import net.minecraft.class_2487;
import net.minecraft.class_2499;
import org.jetbrains.annotations.Nullable;

public class CompoundTagUtil_fabric {
   public static boolean getBoolean(class_2487 tag, String key) {
      return tag.method_10577(key);
   }

   public static byte getByte(class_2487 tag, String key) {
      return tag.method_10571(key);
   }

   public static short getShort(class_2499 tag, int index) {
      return tag.method_10609(index);
   }

   public static int getInt(class_2487 tag, String key) {
      return tag.method_10550(key);
   }

   public static long getLong(class_2487 tag, String key) {
      return tag.method_10550(key);
   }

   @Nullable
   public static String getString(class_2487 tag, String key) {
      return tag.method_10558(key);
   }

   @Nullable
   public static byte[] getByteArray(class_2487 tag, String key) {
      return tag.method_10547(key);
   }

   @Nullable
   public static class_2487 getCompoundTag(class_2487 tag, String key) {
      return tag.method_10562(key);
   }

   @Nullable
   public static class_2487 getCompoundTag(class_2499 tag, int index) {
      return tag.method_10602(index);
   }

   @Nullable
   public static class_2499 getListTag(class_2487 tag, String key, int elementType) {
      return tag.method_10554(key, elementType);
   }

   @Nullable
   public static class_2499 getListTag(class_2499 tag, int index) {
      return tag.method_10603(index);
   }

   public static boolean contains(class_2487 tag, String key, int index) {
      return tag.method_10573(key, index);
   }
}
