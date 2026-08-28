/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.transfer;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.common.network.packets.PacketRecipeTransfer;
import mezz.jei.common.network.packets.PacketRecipeTransferCounted;
import mezz.jei.common.transfer.RecipeTransferOperationsResult;
import mezz.jei.common.transfer.RecipeTransferUtil;
import mezz.jei.common.transfer.TransferOperation;
import mezz.jei.common.util.StringUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class BasicRecipeTransferHandler<C extends AbstractContainerMenu, R>
implements IRecipeTransferHandler<C, R> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IConnectionToServer serverConnection;
    private final IStackHelper stackHelper;
    private final IRecipeTransferHandlerHelper handlerHelper;
    private final IRecipeTransferInfo<C, R> transferInfo;

    public BasicRecipeTransferHandler(IConnectionToServer serverConnection, IStackHelper stackHelper, IRecipeTransferHandlerHelper handlerHelper, IRecipeTransferInfo<C, R> transferInfo) {
        this.serverConnection = serverConnection;
        this.stackHelper = stackHelper;
        this.handlerHelper = handlerHelper;
        this.transferInfo = transferInfo;
    }

    @Override
    public Class<? extends C> getContainerClass() {
        return this.transferInfo.getContainerClass();
    }

    @Override
    public Optional<MenuType<C>> getMenuType() {
        return this.transferInfo.getMenuType();
    }

    @Override
    public RecipeType<R> getRecipeType() {
        return this.transferInfo.getRecipeType();
    }

    @Override
    @Nullable
    public IRecipeTransferError transferRecipe(C container, R recipe, IRecipeSlotsView recipeSlotsView, Player player, boolean maxTransfer, boolean doTransfer) {
        boolean useCountedTransferPacket;
        List<Slot> inventorySlots;
        if (!this.serverConnection.isJeiOnServer()) {
            MutableComponent tooltipMessage = Component.translatable((String)"jei.tooltip.error.recipe.transfer.no.server");
            return this.handlerHelper.createUserErrorWithTooltip((Component)tooltipMessage);
        }
        if (!this.transferInfo.canHandle(container, recipe)) {
            IRecipeTransferError handlingError = this.transferInfo.getHandlingError(container, recipe);
            if (handlingError != null) {
                return handlingError;
            }
            return this.handlerHelper.createInternalError();
        }
        List<Slot> craftingSlots = Collections.unmodifiableList(this.transferInfo.getRecipeSlots(container, recipe));
        if (!BasicRecipeTransferHandler.validateTransferInfo(this.transferInfo, container, craftingSlots, inventorySlots = Collections.unmodifiableList(this.transferInfo.getInventorySlots(container, recipe)))) {
            return this.handlerHelper.createInternalError();
        }
        List<IRecipeSlotView> inputItemSlotViews = recipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT);
        if (!BasicRecipeTransferHandler.validateRecipeView(this.transferInfo, container, craftingSlots, inputItemSlotViews)) {
            return this.handlerHelper.createInternalError();
        }
        InventoryState inventoryState = BasicRecipeTransferHandler.getInventoryState(craftingSlots, inventorySlots, player, container, this.transferInfo);
        if (inventoryState == null) {
            return this.handlerHelper.createInternalError();
        }
        int inputCount = (int)inputItemSlotViews.stream().filter(slot -> !slot.isEmpty()).count();
        if (!inventoryState.hasRoom(inputCount)) {
            MutableComponent message = Component.translatable((String)"jei.tooltip.error.recipe.transfer.inventory.full");
            return this.handlerHelper.createUserErrorWithTooltip((Component)message);
        }
        RecipeTransferOperationsResult transferOperations = RecipeTransferUtil.getRecipeTransferOperations(this.stackHelper, inventoryState.availableItemStacks, inputItemSlotViews, craftingSlots);
        if (!transferOperations.missingItems.isEmpty()) {
            MutableComponent message = Component.translatable((String)"jei.tooltip.error.recipe.transfer.missing");
            return this.handlerHelper.createUserErrorForMissingSlots((Component)message, transferOperations.missingItems);
        }
        if (!RecipeTransferUtil.validateSlots(player, transferOperations.results, craftingSlots, inventorySlots)) {
            return this.handlerHelper.createInternalError();
        }
        boolean requiresCountedTransferPacket = BasicRecipeTransferHandler.requiresCountedTransferPacket(transferOperations.results);
        boolean bl = useCountedTransferPacket = requiresCountedTransferPacket && this.serverConnection.canSendPacket(PacketRecipeTransferCounted.TYPE);
        if (doTransfer) {
            boolean requireCompleteSets = this.transferInfo.requireCompleteSets(container, recipe);
            if (useCountedTransferPacket) {
                PacketRecipeTransferCounted packet = PacketRecipeTransferCounted.fromSlots(transferOperations.results, craftingSlots, inventorySlots, maxTransfer, requireCompleteSets);
                this.serverConnection.sendPacketToServer(packet);
            } else {
                PacketRecipeTransfer packet = PacketRecipeTransfer.fromSlots(transferOperations.results, craftingSlots, inventorySlots, maxTransfer, requireCompleteSets);
                this.serverConnection.sendPacketToServer(packet);
            }
        }
        return null;
    }

    private static boolean requiresCountedTransferPacket(List<TransferOperation> transferOperations) {
        IntOpenHashSet craftingSlotIds = new IntOpenHashSet();
        for (TransferOperation transferOperation : transferOperations) {
            if (transferOperation.count() <= 1 && craftingSlotIds.add(transferOperation.craftingSlotId())) continue;
            return true;
        }
        return false;
    }

    public static <C extends AbstractContainerMenu, R> boolean validateTransferInfo(IRecipeTransferInfo<C, R> transferInfo, C container, List<Slot> craftingSlots, List<Slot> inventorySlots) {
        for (Slot slot : craftingSlots) {
            if (!slot.isFake()) continue;
            LOGGER.error("Recipe Transfer helper {} does not work for container {}. The Recipe Transfer Helper references crafting slot index [{}] but it is a fake (output) slot, which is not allowed.", transferInfo.getClass(), container.getClass(), (Object)slot.index);
            return false;
        }
        for (Slot slot : inventorySlots) {
            if (!slot.isFake()) continue;
            LOGGER.error("Recipe Transfer helper {} does not work for container {}. The Recipe Transfer Helper references inventory slot index [{}] but it is a fake (output) slot, which is not allowed.", transferInfo.getClass(), container.getClass(), (Object)slot.index);
            return false;
        }
        Set<Integer> craftingSlotIndexes = BasicRecipeTransferHandler.slotIndexes(craftingSlots);
        Set<Integer> inventorySlotIndexes = BasicRecipeTransferHandler.slotIndexes(inventorySlots);
        Set<Integer> containerSlotIndexes = BasicRecipeTransferHandler.slotIndexes((Collection<Slot>)container.slots);
        if (!containerSlotIndexes.containsAll(craftingSlotIndexes)) {
            LOGGER.error("Recipe Transfer helper {} does not work for container {}. The Recipes Transfer Helper references crafting slot indexes [{}] that are not found in the inventory container slots [{}]", transferInfo.getClass(), container.getClass(), (Object)StringUtil.intsToString(craftingSlotIndexes), (Object)StringUtil.intsToString(containerSlotIndexes));
            return false;
        }
        if (!containerSlotIndexes.containsAll(inventorySlotIndexes)) {
            LOGGER.error("Recipe Transfer helper {} does not work for container {}. The Recipes Transfer Helper references inventory slot indexes [{}] that are not found in the inventory container slots [{}]", transferInfo.getClass(), container.getClass(), (Object)StringUtil.intsToString(inventorySlotIndexes), (Object)StringUtil.intsToString(containerSlotIndexes));
            return false;
        }
        return true;
    }

    public static <C extends AbstractContainerMenu, R> boolean validateRecipeView(IRecipeTransferInfo<C, R> transferInfo, C container, List<Slot> craftingSlots, List<IRecipeSlotView> inputSlots) {
        if (inputSlots.size() > craftingSlots.size()) {
            LOGGER.error("Recipe View {} does not work for container {}. The Recipe View has more input slots ({}) than the number of inventory crafting slots ({})", transferInfo.getClass(), container.getClass(), (Object)inputSlots.size(), (Object)craftingSlots.size());
            return false;
        }
        return true;
    }

    public static Set<Integer> slotIndexes(Collection<Slot> slots) {
        IntOpenHashSet set = new IntOpenHashSet(slots.size());
        for (Slot s : slots) {
            set.add(s.index);
        }
        return set;
    }

    @Nullable
    public static <C extends AbstractContainerMenu, R> InventoryState getInventoryState(Collection<Slot> craftingSlots, Collection<Slot> inventorySlots, Player player, C container, IRecipeTransferInfo<C, R> transferInfo) {
        ItemStack stack;
        HashMap<Slot, ItemStack> availableItemStacks = new HashMap<Slot, ItemStack>();
        int filledCraftSlotCount = 0;
        int emptySlotCount = 0;
        for (Slot slot : craftingSlots) {
            stack = slot.getItem();
            if (stack.isEmpty()) continue;
            if (!slot.allowModification(player)) {
                LOGGER.error("Recipe Transfer helper {} does not work for container {}. The Player is not able to move items out of Crafting Slot number {}", transferInfo.getClass(), container.getClass(), (Object)slot.index);
                return null;
            }
            ++filledCraftSlotCount;
            availableItemStacks.put(slot, stack.copy());
        }
        for (Slot slot : inventorySlots) {
            stack = slot.getItem();
            if (!stack.isEmpty()) {
                if (!slot.allowModification(player)) continue;
                availableItemStacks.put(slot, stack.copy());
                continue;
            }
            if (!slot.allowModification(player)) continue;
            ++emptySlotCount;
        }
        return new InventoryState(availableItemStacks, filledCraftSlotCount, emptySlotCount);
    }

    public record InventoryState(Map<Slot, ItemStack> availableItemStacks, int filledCraftSlotCount, int emptySlotCount) {
        public boolean hasRoom(int inputCount) {
            return this.filledCraftSlotCount - inputCount <= this.emptySlotCount;
        }
    }
}

