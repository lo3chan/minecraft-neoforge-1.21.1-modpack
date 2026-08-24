package net.diebuddies.mixins.particle;

import net.diebuddies.physics.settings.animation.ParticleExtension;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Particle.class})
public class MixinParticle implements ParticleExtension {
   @Shadow
   protected boolean hasPhysics;
   @Shadow
   protected float rCol;
   @Shadow
   protected float gCol;
   @Shadow
   protected float bCol;
   @Shadow
   protected float alpha;
   @Unique
   private boolean fakeLight;

   @Inject(
      at = {@At("HEAD")},
      method = {"getLightColor"},
      cancellable = true
   )
   protected void getLightColor(float f, CallbackInfoReturnable<Integer> info) {
      if (this.fakeLight) {
         info.setReturnValue(15728640);
      }
   }

   @Override
   public void setPhysics(boolean physics) {
      this.hasPhysics = physics;
   }

   @Override
   public void setFakeLight(boolean fakeLight) {
      this.fakeLight = fakeLight;
   }

   @Override
   public float getRed() {
      return this.rCol;
   }

   @Override
   public float getGreen() {
      return this.gCol;
   }

   @Override
   public float getBlue() {
      return this.bCol;
   }

   @Override
   public float getAlpha() {
      return this.alpha;
   }
}
