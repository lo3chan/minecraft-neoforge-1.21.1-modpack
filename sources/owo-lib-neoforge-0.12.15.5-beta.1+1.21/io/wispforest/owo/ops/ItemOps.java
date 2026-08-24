package io.wispforest.owo.ops;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ItemOps {
   private ItemOps() {
   }

   public static boolean canStack(ItemStack base, ItemStack addition) {
      return base.isEmpty() || canIncreaseBy(base, addition.getCount()) && ItemStack.isSameItemSameComponents(base, addition);
   }

   public static boolean canIncrease(ItemStack stack) {
      return stack.isStackable() && stack.getCount() < stack.getMaxStackSize();
   }

   public static boolean canIncreaseBy(ItemStack stack, int by) {
      return stack.isStackable() && stack.getCount() + by <= stack.getMaxStackSize();
   }

   public static ItemStack singleCopy(ItemStack stack) {
      ItemStack copy = stack.copy();
      copy.setCount(1);
      return copy;
   }

   public static boolean emptyAwareDecrement(ItemStack stack) {
      return emptyAwareDecrement(stack, 1);
   }

   public static boolean emptyAwareDecrement(ItemStack stack, int amount) {
      stack.shrink(amount);
      return !stack.isEmpty();
   }

   public static boolean decrementPlayerHandItem(Player player, InteractionHand hand) {
      return decrementPlayerHandItem(player, hand, 1);
   }

   public static boolean decrementPlayerHandItem(Player player, InteractionHand hand, int amount) {
      ItemStack stack = player.getItemInHand(hand);
      if (!player.isCreative() && !emptyAwareDecrement(stack, amount)) {
         player.setItemInHand(hand, ItemStack.EMPTY);
      }

      return !stack.isEmpty();
   }

   public static void store(Provider registries, ItemStack stack, CompoundTag nbt, String key) {
      if (!stack.isEmpty()) {
         nbt.put(key, stack.save(registries));
      }
   }

   public static ItemStack get(Provider registries, CompoundTag nbt, String key) {
      if (!nbt.contains(key, 10)) {
         return ItemStack.EMPTY;
      } else {
         CompoundTag stackNbt = nbt.getCompound(key);
         return ItemStack.parseOptional(registries, stackNbt);
      }
   }
}
