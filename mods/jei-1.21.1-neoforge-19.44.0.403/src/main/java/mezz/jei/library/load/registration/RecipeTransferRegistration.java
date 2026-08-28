/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.load.registration;

import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.recipe.transfer.IRecipeTransferManager;
import mezz.jei.api.recipe.transfer.IUniversalRecipeTransferHandler;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.common.Constants;
import mezz.jei.common.collect.Table;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.library.recipes.RecipeTransferManager;
import mezz.jei.library.recipes.UniversalRecipeTransferHandlerAdapter;
import mezz.jei.library.transfer.BasicRecipeTransferHandler;
import mezz.jei.library.transfer.BasicRecipeTransferInfo;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class RecipeTransferRegistration
implements IRecipeTransferRegistration {
    private final Table<Class<? extends AbstractContainerMenu>, RecipeType<?>, IRecipeTransferHandler<?, ?>> recipeTransferHandlers = Table.hashBasedTable();
    private final IStackHelper stackHelper;
    private final IRecipeTransferHandlerHelper handlerHelper;
    private final IJeiHelpers jeiHelpers;
    private final IConnectionToServer serverConnection;

    public RecipeTransferRegistration(IStackHelper stackHelper, IRecipeTransferHandlerHelper handlerHelper, IJeiHelpers jeiHelpers, IConnectionToServer serverConnection) {
        this.stackHelper = stackHelper;
        this.handlerHelper = handlerHelper;
        this.jeiHelpers = jeiHelpers;
        this.serverConnection = serverConnection;
    }

    @Override
    public IJeiHelpers getJeiHelpers() {
        return this.jeiHelpers;
    }

    @Override
    public IRecipeTransferHandlerHelper getTransferHelper() {
        return this.handlerHelper;
    }

    @Override
    public <C extends AbstractContainerMenu, R> void addRecipeTransferHandler(Class<? extends C> containerClass, @Nullable MenuType<C> menuType, RecipeType<R> recipeType, int recipeSlotStart, int recipeSlotCount, int inventorySlotStart, int inventorySlotCount) {
        ErrorUtil.checkNotNull(containerClass, "containerClass");
        ErrorUtil.checkNotNull(recipeType, "recipeType");
        BasicRecipeTransferInfo<? extends C, R> recipeTransferInfo = new BasicRecipeTransferInfo<C, R>(containerClass, menuType, recipeType, recipeSlotStart, recipeSlotCount, inventorySlotStart, inventorySlotCount);
        this.addRecipeTransferHandler(recipeTransferInfo);
    }

    @Override
    public <C extends AbstractContainerMenu, R> void addRecipeTransferHandler(IRecipeTransferInfo<C, R> recipeTransferInfo) {
        ErrorUtil.checkNotNull(recipeTransferInfo, "recipeTransferInfo");
        BasicRecipeTransferHandler<C, R> recipeTransferHandler = new BasicRecipeTransferHandler<C, R>(this.serverConnection, this.stackHelper, this.handlerHelper, recipeTransferInfo);
        this.addRecipeTransferHandler(recipeTransferHandler, recipeTransferInfo.getRecipeType());
    }

    @Override
    public <C extends AbstractContainerMenu, R> void addRecipeTransferHandler(IRecipeTransferHandler<C, R> recipeTransferHandler, RecipeType<R> recipeType) {
        ErrorUtil.checkNotNull(recipeTransferHandler, "recipeTransferHandler");
        ErrorUtil.checkNotNull(recipeType, "recipeType");
        Class<C> containerClass = recipeTransferHandler.getContainerClass();
        this.recipeTransferHandlers.put(containerClass, recipeType, recipeTransferHandler);
    }

    @Override
    public <C extends AbstractContainerMenu> void addUniversalRecipeTransferHandler(IUniversalRecipeTransferHandler<C> universalRecipeTransferHandler) {
        ErrorUtil.checkNotNull(universalRecipeTransferHandler, "universalRecipeTransferHandler");
        Class<C> containerClass = universalRecipeTransferHandler.getContainerClass();
        UniversalRecipeTransferHandlerAdapter adapter = new UniversalRecipeTransferHandlerAdapter(universalRecipeTransferHandler);
        this.recipeTransferHandlers.put(containerClass, adapter.getRecipeType(), adapter);
    }

    @Override
    public <C extends AbstractContainerMenu, R> void addUniversalRecipeTransferHandler(IRecipeTransferHandler<C, R> recipeTransferHandler) {
        ErrorUtil.checkNotNull(recipeTransferHandler, "recipeTransferHandler");
        Class<C> containerClass = recipeTransferHandler.getContainerClass();
        this.recipeTransferHandlers.put(containerClass, Constants.UNIVERSAL_RECIPE_TRANSFER_TYPE, recipeTransferHandler);
    }

    public IRecipeTransferManager createRecipeTransferManager() {
        return new RecipeTransferManager(this.recipeTransferHandlers.toImmutable());
    }
}

