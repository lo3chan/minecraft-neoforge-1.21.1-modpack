package net.diebuddies.opengl;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.nio.ByteBuffer;
import java.util.List;
import org.lwjgl.opengl.GL32C;

public class ArenaBuffer {
   private static final double GROW_MULTIPLICATOR = 1.2;
   private int arenaBuffer = GL32C.glGenBuffers();
   private int stagingBuffer = GL32C.glGenBuffers();
   private int target;
   private int usage;
   private int totalSize;
   private int bytesUsed;
   private ArenaBuffer.MemorySegment root;

   public ArenaBuffer(int size, int target) {
      this.target = target;
      this.usage = 35044;
      this.root = new ArenaBuffer.MemorySegment(this, 0, size);
      this.totalSize = size;
      this.bind();
      GL32C.glBufferData(target, size, this.usage);
   }

   public ArenaBuffer(int size) {
      this(size, 34962);
   }

   public ArenaBuffer.MemorySegment uploadData(ByteBuffer data) {
      int needed = data.remaining();
      ArenaBuffer.MemorySegment segment = this.uploadToFreeSegment(this.root, data);
      if (segment != null) {
         return segment;
      } else {
         if (this.totalSize - this.bytesUsed >= needed) {
            this.defragment(Math.max(this.totalSize, (int)((this.bytesUsed + needed) * 1.2)));
         } else {
            this.defragment((int)((this.bytesUsed + needed) * 1.2));
         }

         segment = this.uploadToFreeSegment(this.root, data);
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
      int defragmentedArenaBuffer = GL32C.glGenBuffers();
      GL32C.glBindBuffer(this.target, defragmentedArenaBuffer);
      GL32C.glBufferData(this.target, newSize, this.usage);
      GL32C.glBindBuffer(36662, this.arenaBuffer);
      GL32C.glBindBuffer(36663, defragmentedArenaBuffer);
      this.totalSize = newSize;
      ArenaBuffer.MemorySegment segment = this.root;
      ArenaBuffer.MemorySegment tail = this.root;
      int offset = 0;

      List<ArenaBuffer.CopyCommand> copyCommands;
      for (copyCommands = new ObjectArrayList(); segment != null; segment = segment.next) {
         if (segment.used) {
            copyCommands.add(new ArenaBuffer.CopyCommand(segment.offset, offset, segment.size));
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
         tail.next = new ArenaBuffer.MemorySegment(this, offset, this.totalSize - this.bytesUsed);
         tail.next.previous = tail;
      } else {
         tail.size = this.totalSize;
      }

      int size = 0;
      int readOffset = 0;
      int writeOffset = 0;

      for (int i = 0; i < copyCommands.size(); i++) {
         ArenaBuffer.CopyCommand copyCommand = copyCommands.get(i);
         if (readOffset != copyCommand.readOffset || writeOffset != copyCommand.writeOffset) {
            GL32C.glCopyBufferSubData(36662, 36663, readOffset - size, writeOffset - size, size);
            readOffset = copyCommand.readOffset;
            writeOffset = copyCommand.writeOffset;
            size = 0;
         }

         readOffset += copyCommand.size;
         writeOffset += copyCommand.size;
         size += copyCommand.size;
      }

      if (copyCommands.size() > 0) {
         GL32C.glCopyBufferSubData(36662, 36663, readOffset - size, writeOffset - size, size);
      }

      GL32C.glDeleteBuffers(this.arenaBuffer);
      this.arenaBuffer = defragmentedArenaBuffer;
   }

   private ArenaBuffer.MemorySegment uploadToFreeSegment(ArenaBuffer.MemorySegment segment, ByteBuffer data) {
      int needed = data.remaining();

      while (segment != null) {
         if (segment.size >= needed && !segment.used) {
            this.uploadToSegment(segment, data, needed);
            return segment;
         }

         segment = segment.next;
      }

      return null;
   }

   private void uploadToSegment(ArenaBuffer.MemorySegment segment, ByteBuffer data, int size) {
      this.bytesUsed += size;
      segment.used = true;
      if (segment.size == size) {
         this.transferData(segment.offset, data, size);
      } else {
         int newSize = segment.size - size;
         ArenaBuffer.MemorySegment newSegment = new ArenaBuffer.MemorySegment(this, segment.offset + size, newSize);
         newSegment.previous = segment;
         newSegment.next = segment.next;
         segment.next = newSegment;
         segment.size = size;
         if (newSegment.next != null) {
            newSegment.next.previous = newSegment;
         }

         this.transferData(segment.offset, data, size);
      }
   }

   private void transferData(int offset, ByteBuffer data, int size) {
      GL32C.glBindBuffer(34962, this.stagingBuffer);
      GL32C.glBufferData(34962, data, 35042);
      GL32C.glBindBuffer(36662, this.stagingBuffer);
      GL32C.glBindBuffer(36663, this.arenaBuffer);
      GL32C.glCopyBufferSubData(36662, 36663, 0L, offset, size);
   }

   private void freeSegment(ArenaBuffer.MemorySegment segment) {
      if (segment.used) {
         this.bytesUsed = this.bytesUsed - segment.size;
         segment.used = false;
         this.mergeSegmentsIfPossible(segment, segment.next);
         this.mergeSegmentsIfPossible(segment.previous, segment);
      }
   }

   private void mergeSegmentsIfPossible(ArenaBuffer.MemorySegment mergeInto, ArenaBuffer.MemorySegment segment) {
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
      GL32C.glBindBuffer(this.target, this.arenaBuffer);
   }

   public void destroy() {
      GL32C.glDeleteBuffers(this.arenaBuffer);
      GL32C.glDeleteBuffers(this.stagingBuffer);
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
      public ArenaBuffer arenaBuffer;
      public int offset;
      public int size;
      public ArenaBuffer.MemorySegment previous;
      public ArenaBuffer.MemorySegment next;
      public boolean used;

      public MemorySegment(ArenaBuffer arenaBuffer, int offset, int size) {
         this.arenaBuffer = arenaBuffer;
         this.offset = offset;
         this.size = size;
      }

      public void free() {
         this.arenaBuffer.freeSegment(this);
      }
   }
}
