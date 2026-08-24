package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.MobAccessoryAttachment;
import com.aetherteam.aether.event.hooks.EntityHooks;
import com.aetherteam.aether.mixin.AetherMixinHooks;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Mob.class})
public class MobMixin {
   @ModifyReturnValue(
      at = {@At("RETURN")},
      method = {"canTakeItem(Lnet/minecraft/world/item/ItemStack;)Z"}
   )
   private boolean canTakeItem(boolean original, @Local(ordinal = 0,argsOnly = true) ItemStack stack) {
      Mob mob = (Mob)this;
      if (EntityHooks.canMobSpawnWithAccessories(mob)) {
         SlotTypeReference identifier = AetherMixinHooks.getIdentifierForItem(mob, stack);
         if (identifier != null) {
            ItemStack accessory = AetherMixinHooks.getItemByIdentifier(mob, identifier);
            if (accessory.isEmpty()) {
               return true;
            }
         }
      }

      return original;
   }

   @ModifyReturnValue(
      at = {@At("RETURN")},
      method = {"equipItemIfPossible(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"}
   )
   private ItemStack equipItemIfPossible(ItemStack original, @Local(ordinal = 0,argsOnly = true) ItemStack stack) {
      Mob mob = (Mob)this;
      MobAccessoryAttachment data = (MobAccessoryAttachment)mob.getData(AetherDataAttachments.MOB_ACCESSORY);
      SlotTypeReference identifier = AetherMixinHooks.getIdentifierForItem(mob, stack);
      if (identifier != null) {
         ItemStack accessory = AetherMixinHooks.getItemByIdentifier(mob, identifier);
         boolean flag = AetherMixinHooks.canReplaceCurrentAccessory(mob, stack, accessory);
         if (flag && mob.canHoldItem(stack)) {
            double dropChance = data.getEquipmentDropChance(identifier);
            if (!accessory.isEmpty() && Math.max(mob.getRandom().nextFloat() - 0.1F, 0.0F) < dropChance) {
               mob.spawnAtLocation(accessory);
            }

            AetherMixinHooks.setItemByIdentifier(mob, stack, identifier);
            data.setGuaranteedDrop(identifier);
            mob.setPersistenceRequired();
            return stack;
         }
      }

      return original;
   }
}
