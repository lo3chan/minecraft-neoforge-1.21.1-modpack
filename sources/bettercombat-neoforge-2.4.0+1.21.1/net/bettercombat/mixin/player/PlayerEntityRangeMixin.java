package net.bettercombat.mixin.player;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.bettercombat.logic.PlayerAttackHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Player.class})
public class PlayerEntityRangeMixin {
   @WrapOperation(
      method = {"entityInteractionRange()D"},
      require = 0,
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/core/Holder;)D"
      )}
   )
   private double getEntityInteractionRange_Wrapped_BetterCombat(Player instance, Holder registryEntry, Operation<Double> original) {
      Double originalResult = (Double)original.call(new Object[]{instance, registryEntry});
      return PlayerAttackHelper.getRangeWithWeapon(instance, originalResult);
   }
}
