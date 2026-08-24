package com.seibel.distanthorizons.core.util;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.threading.DhThreadFactory;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final String THREAD_NAME_PREFIX = "DH-";

   public static ThreadPoolExecutor makeThreadPool(int poolSize, String name, int priority, boolean isDaemon) {
      return new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new DhThreadFactory(name, priority, isDaemon));
   }

   public static ThreadPoolExecutor makeThreadPool(int poolSize, Class<?> clazz, int priority) {
      return makeThreadPool(poolSize, clazz.getSimpleName(), priority, false);
   }

   public static ThreadPoolExecutor makeThreadPool(int poolSize, String name) {
      return makeThreadPool(poolSize, name, 5, false);
   }

   public static ThreadPoolExecutor makeThreadPool(int poolSize, Class<?> clazz) {
      return makeThreadPool(poolSize, clazz.getSimpleName(), 5, false);
   }

   public static ThreadPoolExecutor makeSingleThreadPool(String name, int priority) {
      return makeThreadPool(1, name, priority, false);
   }

   public static ThreadPoolExecutor makeSingleThreadPool(Class<?> clazz, int priority) {
      return makeThreadPool(1, clazz.getSimpleName(), priority, false);
   }

   public static ThreadPoolExecutor makeSingleThreadPool(String name) {
      return makeThreadPool(1, name, 5, false);
   }

   public static ThreadPoolExecutor makeSingleThreadPool(Class<?> clazz) {
      return makeThreadPool(1, clazz.getSimpleName(), 5, false);
   }

   public static ThreadPoolExecutor makeSingleDaemonThreadPool(String name) {
      return makeThreadPool(1, name, 5, true);
   }
}
