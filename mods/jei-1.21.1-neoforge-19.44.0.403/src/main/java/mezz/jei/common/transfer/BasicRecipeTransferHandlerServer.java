/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.common.transfer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import mezz.jei.common.transfer.RecipeTransferUtil;
import mezz.jei.common.transfer.TransferOperation;
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

    public static void setItems(Player player, List<TransferOperation> transferOperations, List<Slot> craftingSlots, List<Slot> inventorySlots, boolean maxTransfer, boolean requireCompleteSets) {
        if (!RecipeTransferUtil.validateSlots(player, transferOperations, craftingSlots, inventorySlots)) {
            return;
        }
        if (!BasicRecipeTransferHandlerServer.canClearCraftingSlots(player, craftingSlots)) {
            return;
        }
        List<RequiredTransfer> requiredTransfers = BasicRecipeTransferHandlerServer.calculateRequiredTransfers(transferOperations, player);
        if (requiredTransfers == null) {
            return;
        }
        boolean transferAsCompleteSets = requireCompleteSets || !maxTransfer;
        Map<Slot, ItemStack> recipeSlotToTakenStacks = BasicRecipeTransferHandlerServer.takeItemsFromInventory(player, requiredTransfers, craftingSlots, inventorySlots, transferAsCompleteSets, maxTransfer);
        if (recipeSlotToTakenStacks.isEmpty()) {
            LOGGER.error("Tried to transfer recipe but was unable to remove any items from the inventory.");
            return;
        }
        List<ItemStack> clearedCraftingItems = BasicRecipeTransferHandlerServer.clearCraftingGrid(craftingSlots, player);
        List<ItemStack> remainderItems = BasicRecipeTransferHandlerServer.putItemsIntoCraftingGrid(recipeSlotToTakenStacks, requireCompleteSets);
        BasicRecipeTransferHandlerServer.stowItems(player, inventorySlots, clearedCraftingItems);
        BasicRecipeTransferHandlerServer.stowItems(player, inventorySlots, remainderItems);
        AbstractContainerMenu container = player.containerMenu;
        container.broadcastChanges();
    }

    private static boolean canClearCraftingSlots(Player player, List<Slot> craftingSlots) {
        for (Slot craftingSlot : craftingSlots) {
            ItemStack stack = craftingSlot.getItem();
            if (stack.isEmpty() || craftingSlot.mayPickup(player) && craftingSlot.mayPlace(stack)) continue;
            LOGGER.error("Tried to transfer recipe but crafting slot {} contains an item that cannot be moved: {}", (Object)craftingSlot.index, (Object)stack);
            return false;
        }
        return true;
    }

    private static int getSlotStackLimit(Map<Slot, ItemStack> recipeSlotToTakenStacks, boolean requireCompleteSets) {
        if (!requireCompleteSets) {
            return Integer.MAX_VALUE;
        }
        return recipeSlotToTakenStacks.entrySet().stream().mapToInt(e -> {
            ItemStack transferItem;
            Slot craftingSlot = (Slot)e.getKey();
            if (craftingSlot.mayPlace(transferItem = (ItemStack)e.getValue())) {
                return craftingSlot.getMaxStackSize(transferItem);
            }
            return Integer.MAX_VALUE;
        }).min().orElse(Integer.MAX_VALUE);
    }

    private static List<ItemStack> clearCraftingGrid(List<Slot> craftingSlots, Player player) {
        ArrayList<ItemStack> clearedCraftingItems = new ArrayList<ItemStack>();
        for (Slot craftingSlot : craftingSlots) {
            ItemStack item;
            if (!craftingSlot.mayPickup(player) || (item = craftingSlot.getItem()).isEmpty() || !craftingSlot.mayPlace(item)) continue;
            ItemStack craftingItem = craftingSlot.safeTake(Integer.MAX_VALUE, Integer.MAX_VALUE, player);
            clearedCraftingItems.add(craftingItem);
        }
        return clearedCraftingItems;
    }

    private static List<ItemStack> putItemsIntoCraftingGrid(Map<Slot, ItemStack> recipeSlotToTakenStacks, boolean requireCompleteSets) {
        int slotStackLimit = BasicRecipeTransferHandlerServer.getSlotStackLimit(recipeSlotToTakenStacks, requireCompleteSets);
        ArrayList<ItemStack> remainderItems = new ArrayList<ItemStack>();
        recipeSlotToTakenStacks.forEach((slot, stack) -> {
            ItemStack remainder = slot.safeInsert(stack, slotStackLimit);
            if (!remainder.isEmpty()) {
                remainderItems.add(remainder);
            }
        });
        return remainderItems;
    }

    @Nullable
    private static List<RequiredTransfer> calculateRequiredTransfers(List<TransferOperation> transferOperations, Player player) {
        ArrayList<RequiredTransfer> requiredTransfers = new ArrayList<RequiredTransfer>(transferOperations.size());
        HashMap<Slot, ItemStack> targetSlotStacks = new HashMap<Slot, ItemStack>();
        for (TransferOperation transferOperation : transferOperations) {
            Slot recipeSlot = transferOperation.craftingSlot(player.containerMenu);
            Slot inventorySlot = transferOperation.inventorySlot(player.containerMenu);
            if (!inventorySlot.allowModification(player)) {
                LOGGER.error("Tried to transfer recipe but was given an inventory slot that the player can't pickup from: {}", (Object)inventorySlot.index);
                return null;
            }
            ItemStack slotStack = inventorySlot.getItem();
            if (slotStack.isEmpty()) {
                LOGGER.error("Tried to transfer recipe but was given an empty inventory slot as an ingredient source: {}", (Object)inventorySlot.index);
                return null;
            }
            ItemStack stack = slotStack.copy();
            stack.setCount(transferOperation.count());
            if (!recipeSlot.mayPlace(stack)) {
                LOGGER.error("Tried to transfer recipe but crafting slot {} does not accept ingredient: {}", (Object)recipeSlot.index, (Object)stack);
                return null;
            }
            ItemStack targetSlotStack = targetSlotStacks.putIfAbsent(recipeSlot, stack);
            if (targetSlotStack != null && !ItemStack.isSameItemSameComponents((ItemStack)targetSlotStack, (ItemStack)stack)) {
                LOGGER.error("Tried to transfer different ingredients into the same crafting slot {}: {} and {}", (Object)recipeSlot.index, (Object)targetSlotStack, (Object)stack);
                return null;
            }
            requiredTransfers.add(new RequiredTransfer(recipeSlot, inventorySlot, stack));
        }
        return requiredTransfers;
    }

    private static Map<Slot, ItemStack> takeItemsFromInventory(Player player, List<RequiredTransfer> requiredTransfers, List<Slot> craftingSlots, List<Slot> inventorySlots, boolean transferAsCompleteSets, boolean maxTransfer) {
        if (!maxTransfer) {
            return BasicRecipeTransferHandlerServer.removeOneSetOfItemsFromInventory(player, requiredTransfers, craftingSlots, inventorySlots, transferAsCompleteSets);
        }
        ArrayList<RequiredTransfer> remainingRequiredTransfers = new ArrayList<RequiredTransfer>(requiredTransfers);
        HashMap<Slot, ItemStack> recipeSlotToResult = new HashMap<Slot, ItemStack>(requiredTransfers.size());
        while (true) {
            Map<Slot, ItemStack> foundItemsInSet;
            BasicRecipeTransferHandlerServer.removeFullRecipeSlots(remainingRequiredTransfers, recipeSlotToResult);
            if (remainingRequiredTransfers.isEmpty() || (foundItemsInSet = BasicRecipeTransferHandlerServer.removeOneSetOfItemsFromInventory(player, remainingRequiredTransfers, craftingSlots, inventorySlots, transferAsCompleteSets)).isEmpty()) break;
            BasicRecipeTransferHandlerServer.merge(recipeSlotToResult, foundItemsInSet);
        }
        return recipeSlotToResult;
    }

    private static void removeFullRecipeSlots(List<RequiredTransfer> requiredTransfers, Map<Slot, ItemStack> recipeSlotToResult) {
        HashSet<Slot> fullRecipeSlots = new HashSet<Slot>();
        for (RequiredTransfer requiredTransfer2 : requiredTransfers) {
            int maxStackSize;
            Slot recipeSlot = requiredTransfer2.recipeSlot;
            ItemStack resultStack = recipeSlotToResult.get(recipeSlot);
            if (resultStack == null) continue;
            int requiredCount = BasicRecipeTransferHandlerServer.getRequiredCount(requiredTransfers, recipeSlot);
            int n = maxStackSize = recipeSlot.mayPlace(resultStack) ? recipeSlot.getMaxStackSize(resultStack) : Integer.MAX_VALUE;
            if (resultStack.getCount() + requiredCount <= maxStackSize) continue;
            fullRecipeSlots.add(recipeSlot);
        }
        requiredTransfers.removeIf(requiredTransfer -> fullRecipeSlots.contains(requiredTransfer.recipeSlot));
    }

    private static int getRequiredCount(List<RequiredTransfer> requiredTransfers, Slot recipeSlot) {
        return requiredTransfers.stream().filter(requiredTransfer -> requiredTransfer.recipeSlot == recipeSlot).mapToInt(requiredTransfer -> requiredTransfer.stack.getCount()).sum();
    }

    private static Map<Slot, ItemStack> removeOneSetOfItemsFromInventory(Player player, List<RequiredTransfer> requiredTransfers, List<Slot> craftingSlots, List<Slot> inventorySlots, boolean transferAsCompleteSets) {
        HashMap<Slot, ItemStack> originalSlotContents = null;
        if (transferAsCompleteSets) {
            originalSlotContents = new HashMap<Slot, ItemStack>();
        }
        HashMap<Slot, ItemStack> foundItemsInSet = new HashMap<Slot, ItemStack>(requiredTransfers.size());
        for (RequiredTransfer requiredTransfer : requiredTransfers) {
            Slot recipeSlot = requiredTransfer.recipeSlot;
            ItemStack requiredStack = requiredTransfer.stack;
            Slot hint = requiredTransfer.hint;
            Slot sourceSlot = BasicRecipeTransferHandlerServer.getSlotWithStack(player, requiredStack, craftingSlots, inventorySlots, hint).orElse(null);
            if (sourceSlot != null) {
                if (originalSlotContents != null && !originalSlotContents.containsKey(sourceSlot)) {
                    originalSlotContents.put(sourceSlot, sourceSlot.getItem().copy());
                }
                ItemStack removedItemStack = sourceSlot.safeTake(requiredStack.getCount(), Integer.MAX_VALUE, player);
                BasicRecipeTransferHandlerServer.merge(foundItemsInSet, recipeSlot, removedItemStack);
                continue;
            }
            if (!transferAsCompleteSets) continue;
            for (Map.Entry slotEntry : originalSlotContents.entrySet()) {
                ItemStack stack = (ItemStack)slotEntry.getValue();
                Slot slot = (Slot)slotEntry.getKey();
                slot.set(stack);
            }
            return Map.of();
        }
        return foundItemsInSet;
    }

    private static void merge(Map<Slot, ItemStack> result, Map<Slot, ItemStack> addition) {
        addition.forEach((slot, itemStack) -> BasicRecipeTransferHandlerServer.merge(result, slot, itemStack));
    }

    private static ItemStack merge(Map<Slot, ItemStack> result, Slot slot, ItemStack itemStack) {
        ItemStack resultItemStack = result.get(slot);
        if (resultItemStack == null) {
            resultItemStack = itemStack;
            result.put(slot, resultItemStack);
        } else {
            assert (ItemStack.isSameItemSameComponents((ItemStack)resultItemStack, (ItemStack)itemStack));
            resultItemStack.grow(itemStack.getCount());
        }
        return resultItemStack;
    }

    private static Optional<Slot> getSlotWithStack(Player player, ItemStack stack, List<Slot> craftingSlots, List<Slot> inventorySlots, Slot hint) {
        return BasicRecipeTransferHandlerServer.getValidatedHintSlot(player, stack, hint).or(() -> BasicRecipeTransferHandlerServer.getSlotWithStack(player, craftingSlots, stack)).or(() -> BasicRecipeTransferHandlerServer.getSlotWithStack(player, inventorySlots, stack));
    }

    private static Optional<Slot> getValidatedHintSlot(Player player, ItemStack stack, Slot hint) {
        if (BasicRecipeTransferHandlerServer.isValidAndMatches(player, hint, stack)) {
            return Optional.of(hint);
        }
        return Optional.empty();
    }

    private static void stowItems(Player player, List<Slot> inventorySlots, List<ItemStack> itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            ItemStack remainder = BasicRecipeTransferHandlerServer.stowItem(player, inventorySlots, itemStack);
            if (remainder.isEmpty() || player.getInventory().add(remainder)) continue;
            player.drop(remainder, false);
        }
    }

    private static ItemStack stowItem(Player player, Collection<Slot> slots, ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack remainder = stack.copy();
        for (Slot slot : slots) {
            ItemStack inventoryStack;
            if (!slot.mayPickup(player) || (inventoryStack = slot.getItem()).isEmpty() || !inventoryStack.isStackable() || !(remainder = slot.safeInsert(remainder)).isEmpty()) continue;
            return ItemStack.EMPTY;
        }
        for (Slot slot : slots) {
            if (!slot.getItem().isEmpty() || !(remainder = slot.safeInsert(remainder)).isEmpty()) continue;
            return ItemStack.EMPTY;
        }
        return remainder;
    }

    private static Optional<Slot> getSlotWithStack(Player player, Collection<Slot> slots, ItemStack itemStack) {
        return slots.stream().filter(slot -> BasicRecipeTransferHandlerServer.isValidAndMatches(player, slot, itemStack)).findFirst();
    }

    private static boolean isValidAndMatches(Player player, Slot slot, ItemStack stack) {
        ItemStack containedStack = slot.getItem();
        return ItemStack.isSameItemSameComponents((ItemStack)stack, (ItemStack)containedStack) && containedStack.getCount() >= stack.getCount() && slot.allowModification(player);
    }

    private record RequiredTransfer(Slot recipeSlot, Slot hint, ItemStack stack) {
    }
}

