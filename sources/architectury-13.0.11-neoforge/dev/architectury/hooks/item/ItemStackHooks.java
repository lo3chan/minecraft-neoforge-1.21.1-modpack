package dev.architectury.hooks.item;

import dev.architectury.hooks.item.forge.ItemStackHooksImpl;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public final class ItemStackHooks {
   private ItemStackHooks() {
   }

   public static ItemStack copyWithCount(ItemStack stack, int count) {
      ItemStack copy = stack.copy();
      copy.setCount(count);
      return copy;
   }

   public static void giveItem(ServerPlayer player, ItemStack stack) {
      boolean bl = player.getInventory().add(stack);
      if (bl && stack.isEmpty()) {
         stack.setCount(1);
         ItemEntity entity = player.drop(stack, false);
         if (entity != null) {
            entity.makeFakeItem();
         }

         player.level()
            .playSound(
               null,
               player.getX(),
               player.getY(),
               player.getZ(),
               SoundEvents.ITEM_PICKUP,
               SoundSource.PLAYERS,
               0.2F,
               ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
            );
         player.inventoryMenu.broadcastChanges();
      } else {
         ItemEntity entity = player.drop(stack, false);
         if (entity != null) {
            entity.setNoPickUpDelay();
            entity.setTarget(player.getUUID());
         }
      }
   }

   @ExpectPlatform
   @Transformed
   public static boolean hasCraftingRemainingItem(ItemStack stack) {
      return ItemStackHooksImpl.hasCraftingRemainingItem(stack);
   }

   @ExpectPlatform
   @Transformed
   public static ItemStack getCraftingRemainingItem(ItemStack stack) {
      return ItemStackHooksImpl.getCraftingRemainingItem(stack);
   }
}
