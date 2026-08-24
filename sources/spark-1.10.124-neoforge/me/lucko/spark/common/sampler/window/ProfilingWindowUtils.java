package me.lucko.spark.common.sampler.window;

import java.util.function.IntPredicate;

public enum ProfilingWindowUtils {
   public static final int WINDOW_SIZE_SECONDS = 60;
   public static final int HISTORY_SIZE = Integer.getInteger("spark.continuousProfilingHistorySize", 60);

   public static int unixMillisToWindow(long time) {
      return (int)(time / 60000L);
   }

   public static int windowNow() {
      return unixMillisToWindow(System.currentTimeMillis());
   }

   public static IntPredicate keepHistoryBefore(int currentWindow) {
      return window -> window < currentWindow - HISTORY_SIZE;
   }
}
