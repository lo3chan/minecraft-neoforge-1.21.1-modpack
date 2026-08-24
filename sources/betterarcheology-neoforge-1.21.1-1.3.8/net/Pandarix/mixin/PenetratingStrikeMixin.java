package net.Pandarix.mixin;

import net.Pandarix.BACommon;
import net.Pandarix.config.BAConfig;
import net.Pandarix.enchantment.ModEnchantments;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EnchantmentHelper.class})
public class PenetratingStrikeMixin {
   @Inject(
      method = {"getDamageProtection(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;)F"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private static void injectMethod(ServerLevel pLevel, LivingEntity pEntity, DamageSource pDamageSource, CallbackInfoReturnable<Float> cir) {
      if (BAConfig.artifactsEnabled && BAConfig.penetratingStrikeEnabled) {
         try {
            Reference<Enchantment> ba$penetratingStrike = pLevel.registryAccess()
               .asGetterLookup()
               .lookupOrThrow(Registries.ENCHANTMENT)
               .getOrThrow(ModEnchantments.PENETRATING_STRIKE_KEY);
            if (pDamageSource.getWeaponItem() != null && EnchantmentHelper.getItemEnchantmentLevel(ba$penetratingStrike, pDamageSource.getWeaponItem()) >= 1) {
               float dmgWithReducedProt = (Float)cir.getReturnValue() * (float)(1.0 - BAConfig.penetratingStrikeIgnorance);
               cir.setReturnValue(Math.max(0.0F, dmgWithReducedProt));
            }
         } catch (Exception var6) {
            BACommon.LOGGER.error("Could not test for Penetrating Strike because EnchantmentEntry could not be found", var6);
         }
      }

      cir.setReturnValue((Float)cir.getReturnValue());
   }
}
