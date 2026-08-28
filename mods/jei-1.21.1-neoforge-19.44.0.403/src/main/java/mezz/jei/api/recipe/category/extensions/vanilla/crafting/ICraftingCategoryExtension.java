/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.api.recipe.category.extensions.vanilla.crafting;

import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public interface ICraftingCategoryExtension<R extends CraftingRecipe>
extends IRecipeCategoryExtension<RecipeHolder<R>> {
    default public void setRecipe(RecipeHolder<R> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        this.setRecipe(builder, craftingGridHelper, focuses);
    }

    default public void onDisplayedIngredientsUpdate(RecipeHolder<R> recipeHolder, List<IRecipeSlotDrawable> recipeSlots, IFocusGroup focuses) {
    }

    @Deprecated(since="19.4.1", forRemoval=true)
    default public Optional<ResourceLocation> getRegistryName(RecipeHolder<R> recipeHolder) {
        return Optional.ofNullable(this.getRegistryName()).or(() -> Optional.of(recipeHolder.id()));
    }

    default public int getWidth(RecipeHolder<R> recipeHolder) {
        return this.getWidth();
    }

    default public int getHeight(RecipeHolder<R> recipeHolder) {
        return this.getHeight();
    }

    @Deprecated(since="16.0.0", forRemoval=true)
    default public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
    }

    @Deprecated(since="16.0.0", forRemoval=true)
    @Nullable
    default public ResourceLocation getRegistryName() {
        return null;
    }

    @Deprecated(since="16.0.0", forRemoval=true)
    default public int getWidth() {
        return 0;
    }

    @Deprecated(since="16.0.0", forRemoval=true)
    default public int getHeight() {
        return 0;
    }
}

