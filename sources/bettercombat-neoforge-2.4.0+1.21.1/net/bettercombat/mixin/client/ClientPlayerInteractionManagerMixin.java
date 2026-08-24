package net.bettercombat.mixin.client;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.mixin.player.LivingEntityAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MultiPlayerGameMode.class})
public class ClientPlayerInteractionManagerMixin {
   @Shadow
   @Final
   private Minecraft minecraft;

   @Inject(
      method = {"stopDestroyBlock()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V",
         shift = Shift.AFTER
      )}
   )
   public void cancelBlockBreaking_FixAttackCD(CallbackInfo ci) {
      try {
         LocalPlayer player = this.minecraft.player;
         float cooldownLength = PlayerAttackHelper.getAttackCooldownTicksCapped(player);
         float typicalUpswing = 0.5F;
         int reducedCooldown = Math.round(cooldownLength * typicalUpswing * BetterCombatMod.config.upswing_multiplier);
         ((LivingEntityAccessor)player).setLastAttackedTicks(reducedCooldown);
      } catch (Exception var6) {
      }
   }
}
