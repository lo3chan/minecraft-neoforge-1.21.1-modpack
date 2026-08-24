package mezz.jei.common.transfer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class BasicRecipeTransferHandlerServer {
   private static final Logger LOGGER = LogManager.getLogger();

   private BasicRecipeTransferHandlerServer() {
   }

   public static void setItems(
      Player player,
      List<TransferOperation> transferOperations,
      List<Slot> craftingSlots,
      List<Slot> inventorySlots,
      boolean maxTransfer,
      boolean requireCompleteSets
   ) {
      if (RecipeTransferUtil.validateSlots(player, transferOperations, craftingSlots, inventorySlots)) {
         if (canClearCraftingSlots(player, craftingSlots)) {
            List<BasicRecipeTransferHandlerServer.RequiredTransfer> requiredTransfers = calculateRequiredTransfers(transferOperations, player);
            if (requiredTransfers != null) {
               boolean transferAsCompleteSets = requireCompleteSets || !maxTransfer;
               Map<Slot, ItemStack> recipeSlotToTakenStacks = takeItemsFromInventory(
                  player, requiredTransfers, craftingSlots, inventorySlots, transferAsCompleteSets, maxTransfer
               );
               if (recipeSlotToTakenStacks.isEmpty()) {
                  LOGGER.error("Tried to transfer recipe but was unable to remove any items from the inventory.");
               } else {
                  List<ItemStack> clearedCraftingItems = clearCraftingGrid(craftingSlots, player);
                  List<ItemStack> remainderItems = putItemsIntoCraftingGrid(recipeSlotToTakenStacks, requireCompleteSets);
                  stowItems(player, inventorySlots, clearedCraftingItems);
                  stowItems(player, inventorySlots, remainderItems);
                  AbstractContainerMenu container = player.containerMenu;
                  container.broadcastChanges();
               }
            }
         }
      }
   }

   private static boolean canClearCraftingSlots(Player player, List<Slot> craftingSlots) {
      for (Slot craftingSlot : craftingSlots) {
         ItemStack stack = craftingSlot.getItem();
         if (!stack.isEmpty() && (!craftingSlot.mayPickup(player) || !craftingSlot.mayPlace(stack))) {
            LOGGER.error("Tried to transfer recipe but crafting slot {} contains an item that cannot be moved: {}", craftingSlot.index, stack);
            return false;
         }
      }

      return true;
   }

   private static int getSlotStackLimit(Map<Slot, ItemStack> recipeSlotToTakenStacks, boolean requireCompleteSets) {
      return !requireCompleteSets ? 2147483647 : recipeSlotToTakenStacks.entrySet().stream().mapToInt(e -> {
         Slot craftingSlot = e.getKey();
         ItemStack transferItem = e.getValue();
         return craftingSlot.mayPlace(transferItem) ? craftingSlot.getMaxStackSize(transferItem) : 2147483647;
      }).min().orElse(2147483647);
   }

   private static List<ItemStack> clearCraftingGrid(List<Slot> craftingSlots, Player player) {
      List<ItemStack> clearedCraftingItems = new ArrayList<>();

      for (Slot craftingSlot : craftingSlots) {
         if (craftingSlot.mayPickup(player)) {
            ItemStack item = craftingSlot.getItem();
            if (!item.isEmpty() && craftingSlot.mayPlace(item)) {
               ItemStack craftingItem = craftingSlot.safeTake(2147483647, 2147483647, player);
               clearedCraftingItems.add(craftingItem);
            }
         }
      }

      return clearedCraftingItems;
   }

   private static List<ItemStack> putItemsIntoCraftingGrid(Map<Slot, ItemStack> recipeSlotToTakenStacks, boolean requireCompleteSets) {
      int slotStackLimit = getSlotStackLimit(recipeSlotToTakenStacks, requireCompleteSets);
      List<ItemStack> remainderItems = new ArrayList<>();
      recipeSlotToTakenStacks.forEach((slot, stack) -> {
         ItemStack remainder = slot.safeInsert(stack, slotStackLimit);
         if (!remainder.isEmpty()) {
            remainderItems.add(remainder);
         }
      });
      return remainderItems;
   }

   @Nullable
   private static List<BasicRecipeTransferHandlerServer.RequiredTransfer> calculateRequiredTransfers(List<TransferOperation> transferOperations, Player player) {
      List<BasicRecipeTransferHandlerServer.RequiredTransfer> requiredTransfers = new ArrayList<>(transferOperations.size());
      Map<Slot, ItemStack> targetSlotStacks = new HashMap<>();

      for (TransferOperation transferOperation : transferOperations) {
         Slot recipeSlot = transferOperation.craftingSlot(player.containerMenu);
         Slot inventorySlot = transferOperation.inventorySlot(player.containerMenu);
         if (!inventorySlot.allowModification(player)) {
            LOGGER.error("Tried to transfer recipe but was given an inventory slot that the player can't pickup from: {}", inventorySlot.index);
            return null;
         }

         ItemStack slotStack = inventorySlot.getItem();
         if (slotStack.isEmpty()) {
            LOGGER.error("Tried to transfer recipe but was given an empty inventory slot as an ingredient source: {}", inventorySlot.index);
            return null;
         }

         ItemStack stack = slotStack.copy();
         stack.setCount(transferOperation.count());
         if (!recipeSlot.mayPlace(stack)) {
            LOGGER.error("Tried to transfer recipe but crafting slot {} does not accept ingredient: {}", recipeSlot.index, stack);
            return null;
         }

         ItemStack targetSlotStack = targetSlotStacks.putIfAbsent(recipeSlot, stack);
         if (targetSlotStack != null && !ItemStack.isSameItemSameComponents(targetSlotStack, stack)) {
            LOGGER.error("Tried to transfer different ingredients into the same crafting slot {}: {} and {}", recipeSlot.index, targetSlotStack, stack);
            return null;
         }

         requiredTransfers.add(new BasicRecipeTransferHandlerServer.RequiredTransfer(recipeSlot, inventorySlot, stack));
      }

      return requiredTransfers;
   }

   private static Map<Slot, ItemStack> takeItemsFromInventory(
      Player player,
      List<BasicRecipeTransferHandlerServer.RequiredTransfer> requiredTransfers,
      List<Slot> craftingSlots,
      List<Slot> inventorySlots,
      boolean transferAsCompleteSets,
      boolean maxTransfer
   ) {
      if (!maxTransfer) {
         return removeOneSetOfItemsFromInventory(player, requiredTransfers, craftingSlots, inventorySlots, transferAsCompleteSets);
      } else {
         List<BasicRecipeTransferHandlerServer.RequiredTransfer> remainingRequiredTransfers = new ArrayList<>(requiredTransfers);
         Map<Slot, ItemStack> recipeSlotToResult = new HashMap<>(requiredTransfers.size());

         while (true) {
            removeFullRecipeSlots(remainingRequiredTransfers, recipeSlotToResult);
            if (remainingRequiredTransfers.isEmpty()) {
               break;
            }

            Map<Slot, ItemStack> foundItemsInSet = removeOneSetOfItemsFromInventory(
               player, remainingRequiredTransfers, craftingSlots, inventorySlots, transferAsCompleteSets
            );
            if (foundItemsInSet.isEmpty()) {
               break;
            }

            merge(recipeSlotToResult, foundItemsInSet);
         }

         return recipeSlotToResult;
      }
   }

   private static void removeFullRecipeSlots(List<BasicRecipeTransferHandlerServer.RequiredTransfer> requiredTransfers, Map<Slot, ItemStack> recipeSlotToResult) {
      Set<Slot> fullRecipeSlots = new HashSet<>();

      for (BasicRecipeTransferHandlerServer.RequiredTransfer requiredTransfer : requiredTransfers) {
         Slot recipeSlot = requiredTransfer.recipeSlot;
         ItemStack resultStack = recipeSlotToResult.get(recipeSlot);
         if (resultStack != null) {
            int requiredCount = getRequiredCount(requiredTransfers, recipeSlot);
            int maxStackSize = recipeSlot.mayPlace(resultStack) ? recipeSlot.getMaxStackSize(resultStack) : 2147483647;
            if (resultStack.getCount() + requiredCount > maxStackSize) {
               fullRecipeSlots.add(recipeSlot);
            }
         }
      }

      requiredTransfers.removeIf(requiredTransferx -> fullRecipeSlots.contains(requiredTransferx.recipeSlot));
   }

   private static int getRequiredCount(List<BasicRecipeTransferHandlerServer.RequiredTransfer> requiredTransfers, Slot recipeSlot) {
      return requiredTransfers.stream()
         .filter(requiredTransfer -> requiredTransfer.recipeSlot == recipeSlot)
         .mapToInt(requiredTransfer -> requiredTransfer.stack.getCount())
         .sum();
   }

   private static Map<Slot, ItemStack> removeOneSetOfItemsFromInventory(
      Player player,
      List<BasicRecipeTransferHandlerServer.RequiredTransfer> requiredTransfers,
      List<Slot> craftingSlots,
      List<Slot> inventorySlots,
      boolean transferAsCompleteSets
   ) {
      Map<Slot, ItemStack> originalSlotContents = null;
      if (transferAsCompleteSets) {
         originalSlotContents = new HashMap<>();
      }

      Map<Slot, ItemStack> foundItemsInSet = new HashMap<>(requiredTransfers.size());

      for (BasicRecipeTransferHandlerServer.RequiredTransfer requiredTransfer : requiredTransfers) {
         Slot recipeSlot = requiredTransfer.recipeSlot;
         ItemStack requiredStack = requiredTransfer.stack;
         Slot hint = requiredTransfer.hint;
         Slot sourceSlot = getSlotWithStack(player, requiredStack, craftingSlots, inventorySlots, hint).orElse(null);
         if (sourceSlot != null) {
            if (originalSlotContents != null && !originalSlotContents.containsKey(sourceSlot)) {
               originalSlotContents.put(sourceSlot, sourceSlot.getItem().copy());
            }

            ItemStack removedItemStack = sourceSlot.safeTake(requiredStack.getCount(), 2147483647, player);
            merge(foundItemsInSet, recipeSlot, removedItemStack);
         } else if (transferAsCompleteSets) {
            for (Entry<Slot, ItemStack> slotEntry : originalSlotContents.entrySet()) {
               ItemStack stack = slotEntry.getValue();
               Slot slot = slotEntry.getKey();
               slot.set(stack);
            }

            return Map.of();
         }
      }

      return foundItemsInSet;
   }

   private static void merge(Map<Slot, ItemStack> result, Map<Slot, ItemStack> addition) {
      addition.forEach((slot, itemStack) -> merge(result, slot, itemStack));
   }

   private static ItemStack merge(Map<Slot, ItemStack> result, Slot slot, ItemStack itemStack) {
      ItemStack resultItemStack = result.get(slot);
      if (resultItemStack == null) {
         resultItemStack = itemStack;
         result.put(slot, itemStack);
      } else {
         assert ItemStack.isSameItemSameComponents(resultItemStack, itemStack);

         resultItemStack.grow(itemStack.getCount());
      }

      return resultItemStack;
   }

   private static Optional<Slot> getSlotWithStack(Player player, ItemStack stack, List<Slot> craftingSlots, List<Slot> inventorySlots, Slot hint) {
      return getValidatedHintSlot(player, stack, hint)
         .or(() -> getSlotWithStack(player, craftingSlots, stack))
         .or(() -> getSlotWithStack(player, inventorySlots, stack));
   }

   private static Optional<Slot> getValidatedHintSlot(Player player, ItemStack stack, Slot hint) {
      return isValidAndMatches(player, hint, stack) ? Optional.of(hint) : Optional.empty();
   }

   private static void stowItems(Player player, List<Slot> inventorySlots, List<ItemStack> itemStacks) {
      for (ItemStack itemStack : itemStacks) {
         ItemStack remainder = stowItem(player, inventorySlots, itemStack);
         if (!remainder.isEmpty() && !player.getInventory().add(remainder)) {
            player.drop(remainder, false);
         }
      }
   }

   private static ItemStack stowItem(Player player, Collection<Slot> slots, ItemStack stack) {
      if (stack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack remainder = stack.copy();

         for (Slot slot : slots) {
            if (slot.mayPickup(player)) {
               ItemStack inventoryStack = slot.getItem();
               if (!inventoryStack.isEmpty() && inventoryStack.isStackable()) {
                  remainder = slot.safeInsert(remainder);
                  if (remainder.isEmpty()) {
                     return ItemStack.EMPTY;
                  }
               }
            }
         }

         for (Slot slotx : slots) {
            if (slotx.getItem().isEmpty()) {
               remainder = slotx.safeInsert(remainder);
               if (remainder.isEmpty()) {
                  return ItemStack.EMPTY;
               }
            }
         }

         return remainder;
      }
   }

   private static Optional<Slot> getSlotWithStack(Player player, Collection<Slot> slots, ItemStack itemStack) {
      return slots.stream().filter(slot -> isValidAndMatches(player, slot, itemStack)).findFirst();
   }

   private static boolean isValidAndMatches(Player player, Slot slot, ItemStack stack) {
      ItemStack containedStack = slot.getItem();
      return ItemStack.isSameItemSameComponents(stack, containedStack) && containedStack.getCount() >= stack.getCount() && slot.allowModification(player);
   }

   private record RequiredTransfer(Slot recipeSlot, Slot hint, ItemStack stack) {
   }
}
