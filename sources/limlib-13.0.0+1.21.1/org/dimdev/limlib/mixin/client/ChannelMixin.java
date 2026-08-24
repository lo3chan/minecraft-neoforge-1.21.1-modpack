package org.dimdev.limlib.mixin.client;

import com.mojang.blaze3d.audio.Channel;
import org.dimdev.limlib.api.client.ChannelExt;
import org.lwjgl.openal.AL10;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Channel.class})
public abstract class ChannelMixin implements ChannelExt {
   @Shadow
   @Final
   private int source;

   @Inject(
      method = {"linearAttenuation"},
      at = {@At("RETURN")}
   )
   private void linearAttenuation2(float attenuation, CallbackInfo ci) {
      AL10.alSourcef(this.source, 4128, attenuation / 2.0F);
   }

   @Override
   public int liminal_Library$getSource() {
      return this.source;
   }
}
