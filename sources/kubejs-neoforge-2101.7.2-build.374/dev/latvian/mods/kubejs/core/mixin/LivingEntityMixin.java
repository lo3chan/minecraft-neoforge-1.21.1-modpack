package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.LivingEntityKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@RemapPrefixForJS("kjs$")
@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin implements LivingEntityKJS {
   @Inject(
      method = {"eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;"},
      at = {@At("HEAD")}
   )
   private void foodEaten(Level level, ItemStack item, FoodProperties food, CallbackInfoReturnable<ItemStack> cir) {
      this.kjs$foodEaten(item, food);
   }
}
