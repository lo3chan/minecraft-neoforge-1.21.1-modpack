package net.bettercombat.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ProjectileWeaponItem.class})
public class RangedWeaponItemMixin {
   @WrapOperation(
      method = {"getHeldProjectile(Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Predicate;)Lnet/minecraft/world/item/ItemStack;"},
      require = 0,
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"
      )}
   )
   private static ItemStack getHeldProjectile_Wrapped_BetterCombat(LivingEntity entity, InteractionHand hand, Operation<ItemStack> original) {
      ItemStack originalResult = (ItemStack)original.call(new Object[]{entity, hand});
      return entity instanceof Player player ? (ItemStack)player.getInventory().offhand.get(0) : originalResult;
   }
}
