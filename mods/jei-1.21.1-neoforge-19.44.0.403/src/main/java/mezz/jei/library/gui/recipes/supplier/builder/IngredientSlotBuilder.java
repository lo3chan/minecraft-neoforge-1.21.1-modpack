/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponentPatch
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.material.Fluid
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.gui.recipes.supplier.builder;

import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.TilingDirection;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.library.ingredients.SimpleIngredientAcceptor;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public class IngredientSlotBuilder
implements IRecipeSlotBuilder {
    private final SimpleIngredientAcceptor ingredients;

    public IngredientSlotBuilder(IIngredientManager ingredientManager) {
        this.ingredients = new SimpleIngredientAcceptor(ingredientManager);
    }

    @Override
    public <I> IRecipeSlotBuilder addIngredients(IIngredientType<I> ingredientType, List<@Nullable I> ingredients) {
        this.ingredients.addIngredients((IIngredientType)ingredientType, (List)ingredients);
        return this;
    }

    @Override
    public <I> IRecipeSlotBuilder addIngredient(IIngredientType<I> ingredientType, I ingredient) {
        this.ingredients.addIngredient((IIngredientType)ingredientType, (Object)ingredient);
        return this;
    }

    @Override
    public IRecipeSlotBuilder addFluidStack(Fluid fluid) {
        this.ingredients.addFluidStack(fluid);
        return this;
    }

    @Override
    public IRecipeSlotBuilder addFluidStack(Fluid fluid, long amount) {
        this.ingredients.addFluidStack(fluid, amount);
        return this;
    }

    @Override
    public IRecipeSlotBuilder addFluidStack(Fluid fluid, long amount, DataComponentPatch componentPatch) {
        this.ingredients.addFluidStack(fluid, amount, componentPatch);
        return this;
    }

    @Override
    public IRecipeSlotBuilder addIngredientsUnsafe(List<?> ingredients) {
        this.ingredients.addIngredientsUnsafe((List)ingredients);
        return this;
    }

    @Override
    public IRecipeSlotBuilder addTypedIngredients(List<ITypedIngredient<?>> ingredients) {
        this.ingredients.addTypedIngredients((List)ingredients);
        return this;
    }

    @Override
    public IRecipeSlotBuilder addOptionalTypedIngredients(List<Optional<ITypedIngredient<?>>> ingredients) {
        this.ingredients.addOptionalTypedIngredients((List)ingredients);
        return this;
    }

    @Override
    public IRecipeSlotBuilder addItemStacks(List<ItemStack> itemStacks) {
        this.ingredients.addItemStacks((List)itemStacks);
        return this;
    }

    @Override
    public IRecipeSlotBuilder setStandardSlotBackground() {
        return this;
    }

    @Override
    public IRecipeSlotBuilder setOutputSlotBackground() {
        return this;
    }

    @Override
    public IRecipeSlotBuilder setBackground(IDrawable background, int xOffset, int yOffset) {
        return this;
    }

    @Override
    public IRecipeSlotBuilder setOverlay(IDrawable overlay, int xOffset, int yOffset) {
        return this;
    }

    @Override
    public IRecipeSlotBuilder setFluidRenderer(long capacity, boolean showCapacity, int width, int height) {
        return this;
    }

    @Override
    public IRecipeSlotBuilder setFluidRenderer(long capacity, boolean showCapacity, int width, int height, TilingDirection tilingDirection) {
        return this;
    }

    @Override
    public <T> IRecipeSlotBuilder setCustomRenderer(IIngredientType<T> ingredientType, IIngredientRenderer<T> ingredientRenderer) {
        return this;
    }

    @Override
    public IRecipeSlotBuilder addTooltipCallback(IRecipeSlotTooltipCallback tooltipCallback) {
        return this;
    }

    @Override
    public IRecipeSlotBuilder addRichTooltipCallback(IRecipeSlotRichTooltipCallback tooltipCallback) {
        return this;
    }

    @Override
    public IRecipeSlotBuilder setSlotName(String slotName) {
        return this;
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public IRecipeSlotBuilder setPosition(int xPos, int yPos) {
        return this;
    }

    @Override
    public IRecipeSlotBuilder setPosition(int areaX, int areaY, int areaWidth, int areaHeight, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
        return this;
    }

    public List<ITypedIngredient<?>> getAllIngredients() {
        return this.ingredients.getAllIngredients();
    }
}

