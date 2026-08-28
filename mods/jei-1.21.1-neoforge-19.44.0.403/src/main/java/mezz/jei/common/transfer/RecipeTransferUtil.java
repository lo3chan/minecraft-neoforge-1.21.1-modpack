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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferManager;
import mezz.jei.common.transfer.RecipeTransferErrorInternal;
import mezz.jei.common.transfer.RecipeTransferOperationsResult;
import mezz.jei.common.transfer.TransferOperation;
import mezz.jei.common.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class RecipeTransferUtil {
    private static final Logger LOGGER = LogManager.getLogger();

    private RecipeTransferUtil() {
    }

    public static Optional<IRecipeTransferError> getTransferRecipeError(IRecipeTransferManager recipeTransferManager, AbstractContainerMenu container, IRecipeLayoutDrawable<?> recipeLayout, Player player) {
        return RecipeTransferUtil.transferRecipe(recipeTransferManager, container, recipeLayout, player, false, false);
    }

    public static boolean transferRecipe(IRecipeTransferManager recipeTransferManager, AbstractContainerMenu container, IRecipeLayoutDrawable<?> recipeLayout, Player player, boolean maxTransfer) {
        return RecipeTransferUtil.transferRecipe(recipeTransferManager, container, recipeLayout, player, maxTransfer, true).map(error -> error.getType().allowsTransfer).orElse(true);
    }

    private static <C extends AbstractContainerMenu, R> Optional<IRecipeTransferError> transferRecipe(IRecipeTransferManager recipeTransferManager, C container, IRecipeLayoutDrawable<R> recipeLayout, Player player, boolean maxTransfer, boolean doTransfer) {
        IRecipeCategory<R> recipeCategory = recipeLayout.getRecipeCategory();
        Optional<IRecipeTransferHandler<C, R>> recipeTransferHandler = recipeTransferManager.getRecipeTransferHandler(container, recipeCategory);
        if (recipeTransferHandler.isEmpty()) {
            if (doTransfer) {
                LOGGER.error("No Recipe Transfer handler for container {}", container.getClass());
            }
            return Optional.of(RecipeTransferErrorInternal.INSTANCE);
        }
        IRecipeTransferHandler<C, R> transferHandler = recipeTransferHandler.get();
        IRecipeSlotsView recipeSlotsView = recipeLayout.getRecipeSlotsView();
        try {
            IRecipeTransferError transferError = transferHandler.transferRecipe(container, recipeLayout.getRecipe(), recipeSlotsView, player, maxTransfer, doTransfer);
            return Optional.ofNullable(transferError);
        }
        catch (RuntimeException e) {
            LOGGER.error("Recipe transfer handler '{}' for container '{}' and recipe type '{}' threw an error: ", transferHandler.getClass(), transferHandler.getContainerClass(), recipeCategory.getRecipeType(), (Object)e);
            return Optional.of(RecipeTransferErrorInternal.INSTANCE);
        }
    }

    public static boolean validateSlots(Player player, Collection<TransferOperation> transferOperations, Collection<Slot> craftingSlots, Collection<Slot> inventorySlots) {
        AbstractContainerMenu container = player.containerMenu;
        List<Integer> invalidOperationSlotIndexes = transferOperations.stream().flatMap(op -> Stream.of(op.inventorySlotId(), op.craftingSlotId())).distinct().filter(slotId -> !RecipeTransferUtil.isValidSlotId(container, slotId)).toList();
        if (!invalidOperationSlotIndexes.isEmpty()) {
            LOGGER.error("Transfer request has invalid slot ids in its transfer operations: {}", (Object)StringUtil.intsToString(invalidOperationSlotIndexes));
            return false;
        }
        Set<Integer> inventorySlotIndexes = inventorySlots.stream().map(s -> s.index).collect(Collectors.toSet());
        Set<Integer> craftingSlotIndexes = craftingSlots.stream().map(s -> s.index).collect(Collectors.toSet());
        List<Integer> invalidRecipeIndexes = transferOperations.stream().map(op -> op.craftingSlot(player.containerMenu)).map(s -> s.index).filter(s -> !craftingSlotIndexes.contains(s)).toList();
        if (!invalidRecipeIndexes.isEmpty()) {
            LOGGER.error("Transfer request has invalid slots for the destination of the recipe,  the slots are not included in the list of crafting slots. {}", (Object)StringUtil.intsToString(invalidRecipeIndexes));
            return false;
        }
        List<Integer> invalidInventorySlotIndexes = transferOperations.stream().map(op -> op.inventorySlot(player.containerMenu)).map(s -> s.index).filter(s -> !inventorySlotIndexes.contains(s) && !craftingSlotIndexes.contains(s)).toList();
        if (!invalidInventorySlotIndexes.isEmpty()) {
            LOGGER.error("Transfer request has invalid source slots for the inventory stacks for the recipe, the slots are not included in the list of inventory slots or recipe slots. {}\n inventory slots: {}\n crafting slots: {}", (Object)StringUtil.intsToString(invalidInventorySlotIndexes), (Object)StringUtil.intsToString(inventorySlotIndexes), (Object)StringUtil.intsToString(craftingSlotIndexes));
            return false;
        }
        Set<Integer> overlappingSlots = inventorySlotIndexes.stream().filter(craftingSlotIndexes::contains).collect(Collectors.toSet());
        if (!overlappingSlots.isEmpty()) {
            LOGGER.error("Transfer request has invalid slots, inventorySlots and craftingSlots should not share any slot, but both have: {}", (Object)StringUtil.intsToString(overlappingSlots));
            return false;
        }
        List<Integer> invalidFakeSlots = Stream.concat(craftingSlots.stream(), inventorySlots.stream()).filter(Slot::isFake).map(slot -> slot.index).toList();
        if (!invalidFakeSlots.isEmpty()) {
            LOGGER.error("Transfer request has invalid slots, they are fake slots (recipe outputs): {}", (Object)StringUtil.intsToString(invalidFakeSlots));
            return false;
        }
        return true;
    }

    private static boolean isValidSlotId(AbstractContainerMenu container, int slotId) {
        return slotId >= 0 && slotId < container.slots.size();
    }

    public static RecipeTransferOperationsResult getRecipeTransferOperations(IStackHelper stackhelper, Map<Slot, ItemStack> availableItemStacks, List<IRecipeSlotView> requiredItemStacks, List<Slot> craftingSlots) {
        RecipeTransferOperationsResult transferOperations = new RecipeTransferOperationsResult();
        ArrayList<RequiredSlot> requiredSlots = new ArrayList<RequiredSlot>();
        IdentityHashMap<IRecipeSlotView, Map> slotRequirementCache = new IdentityHashMap<IRecipeSlotView, Map>();
        HashMap<Slot, Integer> availableCounts = new HashMap<Slot, Integer>();
        HashMap<Slot, Object> availableUids = new HashMap<Slot, Object>();
        availableItemStacks.forEach((slot, stack) -> {
            if (!stack.isEmpty()) {
                availableCounts.put((Slot)slot, stack.getCount());
                availableUids.put((Slot)slot, stackhelper.getUidForStack((ItemStack)stack, UidContext.Recipe));
            }
        });
        for (int i = 0; i < requiredItemStacks.size(); ++i) {
            IRecipeSlotView requiredItemStack = requiredItemStacks.get(i);
            if (requiredItemStack.isEmpty()) continue;
            Slot craftingSlot = craftingSlots.get(i);
            Map requiredCountsByUid = slotRequirementCache.computeIfAbsent(requiredItemStack, s -> RecipeTransferUtil.calculateRequiredCountsByUid(s, stackhelper));
            List<CandidateGroup> candidateGroups = RecipeTransferUtil.getCandidateGroups(availableItemStacks, availableUids, requiredCountsByUid);
            if (candidateGroups.isEmpty()) {
                transferOperations.missingItems.add(requiredItemStack);
                continue;
            }
            requiredSlots.add(new RequiredSlot(i, requiredItemStack, craftingSlot, candidateGroups));
        }
        if (!transferOperations.missingItems.isEmpty()) {
            return transferOperations;
        }
        AssignmentResult assignmentResult = RecipeTransferUtil.findAssignments(requiredSlots, availableCounts);
        if (assignmentResult.assignedIndexes().size() != requiredSlots.size()) {
            for (RequiredSlot requiredSlot : requiredSlots) {
                if (assignmentResult.assignedIndexes().contains(requiredSlot.index)) continue;
                transferOperations.missingItems.add(requiredSlot.recipeSlotView);
            }
            return transferOperations;
        }
        assignmentResult.assignments().stream().sorted(Comparator.comparingInt(Assignment::requiredIndex)).map(assignment -> new TransferOperation(assignment.sourceSlot.index, assignment.craftingSlot.index, assignment.count)).forEach(transferOperations.results::add);
        return transferOperations;
    }

    private static List<CandidateGroup> getCandidateGroups(Map<Slot, ItemStack> availableItemStacks, Map<Slot, Object> availableUids, Map<Object, Integer> requiredCountsByUid) {
        HashMap<Object, List> candidatesByUid = new HashMap<Object, List>();
        availableItemStacks.forEach((slot, stack) -> {
            Object uid = availableUids.get(slot);
            if (uid != null && requiredCountsByUid.containsKey(uid)) {
                candidatesByUid.computeIfAbsent(uid, ignored -> new ArrayList()).add(new CandidateSlot((Slot)slot, (ItemStack)stack));
            }
        });
        ArrayList<CandidateGroup> candidateGroups = new ArrayList<CandidateGroup>();
        candidatesByUid.forEach((uid, candidates) -> {
            candidates.sort((a, b) -> {
                int compare = Integer.compare(a.stack.getCount(), b.stack.getCount());
                if (compare == 0) {
                    compare = Integer.compare(a.slot.index, b.slot.index);
                }
                return compare;
            });
            int totalCount = candidates.stream().mapToInt(candidate -> candidate.stack.getCount()).sum();
            int requiredCount = requiredCountsByUid.getOrDefault(uid, 1);
            if (totalCount >= requiredCount) {
                candidateGroups.add(new CandidateGroup(uid, requiredCount, (List<CandidateSlot>)candidates, totalCount));
            }
        });
        candidateGroups.sort((a, b) -> {
            int compare = Integer.compare(b.totalCount, a.totalCount);
            if (compare == 0) {
                compare = Integer.compare(a.getFirstSlotIndex(), b.getFirstSlotIndex());
            }
            return compare;
        });
        return candidateGroups;
    }

    private static AssignmentResult findAssignments(List<RequiredSlot> requiredSlots, Map<Slot, Integer> availableCounts) {
        ArrayList<Assignment> assignments = new ArrayList<Assignment>();
        ArrayList<Assignment> bestAssignments = new ArrayList<Assignment>();
        HashSet<Integer> assignedIndexes = new HashSet<Integer>();
        HashSet<Integer> bestAssignedIndexes = new HashSet<Integer>();
        RecipeTransferUtil.assignRequiredSlots(requiredSlots, availableCounts, new HashSet<Integer>(), assignedIndexes, assignments, bestAssignments, bestAssignedIndexes);
        return new AssignmentResult(bestAssignments, bestAssignedIndexes);
    }

    private static boolean assignRequiredSlots(List<RequiredSlot> requiredSlots, Map<Slot, Integer> availableCounts, Set<Integer> processedIndexes, Set<Integer> assignedIndexes, List<Assignment> assignments, List<Assignment> bestAssignments, Set<Integer> bestAssignedIndexes) {
        if (assignedIndexes.size() > bestAssignedIndexes.size()) {
            bestAssignments.clear();
            bestAssignments.addAll(assignments);
            bestAssignedIndexes.clear();
            bestAssignedIndexes.addAll(assignedIndexes);
        }
        if (processedIndexes.size() == requiredSlots.size()) {
            return assignedIndexes.size() == requiredSlots.size();
        }
        RequiredSlot requiredSlot = RecipeTransferUtil.getMostConstrainedRequiredSlot(requiredSlots, availableCounts, processedIndexes);
        if (requiredSlot == null) {
            return assignedIndexes.size() == requiredSlots.size();
        }
        processedIndexes.add(requiredSlot.index);
        boolean hasAvailableCandidate = false;
        for (CandidateGroup candidateGroup : requiredSlot.candidateGroups) {
            List<Assignment> takenAssignments = RecipeTransferUtil.takeRequiredItems(requiredSlot, candidateGroup, availableCounts);
            if (takenAssignments.isEmpty()) continue;
            hasAvailableCandidate = true;
            assignments.addAll(takenAssignments);
            assignedIndexes.add(requiredSlot.index);
            if (RecipeTransferUtil.assignRequiredSlots(requiredSlots, availableCounts, processedIndexes, assignedIndexes, assignments, bestAssignments, bestAssignedIndexes)) {
                return true;
            }
            assignedIndexes.remove(requiredSlot.index);
            assignments.subList(assignments.size() - takenAssignments.size(), assignments.size()).clear();
            RecipeTransferUtil.restoreAssignments(takenAssignments, availableCounts);
        }
        if (!hasAvailableCandidate && RecipeTransferUtil.assignRequiredSlots(requiredSlots, availableCounts, processedIndexes, assignedIndexes, assignments, bestAssignments, bestAssignedIndexes)) {
            return true;
        }
        processedIndexes.remove(requiredSlot.index);
        return false;
    }

    private static List<Assignment> takeRequiredItems(RequiredSlot requiredSlot, CandidateGroup candidateGroup, Map<Slot, Integer> availableCounts) {
        int remainingCount = candidateGroup.requiredCount;
        ArrayList<Assignment> takenAssignments = new ArrayList<Assignment>();
        for (CandidateSlot candidate : candidateGroup.candidates) {
            int availableCount = availableCounts.getOrDefault(candidate.slot, 0);
            if (availableCount <= 0) continue;
            int count = Math.min(availableCount, remainingCount);
            availableCounts.put(candidate.slot, availableCount - count);
            takenAssignments.add(new Assignment(requiredSlot.index, requiredSlot.craftingSlot, candidate.slot, count));
            if ((remainingCount -= count) != 0) continue;
            return takenAssignments;
        }
        RecipeTransferUtil.restoreAssignments(takenAssignments, availableCounts);
        return List.of();
    }

    private static void restoreAssignments(List<Assignment> assignments, Map<Slot, Integer> availableCounts) {
        for (Assignment assignment : assignments) {
            availableCounts.merge(assignment.sourceSlot, assignment.count, Integer::sum);
        }
    }

    @Nullable
    private static RequiredSlot getMostConstrainedRequiredSlot(List<RequiredSlot> requiredSlots, Map<Slot, Integer> availableCounts, Set<Integer> processedIndexes) {
        RequiredSlot best = null;
        int bestAvailableCandidateCount = Integer.MAX_VALUE;
        for (RequiredSlot requiredSlot : requiredSlots) {
            if (processedIndexes.contains(requiredSlot.index)) continue;
            int availableCandidateCount = RecipeTransferUtil.countAvailableCandidates(requiredSlot, availableCounts);
            if (best != null && availableCandidateCount >= bestAvailableCandidateCount) continue;
            best = requiredSlot;
            bestAvailableCandidateCount = availableCandidateCount;
        }
        return best;
    }

    private static int countAvailableCandidates(RequiredSlot requiredSlot, Map<Slot, Integer> availableCounts) {
        int count = 0;
        for (CandidateGroup candidateGroup : requiredSlot.candidateGroups) {
            int availableCount = candidateGroup.candidates.stream().mapToInt(candidate -> availableCounts.getOrDefault(candidate.slot, 0)).sum();
            if (availableCount < candidateGroup.requiredCount) continue;
            ++count;
        }
        return count;
    }

    private static Map<Object, Integer> calculateRequiredCountsByUid(IRecipeSlotView recipeSlotView, IStackHelper stackhelper) {
        List<@Nullable ITypedIngredient<?>> allIngredientsList = recipeSlotView.getAllIngredientsList();
        HashMap<Object, Integer> requiredCountsByUid = new HashMap<Object, Integer>(allIngredientsList.size());
        for (ITypedIngredient<?> typedIngredient : allIngredientsList) {
            ITypedIngredient<ItemStack> typedItemStack;
            if (typedIngredient == null || (typedItemStack = typedIngredient.castToItemStackType()) == null) continue;
            Object uid = stackhelper.getUidForStack(typedItemStack, UidContext.Recipe);
            int count = Math.max(1, typedItemStack.getIngredient().getCount());
            requiredCountsByUid.merge(uid, count, Math::max);
        }
        return requiredCountsByUid;
    }

    private record RequiredSlot(int index, IRecipeSlotView recipeSlotView, Slot craftingSlot, List<CandidateGroup> candidateGroups) {
    }

    private record AssignmentResult(List<Assignment> assignments, Set<Integer> assignedIndexes) {
    }

    private record CandidateGroup(Object uid, int requiredCount, List<CandidateSlot> candidates, int totalCount) {
        private int getFirstSlotIndex() {
            return this.candidates.stream().mapToInt(candidate -> candidate.slot.index).min().orElse(Integer.MAX_VALUE);
        }
    }

    private record CandidateSlot(Slot slot, ItemStack stack) {
    }

    private record Assignment(int requiredIndex, Slot craftingSlot, Slot sourceSlot, int count) {
    }
}

