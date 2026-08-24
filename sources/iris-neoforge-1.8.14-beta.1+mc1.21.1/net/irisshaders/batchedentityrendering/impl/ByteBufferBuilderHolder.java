package net.irisshaders.batchedentityrendering.impl;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;

public class ByteBufferBuilderHolder implements MemoryTrackingBuffer {
   private final ByteBufferBuilder builder;
   private long lastUse = System.currentTimeMillis();

   public ByteBufferBuilderHolder(ByteBufferBuilder builder) {
      this.builder = builder;
   }

   public ByteBufferBuilder getBuffer() {
      return this.builder;
   }

   public boolean deleteOrClear(int clearTime) {
      if (System.currentTimeMillis() - this.lastUse > clearTime) {
         this.builder.close();
         return true;
      } else {
         this.builder.clear();
         return false;
      }
   }

   public boolean delete(int clearTime) {
      if (System.currentTimeMillis() - this.lastUse > clearTime) {
         this.builder.close();
         return true;
      } else {
         return false;
      }
   }

   public void forceDelete() {
      this.builder.close();
   }

   @Override
   public long getAllocatedSize() {
      return ((MemoryTrackingBuffer)this.builder).getAllocatedSize();
   }

   @Override
   public long getUsedSize() {
      return ((MemoryTrackingBuffer)this.builder).getUsedSize();
   }

   @Override
   public void freeAndDeleteBuffer() {
      this.builder.close();
   }

   public void wasUsed() {
      this.lastUse = System.currentTimeMillis();
   }
}
