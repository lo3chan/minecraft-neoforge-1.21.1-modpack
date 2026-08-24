package net.caffeinemc.mods.sodium.client.platform.unix;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.SharedLibrary;

public class Libc {
   private static final SharedLibrary LIBRARY = APIUtil.apiCreateLibrary("libc.so.6");
   private static final long PFN_setenv = APIUtil.apiGetFunctionAddress(LIBRARY, "setenv");
   private static final long PFN_unsetenv = APIUtil.apiGetFunctionAddress(LIBRARY, "unsetenv");

   public static void setEnvironmentVariable(String name, @Nullable String value) {
      if (value != null) {
         setenv(name, value);
      } else {
         unsetenv(name);
      }
   }

   private static void setenv(String name, @NotNull String value) {
      MemoryStack stack = MemoryStack.stackPush();

      int result;
      try {
         result = JNI.callPPI(MemoryUtil.memAddress(stack.UTF8(name)), MemoryUtil.memAddress(stack.UTF8(value)), 1, PFN_setenv);
      } catch (Throwable var7) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (stack != null) {
         stack.close();
      }

      if (result != 0) {
         throw new RuntimeException("setenv() failed: %d".formatted(result));
      }
   }

   private static void unsetenv(@NotNull String name) {
      MemoryStack stack = MemoryStack.stackPush();

      int result;
      try {
         result = JNI.callPI(MemoryUtil.memAddress(stack.UTF8(name)), PFN_unsetenv);
      } catch (Throwable var6) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (stack != null) {
         stack.close();
      }

      if (result != 0) {
         throw new RuntimeException("unsetenv() failed: %d".formatted(result));
      }
   }
}
