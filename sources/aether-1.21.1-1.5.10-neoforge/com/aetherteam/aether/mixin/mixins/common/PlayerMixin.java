package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.entity.passive.MountableAnimal;
import com.aetherteam.aether.event.hooks.AbilityHooks;
import com.aetherteam.aether.mixin.AetherMixinHooks;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Player.class})
public abstract class PlayerMixin {
   @Shadow
   protected abstract boolean wantsToStopRiding();

   @Inject(
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;setLastHurtMob(Lnet/minecraft/world/entity/Entity;)V",
         shift = Shift.AFTER
      )},
      method = {"attack(Lnet/minecraft/world/entity/Entity;)V"}
   )
   private void attack(Entity target, CallbackInfo ci) {
      Player player = (Player)this;
      if (target instanceof LivingEntity) {
         AbilityHooks.AccessoryHooks.damageGloves(player);
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"rideTick()V"}
   )
   private void rideTickHead(CallbackInfo ci, @Share("wantsToStopRiding") LocalBooleanRef wantsToStopRiding) {
      Player player = (Player)this;
      wantsToStopRiding.set(this.wantsToStopRiding());
      if (!player.level().isClientSide() && player.isPassenger() && player.getVehicle() instanceof MountableAnimal mountableAnimal) {
         mountableAnimal.setPlayerTriedToCrouch(player.isShiftKeyDown());
      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"rideTick()V"}
   )
   private void rideTickTail(CallbackInfo ci, @Share("wantsToStopRiding") LocalBooleanRef wantsToStopRiding) {
      Player player = (Player)this;
      if (!player.level().isClientSide()
         && !player.isShiftKeyDown()
         && wantsToStopRiding.get()
         && player.isPassenger()
         && player.getVehicle() instanceof MountableAnimal) {
         player.setShiftKeyDown(true);
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"isModelPartShown(Lnet/minecraft/world/entity/player/PlayerModelPart;)Z"},
      cancellable = true
   )
   private void isModelPartShown(PlayerModelPart part, CallbackInfoReturnable<Boolean> cir) {
      Player player = (Player)this;
      ItemStack stack = AetherMixinHooks.isCapeVisible(player);
      if (!stack.isEmpty()) {
         cir.setReturnValue(true);
      }
   }
}
