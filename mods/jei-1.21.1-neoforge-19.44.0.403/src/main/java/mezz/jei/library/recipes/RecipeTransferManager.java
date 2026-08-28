/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableTable
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.recipes;

import com.google.common.collect.ImmutableTable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferManager;
import mezz.jei.common.Constants;
import mezz.jei.common.util.ErrorUtil;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class RecipeTransferManager
implements IRecipeTransferManager {
    private final ImmutableTable<Class<? extends AbstractContainerMenu>, RecipeType<?>, IRecipeTransferHandler<?, ?>> recipeTransferHandlers;
    private final Set<AbstractContainerMenu> unsupportedContainers = new HashSet<AbstractContainerMenu>();

    public RecipeTransferManager(ImmutableTable<Class<? extends AbstractContainerMenu>, RecipeType<?>, IRecipeTransferHandler<?, ?>> recipeTransferHandlers) {
        this.recipeTransferHandlers = recipeTransferHandlers;
    }

    @Override
    public <C extends AbstractContainerMenu, R> Optional<IRecipeTransferHandler<C, R>> getRecipeTransferHandler(C container, IRecipeCategory<R> recipeCategory) {
        ErrorUtil.checkNotNull(container, "container");
        ErrorUtil.checkNotNull(recipeCategory, "recipeCategory");
        MenuType<C> menuType = this.getMenuType(container);
        RecipeType<R> recipeType = recipeCategory.getRecipeType();
        Class<?> containerClass = container.getClass();
        Optional<IRecipeTransferHandler<C, R>> handler = this.getHandler(containerClass, menuType, recipeType);
        if (handler.isPresent()) {
            return handler;
        }
        return this.getHandler(containerClass, menuType, Constants.UNIVERSAL_RECIPE_TRANSFER_TYPE);
    }

    @Nullable
    private <C extends AbstractContainerMenu> MenuType<C> getMenuType(C container) {
        if (this.unsupportedContainers.contains(container)) {
            return null;
        }
        try {
            MenuType cast = container.getType();
            return cast;
        }
        catch (UnsupportedOperationException ignored) {
            this.unsupportedContainers.add(container);
            return null;
        }
    }

    private <C extends AbstractContainerMenu, R> Optional<IRecipeTransferHandler<C, R>> getHandler(Class<? extends C> containerClass, @Nullable MenuType<C> menuType, RecipeType<?> recipeType) {
        IRecipeTransferHandler handler = (IRecipeTransferHandler)this.recipeTransferHandlers.get(containerClass, recipeType);
        if (handler == null) {
            return Optional.empty();
        }
        Optional handlerMenuType = handler.getMenuType();
        if (handlerMenuType.isEmpty() || handlerMenuType.get().equals(menuType)) {
            IRecipeTransferHandler cast = handler;
            return Optional.of(cast);
        }
        return Optional.empty();
    }
}

