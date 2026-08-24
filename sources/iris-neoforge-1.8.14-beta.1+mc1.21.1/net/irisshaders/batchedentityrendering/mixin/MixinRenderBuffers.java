package net.irisshaders.batchedentityrendering.mixin;

import net.irisshaders.batchedentityrendering.impl.DrawCallTrackingRenderBuffers;
import net.irisshaders.batchedentityrendering.impl.FullyBufferedMultiBufferSource;
import net.irisshaders.batchedentityrendering.impl.MemoryTrackingBuffer;
import net.irisshaders.batchedentityrendering.impl.MemoryTrackingRenderBuffers;
import net.irisshaders.batchedentityrendering.impl.RenderBuffersExt;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RenderBuffers.class})
public class MixinRenderBuffers implements RenderBuffersExt, MemoryTrackingRenderBuffers, DrawCallTrackingRenderBuffers {
   @Unique
   private final FullyBufferedMultiBufferSource buffered = new FullyBufferedMultiBufferSource();
   @Unique
   private final OutlineBufferSource outlineBufferSource = new OutlineBufferSource(this.buffered);
   @Unique
   private int begins = 0;
   @Unique
   private int maxBegins = 0;
   @Shadow
   @Final
   private BufferSource bufferSource;
   @Shadow
   @Final
   private BufferSource crumblingBufferSource;
   @Shadow
   @Final
   private SectionBufferBuilderPack fixedBufferPack;

   @Inject(
      method = {"bufferSource"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void batchedentityrendering$replaceBufferSource(CallbackInfoReturnable<BufferSource> cir) {
      if (this.begins != 0) {
         cir.setReturnValue(this.buffered);
      }
   }

   @Inject(
      method = {"crumblingBufferSource"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void batchedentityrendering$replaceCrumblingBufferSource(CallbackInfoReturnable<BufferSource> cir) {
      if (this.begins != 0) {
         cir.setReturnValue(this.buffered.getUnflushableWrapper());
      }
   }

   @Inject(
      method = {"outlineBufferSource"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void batchedentityrendering$replaceOutlineBufferSource(CallbackInfoReturnable<OutlineBufferSource> provider) {
      if (this.begins != 0) {
         provider.setReturnValue(this.outlineBufferSource);
      }
   }

   @Override
   public void beginLevelRendering() {
      if (this.begins == 0) {
         this.buffered.assertWrapStackEmpty();
      }

      this.begins++;
      this.maxBegins = Math.max(this.begins, this.maxBegins);
   }

   @Override
   public void endLevelRendering() {
      this.begins--;
      if (this.begins == 0) {
         this.buffered.assertWrapStackEmpty();
      }
   }

   @Override
   public long getEntityBufferAllocatedSize() {
      return this.buffered.getAllocatedSize();
   }

   @Override
   public long getMiscBufferAllocatedSize() {
      return ((MemoryTrackingBuffer)this.bufferSource).getAllocatedSize();
   }

   @Override
   public int getMaxBegins() {
      return this.maxBegins;
   }

   @Override
   public void freeAndDeleteBuffers() {
      this.buffered.freeAndDeleteBuffer();
      ((SectionBufferBuilderPackAccessor)this.fixedBufferPack)
         .getBuffers()
         .values()
         .forEach(bufferBuilder -> ((MemoryTrackingBuffer)bufferBuilder).freeAndDeleteBuffer());
      ((BufferSourceAccessor)this.bufferSource)
         .getFixedBuffers()
         .forEach((renderType, bufferBuilder) -> ((MemoryTrackingBuffer)bufferBuilder).freeAndDeleteBuffer());
      ((BufferSourceAccessor)this.bufferSource).getFixedBuffers().clear();
      ((MemoryTrackingBuffer)((OutlineBufferSourceAccessor)this.outlineBufferSource).getOutlineBufferSource()).freeAndDeleteBuffer();
   }

   @Override
   public int getDrawCalls() {
      return this.buffered.getDrawCalls();
   }

   @Override
   public int getRenderTypes() {
      return this.buffered.getRenderTypes();
   }

   @Override
   public void resetDrawCounts() {
      this.buffered.resetDrawCalls();
   }
}
