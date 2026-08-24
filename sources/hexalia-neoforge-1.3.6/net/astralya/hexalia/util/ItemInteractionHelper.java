package net.astralya.hexalia.util;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ItemInteractionHelper {
   private ItemInteractionHelper() {
   }

   public static ItemInteractionResult tryHandleSingleItem(
      Level level, BlockPos pos, Player player, InteractionHand hand, ItemInteractionHelper.SingleItemStorage storage, Predicate<ItemStack> canInsert
   ) {
      ItemStack heldStack = player.getItemInHand(hand);
      ItemStack offhandStack = player.getOffhandItem();
      if (storage.isEmpty()) {
         if (!canInsertFromHand(hand, heldStack, offhandStack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         } else if (!heldStack.isEmpty() && canInsert.test(heldStack)) {
            if (storage.addItem(player.getAbilities().instabuild ? heldStack.copy() : heldStack)) {
               playItemSound(level, pos);
               return ItemInteractionResult.SUCCESS;
            } else {
               return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
         } else {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         }
      } else if (heldStack.isEmpty() && offhandStack.isEmpty() && hand == InteractionHand.MAIN_HAND) {
         ItemStack removed = storage.removeItem();
         if (!removed.isEmpty() && !player.isCreative() && !player.getInventory().add(removed)) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), removed);
         }

         playItemSound(level, pos);
         return ItemInteractionResult.SUCCESS;
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public static ItemInteractionResult tryInsertOneItem(
      Level level, BlockPos pos, Player player, InteractionHand hand, ItemInteractionHelper.ItemStorage storage, Predicate<ItemStack> canInsert
   ) {
      ItemStack heldStack = player.getItemInHand(hand);
      ItemStack offhandStack = player.getOffhandItem();
      if (!canInsertFromHand(hand, heldStack, offhandStack) || heldStack.isEmpty() || !canInsert.test(heldStack) || !storage.canInsertItem(heldStack)) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else if (level.isClientSide()) {
         return ItemInteractionResult.SUCCESS;
      } else if (!storage.addItem(player.getAbilities().instabuild ? heldStack.copy() : heldStack)) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         playItemSound(level, pos);
         return ItemInteractionResult.CONSUME;
      }
   }

   public static InteractionResult tryExtractOneItem(Level level, BlockPos pos, Player player, ItemInteractionHelper.ItemStorage storage) {
      if (!player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty() || !storage.canExtractItem()) {
         return InteractionResult.PASS;
      } else if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else {
         ItemStack removed = storage.removeItem();
         if (removed.isEmpty()) {
            return InteractionResult.PASS;
         } else {
            if (!player.isCreative() && !player.getInventory().add(removed)) {
               Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), removed);
            }

            playItemSound(level, pos);
            return InteractionResult.CONSUME;
         }
      }
   }

   private static boolean canInsertFromHand(InteractionHand hand, ItemStack heldStack, ItemStack offhandStack) {
      if (offhandStack.isEmpty()) {
         return true;
      } else {
         return hand == InteractionHand.MAIN_HAND && !offhandStack.is(ModTags.Items.OFFHAND_EQUIPMENT) && !(heldStack.getItem() instanceof BlockItem)
            ? false
            : hand != InteractionHand.OFF_HAND || !offhandStack.is(ModTags.Items.OFFHAND_EQUIPMENT);
      }
   }

   private static void playItemSound(Level level, BlockPos pos) {
      level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.25F, 0.25F);
   }

   public interface ItemStorage {
      boolean canInsertItem(ItemStack var1);

      boolean addItem(ItemStack var1);

      boolean canExtractItem();

      ItemStack removeItem();
   }

   public interface SingleItemStorage {
      boolean isEmpty();

      boolean addItem(ItemStack var1);

      ItemStack removeItem();
   }
}
