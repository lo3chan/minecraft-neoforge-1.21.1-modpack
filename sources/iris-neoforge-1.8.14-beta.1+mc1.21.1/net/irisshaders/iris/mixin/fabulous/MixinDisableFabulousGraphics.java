package net.irisshaders.iris.mixin.fabulous;

import net.irisshaders.iris.Iris;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelRenderer.class})
public class MixinDisableFabulousGraphics {
   @Inject(
      method = {"onResourceManagerReload"},
      at = {@At("HEAD")}
   )
   private void iris$disableFabulousGraphicsOnResourceReload(CallbackInfo ci) {
      this.iris$disableFabulousGraphics();
   }

   @Inject(
      method = {"allChanged"},
      at = {@At("HEAD")}
   )
   private void iris$disableFabulousGraphicsOnLevelRendererReload(CallbackInfo ci) {
      this.iris$disableFabulousGraphics();
   }

   @Unique
   private void iris$disableFabulousGraphics() {
      Options options = Minecraft.getInstance().options;
      if (Iris.getIrisConfig().areShadersEnabled()) {
         if (options.graphicsMode().get() == GraphicsStatus.FABULOUS) {
            options.graphicsMode().set(GraphicsStatus.FANCY);
         }
      }
   }
}
