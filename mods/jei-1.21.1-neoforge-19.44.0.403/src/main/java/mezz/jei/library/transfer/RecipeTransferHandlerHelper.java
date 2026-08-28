/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.transfer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.common.transfer.RecipeTransferErrorInternal;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.common.util.ImmutableSize2i;
import mezz.jei.library.gui.helpers.CraftingGridHelper;
import mezz.jei.library.plugins.vanilla.crafting.CraftingRecipeCategory;
import mezz.jei.library.transfer.BasicRecipeTransferHandler;
import mezz.jei.library.transfer.BasicRecipeTransferInfo;
import mezz.jei.library.transfer.RecipeTransferErrorMissingSlots;
import mezz.jei.library.transfer.RecipeTransferErrorTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class RecipeTransferHandlerHelper
implements IRecipeTransferHandlerHelper {
    private final IStackHelper stackHelper;
    private final CraftingRecipeCategory craftingRecipeCategory;
    private final IConnectionToServer serverConnection;

    public RecipeTransferHandlerHelper(IStackHelper stackHelper, CraftingRecipeCategory craftingRecipeCategory, IConnectionToServer serverConnection) {
        this.stackHelper = stackHelper;
        this.craftingRecipeCategory = craftingRecipeCategory;
        this.serverConnection = serverConnection;
    }

    @Override
    public IRecipeTransferError createInternalError() {
        return RecipeTransferErrorInternal.INSTANCE;
    }

    @Override
    public IRecipeTransferError createUserErrorWithTooltip(Component tooltipMessage) {
        ErrorUtil.checkNotNull(tooltipMessage, "tooltipMessage");
        return new RecipeTransferErrorTooltip(tooltipMessage);
    }

    @Override
    public <C extends AbstractContainerMenu, R> IRecipeTransferInfo<C, R> createBasicRecipeTransferInfo(Class<? extends C> containerClass, @Nullable MenuType<C> menuType, RecipeType<R> recipeType, int recipeSlotStart, int recipeSlotCount, int inventorySlotStart, int inventorySlotCount) {
        ErrorUtil.checkNotNull(containerClass, "containerClass");
        ErrorUtil.checkNotNull(recipeType, "recipeType");
        return new BasicRecipeTransferInfo<C, R>(containerClass, menuType, recipeType, recipeSlotStart, recipeSlotCount, inventorySlotStart, inventorySlotCount);
    }

    @Override
    public <C extends AbstractContainerMenu, R> IRecipeTransferHandler<C, R> createUnregisteredRecipeTransferHandler(IRecipeTransferInfo<C, R> recipeTransferInfo) {
        ErrorUtil.checkNotNull(recipeTransferInfo, "recipeTransferInfo");
        return new BasicRecipeTransferHandler<C, R>(this.serverConnection, this.stackHelper, this, recipeTransferInfo);
    }

    @Override
    public IRecipeTransferError createUserErrorForMissingSlots(Component tooltipMessage, Collection<IRecipeSlotView> missingItemSlots) {
        ErrorUtil.checkNotNull(tooltipMessage, "tooltipMessage");
        ErrorUtil.checkNotEmpty(missingItemSlots, "missingItemSlots");
        return new RecipeTransferErrorMissingSlots(tooltipMessage, missingItemSlots);
    }

    @Override
    public IRecipeSlotsView createRecipeSlotsView(List<IRecipeSlotView> slotViews) {
        return () -> slotViews;
    }

    @Override
    public boolean recipeTransferHasServerSupport() {
        return this.serverConnection.isJeiOnServer();
    }

    @Override
    public Map<Integer, Ingredient> getGuiSlotIndexToIngredientMap(RecipeHolder<CraftingRecipe> recipeHolder) {
        ImmutableSize2i recipeSize = this.craftingRecipeCategory.getRecipeSize(recipeHolder);
        return CraftingGridHelper.getGuiSlotToIngredientMap(recipeHolder, recipeSize.width(), recipeSize.height());
    }
}

