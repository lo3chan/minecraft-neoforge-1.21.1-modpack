package net.diebuddies.mixins.liquid;

import com.mojang.blaze3d.platform.NativeImage;
import net.diebuddies.physics.liquid.SimpleTextureDimension;
import net.minecraft.client.renderer.texture.SimpleTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SimpleTexture.class})
public class MixinSimpleTexture implements SimpleTextureDimension {
   @Unique
   private int tWidthP;
   @Unique
   private int tHeightP;

   @Inject(
      at = {@At("HEAD")},
      method = {"doLoad"}
   )
   private void doLoad(NativeImage nativeImage, boolean bl, boolean bl2, CallbackInfo info) {
      this.tWidthP = nativeImage.getWidth();
      this.tHeightP = nativeImage.getHeight();
   }

   @Override
   public int getWidth() {
      return this.tWidthP;
   }

   @Override
   public int getHeight() {
      return this.tHeightP;
   }
}
