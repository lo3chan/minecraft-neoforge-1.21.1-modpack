package com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList;

import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.logging.f3.F3Screen;
import com.seibel.distanthorizons.core.util.ThreadUtil;
import com.seibel.distanthorizons.core.util.objects.Pair;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomLoggingHelper;
import com.seibel.distanthorizons.coreapi.ModInfo;
import com.seibel.distanthorizons.coreapi.util.StringUtil;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhantomArrayListPool {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final int PHANTOM_REF_CHECK_TIME_IN_MS = 5000;
   private static final ThreadPoolExecutor RECYCLER_THREAD = ThreadUtil.makeSingleDaemonThreadPool("Phantom Array Recycler");
   private static final ArrayList<PhantomArrayListPool> POOL_LIST = new ArrayList<>();
   private static final boolean LOG_ARRAY_RECOVERY = ModInfo.IS_DEV_BUILD;
   private static boolean lowMemoryWarningLogged = false;
   public final String name;
   public final boolean logGarbageCollectedStacks;
   public final ConcurrentHashMap<Reference<? extends AbstractPhantomArrayList>, PhantomArrayListCheckout> phantomRefToCheckout = new ConcurrentHashMap<>();
   public final ReferenceQueue<AbstractPhantomArrayList> phantomRefQueue = new ReferenceQueue<>();
   private final ConcurrentLinkedQueue<SoftReference<PhantomArrayListCheckout>> pooledCheckoutsRefs = new ConcurrentLinkedQueue<>();
   private boolean clearLastPoolSizes = false;
   public final PhantomArrayListPoolStatTracker<ByteArrayList> bytePoolStatTracker = new PhantomArrayListPoolStatTracker<>(
      "byte[]", 1L, () -> new ByteArrayList(0)
   );
   public final PhantomArrayListPoolStatTracker<ShortArrayList> shortPoolStatTracker = new PhantomArrayListPoolStatTracker<>(
      "short[]", 2L, () -> new ShortArrayList(0)
   );
   public final PhantomArrayListPoolStatTracker<LongArrayList> longPoolStatTracker = new PhantomArrayListPoolStatTracker<>(
      "long[]", 8L, () -> new LongArrayList(0)
   );
   public final PhantomArrayListPoolStatTracker<CharArrayList> charPoolStatTracker = new PhantomArrayListPoolStatTracker<>(
      "char[]", 2L, () -> new CharArrayList(0)
   );
   public final PhantomArrayListPoolStatTracker<ByteBufferCheckoutWrapper> byteBufferPoolStatTracker = new PhantomArrayListPoolStatTracker<>(
      "ByteBuffer", 1L, () -> new ByteBufferCheckoutWrapper()
   );
   public final PhantomArrayListPoolStatTracker<?>[] poolStatTrackers = new PhantomArrayListPoolStatTracker[]{
      this.bytePoolStatTracker, this.shortPoolStatTracker, this.longPoolStatTracker, this.charPoolStatTracker, this.byteBufferPoolStatTracker
   };
   public static final String[] POOL_STAT_TYPE_NAMES = new String[]{"byte[]", "short[]", "long[]", "char[]", "ByteBuffer"};
   public static final int TYPE_COUNT = POOL_STAT_TYPE_NAMES.length;

   public PhantomArrayListPool(String name) {
      this(name, false);
   }

   public PhantomArrayListPool(String name, boolean logGarbageCollectedStacks) {
      POOL_LIST.add(this);
      this.name = name;
      this.logGarbageCollectedStacks = logGarbageCollectedStacks;
   }

   public PhantomArrayListCheckout checkoutByteArrays(int count) {
      return this.checkoutArrays(count, 0, 0, 0, 0);
   }

   public PhantomArrayListCheckout checkoutShortArrays(int count) {
      return this.checkoutArrays(0, count, 0, 0, 0);
   }

   public PhantomArrayListCheckout checkoutLongArrays(int count) {
      return this.checkoutArrays(0, 0, count, 0, 0);
   }

   public PhantomArrayListCheckout checkoutCharArrays(int count) {
      return this.checkoutArrays(0, 0, 0, count, 0);
   }

   public PhantomArrayListCheckout checkoutByteBuffers(int count) {
      return this.checkoutArrays(0, 0, 0, 0, count);
   }

   public PhantomArrayListCheckout checkoutArrays(int byteArrayCount, int shortArrayCount, int longArrayCount, int charArrayCount, int byteBufferCount) {
      PhantomArrayListCheckout checkout = null;

      while (checkout == null) {
         SoftReference<PhantomArrayListCheckout> checkoutRef = this.pooledCheckoutsRefs.poll();
         if (checkoutRef == null) {
            checkout = new PhantomArrayListCheckout(this);
            checkout.onCheckout();
         } else {
            checkout = checkoutRef.get();
            if (checkout != null) {
               checkout.onCheckout();
            } else {
               if (!lowMemoryWarningLogged) {
                  long freeMemoryBytes = Runtime.getRuntime().freeMemory();
                  long expectedFreeMemoryBytes = 134217728L;
                  if (freeMemoryBytes < expectedFreeMemoryBytes) {
                     lowMemoryWarningLogged = true;
                     String message = "§6Distant Horizons: Insufficient memory detected.§r\nThis may cause stuttering or crashing. \nPotential causes: \n1. your allocated memory isn't high enough \n2. your DH CPU preset is too high \n3. your DH quality preset is too high \n4. you have other memory hungry mod(s)";
                     LOGGER.warn(message);
                     if (Config.Common.Logging.Warning.showPoolInsufficientMemoryWarning.get()) {
                        ClientApi.INSTANCE.showChatMessageNextFrame(message);
                     }
                  }
               }

               this.clearLastPoolSizes = true;
            }
         }
      }

      this.bytePoolStatTracker.fillCheckout(byteArrayCount, checkout::getByteArrayCount, checkout::addByteArrayList);
      this.shortPoolStatTracker.fillCheckout(shortArrayCount, checkout::getShortArrayCount, checkout::addShortArrayList);
      this.longPoolStatTracker.fillCheckout(longArrayCount, checkout::getLongArrayCount, checkout::addLongArrayList);
      this.charPoolStatTracker.fillCheckout(charArrayCount, checkout::getCharArrayCount, checkout::addCharArrayList);
      this.byteBufferPoolStatTracker.fillCheckout(byteBufferCount, checkout::getByteBufferWrapperCount, checkout::addByteBufferWrapper);
      return checkout;
   }

   private static void runPhantomReferenceCleanupLoop() {
      while (true) {
         ArrayList<Pair<String, AtomicInteger>> allocationStackTraceCountPairList = new ArrayList<>();

         try {
            try {
               Thread.sleep(5000L);
            } catch (InterruptedException var10) {
            }

            for (int poolIndex = 0; poolIndex < POOL_LIST.size(); poolIndex++) {
               PhantomArrayListPool pool = POOL_LIST.get(poolIndex);
               int returnedByteArrayCount = 0;
               int returnedShortArrayCount = 0;
               int returnedLongArrayCount = 0;
               int returnedCharArrayCount = 0;
               int checkoutCount = 0;
               allocationStackTraceCountPairList.clear();

               for (Reference<? extends AbstractPhantomArrayList> phantomRef = pool.phantomRefQueue.poll();
                  phantomRef != null;
                  phantomRef = pool.phantomRefQueue.poll()
               ) {
                  PhantomArrayListCheckout checkout = pool.phantomRefToCheckout.remove(phantomRef);
                  if (checkout != null) {
                     returnedByteArrayCount += checkout.getByteArrayCount();
                     returnedShortArrayCount += checkout.getShortArrayCount();
                     returnedLongArrayCount += checkout.getLongArrayCount();
                     returnedCharArrayCount += checkout.getCharArrayCount();
                     checkoutCount++;
                     pool.returnCheckout(checkout);
                     if (pool.logGarbageCollectedStacks && checkout.allocationStackTrace != null) {
                        PhantomLoggingHelper.putAndIncrementTrackingString(checkout.allocationStackTrace, allocationStackTraceCountPairList);
                     }
                  } else {
                     LOGGER.warn(
                        "Pool: [" + pool.name + "]. Unable to find checkout for phantom reference [" + phantomRef + "], arrays will need to be recreated."
                     );
                  }
               }

               if ((LOG_ARRAY_RECOVERY || pool.logGarbageCollectedStacks)
                  && (
                     checkoutCount != 0
                        || returnedByteArrayCount != 0
                        || returnedShortArrayCount != 0
                        || returnedLongArrayCount != 0
                        || returnedCharArrayCount != 0
                  )) {
                  LOGGER.warn(
                     "Pool: ["
                        + pool.name
                        + "] phantom recovery. Returned checkouts:["
                        + F3Screen.NUMBER_FORMAT.format((long)checkoutCount)
                        + "], byte:["
                        + F3Screen.NUMBER_FORMAT.format((long)returnedByteArrayCount)
                        + "], short:["
                        + F3Screen.NUMBER_FORMAT.format((long)returnedShortArrayCount)
                        + "], long:["
                        + F3Screen.NUMBER_FORMAT.format((long)returnedLongArrayCount)
                        + "],char:["
                        + F3Screen.NUMBER_FORMAT.format((long)returnedCharArrayCount)
                        + "]."
                  );
                  if (pool.logGarbageCollectedStacks) {
                     PhantomLoggingHelper.LogAllocationStackTracePairCounts(LOGGER, allocationStackTraceCountPairList);
                  }
               }

               pool.recalculateSizeForDebugging();
            }
         } catch (Exception var11) {
            LOGGER.error("Unexpected error in phantom pool return thread, error: [" + var11.getMessage() + "].", var11);
         }
      }
   }

   public void returnParentPhantomRef(@NotNull PhantomReference<AbstractPhantomArrayList> parentRef) {
      try {
         parentRef.clear();
         PhantomArrayListCheckout checkout = this.phantomRefToCheckout.remove(parentRef);
         this.returnCheckout(checkout);
      } catch (Exception var3) {
         LOGGER.error("Unable to close Phantom Array, error: [" + var3.getMessage() + "].", var3);
      }
   }

   public void returnCheckout(@Nullable PhantomArrayListCheckout checkout) {
      if (checkout == null) {
         throw new IllegalArgumentException("Null phantom checkout, object is being closed multiple times.");
      } else {
         SoftReference<PhantomArrayListCheckout> checkoutRef = checkout.ownerSoftReference;
         this.pooledCheckoutsRefs.add(checkoutRef);
      }
   }

   public static void addDebugMenuStringsToListForCombinedPools(List<String> messageList) {
      int[] totalCounts = new int[TYPE_COUNT];
      int[] pooledCounts = new int[TYPE_COUNT];
      long[] poolByteSize = new long[TYPE_COUNT];

      for (int poolIndex = 0; poolIndex < POOL_LIST.size(); poolIndex++) {
         PhantomArrayListPool pool = POOL_LIST.get(poolIndex);
         PhantomArrayListPoolStatTracker<?>[] statTrackers = pool.poolStatTrackers;

         for (int typeIndex = 0; typeIndex < statTrackers.length; typeIndex++) {
            PhantomArrayListPoolStatTracker<?> statTracker = statTrackers[typeIndex];
            totalCounts[typeIndex] += statTracker.totalArrayCountRef.get();
            pooledCounts[typeIndex] += statTracker.lastPoolCount;
            poolByteSize[typeIndex] += statTracker.lastPoolSizeInBytes;
         }
      }

      addDebugMenuStringsToList(messageList, "Combined", totalCounts, pooledCounts, poolByteSize);
   }

   public static void addDebugMenuStringsToListForSeparatePools(List<String> messageList) {
      for (int i = 0; i < POOL_LIST.size(); i++) {
         PhantomArrayListPool pool = POOL_LIST.get(i);
         pool.addDebugMenuStringsToList(messageList);
      }
   }

   private void addDebugMenuStringsToList(List<String> messageList) {
      PhantomArrayListPoolStatTracker<?>[] statTrackers = this.poolStatTrackers;
      int[] totalCounts = new int[statTrackers.length];
      int[] pooledCounts = new int[statTrackers.length];
      long[] poolSizesBytes = new long[statTrackers.length];

      for (int i = 0; i < statTrackers.length; i++) {
         PhantomArrayListPoolStatTracker<?> statTracker = statTrackers[i];
         totalCounts[i] = statTracker.totalArrayCountRef.get();
         pooledCounts[i] = statTracker.lastPoolCount;
         poolSizesBytes[i] = statTracker.lastPoolSizeInBytes;
      }

      addDebugMenuStringsToList(messageList, this.name, totalCounts, pooledCounts, poolSizesBytes);
   }

   private static void addDebugMenuStringsToList(List<String> messageList, String name, int[] totalCounts, int[] pooledCounts, long[] poolSizesBytes) {
      String a = "§b";
      String y = "§e";
      String o = "§6";
      String cf = "§r";
      long totalByteSize = 0L;

      for (int i = 0; i < POOL_STAT_TYPE_NAMES.length; i++) {
         totalByteSize += poolSizesBytes[i];
      }

      String totalByteSizeStr = StringUtil.convertBytesToHumanReadable(totalByteSize);
      messageList.add(a + name + cf + " - Pools");
      int nonEmptyPools = 0;

      for (int i = 0; i < POOL_STAT_TYPE_NAMES.length; i++) {
         if (totalCounts[i] != 0) {
            String pooledFormatted = F3Screen.NUMBER_FORMAT.format((long)pooledCounts[i]);
            String totalFormatted = F3Screen.NUMBER_FORMAT.format((long)totalCounts[i]);
            nonEmptyPools++;
            String sizeStr = "";
            if (poolSizesBytes[i] != -1L) {
               sizeStr = StringUtil.convertBytesToHumanReadable(poolSizesBytes[i]);
            }

            messageList.add(POOL_STAT_TYPE_NAMES[i] + ": " + pooledFormatted + "/" + totalFormatted + " " + y + sizeStr + cf);
         }
      }

      if (nonEmptyPools > 1) {
         messageList.add("Total: " + o + totalByteSizeStr + cf);
      }
   }

   public void recalculateSizeForDebugging() {
      for (SoftReference<PhantomArrayListCheckout> pooledCheckoutRef : this.pooledCheckoutsRefs) {
         PhantomArrayListCheckout pooledCheckout = pooledCheckoutRef.get();
         if (pooledCheckout != null) {
            this.bytePoolStatTracker.debugAddPoolByteSize(pooledCheckout.getAllByteArrays());
            this.shortPoolStatTracker.debugAddPoolByteSize(pooledCheckout.getAllShortArrays());
            this.longPoolStatTracker.debugAddPoolByteSize(pooledCheckout.getAllLongArrays());
            this.charPoolStatTracker.debugAddPoolByteSize(pooledCheckout.getAllCharArrays());
            this.byteBufferPoolStatTracker.debugAddPoolByteSize(pooledCheckout.getAllByteBufferWrappers());
         }
      }

      boolean clearLastPoolSize = this.clearLastPoolSizes;
      if (clearLastPoolSize) {
         this.clearLastPoolSizes = false;
      }

      this.bytePoolStatTracker.updateDebugValues(clearLastPoolSize);
      this.shortPoolStatTracker.updateDebugValues(clearLastPoolSize);
      this.longPoolStatTracker.updateDebugValues(clearLastPoolSize);
      this.charPoolStatTracker.updateDebugValues(clearLastPoolSize);
      this.byteBufferPoolStatTracker.updateDebugValues(clearLastPoolSize);
   }

   static {
      RECYCLER_THREAD.execute(() -> runPhantomReferenceCleanupLoop());
   }
}
