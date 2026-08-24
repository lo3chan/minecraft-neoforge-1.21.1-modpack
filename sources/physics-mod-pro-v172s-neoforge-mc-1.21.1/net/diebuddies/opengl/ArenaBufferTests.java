package net.diebuddies.opengl;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArenaBufferTests {
   private static final double GROW_MULTIPLICATOR = 1.2;
   private int arenaBuffer;
   private int stagingBuffer;
   private int target;
   private int usage;
   private int totalSize;
   private int bytesUsed;
   private ArenaBufferTests.MemorySegment root;
   private static List<ArenaBufferTests.MemorySegment> list = new ArrayList<>();

   public static void main(String[] args) {
      ArenaBufferTests test = new ArenaBufferTests(1048576);
      Random r = new Random(0L);

      while (true) {
         if (r.nextDouble() < 0.5) {
            list.add(test.uploadData(r.nextInt(1412, 21904)));
         } else if (list.size() > 0) {
            test.freeSegment(list.remove(r.nextInt(list.size())));
         }

         System.out.println(test.totalSize + " > " + list.size());

         for (int i = 0; i < list.size(); i++) {
            ArenaBufferTests.MemorySegment segment = list.get(i);
            if (!segment.used) {
               throw new RuntimeException("should be used");
            }

            if (segment.size < 0) {
               throw new RuntimeException("invalid size");
            }

            for (int j = 0; j < list.size(); j++) {
               ArenaBufferTests.MemorySegment offSegment = list.get(j);
               if (segment != offSegment
                  && (
                     segment.offset >= offSegment.offset && segment.offset + segment.size <= offSegment.offset + offSegment.size
                        || offSegment.offset >= segment.offset && offSegment.offset + offSegment.size <= segment.offset + segment.size
                  )) {
                  System.out.println(segment.offset + " > " + segment.size + " > " + offSegment.offset + " > " + offSegment.size);
                  System.out.println("index: " + i + " > " + j);
                  throw new RuntimeException("overlap");
               }
            }
         }
      }
   }

   public ArenaBufferTests(int size, int target) {
      this.target = target;
      this.root = new ArenaBufferTests.MemorySegment(this, 0, size);
      this.totalSize = size;
      this.bind();
   }

   public ArenaBufferTests(int size) {
      this(size, 0);
   }

   public ArenaBufferTests.MemorySegment uploadData(int needed) {
      ArenaBufferTests.MemorySegment segment = this.uploadToFreeSegment(this.root, needed);
      if (segment != null) {
         return segment;
      } else {
         if (this.totalSize - this.bytesUsed >= needed) {
            this.defragment(Math.max(this.totalSize, (int)((this.bytesUsed + needed) * 1.2)));
         } else {
            this.defragment((int)((this.bytesUsed + needed) * 1.2));
         }

         segment = this.uploadToFreeSegment(this.root, needed);
         if (segment != null) {
            return segment;
         } else {
            throw new RuntimeException("arena buffer couldn't find free memory segment");
         }
      }
   }

   public void defragment() {
      this.defragment(this.totalSize);
   }

   private void defragment(int newSize) {
      this.totalSize = newSize;
      ArenaBufferTests.MemorySegment segment = this.root;
      ArenaBufferTests.MemorySegment tail = this.root;
      int offset = 0;

      List<ArenaBufferTests.CopyCommand> copyCommands;
      for (copyCommands = new ObjectArrayList(); segment != null; segment = segment.next) {
         if (segment.used) {
            copyCommands.add(new ArenaBufferTests.CopyCommand(segment.offset, offset, segment.size));
            segment.offset = offset;
            offset += segment.size;
            tail = segment;
         } else {
            if (segment.previous != null) {
               segment.previous.next = segment.next;
            }

            if (segment.next != null) {
               segment.next.previous = segment.previous;
            }

            if (segment == this.root && segment.next != null) {
               this.root = segment.next;
            }
         }
      }

      if (tail.used) {
         tail.next = new ArenaBufferTests.MemorySegment(this, offset, this.totalSize - this.bytesUsed);
         tail.next.previous = tail;
      } else {
         tail.size = this.totalSize;
      }

      int size = 0;
      int readOffset = 0;
      int writeOffset = 0;

      for (int i = 0; i < copyCommands.size(); i++) {
         ArenaBufferTests.CopyCommand copyCommand = copyCommands.get(i);
         if (readOffset != copyCommand.readOffset || writeOffset != copyCommand.writeOffset) {
            readOffset = copyCommand.readOffset;
            writeOffset = copyCommand.writeOffset;
            size = 0;
         }

         readOffset += copyCommand.size;
         writeOffset += copyCommand.size;
         size += copyCommand.size;
      }
   }

   private void printSegments() {
      ArenaBufferTests.MemorySegment t = this.root;

      do {
         System.out.println(t.offset + " > " + t.size + " > " + t.used);
         t = t.next;
      } while (t != null);
   }

   private ArenaBufferTests.MemorySegment uploadToFreeSegment(ArenaBufferTests.MemorySegment segment, int needed) {
      while (segment != null) {
         if (segment.size >= needed && !segment.used) {
            this.uploadToSegment(segment, needed);
            return segment;
         }

         segment = segment.next;
      }

      return null;
   }

   private void uploadToSegment(ArenaBufferTests.MemorySegment segment, int size) {
      this.bytesUsed += size;
      segment.used = true;
      if (segment.size == size) {
         this.transferData(segment.offset, size);
      } else {
         int newSize = segment.size - size;
         ArenaBufferTests.MemorySegment newSegment = new ArenaBufferTests.MemorySegment(this, segment.offset + size, newSize);
         newSegment.previous = segment;
         newSegment.next = segment.next;
         segment.next = newSegment;
         segment.size = size;
         if (newSegment.next != null) {
            newSegment.next.previous = newSegment;
         }

         this.transferData(segment.offset, size);
      }
   }

   private void transferData(int offset, int size) {
   }

   private void freeSegment(ArenaBufferTests.MemorySegment segment) {
      if (segment.used) {
         this.bytesUsed = this.bytesUsed - segment.size;
         segment.used = false;
         this.mergeSegmentsIfPossible(segment, segment.next);
         this.mergeSegmentsIfPossible(segment.previous, segment);
      }
   }

   private void mergeSegmentsIfPossible(ArenaBufferTests.MemorySegment mergeInto, ArenaBufferTests.MemorySegment segment) {
      if (mergeInto != null && segment != null && !segment.used && !mergeInto.used) {
         mergeInto.next = segment.next;
         mergeInto.size = mergeInto.size + segment.size;
         if (mergeInto.next != null) {
            mergeInto.next.previous = mergeInto;
         }
      }
   }

   public int getTotalSize() {
      return this.totalSize;
   }

   public void bind() {
   }

   public void destroy() {
   }

   public class CopyCommand {
      public int readOffset;
      public int writeOffset;
      public int size;

      public CopyCommand(int readOffset, int writeOffset, int size) {
         this.readOffset = readOffset;
         this.writeOffset = writeOffset;
         this.size = size;
      }
   }

   public class MemorySegment {
      public ArenaBufferTests arenaBuffer;
      public int offset;
      public int size;
      public ArenaBufferTests.MemorySegment previous;
      public ArenaBufferTests.MemorySegment next;
      public boolean used;

      public MemorySegment(ArenaBufferTests arenaBuffer, int offset, int size) {
         this.arenaBuffer = arenaBuffer;
         this.offset = offset;
         this.size = size;
      }

      public void free() {
         this.arenaBuffer.freeSegment(this);
      }
   }
}
