package com.finndog.moogs_structures.utils;

public final class DebugFlags {
   private static boolean enabled;
   private static volatile boolean keepJigsawBlocks;

   private DebugFlags() {
   }

   public static boolean isEnabled() {
      return enabled;
   }

   public static boolean setEnabled(boolean value) {
      enabled = value;
      return enabled;
   }

   public static boolean toggle() {
      enabled = !enabled;
      return enabled;
   }

   public static boolean isKeepJigsawBlocks() {
      return keepJigsawBlocks;
   }

   public static boolean setKeepJigsawBlocks(boolean value) {
      keepJigsawBlocks = value;
      return keepJigsawBlocks;
   }

   public static boolean toggleKeepJigsawBlocks() {
      keepJigsawBlocks = !keepJigsawBlocks;
      return keepJigsawBlocks;
   }
}
