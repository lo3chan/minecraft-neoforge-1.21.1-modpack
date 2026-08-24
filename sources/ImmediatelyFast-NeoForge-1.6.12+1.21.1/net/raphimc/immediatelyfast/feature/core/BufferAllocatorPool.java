package net.raphimc.immediatelyfast.feature.core;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import net.raphimc.immediatelyfast.ImmediatelyFast;

public class BufferAllocatorPool {
   private static final ReferenceList<BufferAllocatorPool.Entry> FREE = new ReferenceArrayList();
   private static final ReferenceList<BufferAllocatorPool.Entry> IN_USE = new ReferenceArrayList();
   private static final Reference2ObjectMap<ByteBufferBuilder, BufferAllocatorPool.Entry> BUFFER_ALLOCATOR_MAPPING = new Reference2ObjectOpenHashMap();

   private BufferAllocatorPool() {
   }

   public static ByteBufferBuilder borrowBufferAllocator() {
      RenderSystem.assertOnRenderThread();
      BufferAllocatorPool.Entry entry;
      if (FREE.isEmpty()) {
         entry = new BufferAllocatorPool.Entry(new ByteBufferBuilder(256));
      } else {
         entry = (BufferAllocatorPool.Entry)FREE.removeFirst();
         if (entry.bufferAllocator.pointer == 0L) {
            BUFFER_ALLOCATOR_MAPPING.remove(entry.bufferAllocator);
            entry = new BufferAllocatorPool.Entry(new ByteBufferBuilder(256));
         }
      }

      IN_USE.add(entry);
      BUFFER_ALLOCATOR_MAPPING.put(entry.bufferAllocator, entry);
      entry.onBorrow();
      return entry.bufferAllocator;
   }

   public static void returnBufferAllocatorSafe(ByteBufferBuilder bufferAllocator) {
      RenderSystem.assertOnRenderThread();
      BufferAllocatorPool.Entry entry = (BufferAllocatorPool.Entry)BUFFER_ALLOCATOR_MAPPING.get(bufferAllocator);
      if (IN_USE.remove(entry)) {
         entry.onReturn();
         FREE.addFirst(entry);
      }
   }

   public static int getSize() {
      return FREE.size() + IN_USE.size();
   }

   public static void onEndFrame() {
      if (!IN_USE.isEmpty()) {
         IN_USE.removeIf(
            entryx -> {
               if (!entryx.inUseOverMultipleFrames) {
                  return false;
               } else {
                  ImmediatelyFast.LOGGER
                     .warn("!!! Possible memory leak detected!!! A BufferAllocator was not returned to the pool. This is not a bug in ImmediatelyFast.");
                  ImmediatelyFast.LOGGER.warn("Allocation stack trace:");
                  if (entryx.allocationStackTrace != null) {
                     for (StackTraceElement element : entryx.allocationStackTrace) {
                        ImmediatelyFast.LOGGER.warn("\tat {}", element.toString());
                     }
                  } else {
                     ImmediatelyFast.LOGGER
                        .warn("\t<No stack trace available. Enable debug_only_detailed_memory_leak_detection in the config to get stack traces>");
                  }

                  return true;
               }
            }
         );
         ObjectListIterator var0 = IN_USE.iterator();

         while (var0.hasNext()) {
            BufferAllocatorPool.Entry entry = (BufferAllocatorPool.Entry)var0.next();
            entry.inUseOverMultipleFrames = true;
         }
      }

      FREE.removeIf(entryx -> {
         if (entryx.shouldBeClosed()) {
            entryx.bufferAllocator.close();
            BUFFER_ALLOCATOR_MAPPING.remove(entryx.bufferAllocator);
            return true;
         } else {
            return false;
         }
      });
   }

   private static class Entry {
      private final ByteBufferBuilder bufferAllocator;
      private long lastAccessTime;
      private boolean inUseOverMultipleFrames;
      private StackTraceElement[] allocationStackTrace;

      public Entry(ByteBufferBuilder bufferAllocator) {
         this.bufferAllocator = bufferAllocator;
         this.lastAccessTime = System.currentTimeMillis();
      }

      public boolean shouldBeClosed() {
         return System.currentTimeMillis() - this.lastAccessTime > 60000L;
      }

      public void onBorrow() {
         this.lastAccessTime = System.currentTimeMillis();
         if (ImmediatelyFast.config.debug_only_detailed_memory_leak_detection) {
            this.allocationStackTrace = Thread.currentThread().getStackTrace();
         }
      }

      public void onReturn() {
         this.bufferAllocator.discard();
         this.inUseOverMultipleFrames = false;
         this.allocationStackTrace = null;
      }
   }
}
