/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.recipes;

import java.util.Optional;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IUniversalRecipeTransferHandler;
import mezz.jei.common.Constants;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class UniversalRecipeTransferHandlerAdapter<C extends AbstractContainerMenu, R>
implements IRecipeTransferHandler<C, R> {
    private final IUniversalRecipeTransferHandler<C> universalRecipeTransferHandler;

    public UniversalRecipeTransferHandlerAdapter(IUniversalRecipeTransferHandler<C> universalRecipeTransferHandler) {
        this.universalRecipeTransferHandler = universalRecipeTransferHandler;
    }

    @Override
    public Class<? extends C> getContainerClass() {
        return this.universalRecipeTransferHandler.getContainerClass();
    }

    @Override
    public Optional<MenuType<C>> getMenuType() {
        return this.universalRecipeTransferHandler.getMenuType();
    }

    @Override
    public RecipeType<R> getRecipeType() {
        return Constants.UNIVERSAL_RECIPE_TRANSFER_TYPE;
    }

    @Override
    @Nullable
    public IRecipeTransferError transferRecipe(C container, R recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        return this.universalRecipeTransferHandler.transferRecipe(container, recipe, recipeSlots, player, maxTransfer, doTransfer);
    }
}

