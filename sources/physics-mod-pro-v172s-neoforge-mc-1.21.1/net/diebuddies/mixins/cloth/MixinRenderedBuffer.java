package net.diebuddies.mixins.cloth;

import com.mojang.blaze3d.vertex.ByteBufferBuilder.Result;
import net.diebuddies.physics.verlet.RenderedBufferAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Result.class})
public class MixinRenderedBuffer implements RenderedBufferAccessor {
   @Unique
   private boolean ignoreRelease;

   @Inject(
      at = {@At("HEAD")},
      method = {"close"},
      cancellable = true
   )
   public void release(CallbackInfo info) {
      if (this.ignoreRelease) {
         info.cancel();
      }
   }

   @Override
   public void setIgnoreRelease(boolean ignoreRelease) {
      this.ignoreRelease = ignoreRelease;
   }
}
