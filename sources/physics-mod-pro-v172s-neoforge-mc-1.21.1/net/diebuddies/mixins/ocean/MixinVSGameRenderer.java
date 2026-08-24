package net.diebuddies.mixins.ocean;

import net.diebuddies.physics.StarterClient;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({GameRenderer.class})
public class MixinVSGameRenderer {
   @ModifyVariable(
      method = {"render"},
      at = @At("HEAD"),
      ordinal = 0
   )
   private float physicsmod$fixJitter(float tick) {
      return StarterClient.valkyrienSkies ? Math.min(0.999989F, tick) : tick;
   }
}
