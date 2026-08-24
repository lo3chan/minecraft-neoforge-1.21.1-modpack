package net.irisshaders.batchedentityrendering.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.caffeinemc.mods.sodium.api.memory.MemoryIntrinsics;
import net.irisshaders.batchedentityrendering.impl.BufferBuilderExt;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {BufferBuilder.class},
   priority = 1010
)
public class MixinBufferBuilder_SegmentRendering implements BufferBuilderExt {
   @Final
   @Shadow
   private ByteBufferBuilder buffer;
   @Final
   @Shadow
   private VertexFormat format;
   @Shadow
   private int vertices;
   @Shadow
   @Final
   private int vertexSize;
   @Unique
   private boolean dupeNextVertex;
   @Unique
   private boolean dupeNextVertexAfter;

   @Override
   public void splitStrip() {
      if (this.vertices != 0) {
         this.duplicateLastVertex();
         this.dupeNextVertexAfter = true;
         this.dupeNextVertex = false;
      }
   }

   @Unique
   private void duplicateLastVertex() {
      long l = this.buffer.reserve(this.vertexSize);
      MemoryIntrinsics.copyMemory(l - this.vertexSize, l, this.vertexSize);
      this.vertices++;
   }

   @Inject(
      method = {"endLastVertex"},
      at = {@At("RETURN")}
   )
   private void batchedentityrendering$onNext(CallbackInfo ci) {
      if (this.dupeNextVertexAfter) {
         this.dupeNextVertexAfter = false;
         this.dupeNextVertex = true;
      } else {
         if (this.dupeNextVertex) {
            this.dupeNextVertex = false;
            this.duplicateLastVertex();
         }
      }
   }
}
