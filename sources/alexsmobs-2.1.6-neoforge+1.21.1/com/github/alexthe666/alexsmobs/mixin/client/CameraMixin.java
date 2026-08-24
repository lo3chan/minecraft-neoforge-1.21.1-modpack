package com.github.alexthe666.alexsmobs.mixin.client;

import com.github.alexthe666.alexsmobs.client.event.ClientEvents;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Camera.class})
public abstract class CameraMixin {
   @Inject(
      method = {"setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V"},
      at = {@At("TAIL")}
   )
   private void alexsmobs$earthquakeShake(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
      ClientEvents.applyEarthquakeShake((Camera)this);
   }
}
