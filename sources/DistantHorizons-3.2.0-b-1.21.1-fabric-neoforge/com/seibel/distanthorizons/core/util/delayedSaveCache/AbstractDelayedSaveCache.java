package com.seibel.distanthorizons.core.util.delayedSaveCache;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.KeyedLockContainer;
import com.seibel.distanthorizons.core.util.ThreadUtil;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractDelayedSaveCache<TSaveObj, TSaveContainer extends AbstractSaveObjContainer<TSaveObj>> implements AutoCloseable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().name(AbstractDelayedSaveCache.class.getSimpleName()).build();
   private static final ThreadPoolExecutor BACKGROUND_CLEAN_UP_THREAD = ThreadUtil.makeSingleDaemonThreadPool("delayed save cache cleaner");
   private static final Set<WeakReference<AbstractDelayedSaveCache<?, ?>>> SAVE_CACHE_SET = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private static final int CLEANUP_CHECK_TIME_IN_MS = 1000;
   protected final ConcurrentHashMap<Long, TSaveContainer> saveContainerByPosition = new ConcurrentHashMap<>();
   protected final KeyedLockContainer<Long> saveLockContainer = new KeyedLockContainer<>();
   protected final int saveDelayInMs;

   public AbstractDelayedSaveCache(int saveDelayInMs) {
      if (saveDelayInMs < 1000) {
         LOGGER.warn("The save delay [" + saveDelayInMs + "] shouldn't be less than the cleanup check timer interval [" + 1000 + "].");
      }

      this.saveDelayInMs = saveDelayInMs;
      SAVE_CACHE_SET.add(new WeakReference<>(this));
   }

   protected abstract TSaveContainer createEmptySaveObjContainer(long l);

   protected abstract void handleDataSourceRemoval(@NotNull TSaveContainer abstractSaveObjContainer);

   public TSaveContainer writeToMemoryAndQueueSave(long inputPos, @Nullable TSaveObj inputObj) {
      ReentrantLock lockForPos = this.saveLockContainer.getLockForPos(inputPos);

      TSaveContainer oldContainer;
      try {
         lockForPos.lock();
         TSaveContainer container = this.saveContainerByPosition.get(inputPos);
         if (container == null) {
            container = this.createEmptySaveObjContainer(inputPos);
            oldContainer = this.saveContainerByPosition.put(inputPos, container);
            if (oldContainer != null) {
               this.handleDataSourceRemoval(oldContainer);
            }
         }

         container.update(inputObj);
         container.updateLastWrittenTimestamp();
         oldContainer = container;
      } finally {
         lockForPos.unlock();
      }

      return oldContainer;
   }

   public int getUnsavedCount() {
      return this.saveContainerByPosition.size();
   }

   public void flush() {
      this.cleanUp(true);
   }

   public void cleanUp(boolean flushAll) {
      Enumeration<Long> keyIterator = this.saveContainerByPosition.keys();
      ArrayList<TSaveContainer> removedContainers = null;

      while (keyIterator.hasMoreElements()) {
         Long pos = keyIterator.nextElement();
         ReentrantLock posLock = this.saveLockContainer.getLockForPos(pos);

         try {
            posLock.lock();
            TSaveContainer savedContainer = this.saveContainerByPosition.get(pos);
            if (savedContainer != null && (flushAll || savedContainer.hasTimedOut(this.saveDelayInMs))) {
               this.saveContainerByPosition.remove(pos);
               if (removedContainers == null) {
                  removedContainers = new ArrayList<>();
               }

               removedContainers.add(savedContainer);
            }
         } finally {
            posLock.unlock();
         }
      }

      if (removedContainers != null) {
         try {
            Thread.sleep(100L);
         } catch (InterruptedException var10) {
         }

         for (TSaveContainer container : removedContainers) {
            this.handleDataSourceRemoval(container);
         }
      }
   }

   private static void runCleanupLoop() {
      while (true) {
         try {
            try {
               Thread.sleep(1000L);
            } catch (InterruptedException var1) {
            }

            SAVE_CACHE_SET.forEach(cacheRef -> {
               AbstractDelayedSaveCache<?, ?> cache = cacheRef.get();
               if (cache == null) {
                  SAVE_CACHE_SET.remove(cacheRef);
               } else {
                  cache.cleanUp(false);
               }
            });
         } catch (Exception var2) {
            LOGGER.error("Unexpected error in cleanup thread: [" + var2.getMessage() + "].", var2);
         }
      }
   }

   @Override
   public void close() {
      SAVE_CACHE_SET.removeIf(cacheRef -> {
         AbstractDelayedSaveCache<?, ?> cache = cacheRef.get();
         return cache != null && cache.equals(this);
      });
   }

   static {
      BACKGROUND_CLEAN_UP_THREAD.execute(() -> runCleanupLoop());
   }
}
