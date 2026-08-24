package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.mixin.AetherMixinHooks;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ElytraLayer.class})
public class ElytraLayerMixin<T extends LivingEntity> {
   @ModifyReturnValue(
      at = {@At("RETURN")},
      method = {"getElytraTexture(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/resources/ResourceLocation;"},
      remap = false
   )
   private ResourceLocation getElytraTexture(
      ResourceLocation original, @Local(ordinal = 0,argsOnly = true) ItemStack stack, @Local(ordinal = 0,argsOnly = true) T entity
   ) {
      if (entity instanceof ArmorStand armorStand) {
         ItemStack capeStack = AetherMixinHooks.isCapeVisible(armorStand);
         if (!capeStack.isEmpty()) {
            ResourceLocation texture = AetherMixinHooks.getCapeTexture(capeStack);
            if (texture != null) {
               return texture;
            }
         }
      }

      return original;
   }
}
