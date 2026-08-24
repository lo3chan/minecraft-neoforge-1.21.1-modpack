package com.seibel.distanthorizons.core.util.objects.pooling;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.logging.f3.F3Screen;
import com.seibel.distanthorizons.core.util.ThreadUtil;
import com.seibel.distanthorizons.core.util.objects.Pair;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class PhantomLoggingHelper {
   public static void putAndIncrementTrackingString(String key, ArrayList<Pair<String, AtomicInteger>> allocationStackTraceCountPairList) {
      boolean pairFound = false;

      for (int i = 0; i < allocationStackTraceCountPairList.size(); i++) {
         Pair<String, AtomicInteger> possiblePair = allocationStackTraceCountPairList.get(i);
         if (possiblePair.first.equals(key)) {
            possiblePair.second.getAndIncrement();
            pairFound = true;
            break;
         }
      }

      if (!pairFound) {
         allocationStackTraceCountPairList.add(new Pair<>(key, new AtomicInteger(1)));
      }
   }

   public static void LogAllocationStackTracePairCounts(DhLogger logger, ArrayList<Pair<String, AtomicInteger>> allocationStackTraceCountPairList) {
      allocationStackTraceCountPairList.sort((a, b) -> Integer.compare(b.second.get(), a.second.get()));
      StringBuilder stringBuilder = new StringBuilder();

      for (int j = 0; j < allocationStackTraceCountPairList.size(); j++) {
         int count = ((AtomicInteger)allocationStackTraceCountPairList.get(j).second).get();
         String stack = (String)allocationStackTraceCountPairList.get(j).first;
         stringBuilder.append(count).append(". ").append(stack).append("\n");
      }

      logger.warn("Stacks: [" + allocationStackTraceCountPairList.size() + "]\n" + stringBuilder.toString());
   }

   public static class BasicPhantomReference implements AutoCloseable {
      private static final DhLogger LOGGER = new DhLoggerBuilder().build();
      private static final boolean LOG_PHANTOM_RECOVERY = true;
      private static final boolean LOG_PHANTOM_ALLOCATION_STACKS = true;
      private static final int PHANTOM_REF_CHECK_TIME_IN_MS = 5000;
      private static final ReferenceQueue<PhantomLoggingHelper.BasicPhantomReference> PHANTOM_REFERENCE_QUEUE = new ReferenceQueue<>();
      private static final ConcurrentHashMap<PhantomReference<? extends PhantomLoggingHelper.BasicPhantomReference>, Class<?>> PHANTOM_TO_PARENT_CLASS = new ConcurrentHashMap<>();
      private static final ThreadPoolExecutor CLEANUP_THREAD = ThreadUtil.makeSingleDaemonThreadPool("BasicPhantom Cleanup");
      private final Class<?> parentClass;
      private final PhantomReference<? extends PhantomLoggingHelper.BasicPhantomReference> phantomReference;

      public BasicPhantomReference(Class<?> parentClass) {
         this.parentClass = parentClass;
         this.phantomReference = new PhantomReference<>(this, PHANTOM_REFERENCE_QUEUE);
         PHANTOM_TO_PARENT_CLASS.put(this.phantomReference, this.parentClass);
      }

      @Override
      public void close() {
         this.phantomReference.clear();
         PHANTOM_TO_PARENT_CLASS.remove(this.phantomReference);
      }

      private static void runPhantomReferenceCleanupLoop() {
         ArrayList<Pair<String, AtomicInteger>> allocationStackTraceCountPairList = new ArrayList<>();
         ArrayList<Pair<String, AtomicInteger>> parentClassNameCountPairList = new ArrayList<>();

         while (true) {
            allocationStackTraceCountPairList.clear();
            parentClassNameCountPairList.clear();

            try {
               try {
                  Thread.sleep(5000L);
               } catch (InterruptedException var6) {
               }

               int collectedCount = 0;

               for (Reference<? extends PhantomLoggingHelper.BasicPhantomReference> phantomRef = PHANTOM_REFERENCE_QUEUE.poll();
                  phantomRef != null;
                  phantomRef = PHANTOM_REFERENCE_QUEUE.poll()
               ) {
                  Class<?> parentClass = PHANTOM_TO_PARENT_CLASS.remove(phantomRef);
                  String parentClassName = "NULL";
                  if (parentClass != null) {
                     parentClassName = parentClass.getSimpleName();
                  }

                  PhantomLoggingHelper.putAndIncrementTrackingString(parentClassName, parentClassNameCountPairList);
                  collectedCount++;
               }

               if (collectedCount != 0) {
                  LOGGER.warn("Phantoms collected: [" + F3Screen.NUMBER_FORMAT.format((long)collectedCount) + "].");
                  PhantomLoggingHelper.LogAllocationStackTracePairCounts(LOGGER, parentClassNameCountPairList);
               }
            } catch (Exception var7) {
               LOGGER.error("Unexpected error in buffer cleanup thread: [" + var7.getMessage() + "].", var7);
            }
         }
      }

      static {
         CLEANUP_THREAD.execute(() -> runPhantomReferenceCleanupLoop());
      }
   }
}
