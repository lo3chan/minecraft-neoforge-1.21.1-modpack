package net.bettercombat.mixin.player;

import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.bettercombat.logic.knockback.ConfigurableKnockback;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})
public class LivingEntityMixin implements ConfigurableKnockback {
   private float customKnockbackMultiplier_BetterCombat = 1.0F;

   @Inject(
      method = {"getAttributeValue(Lnet/minecraft/core/Holder;)D"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getAttributeValue_Inject(Holder<Attribute> attribute, CallbackInfoReturnable<Double> cir) {
      if (this instanceof Player player) {
         int comboCount = ((PlayerAttackProperties)player).getComboCount();
         if (player.level().isClientSide && comboCount > 0 && PlayerAttackHelper.shouldAttackWithOffHand(player, comboCount)) {
            PlayerAttackHelper.swapHandAttributes(player, () -> {
               double value = player.getAttributes().getValue(attribute);
               cir.setReturnValue(value);
            });
            cir.cancel();
         }
      }
   }

   @Override
   public void setKnockbackMultiplier_BetterCombat(float value) {
      this.customKnockbackMultiplier_BetterCombat = value;
   }

   @ModifyVariable(
      method = {"knockback(DDD)V"},
      at = @At("HEAD"),
      ordinal = 0,
      argsOnly = true
   )
   public double takeKnockback_HEAD_changeStrength(double knockbackStrength) {
      return knockbackStrength * this.customKnockbackMultiplier_BetterCombat;
   }
}
