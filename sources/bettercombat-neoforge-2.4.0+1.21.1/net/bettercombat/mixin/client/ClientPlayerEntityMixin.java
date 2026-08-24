package net.bettercombat.mixin.client;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.config.ServerConfig;
import net.bettercombat.utils.MathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LocalPlayer.class})
public class ClientPlayerEntityMixin {
   @Inject(
      method = {"aiStep()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/Input;tick(ZF)V",
         shift = Shift.AFTER
      )}
   )
   private void tickMovement_ModifyInput(CallbackInfo ci) {
      ServerConfig config = BetterCombatMod.config;
      MinecraftClient_BetterCombat client = (MinecraftClient_BetterCombat)Minecraft.getInstance();
      double multiplier = Math.min(Math.max((double)config.movement_speed_while_attacking, 0.0), 1.0);
      WeaponAttributes.Attack attack = client.getCurrentAttack();
      if (attack != null) {
         multiplier *= attack.movementSpeedMultiplier();
      }

      if (multiplier != 1.0) {
         LocalPlayer clientPlayer = (LocalPlayer)this;
         if (!clientPlayer.isPassenger() || config.movement_speed_effected_while_mounting) {
            float swingProgress = client.getSwingProgress();
            if (swingProgress < 0.98) {
               if (config.movement_speed_applied_smoothly) {
                  double p2 = 0.0;
                  if (swingProgress <= 0.5) {
                     p2 = MathHelper.easeOutCubic(swingProgress * 2.0F);
                  } else {
                     p2 = MathHelper.easeOutCubic(1.0 - (swingProgress - 0.5) * 2.0);
                  }

                  multiplier = (float)(1.0 - (1.0 - multiplier) * p2);
               }

               clientPlayer.input.forwardImpulse = (float)(clientPlayer.input.forwardImpulse * multiplier);
               clientPlayer.input.leftImpulse = (float)(clientPlayer.input.leftImpulse * multiplier);
            }
         }
      }
   }
}
