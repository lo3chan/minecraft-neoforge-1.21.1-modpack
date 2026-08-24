package com.seibel.distanthorizons.core.util.threading;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.concurrent.ThreadFactory;
import org.jetbrains.annotations.NotNull;

public class DhThreadFactory implements ThreadFactory {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public final String threadName;
   public final int priority;
   public final boolean isDaemon;
   private int threadCount = 0;
   private final LinkedList<WeakReference<Thread>> threads = new LinkedList<>();

   public DhThreadFactory(String newThreadName, int priority, boolean isDaemon) {
      if (priority >= 1 && priority <= 10) {
         this.threadName = "DH-" + newThreadName + " Thread";
         this.priority = priority;
         this.isDaemon = isDaemon;
      } else {
         throw new IllegalArgumentException("Thread priority [" + priority + "] out of bounds. Priority should be between [" + 1 + "-" + 10 + "]!");
      }
   }

   @Override
   public Thread newThread(@NotNull Runnable runnable) {
      Thread thread = new Thread(runnable, this.threadName + "[" + this.threadCount++ + "]");
      thread.setPriority(this.priority);
      thread.setDaemon(this.isDaemon);
      this.threads.add(new WeakReference<>(thread));
      return thread;
   }

   private static String StackTraceToString(StackTraceElement[] stackTraceArray) {
      StringBuilder str = new StringBuilder();
      str.append(stackTraceArray[0]);
      str.append('\n');

      for (int i = 1; i < stackTraceArray.length; i++) {
         str.append("  at ");
         str.append(stackTraceArray[i]);
         str.append('\n');
      }

      return str.toString();
   }

   public void dumpAllThreadStacks() {
      for (WeakReference<Thread> threadRef : this.threads) {
         Thread thread = threadRef.get();
         if (thread != null) {
            StackTraceElement[] stacks = thread.getStackTrace();
            if (stacks.length != 0) {
               LOGGER.info("===========================================\nThread: " + thread.getName() + "\n" + StackTraceToString(stacks));
            }
         }
      }

      this.threads.removeIf(weakRef -> weakRef.get() == null);
   }
}
