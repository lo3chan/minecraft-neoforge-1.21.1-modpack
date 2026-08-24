package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.entity.monster.dungeon.boss.ValkyrieQueen;
import com.aetherteam.aether.item.combat.abilities.armor.PhoenixArmor;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntity.class})
public class LivingEntityMixin {
   @Inject(
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;getFluidJumpThreshold()D",
         shift = Shift.AFTER
      )},
      method = {"travel(Lnet/minecraft/world/phys/Vec3;)V"}
   )
   private void travel(CallbackInfo ci) {
      LivingEntity livingEntity = (LivingEntity)this;
      PhoenixArmor.boostVerticalLavaSwimming(livingEntity);
   }

   @WrapWithCondition(
      method = {"hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"
      )}
   )
   private boolean hurt(LivingEntity instance, double strength, double x, double z, @Local(argsOnly = true) DamageSource source) {
      return !(instance instanceof ValkyrieQueen) || !(source.getDirectEntity() instanceof Projectile);
   }
}
