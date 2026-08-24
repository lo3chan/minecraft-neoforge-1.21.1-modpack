package net.Pandarix.mixin;

import net.Pandarix.Platform;
import net.Pandarix.config.BAConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Player.class})
public abstract class ElytraStartupMixin {
   @Inject(
      method = {"startFallFlying()V"},
      at = {@At("TAIL")}
   )
   private void injectMethod(CallbackInfo ci) {
      if (BAConfig.artifactsEnabled && BAConfig.soaringWindsEnabled) {
         Player betterarcheology$player = (Player)this;
         if (Platform.hasSoaringWinds(betterarcheology$player)) {
            float betterarcheology$boost = (float)(BAConfig.soaringWindsBoost * 0.5);
            Vec3 betterarcheology$vec3d = betterarcheology$player.getLookAngle();
            Vec3 betterarcheology$vec3d2 = betterarcheology$player.getDeltaMovement();
            betterarcheology$player.setDeltaMovement(
               betterarcheology$vec3d2.add(
                  betterarcheology$vec3d.x * 0.1 + (betterarcheology$vec3d.x * 1.5 - betterarcheology$vec3d2.x) * betterarcheology$boost,
                  betterarcheology$vec3d.y * 0.1 + (betterarcheology$vec3d.y * 1.5 - betterarcheology$vec3d2.y) * betterarcheology$boost / 1.8,
                  betterarcheology$vec3d.z * 0.1 + (betterarcheology$vec3d.z * 1.5 - betterarcheology$vec3d2.z) * betterarcheology$boost
               )
            );
            if (betterarcheology$player.level() instanceof ServerLevel betterarcheology$serverlevel) {
               betterarcheology$serverlevel.sendParticles(
                  ParticleTypes.POOF,
                  betterarcheology$player.position().x,
                  betterarcheology$player.position().y,
                  betterarcheology$player.position().z,
                  7,
                  0.0,
                  0.1,
                  -0.05,
                  0.1
               );
            }
         }
      }
   }
}
