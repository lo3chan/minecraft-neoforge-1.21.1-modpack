/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.api.recipe.transfer;

import java.util.Optional;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public interface IRecipeTransferHandler<C extends AbstractContainerMenu, R> {
    public Class<? extends C> getContainerClass();

    public Optional<MenuType<C>> getMenuType();

    public RecipeType<R> getRecipeType();

    @Nullable
    public IRecipeTransferError transferRecipe(C var1, R var2, IRecipeSlotsView var3, Player var4, boolean var5, boolean var6);
}

