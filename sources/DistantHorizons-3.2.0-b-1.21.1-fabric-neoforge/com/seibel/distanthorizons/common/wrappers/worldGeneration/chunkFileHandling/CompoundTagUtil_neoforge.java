package com.seibel.distanthorizons.common.wrappers.worldGeneration.chunkFileHandling;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.jetbrains.annotations.Nullable;

public class CompoundTagUtil_neoforge {
   public static boolean getBoolean(CompoundTag tag, String key) {
      return tag.getBoolean(key);
   }

   public static byte getByte(CompoundTag tag, String key) {
      return tag.getByte(key);
   }

   public static short getShort(ListTag tag, int index) {
      return tag.getShort(index);
   }

   public static int getInt(CompoundTag tag, String key) {
      return tag.getInt(key);
   }

   public static long getLong(CompoundTag tag, String key) {
      return tag.getInt(key);
   }

   @Nullable
   public static String getString(CompoundTag tag, String key) {
      return tag.getString(key);
   }

   @Nullable
   public static byte[] getByteArray(CompoundTag tag, String key) {
      return tag.getByteArray(key);
   }

   @Nullable
   public static CompoundTag getCompoundTag(CompoundTag tag, String key) {
      return tag.getCompound(key);
   }

   @Nullable
   public static CompoundTag getCompoundTag(ListTag tag, int index) {
      return tag.getCompound(index);
   }

   @Nullable
   public static ListTag getListTag(CompoundTag tag, String key, int elementType) {
      return tag.getList(key, elementType);
   }

   @Nullable
   public static ListTag getListTag(ListTag tag, int index) {
      return tag.getList(index);
   }

   public static boolean contains(CompoundTag tag, String key, int index) {
      return tag.contains(key, index);
   }
}
