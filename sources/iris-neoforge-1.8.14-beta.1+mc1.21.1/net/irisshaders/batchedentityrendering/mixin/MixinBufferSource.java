package net.irisshaders.batchedentityrendering.mixin;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import java.util.SequencedMap;
import net.irisshaders.batchedentityrendering.impl.MemoryTrackingBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({BufferSource.class})
public class MixinBufferSource implements MemoryTrackingBuffer {
   @Shadow
   @Final
   protected ByteBufferBuilder sharedBuffer;
   @Shadow
   @Final
   protected SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers;

   @Override
   public long getAllocatedSize() {
      long allocatedSize = ((MemoryTrackingBuffer)this.sharedBuffer).getAllocatedSize();

      for (ByteBufferBuilder builder : this.fixedBuffers.values()) {
         allocatedSize += ((MemoryTrackingBuffer)builder).getAllocatedSize();
      }

      return allocatedSize;
   }

   @Override
   public long getUsedSize() {
      long allocatedSize = ((MemoryTrackingBuffer)this.sharedBuffer).getUsedSize();

      for (ByteBufferBuilder builder : this.fixedBuffers.values()) {
         allocatedSize += ((MemoryTrackingBuffer)builder).getUsedSize();
      }

      return allocatedSize;
   }

   @Override
   public void freeAndDeleteBuffer() {
      ((MemoryTrackingBuffer)this.sharedBuffer).freeAndDeleteBuffer();

      for (ByteBufferBuilder builder : this.fixedBuffers.values()) {
         ((MemoryTrackingBuffer)builder).freeAndDeleteBuffer();
      }
   }
}
