package net.irisshaders.iris.mixin.integrationtest;

import com.mojang.blaze3d.pipeline.RenderTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({RenderTarget.class})
public class MixinRenderTarget_StencilBufferTest {
   @Unique
   private static final boolean STENCIL = true;

   @ModifyArgs(
      method = {"createBuffers"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V",
         remap = false,
         ordinal = 0
      )
   )
   public void init(Args args) {
      args.set(2, 36013);
      args.set(6, 34041);
      args.set(7, 36269);
   }

   @ModifyArgs(
      method = {"createBuffers"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glFramebufferTexture2D(IIIII)V",
         remap = false
      ),
      slice = @Slice(
         from = @At(
            value = "FIELD",
            target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;useDepth:Z",
            ordinal = 1
         )
      )
   )
   public void init2(Args args) {
      args.set(1, 33306);
   }
}
