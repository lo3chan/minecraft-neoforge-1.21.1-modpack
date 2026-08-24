package dev.isxander.yacl3.gui.utils;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.Util;
import net.minecraft.Util.OS;
import net.minecraft.client.Minecraft;

public final class KeyUtils {
   public static boolean isSelection(int input) {
      return input == 257 || input == 32 || input == 335;
   }

   public static boolean isConfirmation(int input) {
      return input == 257 || input == 335;
   }

   public static boolean isEscape(int input) {
      return input == 256;
   }

   public static boolean isLeft(int input) {
      return input == 263;
   }

   public static boolean isRight(int input) {
      return input == 262;
   }

   public static boolean isUp(int input) {
      return input == 265;
   }

   public static boolean isDown(int input) {
      return input == 264;
   }

   public static boolean isCycleFocus(int input) {
      return input == 258;
   }

   public static int getDigit(int input) {
      int i = input - 48;
      return i >= 0 && i <= 9 ? i : -1;
   }

   public static boolean hasAltDown(int modifiers) {
      return (modifiers & 4) != 0;
   }

   public static boolean hasShiftDown(int modifiers) {
      return (modifiers & 1) != 0;
   }

   public static boolean hasShiftDown() {
      long window = Minecraft.getInstance().getWindow().getWindow();
      return InputConstants.isKeyDown(window, 340) || InputConstants.isKeyDown(window, 344);
   }

   public static boolean hasControlDown(int modifiers) {
      return (modifiers & (Util.getPlatform() == OS.OSX ? 8 : 2)) != 0;
   }

   public static boolean hasControlDown() {
      long window = Minecraft.getInstance().getWindow().getWindow();
      return InputConstants.isKeyDown(window, 341) || InputConstants.isKeyDown(window, 345);
   }

   public static boolean isSelectAll(int input, int modifiers) {
      return input == 65 && hasControlDown(modifiers) && !hasShiftDown(modifiers) && !hasAltDown(modifiers);
   }

   public static boolean isCopy(int input, int modifiers) {
      return input == 67 && hasControlDown(modifiers) && !hasShiftDown(modifiers) && !hasAltDown(modifiers);
   }

   public static boolean isPaste(int input, int modifiers) {
      return input == 86 && hasControlDown(modifiers) && !hasShiftDown(modifiers) && !hasAltDown(modifiers);
   }

   public static boolean isCut(int input, int modifiers) {
      return input == 88 && hasControlDown(modifiers) && !hasShiftDown(modifiers) && !hasAltDown(modifiers);
   }

   private KeyUtils() {
   }
}
